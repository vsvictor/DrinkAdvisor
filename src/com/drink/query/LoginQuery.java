
package com.drink.query;

import java.security.NoSuchAlgorithmException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.drink.helpers.Defines;
import com.drink.helpers.SHA1Helper;
import com.drink.settings.AppSettings;

public class LoginQuery extends WebQuery 
{
	public LoginQuery(AppSettings appSettings, int type, String accessToken, String socialId) 
	{
		mURLBuilder.setPath("users");

		nameValuePairs.add(new BasicNameValuePair("device_token", appSettings.getContentToken()));
		nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));

		nameValuePairs.add(new BasicNameValuePair("auth_type", Integer.toString(type)));
		nameValuePairs.add(new BasicNameValuePair("social_identifier", socialId));
	}

	public LoginQuery(AppSettings appSettings, String login, String password) 
	{
		mURLBuilder.setPath("users");

		nameValuePairs.add(new BasicNameValuePair("device_token", appSettings.getContentToken()));

		nameValuePairs.add(new BasicNameValuePair("auth_type", Integer.toString(Defines.TYPE_USUAL)));
		nameValuePairs.add(new BasicNameValuePair("login", login));
		
		String sha1 = "";
		try 
		{
			sha1 = SHA1Helper.sha1(password);
		} 
		catch (NoSuchAlgorithmException e) {}
		
		nameValuePairs.add(new BasicNameValuePair("password", sha1));
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();

		if (getResult())
		{
			try 
			{
				if (mJSONResponse.opt("error_code") == null) 
				{
					data.user_token = mJSONResponse.optString("token");
					data.user_id = mJSONResponse.optString("user_id");
					data.user_name = mJSONResponse.optString("user_name");
					data.user_picture = mJSONResponse.optString("user_picture");

					JSONObject jsEvents = mJSONResponse.getJSONObject("events_count");
					data.events_feed = jsEvents.optInt("feed");
					data.events_friends = jsEvents.optInt("friends");
					data.events_meetings = jsEvents.optInt("meetings");

					data.connect_twitter = mJSONResponse.optBoolean("connect_twitter");
					data.connect_facebook = mJSONResponse.optBoolean("connect_facebook");

					data.publish_on_twitter = mJSONResponse.optBoolean("publish_on_twitter");
					data.publish_on_facebook = mJSONResponse.optBoolean("publish_on_facebook");

					data.error = -1;
				}
			} 
			catch (Exception e) {}
		}

		return data;
	}

	public class Data extends WebQueryData 
	{
		public int error = 3;
		
		public String user_token;
		public String user_id;
		public String user_name;
		public String user_picture;
		
		public int events_feed = 0;
		public int events_friends = 0;
		public int events_meetings = 0;
		
		public boolean connect_twitter = false;
		public boolean connect_facebook = false;
		
		public boolean publish_on_facebook = false;
		public boolean publish_on_twitter = false;
	}
}
