package jp.hutcraft.tsr.service;

import java.util.Date;

/**
 * see https://dev.twitter.com/docs/api/1/get/statuses/show/%3Aid
 *
 */
public class Tweet {

	public String text;
	public String id_str;
	public boolean favorited;
	public boolean truncated;
	public String retweet_count;
	public String in_reply_to_screen_name;
	public String source;
	public Date created_at;
	public boolean retweeted;
	public String in_reply_to_status_id_str;
	public String geo;
	public String in_reply_to_user_id_str;
	public User user;
	
	public static class User {
		public String name;
		public String profile_image_url;
		public String screen_name;
	}
}
