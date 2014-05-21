package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.settings.AppSettings;

import android.content.Context;

public class FeedQuery extends WebQuery {
	/*
	 * device_token - подпись контента user_token - Подпись пользователя limit -
	 * количество запрашиваемых offset - с какой записи по счету показывать
	 */
	public FeedQuery(Context context) {

		mURLBuilder.setPath("feed");
		mURLBuilder.setParameter("device_token",
				new AppSettings(context).getContentToken());
		mURLBuilder.setParameter("user_token",
				new AppSettings(context).getUserToken());

		setLimit("30");
		setOffset("0");
	}

	public void setLimit(String string) {
		mURLBuilder.setParameter(LIST_LIMIT, string);
	}

	public void setOffset(String string) {
		mURLBuilder.setParameter(LIST_OFFSET, string);
	}

	@Override
	public Data getData() {

		Data data = new Data();
		if(getResult()){
			try {
				data.count = mJSONResponse.optInt("count");
				JSONArray array = mJSONResponse.getJSONArray("events");
				for (int i = 0; i < array.length(); i++) {
					FeedQuery.Event event = new Event();
					JSONObject object = array.getJSONObject(i);
					
					event.datetime = object.optString("datetime");
					event.message = object.optString("message");
					event.avatar = object.optString("avatar");
					
					data.events.add(event);
					
				}
			} catch (Exception e) {

			}
		}
		return data;
	}

	public class Data extends WebQueryData {
		public int count;
		public ArrayList<Event> events = new ArrayList<Event>();
	}

	public class Event {
		public String datetime;
		public String message;
		public String avatar;
	}

}
