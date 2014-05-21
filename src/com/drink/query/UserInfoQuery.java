
package com.drink.query;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.drink.settings.AppSettings;

public class UserInfoQuery extends WebQuery 
{
	public UserInfoQuery(AppSettings appSettings) 
	{
		mURLBuilder.setPath("users/info");

		nameValuePairs.add(new BasicNameValuePair("device_token", appSettings.getContentToken()));
		nameValuePairs.add(new BasicNameValuePair("user_token", appSettings.getUserToken()));
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();

		if (getResult())
		{
			try 
			{
				if (mJSONResponse.opt("code") != null) 
				{
					data.error = 3;
				}
				else
				{
					data.user_token = mJSONResponse.optString("token");
					data.user_id = mJSONResponse.optString("user_id");
					data.user_name = mJSONResponse.optString("user_name");
					data.user_picture = mJSONResponse.optString("user_picture");

					JSONObject jsEvents = mJSONResponse.getJSONObject("events_count");
					data.events_feed = jsEvents.optInt("feed");
					data.events_friends = jsEvents.optInt("friends");
					data.events_meetings = jsEvents.optInt("meetings");

					data.connect_facebook = mJSONResponse.optBoolean("connect_facebook");
					data.connect_twitter = mJSONResponse.optBoolean("connect_twitter");

					data.publish_on_facebook = mJSONResponse.optBoolean("publish_on_facebook");
					data.publish_on_twitter = mJSONResponse.optBoolean("publish_on_twitter");
				}
			} 
			catch (Exception e) {}
		}

		return data;
	}

	public class Data extends WebQueryData 
	{
		public int error = -1;
		
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
