package com.drink.query;

import org.json.JSONException;

import com.drink.settings.AppSettings;

public class ProfileQuery extends WebQuery
{
	public ProfileQuery(AppSettings appSettings, String id) 
	{
		mURLBuilder.setPath("friends/getV2");
		
		mURLBuilder.setParameter(DEVICE_TOKEN, appSettings.getContentToken());
		mURLBuilder.setParameter(USER_TOKEN, appSettings.getUserToken());
		
		mURLBuilder.setParameter("user_id_from", appSettings.getUserId());
		mURLBuilder.setParameter("user_id", id);
	}
	
	@Override
	public UserProfile getData() 
	{
		UserProfile user = new UserProfile();
		if (getResult())
		{
			try 
			{
				user.mStatus = mJSONResponse.getInt("status");
				
				user.mPrivate = false;
				
				if (mJSONResponse.has("message"))
				{
					int message = mJSONResponse.getInt("message");
					if (message == 802)
					{
						user.mPrivate = true;
					}
				}
				
				if (!user.mPrivate)
				{
					user.mUserId = mJSONResponse.getString("id");
					user.mName   = mJSONResponse.getString("name");
					user.mAvatar = mJSONResponse.getString("picture");
					user.mCheckins = mJSONResponse.getInt("checkins");
					user.mComments = mJSONResponse.getInt("bars_comments_and_ratings");
					user.mFollowers = mJSONResponse.getInt("followers");
					user.mFollow = mJSONResponse.getInt("i_follow");
				}
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		
		return user;
	}
	
	public static class UserProfile extends WebQueryData
	{
		public int mStatus = -1;
		public boolean mPrivate = true;
		public String mUserId;
		public String mName;
		public String mAvatar;
		public int mCheckins = 0;
		public int mFollowers = 0;
		public int mFollow = 0;
		public int mComments = 0;
	}
}

