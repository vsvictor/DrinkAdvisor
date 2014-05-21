package com.drink.types;

import org.json.JSONException;
import org.json.JSONObject;

public class Drink 
{
	static public Drink parse(JSONObject object)
	{
		try 
		{
			Drink drink = new Drink();
			
			drink.id = object.getString("id");
			drink.name = object.getString("name");
			drink.picture = object.getString("picture");
			drink.cocktails_drink = object.optBoolean("cocktails_drink", false);

			return drink;
		} 
		catch (JSONException ex) {}
		
		return null;
	}

	public String id;
	public String name;
	public String picture;
	public boolean cocktails_drink = false;
}
