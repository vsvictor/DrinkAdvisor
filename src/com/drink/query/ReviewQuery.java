package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.settings.AppSettings;

public class ReviewQuery extends WebQuery 
{
	public ReviewQuery(AppSettings appSettings, int offset, int limit) 
	{
		mURLBuilder.setPath("blog");
		
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("version","2");
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
/*{"count":8,"reviews":[
	{"id":10,"title":"Bars with character: 10 types of bars and how to prepare for them",
	"author":{"id":1,"name":"DrinkAdvisor","text":null,"picture":"http:\/\/www.drinkadvisor.com\/cache\/3\/3531fffec04a867bf8b8f09013499a77_200_200.png"},
	"picture":"http:\/\/www.drinkadvisor.com\/cache\/6\/66fbfaa4efc458c30ef7f25186c3e32c_640_320.jpg","date":1392854400}*/
				
				data.count = mJSONResponse.optInt("count");
				JSONArray array = mJSONResponse.getJSONArray("reviews");
				for (int i = 0; i < array.length(); i++) 
				{
					ReviewQuery.Post post = new Post();
					JSONObject object = array.getJSONObject(i);
					
					post.id = object.optString("id");
					post.title = object.optString("title");
					JSONObject user  = array.getJSONObject(i).getJSONObject("author");
					post.authId = user.optString("id");
					post.authName = user.optString("name");
					post.authPic = user.optString("picture");
	
					post.picture = object.optString("picture");
					post.date = object.optString("date");
					data.posts.add(post);
				}
			} 
			catch (Exception e) {}
		}
		
		return data;
	}

	public class Data extends WebQueryData 
	{
		public int count;
		public ArrayList<Post> posts = new ArrayList<Post>();
	}

	public class Post 
	{
		public String id;
		
		public String title;
		public String authId;
		public String authName;
		public String authPic;
		public String picture;
		public String date;
	}
}