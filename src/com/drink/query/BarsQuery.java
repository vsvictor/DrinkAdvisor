package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.query.BarQuery.Status;
import com.drink.settings.AppSettings;

import android.location.Location;

public class BarsQuery extends WebQuery 
{
	public BarsQuery(AppSettings appSettings, int cityId, String sort, int offset, int limit, String name) 
	{
		mURLBuilder.setPath("bars2");
		
		mURLBuilder.setParameter(DEVICE_TOKEN, appSettings.getContentToken());
		mURLBuilder.setParameter(USER_TOKEN, appSettings.getUserToken());

		mURLBuilder.setParameter("cities_id", Integer.toString(cityId));
		mURLBuilder.setParameter("sort", sort);
		mURLBuilder.setParameter(LIST_OFFSET, Integer.toString(offset));
		mURLBuilder.setParameter(LIST_LIMIT, Integer.toString(limit));
		
		if (name != null)
		{
			mURLBuilder.setParameter("name", name);
		}
	}

	public void setCoord(Location loc) 
	{
		mURLBuilder.setParameter("lat", String.valueOf(loc.getLatitude()));
		mURLBuilder.setParameter("long", String.valueOf(loc.getLongitude()));
	}

	public void setRadius(String string) 
	{
		mURLBuilder.setParameter("radius", string);
	}

	public void setCacheId(String string) 
	{
		mURLBuilder.setParameter("cache_id", "1");
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		data.bars = new ArrayList<BarsQuery.Bar>();

		if (getResult()) 
		{
			try 
			{
				data.total = mJSONResponse.getInt("count");
				
				JSONArray barsArray = mJSONResponse.getJSONArray("bars");
				for (int i = 0; i < barsArray.length(); ++i) 
				{
					JSONObject obj = (JSONObject) barsArray.get(i);
					
					Bar bar = new Bar();
					bar.id = obj.optInt("id", -1);
					bar.title = obj.optString("title", "null");
					bar.rating = obj.optInt("rating", 0);
					bar.dist = obj.optDouble("dist", 0);
					bar.picture = obj.optString("picture");
					bar.lat = obj.optString("latitude");
					bar.lon = obj.optString("longitude");
					bar.favotites = obj.optBoolean("in_favorites");
					int status = obj.optInt("work_now");
					switch (status)
					{
					case 0:
						bar.status = Status.Closed_Today;
						break;
					case 1:
						bar.status = Status.Opened_Tonight;
						break;
					case 2:
					case 3:
						bar.status = Status.Opened_Today;
						break;
					}

					data.bars.add(bar);
				}
			} 
			catch (Exception e) {}
		}
		
		return data;
	}

	public Bar getNewbar() 
	{
		return new Bar();
	}
	
	public class Data extends WebQueryData 
	{
		public int total = 0;
		public ArrayList<Bar> bars;
	}

	public class Bar 
	{
		public int id;
		public String title;
		public String picture;
		public int rating;
		public double dist;
		public String lat;
		public String lon;
		public Status status = Status.Closed_Today;
		public boolean favotites;
	}
}
