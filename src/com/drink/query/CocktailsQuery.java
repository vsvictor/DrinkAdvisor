package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.settings.AppSettings;

public class CocktailsQuery extends WebQuery 
{
	public CocktailsQuery(AppSettings appSettings, String id, int offset, int limit) 
	{
		mURLBuilder.setPath("cocktails");
		
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("user_id", appSettings.getUserId());
		
		mURLBuilder.setParameter("drink_id", id);
		mURLBuilder.setParameter(LIST_OFFSET, Integer.toString(offset));
		mURLBuilder.setParameter(LIST_LIMIT, Integer.toString(limit));
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			try 
			{
				data.drink_id = mJSONResponse.optInt("drink_id");
				data.drink_name = mJSONResponse.optString("drink_name");
				data.count = mJSONResponse.optInt("count");
				JSONArray jsonArray = mJSONResponse.getJSONArray("cocktails");
				for (int i = 0; i < jsonArray.length(); i++) 
				{
					Cocktail cocktail = new Cocktail();
					JSONObject object = jsonArray.getJSONObject(i);
					cocktail.id = object.optInt("id");
					cocktail.coctail_drink_id = object.optString("coctail_drink_id");
					cocktail.interesting_fact = object.optString("interesting_fact");
					cocktail.name = object.optString("name");
					cocktail.picture = object.getString("picture");
					data.cocktails.add(cocktail);
				}
			} 
			catch (Exception e) {}
		}

		return data;
	}

	public class Data extends WebQueryData 
	{
		public int drink_id;
		public String drink_name;
		public int count;
		public ArrayList<Cocktail> cocktails = new ArrayList<CocktailsQuery.Cocktail>();
	}

	public class Cocktail 
	{
		public int id;
		public String coctail_drink_id;
		public String name;
		public String interesting_fact;
		public String picture;
	}
}
