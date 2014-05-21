package com.drink.twitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import twitter4j.auth.AccessToken;

public class TwitterSession 
{
	private SharedPreferences sharedPref;

	private static final String TWEET_AUTH_KEY = "twitter_auth_key";
	private static final String TWEET_AUTH_SECRET_KEY = "twitter_auth_secret_key";
	private static final String TWEET_USER_NAME = "twitter_user_name";
	private static final String TWEET_USER_ID = "twitter_user_id";
	private static final String SHARED = "twitter_preferences";

	public TwitterSession(Context context) 
	{
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
	}

	public void storeAccessToken(AccessToken accessToken, String username) 
	{
		Editor editor = sharedPref.edit();
		
		editor.putString(TWEET_AUTH_KEY, accessToken.getToken());
		editor.putString(TWEET_AUTH_SECRET_KEY, accessToken.getTokenSecret());
		editor.putString(TWEET_USER_NAME, username);
		
		Long userID = accessToken.getUserId();
		editor.putString(TWEET_USER_ID, userID.toString());

		editor.commit();
	}

	public void resetAccessToken() 
	{
		Editor editor = sharedPref.edit();
		
		editor.putString(TWEET_AUTH_KEY, null);
		editor.putString(TWEET_AUTH_SECRET_KEY, null);
		editor.putString(TWEET_USER_NAME, null);
		editor.putString(TWEET_USER_ID, null);

		editor.commit();
	}

	public String getUsername() 
	{
		return sharedPref.getString(TWEET_USER_NAME, "");
	}

	public String getUserID() 
	{
		return sharedPref.getString(TWEET_USER_ID, "");
	}
	
	public AccessToken getAccessToken() 
	{
		String token = sharedPref.getString(TWEET_AUTH_KEY, null);
		String tokenSecret = sharedPref.getString(TWEET_AUTH_SECRET_KEY, null);

		if (token != null && tokenSecret != null)
			return new AccessToken(token, tokenSecret);
		else
			return null;
	}
}
