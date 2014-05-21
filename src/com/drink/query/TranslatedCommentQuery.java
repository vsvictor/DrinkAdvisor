package com.drink.query;

import com.drink.settings.AppSettings;

public class TranslatedCommentQuery extends WebQuery 
{
	public TranslatedCommentQuery(AppSettings appSettings, String commentID) 
	{
		mURLBuilder.setPath("comments/translate");
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("comment_id", commentID);
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			data.text = mJSONResponse.optString("text");
		}
		
		return data;
	}

	public class Data extends WebQueryData 
	{
		public String text;
	}
}
