package com.drink.query;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.settings.AppSettings;

public class BarQuery extends WebQuery 
{
	public enum Status
	{
		Closed_Today, Opened_Tonight, Opened_Today
	};
	
	public BarQuery(AppSettings appSettings, String id) 
	{
		mURLBuilder.setPath("bars2/" + id);

		mURLBuilder.setParameter(DEVICE_TOKEN, appSettings.getContentToken());
		mURLBuilder.setParameter(USER_TOKEN, appSettings.getUserToken());
		mURLBuilder.setParameter(USER_ID, appSettings.getUserId());
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();

		if (getResult()) 
		{
			try 
			{
				JSONArray picArray = mJSONResponse.getJSONArray("pictures");
				JSONArray comArray = mJSONResponse.getJSONArray("comments");
				for (int i = 0; i < picArray.length(); i++) 
				{
					data.picList.add(picArray.getString(i));
				}

				for (int i = 0; i < comArray.length(); i++)
				{
					data.comList.add(picArray.getString(i));
				}

				data.id = mJSONResponse.optInt("id");
				data.name = mJSONResponse.optString("name");
				data.rating = mJSONResponse.optString("rating");
				data.info = mJSONResponse.optString("info");
				data.address = mJSONResponse.optString("address");
				data.lat = mJSONResponse.optDouble("lat");
				data.lng = mJSONResponse.optDouble("long");
				data.phone = mJSONResponse.optString("phone");
				data.checkins_count = mJSONResponse.optString("checkins_count");
				data.reviews_count = mJSONResponse.optString("reviews_count");
				data.likes_count = mJSONResponse.optInt("likes_count", 0);
				data.pictures_count = mJSONResponse.optInt("pictures_count");
				data.positiveRatingCount = mJSONResponse.optInt("positive_rating_count");
				data.negativeRatingCount = mJSONResponse.optInt("negative_rating_count");
				data.state_like = mJSONResponse.optBoolean("state_like", false);
				data.hasPhoto = mJSONResponse.optBoolean("isPhoto");

				int status = mJSONResponse.optInt("work_now");
				switch (status)
				{
				case 0:
					data.status = Status.Closed_Today;
					break;
				case 1:
					data.status = Status.Opened_Tonight;
					break;
				case 2:
				case 3:
					data.status = Status.Opened_Today;
					break;
				}
				
				String day = mJSONResponse.optString("working_day");
				if (day.equalsIgnoreCase("sun"))
				{
					data.workingDay = 0;
				}
				else if (day.equalsIgnoreCase("mon"))
				{
					data.workingDay = 1;
				}
				else if (day.equalsIgnoreCase("tue"))
				{
					data.workingDay = 2;
				}
				else if (day.equalsIgnoreCase("wed"))
				{
					data.workingDay = 3;
				}
				else if (day.equalsIgnoreCase("thu"))
				{
					data.workingDay = 4;
				}
				else if (day.equalsIgnoreCase("fri"))
				{
					data.workingDay = 5;
				}
				else if (day.equalsIgnoreCase("sat"))
				{
					data.workingDay = 6;
				}
				
				JSONObject workingTime = mJSONResponse.getJSONObject("working_time");
				
				data.workingTimeList1.add(workingTime.optString("working_time_day_sun"));
				data.workingTimeList1.add(workingTime.optString("working_time_day_mon"));
				data.workingTimeList1.add(workingTime.optString("working_time_day_tue"));
				data.workingTimeList1.add(workingTime.optString("working_time_day_wed"));
				data.workingTimeList1.add(workingTime.optString("working_time_day_thu"));
				data.workingTimeList1.add(workingTime.optString("working_time_day_fri"));
				data.workingTimeList1.add(workingTime.optString("working_time_day_sat"));

				data.workingTimeList2.add(workingTime.optString("working_time_sun"));
				data.workingTimeList2.add(workingTime.optString("working_time_mon"));
				data.workingTimeList2.add(workingTime.optString("working_time_tue"));
				data.workingTimeList2.add(workingTime.optString("working_time_wed"));
				data.workingTimeList2.add(workingTime.optString("working_time_thu"));
				data.workingTimeList2.add(workingTime.optString("working_time_fri"));
				data.workingTimeList2.add(workingTime.optString("working_time_sat"));
				
				data.like = mJSONResponse.optBoolean("state_like");
				data.favorite = mJSONResponse.optBoolean("state_fav");
				data.cityName = mJSONResponse.optString("city");
			} 
			catch (Exception e) {}
		}

		return data;
	}

	public class Data extends WebQueryData 
	{
		public int id = -1;
		public String name;
		public String rating;
		public String info;
		public String address;
		public double lat;
		public double lng;
		public String checkins_count;
		public String reviews_count;
		public String phone;
		public int likes_count = 0;
		public int pictures_count = 0;
		public int positiveRatingCount = 0;
		public int negativeRatingCount = 0;
		public ArrayList<String> picList = new ArrayList<String>();
		public ArrayList<String> comList = new ArrayList<String>();
		public boolean state_like = false;
		public boolean hasPhoto = false;
		public ArrayList<String> workingTimeList1 = new ArrayList<String>();
		public ArrayList<String> workingTimeList2 = new ArrayList<String>();
		public Status status = Status.Closed_Today;
		public int workingDay = 0;
		public boolean like = false;
		public boolean favorite = false;
		public String cityName;
	}
}
