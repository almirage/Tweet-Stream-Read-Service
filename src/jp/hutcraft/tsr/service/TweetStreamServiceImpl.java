package jp.hutcraft.tsr.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import jp.hutcraft.tsr.service.TweetStreamServiceFactory.LogAppender;
import jp.hutcraft.tsr.service.TweetStreamServiceFactory.TweetHandler;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

import org.apache.commons.codec.binary.Base64;

public final class TweetStreamServiceImpl implements TweetStreamService {

	volatile private boolean running = true;
	
	private final TweetHandler handler;
	private final String user;
	private final String pass;
	private final String keyword;
	private final LogAppender logAppender;
	
	/*package private*/ TweetStreamServiceImpl(final TweetHandler handler,
			final String user,
			final String pass,
			final String keyword,
			final LogAppender logAppender) {
		this.handler = handler;
		this.user = user;
		this.pass = pass;
		this.keyword = keyword;
		this.logAppender = logAppender;
	}
	
	public void invoke() {
		try {
			logAppender.notice("service start reading");
		for (int i = 0; i < 10 && running; i++) {
			proceed(handler, user, pass, keyword, logAppender);
			logAppender.notice("proceed returns");
		}
		} catch (final Throwable t) {
			logAppender.notice(t);
		}
	}

	private void proceed(final TweetHandler handler,
			final String user, final String pass, final String hash, final LogAppender logAppender) {
		try {
			final SocketFactory factory = SSLSocketFactory.getDefault();
			final Socket sock = factory.createSocket(InetAddress.getByName("stream.twitter.com"), 443);
			try {
				processStream(sock, handler, user, pass, hash, logAppender);
			} catch (final IOException e) {
				logAppender.notice(e);
			} finally {
				sock.close();
			}
		} catch (final UnknownHostException e) {
			logAppender.notice(e);
		} catch (final IOException e) {
			logAppender.notice(e);
		}
	}

	private void processStream(final Socket sock,
			final TweetHandler handler, final String user, final String pass, final String hash, final LogAppender logAppender)
	throws IOException {
		logAppender.notice("processStream started");
		
		final String query = "track="+ hash;
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		writer.write("POST /1/statuses/filter.json HTTP/1.1\r\n");
		writer.write("Authorization: Basic "+ new String(Base64.encodeBase64((user+":"+pass).getBytes())).trim()+"\r\n");
		writer.write("User-Agent: test\r\n");
		writer.write("Host: stream.twitter.com\r\n");
		writer.write("Accept: */*\r\n");
		writer.write("Content-Length: "+ query.length() +"\r\n");
		writer.write("Content-Type: application/x-www-form-urlencoded\r\n");
		writer.write("\r\n");
		writer.write(query);
		writer.flush();
		
		final BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		
		cutHeader(reader);
		
		String line = "";
		while ((line = reader.readLine()) != null) {
			if (!running) return;
			line = line.trim();
			if ("2".equals(line)) continue;
			if ("".equals(line)) continue;
			// ignore the byte message, read a message body
			line = reader.readLine();

			logAppender.notice(line);

			while (running) {
				try {
					final Tweet tweet = parse(line);
					handler.notice(tweet);
					break;
				} catch (final JSONException e) {
					reader.readLine(); // truncate
					line += reader.readLine();
				}
			}
		}
	}

	/* package private */ Tweet parse(final String line) {
		try {
			final Tweet r = JSON.decode(line, Tweet.class);
			return r;
		} catch (final JSONException e) {
			logAppender.notice(e);
			throw e;
		}
	}

	private void cutHeader(BufferedReader reader) throws IOException {
		String line = "";
		while ((line = reader.readLine()) != null) {
			logAppender.notice(line);
			if ("".equals(line)) return;
		}
	}

	public void finish() {
		running = false;
	}
}
