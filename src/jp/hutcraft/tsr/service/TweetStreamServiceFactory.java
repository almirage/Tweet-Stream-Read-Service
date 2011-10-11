package jp.hutcraft.tsr.service;


public final class TweetStreamServiceFactory {

	private TweetStreamServiceFactory() {}
	
	public interface LogAppender {
		void notice(final String message);
		void notice(final Throwable e);
	}
	public interface TweetHandler {
		void notice(final Tweet tweet);
	}

	private static final LogAppender nullAppender = new LogAppender(){
		@Override public void notice(final String message) {
		}
		@Override public void notice(final Throwable e) {
		}
	};
	
	public static TweetStreamService create(final TweetHandler handler,
			final String user,
			final String pass,
			final String keyword) {
		return new TweetStreamServiceImpl(handler, user, pass, keyword, nullAppender);
	}

	public static TweetStreamService create(final TweetHandler handler,
			final String user,
			final String pass,
			final String keyword,
			final LogAppender logAppender) {
		return new TweetStreamServiceImpl(handler, user, pass, keyword, logAppender);
	}
	
	public TweetStreamService createMock(final TweetHandler handler) {
		return new TweetStreamServiceMock(handler);
	}
}
