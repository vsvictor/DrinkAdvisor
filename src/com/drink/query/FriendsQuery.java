package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.settings.AppSettings;

public class FriendsQuery extends WebQuery 
{
	public FriendsQuery(AppSettings appSettings) 
	{
		mURLBuilder.setPath("friends");
		
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
				data.count = mJSONResponse.getInt("count");
				JSONArray array = mJSONResponse.getJSONArray("friends");
				for (int i = 0; i < array.length(); i++) 
				{
					Friend friend = new Friend();
					JSONObject object = array.getJSONObject(i);
					friend.id = object.getString("id");
					friend.name = object.getString("name");
					friend.picture = object.getString("picture");
					data.friends.add(friend);
				}
			} 
			catch (Exception e) {}
		}

		return data;
	}

	public class Data extends WebQueryData 
	{
		public int count;
		public ArrayList<Friend> friends = new ArrayList<Friend>();
	}
	
	public class Friend 
	{
		public String id;
		public String name;
		public String picture;
		
		public boolean select = false;
	}
}
