package com.drink.query;

import android.content.Context;

import com.drink.settings.AppSettings;

public class FriendRequestQuery extends WebQuery {

	public FriendRequestQuery(Context context, String path) {
		mURLBuilder.setPath("friends/"+path);
		mURLBuilder.setParameter("device_token",
				new AppSettings(context).getContentToken());
		mURLBuilder.setParameter("user_token",
				new AppSettings(context).getUserToken());
		mURLBuilder.setParameter("user_id",
				new AppSettings(context).getUserId());
	}
	
	public void setFriendId(String userId){
		mURLBuilder.setParameter("friend_user_id",userId);
	}
	
	public void setMessageText(String message){
		mURLBuilder.setParameter("message",message);
	}
	
	@Override
	public FriendRequestData getData() {
		FriendRequestData data = new FriendRequestData();
		if (getResult()) {
			try {
				data.mIsSuccess = mJSONResponse.getBoolean("success");
			}catch(Exception e){
				
			}
			
		}
		return data;
	}

	public static class FriendRequestData extends WebQueryData{
		public Boolean mIsSuccess;
	}
}
