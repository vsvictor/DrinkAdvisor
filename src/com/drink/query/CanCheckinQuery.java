package com.drink.query;

import org.json.JSONException;

import com.drink.settings.AppSettings;

public class CanCheckinQuery extends WebQuery
{
	public CanCheckinQuery(AppSettings appSettings, String barId)
	{
		mURLBuilder.setPath("bars/canCheckin");

		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		
		mURLBuilder.setParameter("id", barId);
	}
	
	@Override
	public Data getData() 
	{
		Data data = new Data();
		try 
		{
			data.mCanCheckin = mJSONResponse.getBoolean("can_checkin");
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}

		return data;
	}

	public class Data extends WebQueryData
	{
		public Boolean mCanCheckin = false;
	}
}
