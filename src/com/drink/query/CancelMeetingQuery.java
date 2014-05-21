package com.drink.query;

import org.apache.http.message.BasicNameValuePair;

import com.drink.settings.AppSettings;

public class CancelMeetingQuery extends WebQuery 
{
	public CancelMeetingQuery(AppSettings appSettings, String meeting_id) 
	{
		mURLBuilder.setPath("meetings/cancel");

		nameValuePairs.add(new BasicNameValuePair("device_token", appSettings.getContentToken()));
		nameValuePairs.add(new BasicNameValuePair("user_token", appSettings.getUserToken()));
		nameValuePairs.add(new BasicNameValuePair("user_id", appSettings.getUserId()));
		nameValuePairs.add(new BasicNameValuePair("meeting_id", meeting_id));

		getResponse(WebQuery.POST);
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		
		if (getResult()) 
		{
			try 
			{
				data.success = mJSONResponse.optBoolean("success", false);
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
