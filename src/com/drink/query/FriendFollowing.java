package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.query.FriendsQuery.Data;
import com.drink.query.FriendsQuery.Friend;
import com.drink.settings.AppSettings;
import com.drink.types.BaseObject;
import com.drink.types.BaseObjectList;

public class FriendFollowing extends WebQuery{

	public FriendFollowing(AppSettings appSettings){
		mURLBuilder.setPath("follow/iFollow");
		
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("user_id",	appSettings.getUserId());
	}
	public void setOnlyReq() 
	{
		mURLBuilder.setParameter("only_requests", "1");
	}

	@Override
	public WebQueryData getData() {
		Data data = new Data();
		if (getResult()) 
		{
			try 
			{
				data.count = mJSONResponse.getInt("count");
				JSONArray array = mJSONResponse.getJSONArray("results");
				for (int i = 0; i < array.length(); i++) 
				{
					JSONObject object = array.getJSONObject(i);
					int id = object.getInt("id");
					String name = object.getString("name");
					String picture = object.getString("avatar");
					int status = object.getInt("status");
					
					Friend friend = new Friend(id, name, picture);
					friend.setStatus(status);
					friend.setWatched(status);
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
		public BaseObjectList friends = new BaseObjectList();
	}
	
	public static class Friend extends BaseObject 
	{
		private String picture;
		private int status;
		private int watched;
		private boolean isPrivate;
		public Friend(int id, String name, String picture){
			super(id, name);
			this.picture = picture;
		}
		public void setPicture(String picture){
			this.picture = picture;
		}
		public String getPicture(){
			return picture;
		}
		public void setStatus(int status){
			this.status = status;
		}
		public int getStatus(){
			return status;
		}
		public void setWatched(int watched){
			this.watched = watched;
		}
		public int getWatched(){
			return this.watched;
		}
		public void setPrivate(boolean isProvate){
			this.isPrivate = isPrivate;
		}
		public boolean getPrivate(){
			return this.isPrivate;
		}
	}
}
