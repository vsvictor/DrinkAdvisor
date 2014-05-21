package com.drink.query;

import com.drink.settings.AppSettings;

public class FriendsQueryMain extends WebQuery {
	public FriendsQueryMain(AppSettings appSettings) {
		mURLBuilder.setPath("profile/followInfo");
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("user_id", appSettings.getUserId());
		int i = 0;
		i++;
	}

	public void setOnlyReq() {
		mURLBuilder.setParameter("only_requests", "1");
	}

	@Override
	public Data getData() {
		Data data = new Data();
		if (getResult()) {
			try {// {"followers":8,"i_follow":5,"waiting_for_approval":0,"twitter":false,"facebook":true}
				data.followers = mJSONResponse.getInt("followers");
				data.i_follow = mJSONResponse.getInt("i_follow");
				data.waiting_app = mJSONResponse.getInt("waiting_for_approval");
				data.facebook = mJSONResponse.getBoolean("facebook");
				data.twitter = mJSONResponse.getBoolean("twitter");

			} catch (Exception e) {
			}
		}

		return data;
	}

	public class Data extends WebQueryData {
		public int followers;
		public int i_follow;
		public int waiting_app;
		public boolean twitter;
		public boolean facebook;

	}

}
