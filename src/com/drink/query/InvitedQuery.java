package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;

import com.drink.settings.AppSettings;
import com.drink.types.Invited;

public class InvitedQuery extends WebQuery 
{
	public InvitedQuery(AppSettings appSettings, String meetingId) 
	{
		mURLBuilder.setPath("meetings/UsersList");
		
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("user_id",	appSettings.getUserId());
		
		mURLBuilder.setParameter("meeting_id", meetingId);
	}
	
	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			try 
			{
				JSONArray array = mJSONResponse.getJSONArray("array");
				for (int i = 0; i < array.length(); i++) 
				{
					data.invited.add(Invited.parse(array.getJSONObject(i)));
				}
			} 
			catch (Exception e) {}
		}

		return data;
	}

	public class Data extends WebQueryData 
	{
		public ArrayList<Invited> invited = new ArrayList<Invited>();
	}
}
