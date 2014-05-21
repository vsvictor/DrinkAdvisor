package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;

import com.drink.settings.AppSettings;
import com.drink.types.Drink;

public class DrinksQuery extends WebQuery 
{
	public DrinksQuery(AppSettings appSettings) 
	{
		mURLBuilder.setPath("drinks/listV2");
		
		mURLBuilder.setParameter(DEVICE_TOKEN, appSettings.getContentToken());
		mURLBuilder.setParameter(USER_TOKEN, appSettings.getUserToken());
		mURLBuilder.setParameter(USER_ID, appSettings.getUserId());
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		data.drinks = new ArrayList<Drink>();
		if (getResult()) 
		{
			try 
			{
				data.count = mJSONResponse.optInt("count");
				JSONArray drinksArray = mJSONResponse.getJSONArray("drinks");
				for (int i = 0; i < drinksArray.length(); i++) 
				{
					data.drinks.add(Drink.parse(drinksArray.getJSONObject(i)));
				}
			} 
			catch (Exception e) {}
		}
		return data;
	}
	
	public class Data extends WebQueryData 
	{
		public int count;

		public ArrayList<Drink> drinks;
	}
}
