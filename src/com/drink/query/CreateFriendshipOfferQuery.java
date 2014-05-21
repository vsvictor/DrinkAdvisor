package com.drink.query;

import com.drink.settings.AppSettings;

import android.content.Context;

/*
 * Запрос GET /api/friends/createFriendshipOffer
 Параметры:
 device_token - подпись устройства,
 user_token - Подпись пользователя(если есть)
 user_id - айди того кто делает запрос дружбу
 friend_user_id - айди друга
 message - сообщение
 Пример ответа: {"success":true} - если все прошло хорошо


 * 
 * */
public class CreateFriendshipOfferQuery extends WebQuery {

	public CreateFriendshipOfferQuery(Context context, String friend_user_id,
			String message) {
		mURLBuilder.setPath("friends/createFriendshipOffer");
		mURLBuilder.setParameter("device_token",
				new AppSettings(context).getContentToken());
		mURLBuilder.setParameter("user_token",
				new AppSettings(context).getUserToken());
		mURLBuilder.setParameter("user_id",
				new AppSettings(context).getUserId());
		mURLBuilder.setParameter("message", message);
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
