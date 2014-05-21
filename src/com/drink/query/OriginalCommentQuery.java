package com.drink.query;

import com.drink.settings.AppSettings;

import android.content.Context;

public class OriginalCommentQuery extends WebQuery {

	public OriginalCommentQuery(Context context, String commentID) {

		mURLBuilder.setPath("original");
		mURLBuilder.setParameter("user_token",
				new AppSettings(context).getUserToken());
		mURLBuilder.setParameter("device_token",
				new AppSettings(context).getContentToken());
		mURLBuilder.setParameter("comment_id", commentID);

	}

	@Override
	public Data getData() {
		Data data = new Data();
		if (getResult()) {
			data.text = mJSONResponse.optString("text");
		}
		return data;
	}

	public class Data extends WebQueryData {
		public String text;
	}

}
