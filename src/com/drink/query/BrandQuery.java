package com.drink.query;

import com.drink.settings.AppSettings;

import android.content.Context;

public class BrandQuery extends WebQuery 
{
	public BrandQuery(Context context, String id) 
	{
		mURLBuilder.setPath("brands/get");

		mURLBuilder.setParameter("device_token", new AppSettings(context).getContentToken());
		mURLBuilder.setParameter("brand_id", id);
		mURLBuilder.setParameter("user_token", new AppSettings(context).getUserToken());

		getResponse(GET);
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			try 
			{
				data.id = mJSONResponse.optInt("id");
				data.name = mJSONResponse.optString("name");
				data.picture = mJSONResponse.optString("picture");
				data.description = mJSONResponse.optString("description");
				data.like = mJSONResponse.optInt("like");
				data.state_like = mJSONResponse.optBoolean("state_like");
			} 
			catch (Exception e) {}
		}
		
		return data;
	}

	public class Data extends WebQueryData 
	{
		public int id = -1;
		public String name;
		public String picture;
		public String description;
		public int like = 0;
		public boolean state_like = false;
	}
}
