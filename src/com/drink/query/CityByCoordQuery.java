package com.drink.query;

import android.location.Location;

public class CityByCoordQuery extends WebQuery 
{
	public CityByCoordQuery() 
	{
		mURLBuilder.setPath("cities/getByCoordinates");
	}

	public void setCoord(Location loc) 
	{
		mURLBuilder.setParameter("lat", String.valueOf(loc.getLatitude()));
		mURLBuilder.setParameter("long", String.valueOf(loc.getLongitude()));
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult())
		{
			try 
			{
				data.city_id = mJSONResponse.optInt("city_id", -1);
				data.city_name = mJSONResponse.optString("city_name", "");
				data.country_id = mJSONResponse.optInt("country_id", -1);
				data.country_name = mJSONResponse.optString("country_name", "");
			} 
			catch (Exception e) {}
		}
		
		return data;
	}
	
	public class Data extends WebQueryData
	{
		public int city_id = -1;
		public String city_name = "";
		public int country_id = -1;
		public String country_name = "";
	}
}
