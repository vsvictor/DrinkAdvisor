package com.drink.query;

import com.drink.types.BaseObject;
import com.drink.types.BaseObjectList;

public class FriendsFacebook {
	public class Data extends WebQueryData 
	{
		public int count;
		public BaseObjectList friends = new BaseObjectList();
	}
	
	public static class Friend extends BaseObject 
	{
		private String picture;
		private int watched;
		public Friend(long id, String name, String picture){
			super(id, name);
			this.picture = picture;
			watched = 0;
		}
		public void setPicture(String picture){
			this.picture = picture;
		}
		public String getPicture(){
			return picture;
		}
		public void setWatched(int w){
			watched = w;
		}
		public int getWatched(){
			return watched;
		}
	}
}
