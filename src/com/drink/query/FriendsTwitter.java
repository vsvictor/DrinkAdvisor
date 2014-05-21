package com.drink.query;

import com.drink.twitter.TwitterApp;
import com.drink.types.BaseObject;
import com.drink.types.BaseObjectList;

public class FriendsTwitter extends WebQuery{

	private TwitterApp app;
	
	public FriendsTwitter(TwitterApp app){
		this.app = app;
	}

	@Override
	public WebQueryData getData() {
		Data data = new Data();
		long[] ids = app.getFriendIds();
		for(int i = 0; i<ids.length; i++){
			long l = ids[i];
			int id = new Long(i).intValue();
			String name = app.getFriendName(l);
			String pic = app.getFriendImageURL(l);
			Friend f = new Friend(id,name, pic);
			f.setWatched(0);
			data.friends.add(f);
		}
		data.count = ids.length;
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
		private int watched;
		public Friend(int id, String name, String picture){
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
