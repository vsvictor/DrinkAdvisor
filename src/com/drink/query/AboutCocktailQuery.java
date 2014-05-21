package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.drink.settings.AppSettings;

public class AboutCocktailQuery extends WebQuery 
{
	public AboutCocktailQuery(AppSettings appSettings, String id, String drinkId) 
	{
		mURLBuilder.setPath("cocktails/" + id);
		
		mURLBuilder.setParameter(DEVICE_TOKEN, appSettings.getContentToken());
		mURLBuilder.setParameter(USER_TOKEN, appSettings.getUserToken());
		mURLBuilder.setParameter(USER_ID, appSettings.getUserId());
		
		mURLBuilder.setParameter("drink_id", drinkId);
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();

		if (getResult()) 
		{
			try 
			{
				data.drink_id = mJSONResponse.optString("drink_id");
				data.drink_name = mJSONResponse.optString("drink_name");
				data.id = mJSONResponse.optString("id");
				data.name = mJSONResponse.optString("name");
				data.history = mJSONResponse.optString("history");
				data.how_do = mJSONResponse.optString("how_do");
				data.with_what_drink = mJSONResponse.optString("with_what_drink");
				data.like = mJSONResponse.optBoolean("like", false);
				data.likes_count = mJSONResponse.optInt("likes_count", 0);

				JSONArray interesting_factsArray = mJSONResponse.getJSONArray("interesting_facts");
				for (int i = 0; i < interesting_factsArray.length(); i++) 
				{
					data.interesting_facts.add(interesting_factsArray.getString(i));
				}
				
				JSONArray picturesArray = mJSONResponse.getJSONArray("pictures");
				for (int i = 0; i < picturesArray.length(); i++) 
				{
					data.pictures.add(picturesArray.getString(i));
				}
				
				JSONArray array = mJSONResponse.getJSONArray("ingridients");

				for (int i = 0; i < array.length(); i++) 
				{
					Ingridient ingridient = new Ingridient();
					JSONObject object = array.getJSONObject(i);

					ingridient.name = object.optString("name");
					ingridient.quantity = object.optString("quantity");
					ingridient.unit = object.optString("unit");

					data.ingridients.add(ingridient);
				}
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}

		return data;
	}

	public class Data extends WebQueryData 
	{
		public String drink_id;
		public String drink_name;
		public String id;
		public String name;
		public String history;
		public String how_do;
		public String with_what_drink;
		public ArrayList<String> interesting_facts = new ArrayList<String>();
		public boolean like = false;
		public int likes_count = 0;
		public ArrayList<String> pictures = new ArrayList<String>();
		public ArrayList<Ingridient> ingridients = new ArrayList<Ingridient>();
	}

	public class Ingridient 
	{
		public String name;
		public String quantity;
		public String unit;
	}
}
