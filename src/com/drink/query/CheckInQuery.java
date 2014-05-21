package com.drink.query;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.location.Location;

import com.drink.settings.AppSettings;

public class CheckInQuery extends WebQuery 
{
	public CheckInQuery(AppSettings appSettings, String barId, boolean facebook, boolean twitter) 
	{
		mURLBuilder.setPath("Checkins/index");
		
		nameValuePairs.add(new BasicNameValuePair("user_token", appSettings.getUserToken()));
		nameValuePairs.add(new BasicNameValuePair("user_id", appSettings.getUserId()));
		nameValuePairs.add(new BasicNameValuePair("device_token", appSettings.getContentToken()));
		
		nameValuePairs.add(new BasicNameValuePair("id_bar", barId));
		nameValuePairs.add(new BasicNameValuePair("publish_on_facebook", facebook ? "true" : "false"));
		nameValuePairs.add(new BasicNameValuePair("publish_on_twitter",	twitter ? "true" : "false"));
		nameValuePairs.add(new BasicNameValuePair("publish_on_google", "false"));
	}

	public void setComment(String comment)
	{
		nameValuePairs.add(new BasicNameValuePair("comment", comment));
	}
	
	public void setPicture(String picture)
	{
		nameValuePairs.add(new BasicNameValuePair("qqfile", picture));
	}
	
	public void setCoord(Location loc) 
	{
		nameValuePairs.add(new BasicNameValuePair("map_latitude", String.valueOf(loc.getLatitude())));
		nameValuePairs.add(new BasicNameValuePair("map_longitude", String.valueOf(loc.getLongitude())));
	}

	public void setFriends(ArrayList<String> IDs)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < IDs.size(); ++i)
		{
			if (i > 0)
			{
				builder.append(",");
			}
			builder.append(IDs.get(i));
		}
		
		nameValuePairs.add(new BasicNameValuePair("invited_ids", builder.toString()));
	}
	
	@Override
	public WebQueryData getData() 
	{
		return null;
	}
}
