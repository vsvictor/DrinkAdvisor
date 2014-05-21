package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.query.BarQuery.Status;
import com.drink.settings.AppSettings;

import android.location.Location;

public class Bars4MapQuery extends WebQuery 
{
	public Bars4MapQuery(AppSettings appSettings, int cityId, String name) 
	{
		mURLBuilder.setPath("bars2/listForMap");
		
		mURLBuilder.setParameter(DEVICE_TOKEN, appSettings.getContentToken());
		mURLBuilder.setParameter(USER_TOKEN, appSettings.getUserToken());

		mURLBuilder.setParameter("cities_id", Integer.toString(cityId));
		
		if (name != null)
		{
			mURLBuilder.setParameter("name", name);
		}
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		data.bars = new ArrayList<Bars4MapQuery.Bar>();

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
	}
}
