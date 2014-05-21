package com.drink.query;

import android.content.Context;

import com.drink.settings.AppSettings;

public class AcceptFriendshipOffer extends WebQuery {

	/*
	 * device_token - подпись устройства, user_token - Подпись пользователя(если
	 * есть) user_id - тот кто подтверждает дружбу friend_user_id - айди друга
	 */

	public AcceptFriendshipOffer(Context context, String friend_user_id) {

		mURLBuilder.setPath("friends/acceptFriendshipOffer");

		mURLBuilder.setParameter("device_token",
				new AppSettings(context).getContentToken());
		mURLBuilder.setParameter("user_token",
				new AppSettings(context).getUserToken());

		mURLBuilder.setParameter("user_id",
				new AppSettings(context).getUserId());

		mURLBuilder.setParameter("friend_user_id", friend_user_id);
		getResponse(GET);

	}

	@Override
	public Data getData() {
		Data data = new Data();
		data.status = false;
		if (getResult()) {

			data.status = true;
		}
		return data;
	}

	public class Data extends WebQueryData {
		public boolean status;
	}

}
