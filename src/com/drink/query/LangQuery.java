package com.drink.query;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

public class LangQuery extends WebQuery 
{
	public LangQuery() 
	{
		mURLBuilder.setPath("languages");
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			try 
			{
				String lang = Locale.getDefault().getLanguage();;
				JSONArray array = mJSONResponse.getJSONArray("array");
				
				for (int i = 0; i < array.length(); i++) 
				{
					JSONObject object = array.getJSONObject(1);
					if (object.optString("code").equals(lang))
					{
						data.string = object.optString("id");
					}
				}

			} catch (Exception e) {

			}
		}
		return data;
	}

	public class Data extends WebQueryData 
	{
		public String string = "2";
	}

}
