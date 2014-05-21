package com.drink.query;

import com.drink.settings.AppSettings;

public class CanCommentQuery extends WebQuery
{
	public CanCommentQuery(AppSettings appSettings, String barId)
	{
		mURLBuilder.setPath("bars/canComment");
		
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("user_id", appSettings.getUserId());

		mURLBuilder.setParameter("id",barId);
	}
	
	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			try 
			{
				if (mJSONResponse.has("can_comment"))
				{
					data.mCanComment = mJSONResponse.getBoolean("can_comment");
				}
				
				if (!data.mCanComment)
				{
					data.mMinutes = mJSONResponse.getInt("minutes_to_next_comment");
				}
			}
			catch(Exception e) {}
		}
		
		return data;
	}

	public class Data extends WebQueryData
	{
		public boolean mCanComment = false;
		public int mMinutes = 0;
	}
}
