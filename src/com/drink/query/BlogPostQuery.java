package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.settings.AppSettings;
import com.drink.types.Comment;

import android.content.Context;

public class BlogPostQuery extends WebQuery 
{
	public BlogPostQuery(Context context, String id) 
	{
		mURLBuilder.setPath("blog/" + id);
		mURLBuilder.setParameter("device_token", new AppSettings(context).getContentToken());
		mURLBuilder.setParameter("user_token", new AppSettings(context).getUserToken());

		getResponse(WebQuery.GET);
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();

		if (getResult()) 
		{
			try 
			{
				data.date = mJSONResponse.optString("date");
				data.title = mJSONResponse.optString("title");
				data.text = mJSONResponse.optString("text");
				data.picture = mJSONResponse.optString("picture");

				JSONArray array = mJSONResponse.optJSONArray("comments");
				for (int i = 0; i < array.length(); i++) 
				{
					Comment comment = new Comment();
					JSONObject object = array.getJSONObject(i);
					comment.id = object.optString("id");
					comment.text = object.optString("text");
					comment.created_at = object.optString("created_at");
					comment.user_id = object.optString("user_id");
					comment.user_name = object.optString("user_name");
					comment.user_picture = object.optString("user_picture");
					comment.bar_rating = object.optString("bar_rating");
					comment.last_checkin_at = object.optString("last_checkin_at");
					comment.text_lang = object.optString("text_lang");

					data.comments.add(comment);
				}
			} 
			catch (Exception e) {}
		}
		
		return data;
	}

	public class Data extends WebQueryData 
	{
		public String date;
		public String title;
		public String text;
		public String picture;
		public ArrayList<Comment> comments = new ArrayList<Comment>();
	}
}
