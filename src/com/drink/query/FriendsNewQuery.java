package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;

import com.drink.settings.AppSettings;
import com.drink.types.Friend;

public class FriendsNewQuery extends WebQuery 
{
	public FriendsNewQuery(AppSettings appSettings) 
	{
		mURLBuilder.setPath("follow/myFollowersAndFollowings");
		
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("user_id",	appSettings.getUserId());
	}
	
	public void setOnlyReq() 
	{
		mURLBuilder.setParameter("only_requests", "1");
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			try 
			{
				JSONArray array = mJSONResponse.getJSONArray("requests");
				for (int i = 0; i < array.length(); i++) 
				{
					data.following.add(Friend.parse(array.getJSONObject(i)));
				}

				array = mJSONResponse.getJSONArray("results");
				for (int i = 0; i < array.length(); i++) 
				{
					data.followers.add(Friend.parse(array.getJSONObject(i)));
				}
			} 
			catch (Exception e) {}
		}

		return data;
	}

	public class Data extends WebQueryData 
	{
		public ArrayList<Friend> following = new ArrayList<Friend>();
		public ArrayList<Friend> followers = new ArrayList<Friend>();
	}
}
