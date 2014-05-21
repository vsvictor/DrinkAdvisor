package com.drink.query;

import org.apache.http.message.BasicNameValuePair;

import com.drink.settings.AppSettings;

public class UpdateGoQuery extends WebQuery 
{
	public UpdateGoQuery(AppSettings appSettings, String meeting_id, String go) 
	{
		mURLBuilder.setPath("meetings/updateWasInMeeting");

		nameValuePairs.add(new BasicNameValuePair("device_token", appSettings.getContentToken()));
		nameValuePairs.add(new BasicNameValuePair("user_token", appSettings.getUserToken()));
		nameValuePairs.add(new BasicNameValuePair("user_id", appSettings.getUserId()));
		nameValuePairs.add(new BasicNameValuePair("go", go));
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
				data.invited = mJSONResponse.optInt("invited_count", 0);
				data.willGo = mJSONResponse.optInt("will_go_count", 0);
			} 
			catch (Exception e) {}
		}
		
		return data;
	}
	
	public class Data extends WebQueryData
	{
		public boolean success = false;
		public int invited = 0;
		public int willGo = 0;
	}
}
