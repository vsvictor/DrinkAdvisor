package com.drink.query;

import com.drink.settings.AppSettings;
import com.drink.types.Meeting;

public class MeetingQuery extends WebQuery 
{
	public MeetingQuery(AppSettings appSettings, String id) 
	{
		mURLBuilder.setPath("meetings/" + String.valueOf(id));
		
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("user_id", appSettings.getUserId());
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			data.meeting = Meeting.parse(mJSONResponse);
		}
		
		return data;
	}

	public class Data extends WebQueryData 
	{
		public Meeting	meeting;
	}
}
