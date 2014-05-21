package com.drink.query;

import com.drink.settings.AppSettings;

public class LikeQuery extends WebQuery {
	public static String DRINKS = "Drinks";
	public static String COCKTAILS = "Cocktails";
	public static String BRANDS = "Brands";
	public static String BARS = "Bars";
	public static String REVIEW = "BlogPosts";

	public LikeQuery(AppSettings appSettings, String model, String id) {
		mURLBuilder.setPath("like/index");

		mURLBuilder.setParameter(DEVICE_TOKEN, appSettings.getContentToken());
		mURLBuilder.setParameter(USER_TOKEN, appSettings.getUserToken());
		mURLBuilder.setParameter(USER_ID, appSettings.getUserId());

		mURLBuilder.setParameter("model", model);
		mURLBuilder.setParameter("action", "1");
		mURLBuilder.setParameter("id_model", id);
	}

	@Override
	public Data getData() {
		Data result = new Data();
		if (getResult()) {
			try {
				result.likes_count = mJSONResponse.optInt("likes_count");
			} catch (Exception e) {
			}
		}

		return result;
	}

	public class Data extends WebQueryData {
		public int likes_count;
	}
}
