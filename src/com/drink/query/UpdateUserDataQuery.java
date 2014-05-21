package com.drink.query;

import org.apache.http.message.BasicNameValuePair;

import com.drink.settings.AppSettings;

public class UpdateUserDataQuery extends WebQuery 
{
	public static final int USER_NAME = 0;
	public static final int USER_BIRTHDAY = 1;
	public static final int USER_SEX = 2;
	public static final int USER_AVATAR = 3;
	public static final int LIKES_TO_EMAIL = 4;
	public static final int LIKES_TO_PUSH = 5;
	public static final int COMMENTS_TO_EMAIL = 6;
	public static final int COMMENTS_TO_PUSH = 7;
	public static final int POSTS_TO_EMAIL = 8;
	public static final int POSTS_TO_PUSH = 9;
	public static final int CHECKINS_TO_EMAIL = 10;
	public static final int CHECHINS_TO_PUSH = 11;
	public static final int PUBLISH_ON_FACEBOOK = 12;
	public static final int PUBLISH_ON_TWITTER = 13;
	public static final int PRIVATE_MODE = 14;
	
	public UpdateUserDataQuery(AppSettings appSettings, int action, String param) 
	{
		mURLBuilder.setPath("profile/updateV2");
		
		nameValuePairs.add(new BasicNameValuePair(USER_TOKEN, appSettings.getUserToken()));
		nameValuePairs.add(new BasicNameValuePair(DEVICE_TOKEN, appSettings.getContentToken()));
		nameValuePairs.add(new BasicNameValuePair("user_id", appSettings.getUserId()));
		
		nameValuePairs.add(new BasicNameValuePair("action", Integer.toString(action)));
		nameValuePairs.add(new BasicNameValuePair("param", param));
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			try 
			{
				if (mJSONResponse.has("success"))
				{
					data.success = mJSONResponse.optBoolean("success");
				}
			} 
			catch (Exception e) {}
		}
		
		return data;
	}

	public class Data extends WebQueryData 
	{
		public boolean success = false;
	}
}