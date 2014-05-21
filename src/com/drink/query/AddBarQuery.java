package com.drink.query;

import com.drink.settings.AppSettings;

public class AddBarQuery extends WebQuery 
{
	public final int NO_ERROR = 0;
	
	public AddBarQuery(AppSettings appSettings, String title, int city_id, double lat, double lng, String address, String info) 
	{
		mURLBuilder.setPath("Add_bar/index");
		
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		if (appSettings.getUserToken().length() > 0)
		{
			mURLBuilder.setParameter("user_token",	appSettings.getUserToken());
		}
		
		mURLBuilder.setParameter("title", title);
		mURLBuilder.setParameter("city_id", Integer.toString(city_id));
		mURLBuilder.setParameter("map_latitude", Double.toString(lat));
		mURLBuilder.setParameter("map_longitude", Double.toString(lng));

		if (address.length() > 0)
		{
			mURLBuilder.setParameter("address", address);
		}
		else
		{
			mURLBuilder.setParameter("address", "noadr");
		}
		
		if (info.length() > 0)
		{
			mURLBuilder.setParameter("common_info", info);
		}
		else
		{
			mURLBuilder.setParameter("common_info", "noInfo");
		}
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			try 
			{
				data.error = mJSONResponse.optInt("code");
				data.barName = mJSONResponse.optString("name_new_bar");
			} 
			catch (Exception e) {}
		}
		return data;
	}

	public class Data extends WebQueryData 
	{
		public int error;
		public String barName;
	}
}
