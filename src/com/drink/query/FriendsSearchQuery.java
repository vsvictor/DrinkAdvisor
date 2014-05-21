package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.drink.constants.Constants.FriendType;
import com.drink.settings.AppSettings;

public class FriendsSearchQuery extends WebQuery 
{
	public static String TWITTER = "twitter";
	public static String FACEBOOK = "facebook";
	public static String GOOGLE = "google";

	public FriendsSearchQuery(Context context) 
	{
		//mURLBuilder.setPath("friends/search");
		mURLBuilder.setPath("follow/search");
		mURLBuilder.setParameter("device_token", new AppSettings(context).getContentToken());
		mURLBuilder.setParameter("user_token", new AppSettings(context).getUserToken());
		mURLBuilder.setParameter("user_id",	new AppSettings(context).getUserId());
	}

	public void setNetworkType(String string) 
	{
		mURLBuilder.setParameter("social_network_type", string);
	}

	public void setSocIds(ArrayList<String> list) 
	{
		String string = "";

		for (int i = 0; i < list.size(); i++) 
		{
			string = string + list.get(i) + ",";
		}
		string = string.substring(0, (string.length() - 1));

		mURLBuilder.setParameter("social_identifiers", string);
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
				JSONArray array = mJSONResponse.optJSONArray("friends");
				for (int i = 0; i < array.length(); i++) 
				{
					Friend friend = new Friend();
					JSONObject object = array.getJSONObject(i);				
					friend.mAppId = object.optString("id");
					friend.name = object.optString("name");
					friend.mType = FriendType.FACEBOOK;
					friend.mPicture = object.optString("picture");
					friend.mSocialId = object.optString("social_identifier");
					friend.mIsFriendOrRequestSent = object.optBoolean("alreadyHaveFriendRequestOrAreFriends");
					friend.status = object.optInt("status");
					data.friends.add(friend);
				}
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		
		return data;
	}

	public class Data extends WebQueryData 
	{
		public int count;
		public ArrayList<Friend> friends = new ArrayList<FriendsSearchQuery.Friend>();
	}

	public static class Friend 
	{
		public String mAppId;
		public String mSocialId;
		public String name;
		public String mPicture;
		public Boolean mIsFriendOrRequestSent = false;
		public FriendType mType;
		public Boolean mIsExistsInSite = false; // add friend if true / else - suggest
		public Boolean mIsAppRequestSent = false;
		public Boolean mIsDialogVisible = false;
		public boolean mSentRequest = false;
		public int status = 0;
	}
}
