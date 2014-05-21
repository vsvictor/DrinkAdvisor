
package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;

import com.drink.settings.AppSettings;

public class AboutDrinkQuery extends WebQuery 
{
	public AboutDrinkQuery(AppSettings appSettings, String id, int limit, int offset) 
	{
		mURLBuilder.setPath("drinks/" + id);
		
		mURLBuilder.setParameter(DEVICE_TOKEN, appSettings.getContentToken());
		mURLBuilder.setParameter(USER_TOKEN, appSettings.getUserToken());
		mURLBuilder.setParameter(USER_ID, appSettings.getUserId());
		
		mURLBuilder.setParameter(LIST_LIMIT, Integer.toString(limit));
		mURLBuilder.setParameter(LIST_OFFSET, Integer.toString(offset));
	}
	
	@Override
	public Data getData() 
	{
		Data data = new Data();

		if (getResult()) 
		{
			try 
			{
				data.name = mJSONResponse.optString("name", "null");
				data.how_do = mJSONResponse.optString("how_do", "null");
				data.with_drink = mJSONResponse.optString("with_what_drink", "null");
				
				JSONArray factsArray = mJSONResponse.getJSONArray("interesting_facts");
				for (int i = 0; i < factsArray.length(); i++) 
				{
					data.facts.add(factsArray.getString(i));
				}
				
				JSONArray picArray = mJSONResponse.getJSONArray("pictures");
				for (int i = 0; i < picArray.length(); i++) 
				{
					data.picture.add(picArray.getString(i));
				}

				data.like = mJSONResponse.optBoolean("like", false);
				data.brands = mJSONResponse.optBoolean("brands_drink", false);
				data.like_count = mJSONResponse.optInt("likes_count", 0);
				data.history = mJSONResponse.optString("history");
			} 
			catch (Exception e) {}
		}
		
		return data;
	}

	public class Data extends WebQueryData 
	{
		public ArrayList<String> picture = new ArrayList<String>();
		public String name;
		public String history;
		public String with_drink;
		public String how_do;
		public boolean like;
		public boolean brands;
		public int like_count;
		public ArrayList<String> facts = new ArrayList<String>();
	}
}
