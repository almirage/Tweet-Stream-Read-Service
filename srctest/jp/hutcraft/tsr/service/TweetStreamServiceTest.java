package jp.hutcraft.tsr.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jp.hutcraft.tsr.service.TweetStreamServiceFactory.LogAppender;
import junit.framework.TestCase;

public class TweetStreamServiceTest extends TestCase {

	public void testParse() {
		final String line = "{\"text\":\"#tutng \\u307b\\u3051\\u304d\\u3087\\u3063\\u304d\\u3087\",\"id_str\":\"119507701490388992\",\"favorited\":false,\"truncated\":false,\"retweet_count\":0,\"in_reply_to_screen_name\":null,\"in_reply_to_user_id\":null,\"source\":\"web\",\"created_at\":\"Thu Sep 29 20:23:51 +0000 2011\",\"retweeted\":false,\"in_reply_to_status_id_str\":null,\"geo\":null,\"in_reply_to_user_id_str\":null,\"entities\":{\"hashtags\":[{\"text\":\"tutng\",\"indices\":[0,6]}],\"urls\":[],\"user_mentions\":[]},\"contributors\":null,\"place\":null,\"coordinates\":null,\"user\":{\"listed_count\":0,\"following\":null,\"notifications\":null,\"profile_background_image_url\":\"http:\\/\\/a0.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"friends_count\":0,\"id_str\":\"382183237\",\"profile_link_color\":\"0084B4\",\"profile_image_url_https\":\"https:\\/\\/si0.twimg.com\\/sticky\\/default_profile_images\\/default_profile_2_normal.png\",\"screen_name\":\"tutest11\",\"verified\":false,\"profile_image_url\":\"http:\\/\\/a1.twimg.com\\/sticky\\/default_profile_images\\/default_profile_2_normal.png\",\"show_all_inline_media\":false,\"contributors_enabled\":false,\"profile_background_color\":\"C0DEED\",\"description\":null,\"default_profile_image\":true,\"profile_background_tile\":false,\"created_at\":\"Thu Sep 29 16:39:26 +0000 2011\",\"statuses_count\":10,\"time_zone\":null,\"profile_sidebar_fill_color\":\"DDEEF6\",\"followers_count\":0,\"default_profile\":true,\"follow_request_sent\":null,\"geo_enabled\":false,\"profile_background_image_url_https\":\"https:\\/\\/si0.twimg.com\\/images\\/themes\\/theme1\\/bg.png\",\"favourites_count\":0,\"profile_sidebar_border_color\":\"C0DEED\",\"protected\":false,\"url\":null,\"lang\":\"en\",\"name\":\"tu\",\"profile_use_background_image\":true,\"id\":382183237,\"is_translator\":false,\"utc_offset\":null,\"profile_text_color\":\"333333\",\"location\":null},\"id\":119507701490388992,\"in_reply_to_status_id\":null}";
		final TweetStreamServiceImpl s = new TweetStreamServiceImpl(null, "", "", "", new LogAppender(){
			@Override public void notice(String message) {
			}
			@Override public void notice(Throwable e) {
			}});
		final Tweet r = s.parse(line);
		assertEquals("#tutng ほけきょっきょ", r.text);
		assertEquals("tu", r.user.name);
		assertEquals("tutest11", r.user.screen_name);
		assertEquals("Fri Sep 30 05:23:51 JST 2011", r.created_at.toString());
	}
	
	public void testCanParse() {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("twStSample.txt")));
		final TweetStreamServiceImpl s = new TweetStreamServiceImpl(null, "", "", "", new LogAppender(){
			@Override public void notice(String message) {
			}
			@Override public void notice(Throwable e) {
			}});
		String line = "";
		try {
			while ((line = reader.readLine()) != null) {
				s.parse(line);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
		try {
			reader.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
