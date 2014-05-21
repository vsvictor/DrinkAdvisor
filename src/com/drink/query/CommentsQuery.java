package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.settings.AppSettings;
import com.drink.types.Comment;

public class CommentsQuery extends WebQuery 
{
	public static String BARS = "Bars";
	public static String BLOG_POSTS = "BlogPosts";
	public static String MEETING = "Meetings";

	public CommentsQuery(AppSettings appSettings, String modelId, String modeType, int offset, int limit) 
	{
		mURLBuilder.setPath("comments");

		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());

		mURLBuilder.setParameter("model_id", modelId);
		mURLBuilder.setParameter("model_type", modeType);
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
				JSONArray array = mJSONResponse.getJSONArray("comments");
				
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
			} catch (Exception e) {}
		}
		
		return data;
	}

	public class Data extends WebQueryData 
	{
		public int count;
		public ArrayList<Comment> comments = new ArrayList<Comment>();
	}
}
