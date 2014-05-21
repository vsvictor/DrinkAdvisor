package com.drink.query;

import org.apache.http.message.BasicNameValuePair;

import com.drink.settings.AppSettings;

import android.content.Context;

public class AddCommentQuery extends WebQuery 
{
	public AddCommentQuery(Context context, String modelType, String  modelId) 
	{
		mURLBuilder.setPath("comments");
		
		nameValuePairs.add(new BasicNameValuePair("user_token",	new AppSettings(context).getUserToken()));
		nameValuePairs.add(new BasicNameValuePair("device_token", new AppSettings(context).getContentToken()));
		nameValuePairs.add(new BasicNameValuePair("model_type", modelType));
		nameValuePairs.add(new BasicNameValuePair("model_id", modelId));
	}

	public void setText(String text) 
	{
		nameValuePairs.add(new BasicNameValuePair("text", text));
	}

	public void setBarRating(String text) 
	{
		nameValuePairs.add(new BasicNameValuePair("bar_rating", text));
	}

	public void setPublishOnTW() 
	{
		nameValuePairs.add(new BasicNameValuePair("publish_on_twitter", "publish_on_twitter"));
	}

	public void setPublishOnFB() 
	{
		nameValuePairs.add(new BasicNameValuePair("publish_on_facebook", "publish_on_facebook"));
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			try 
			{
				data.comment_id = mJSONResponse.optInt("comment_id");
				data.success = mJSONResponse.optBoolean("success");
				data.text_lang = mJSONResponse.optString("text_lang");
				data.text_lang_name = mJSONResponse.optString("text_lang_name");
				data.user_lang = mJSONResponse.optString("user_lang");
				data.message = mJSONResponse.optString("message");
			} 
			catch (Exception e) {}
		}
		
		return data;
	}

	public class Data extends WebQueryData 
	{
		public int comment_id;
		public Boolean success;
		public String text_lang;
		public String text_lang_name;
		public String user_lang;
		public String message;
	}
}
