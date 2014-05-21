package com.drink.query;

import org.apache.http.message.BasicNameValuePair;

import android.util.DisplayMetrics;

public class ClientsQuery extends WebQuery {

	public ClientsQuery() 
	{
		mURLBuilder.setPath("clients");
		setNotificationType("1");
	}

	public void setOldContentToken(String contentToken) 
	{
		nameValuePairs.add(new BasicNameValuePair("old_token", contentToken));
	}

	public void setDeviceType() {
		nameValuePairs.add(new BasicNameValuePair("device_type", "2"));
	}

	public void setDeviceToken(String string) {
		nameValuePairs.add(new BasicNameValuePair("device_token", string));

	}

	public void setDeviceID(String string) {
		nameValuePairs.add(new BasicNameValuePair("device_id", string));
	}

	public void setNotificationType(String string) {
		nameValuePairs
				.add(new BasicNameValuePair("notifications_type", string));
	}

	public void setLangId(String string) {
		nameValuePairs.add(new BasicNameValuePair("language_id", string));
	}

	public void setCityId(String string) {
		nameValuePairs.add(new BasicNameValuePair("city_id", string));
	}

	public void setCountryId(String string) {
		nameValuePairs.add(new BasicNameValuePair("country_id", string));
	}

	public void setDeviceType(DisplayMetrics metrics) {
		String string = "";

		if (metrics.widthPixels <= 320)
			string = "2";
		if (metrics.widthPixels > 320 && metrics.widthPixels <= 480)
			string = "3";
		if (metrics.widthPixels > 480)
			string = "4";

		nameValuePairs.add(new BasicNameValuePair("device_type", string));
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			try 
			{
				data.content_token = mJSONResponse.optString("token");
			} 
			catch (Exception e)	{}
		}
		
		return data;
	}

	public class Data extends WebQueryData 
	{
		public String content_token;
	}
}
