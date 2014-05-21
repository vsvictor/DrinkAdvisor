
package com.drink.query;

import org.apache.http.message.BasicNameValuePair;
import com.drink.settings.AppSettings;

public class WrongInfoQuery extends WebQuery 
{
	public static final int WRONG_ADDRESS = 1;
	public static final int WRONG_PHONE = 2;
	public static final int WRONG_OPENING_HOURS = 3;
	public static final int WRONG_CLOSED_DOWN = 4;
	public static final int WRONG_OTHER = 5;
	
	public WrongInfoQuery(AppSettings appSettings, String barId, int type, String text) 
	{
		mURLBuilder.setPath("errors");
		
		nameValuePairs.add(new BasicNameValuePair("device_token", appSettings.getContentToken()));
		nameValuePairs.add(new BasicNameValuePair("user_token",	appSettings.getUserToken()));
		nameValuePairs.add(new BasicNameValuePair("bar_id", barId));
		nameValuePairs.add(new BasicNameValuePair("error_type", Integer.toString(type)));
		nameValuePairs.add(new BasicNameValuePair("text", text));
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			try 
			{
				data.success = mJSONResponse.getString("error").equals("0");
				if (!data.success)
				{
					data.error = mJSONResponse.getString("desc");
				}
			} 
			catch (Exception e) {}
		}
		
		return data;
	}

	public class Data extends WebQueryData 
	{
		public Boolean success = false;
		public String error;
	}
}
