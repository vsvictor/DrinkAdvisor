package com.drink.query;

import java.util.Date;

import com.drink.helpers.CommonHelper;
import com.drink.settings.AppSettings;

public class UserDataQuery extends WebQuery 
{
	public UserDataQuery(AppSettings appSettings) 
	{
		mURLBuilder.setPath("Profile/getV2");

		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("id_user",	appSettings.getUserId());
	}

	@Override
	public Data getData() 
	{
		Data data = null;
		if (getResult()) 
		{
			try 
			{
				data = new Data();
				
				data.name = mJSONResponse.optString("name");
				data.date_birth = CommonHelper.parseDate(mJSONResponse.optString("date_birth"));
				data.sex = mJSONResponse.optString("sex");
				data.id_city = mJSONResponse.optString("id_city");
				data.avatar = mJSONResponse.optString("avatar");
				data.connectFacebook = mJSONResponse.optBoolean("connect_facebook");
				data.publishFacebook = mJSONResponse.optBoolean("publish_on_facebook");
				data.connectTwitter = mJSONResponse.optBoolean("connect_twitter");
				data.publishTwitter = mJSONResponse.optBoolean("publish_on_twitter");
				data.isPrivate = mJSONResponse.optBoolean("isPrivate");
				data.notifyEmailLikes = mJSONResponse.optBoolean("notify_likes_to_email");
				data.notifyPushLikes = mJSONResponse.optBoolean("notify_likes_to_push");
				data.notifyEmailComments = mJSONResponse.optBoolean("notify_comments_to_email");
				data.notifyPushComments = mJSONResponse.optBoolean("notify_comments_to_push");
				data.notifyEmailPosts = mJSONResponse.optBoolean("notify_posts_to_email");
				data.notifyPushPosts = mJSONResponse.optBoolean("notify_posts_to_push");
				data.notifyEmailCheckins = mJSONResponse.optBoolean("notify_checkins_to_email");
				data.notifyPushCheckins = mJSONResponse.optBoolean("notify_checkins_to_push");
			} 
			catch (Exception e)	{}
		}
		
		return data;
	}

	public class Data extends WebQueryData 
	{
		public String name;
		public Date date_birth;
		public String sex;		
		public String id_city;
		public String avatar;
		public boolean connectFacebook;
		public boolean publishFacebook;
		public boolean connectTwitter;
		public boolean publishTwitter;
		public boolean isPrivate = false;
		public boolean notifyEmailLikes = false;
		public boolean notifyPushLikes = false;
		public boolean notifyEmailComments = false;
		public boolean notifyPushComments = false;
		public boolean notifyEmailPosts = false;
		public boolean notifyPushPosts = false;
		public boolean notifyEmailCheckins = false;
		public boolean notifyPushCheckins = false;
	}
}
