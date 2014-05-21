package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.settings.AppSettings;

public class BlogQuery extends WebQuery 
{
	public BlogQuery(AppSettings appSettings, int offset, int limit) 
	{
		mURLBuilder.setPath("blog");
		
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());

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
				data.count = mJSONResponse.optInt("count");
				JSONArray array = mJSONResponse.getJSONArray("posts");
				for (int i = 0; i < array.length(); i++) 
				{
					BlogQuery.Post post = new Post();
					JSONObject object = array.getJSONObject(i);
					
					post.id = object.optString("id");
					post.date = object.optString("date");
					post.title = object.optString("title");
					post.text = object.optString("text");
					post.picture = object.optString("picture");
					
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
		public String date;
		public String title;
		public String text;
		public String picture;
	}
}