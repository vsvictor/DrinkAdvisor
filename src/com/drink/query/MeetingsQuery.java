package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.drink.settings.AppSettings;
import com.drink.types.Meeting;

public class MeetingsQuery extends WebQuery 
{
	public MeetingsQuery(AppSettings appSettings, int offset, int limit) 
	{
		mURLBuilder.setPath("meetings");

		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("user_id", appSettings.getUserId());

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
				data.count = mJSONResponse.getInt("count");
				JSONArray array = mJSONResponse.getJSONArray("meetings");
				for (int i = 0; i < array.length(); i++) 
				{
					data.meetings.add(Meeting.parse(array.getJSONObject(i)));
				}
			} 
			catch (JSONException e) {}
		}

		return data;
	}

	public class Data extends WebQueryData 
	{
		public int count;
		public ArrayList<Meeting> meetings = new ArrayList<Meeting>();
	}
}
