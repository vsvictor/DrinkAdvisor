package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.settings.AppSettings;

import android.content.Context;

public class PlacesQuery extends WebQuery {

	public PlacesQuery(Context context) {
		mURLBuilder.setPath("MyPlace/Index");
		mURLBuilder.setParameter("user_token",
				new AppSettings(context).getUserToken());

		mURLBuilder.setParameter("id_user",
				new AppSettings(context).getUserId());

		mURLBuilder.setParameter("device_token",
				new AppSettings(context).getContentToken());

	}

	public void setLimit(int limit) {
		mURLBuilder.setParameter("limit", String.valueOf(limit));
	}

	public void setOffset(int offset) {
		mURLBuilder.setParameter("offset", String.valueOf(offset));
	}

	@Override
	public PlacesQueryData getData() {
		PlacesQueryData data = new PlacesQueryData();
		data.mBarPlaces = new ArrayList<PlacesQuery.PlaceBar>();
		if (getResult()) {
			try {
				JSONArray placesArray = mJSONResponse.getJSONArray("bar");
				data.mCount = mJSONResponse.getInt("count");

				for (int i = 0; i < placesArray.length(); i++) {
					PlaceBar bar = new PlaceBar();
					JSONObject obj = (JSONObject) placesArray.get(i);
					bar.mId = obj.getString("id");
					bar.mImage = obj.getString("foto");
					bar.mAddress = obj.getString("adress");
					bar.mName = obj.getString("title");
					bar.mRating = obj.getInt("Rating");
					bar.mDate = obj.getString("date");
					data.mBarPlaces.add(bar);
				}
			} catch (Exception ex) {

			}
		}
		return data;
	}

	public class PlacesQueryData extends WebQueryData {
		public ArrayList<PlaceBar> mBarPlaces;
		public int mCount;
	}

	/*
	 * "bar": [ { "id": 8518, "foto": "uploads/BarPictures/8518/luch.jpg",
	 * "title": "Luch", "adress": "Bolshaya Pirogovskaya., 27/1, Moscow",
	 * "Rating": 98, "date": "2013-05-07 16:36:44"
	 */
	public class PlaceBar {
		public String mId;
		public String mImage;
		public String mName;
		public String mAddress;
		public int mRating;
		public String mDate;

	}

}
