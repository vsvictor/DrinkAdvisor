package com.drink.query;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.drink.R;
import com.drink.constants.Constants;
import com.drink.constants.Constants.EventType;
import com.drink.constants.Constants.EventsHolder;
import com.drink.constants.Constants.TargetType;
import com.drink.settings.AppSettings;

public class EventsQuery extends WebQuery 
{
	public Context mContext;
	public EventsQuery(Context context,EventsHolder currentQuery) 
	{
		if(currentQuery == EventsHolder.PROFILE)
		{
			mURLBuilder.setPath("friends/getEventsV2");
		}
		else if(currentQuery == EventsHolder.MAIN)
		{
			mURLBuilder.setPath("feed/listV2");
		}
		
		mURLBuilder.setParameter("user_token", new AppSettings(context).getUserToken());
		mURLBuilder.setParameter("device_token", new AppSettings(context).getContentToken());
		mContext = context;
	}

	@Override
	public EventQueryData getData() 
	{
		EventQueryData data = new EventQueryData();
		data.mEventList = new ArrayList<EventsQuery.EventData>();

		if (getResult()) 
		{
			try 
			{
				JSONArray eventArray = mJSONResponse.getJSONArray("events");
				data.mAllEventsCount = mJSONResponse.getInt("count");
				for (int i = 0; i < eventArray.length(); i++) 
				{
					JSONObject obj = (JSONObject) eventArray.get(i);
					EventData event = new EventData();
					event.setData(obj);
					data.mEventList.add(event);
				}
			} 
			catch (Exception e) {}
		}
		return data;
	}

	public void setDeviceToken(String string) 
	{
		mURLBuilder.setParameter(DEVICE_TOKEN, string);
	}

	public void setUserToken(String string) 
	{
		mURLBuilder.setParameter(USER_TOKEN, string);
	}

	public void setUserId(String userId) 
	{
		mURLBuilder.setParameter("user_id", userId);
	}

	public void setLimit(int limit) 
	{
		mURLBuilder.setParameter("limit", String.valueOf(limit));
	}

	public void setOffset(int offset) 
	{
		mURLBuilder.setParameter("offset", String.valueOf(offset));
	}

	public static class EventQueryData extends WebQueryData 
	{
		public ArrayList<EventData> mEventList;
		public int mAllEventsCount;
	}

	public class EventData 
	{
		public static final int RATE_STAR = 0;
		public static final int RATE_LIKE = 1;
		public static final int RATE_DISLIKE = 2;
		
		public String mUserId;
		public String mUserName;
		public String mUserSex;
		public String mUserAvatar;
		public String mTargetName;
		public String mTargetPicture;
		public Constants.TargetType mTargetType = TargetType.NON;
		public Constants.EventType mEventType = EventType.NON;
		public int mTimestamp;
		public String mTargetId;
		public String mEventStartText;
		public int mResourceId;
		public String mBarName;
		public String mBarId;
		public int mRate = RATE_STAR;
		
		public void setData(JSONObject obj) 
		{
			String eventType;
			String targetType;
			try 
			{
				eventType = obj.getString("event_type");
				targetType = obj.getString("target_type");
				setEventType(eventType, targetType);
				setTargetType(targetType);
				mTargetName = obj.getString("target_name");
				mTargetPicture = obj.getString("target_pict");
				mTargetId = obj.getString("target_id");
				mUserId = obj.getString("user_id");
				mUserAvatar = obj.getString("user_avatar");
				mUserName = obj.getString("user_name");
				mBarName = obj.getString("bar_name");
				mBarId = obj.getString("bar_id");
				if (mTargetType == TargetType.BARS) 
				{
					mTargetName = mBarName;
				}
				if (eventType.equals("rate"))
				{
					String like = obj.optString("rate_o");
					if (like != null)
					{
						if (like.equals("1"))
						{
							mRate = RATE_LIKE;
						}
						else if (like.equals("0"))
						{
							mRate = RATE_DISLIKE;
						}
						else
						{
							mRate = RATE_STAR;
						}
					}
				}

				mTimestamp = obj.getInt("timestamp");
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}

		void setEventType(String eventType, String targetType) 
		{
			if (eventType == null) return;

			if (eventType.equals("like")) 
			{
				mResourceId = R.drawable.event_like;
				mEventType = EventType.LIKE;
				if (targetType.equals("Cocktails"))
				{
					mEventStartText = EventsQuery.this.mContext.getResources().getString(R.string.event_like_cocktail);
				}
				else if (targetType.equals("BlogPosts"))
				{
					mEventStartText = EventsQuery.this.mContext.getResources().getString(R.string.event_like_review);
				}
				else
				{
					mEventStartText = EventsQuery.this.mContext.getResources().getString(R.string.event_like);
				}
			} 
			else if (eventType.equals("go_to_bar")) 
			{
				mResourceId = R.drawable.event_going_to_meet;
				mEventType = EventType.GO_TO_BAR;
				mEventStartText = EventsQuery.this.mContext.getResources().getString(R.string.event_going_to_meeting);
			} 
			else if (eventType.equals("checkin")) 
			{
				mResourceId = R.drawable.evet_was_in;
				mEventType = EventType.CHECKIN;
				mEventStartText = EventsQuery.this.mContext.getResources().getString(R.string.event_checkin);
			} 
			else if (eventType.equals("comment")) 
			{
				mResourceId = R.drawable.event_wrote_review;
				mEventType = EventType.COMMENT;
				if (targetType.equals("Bars"))
				{
					mEventStartText = EventsQuery.this.mContext.getResources().getString(R.string.event_review);
				}
				else
				{
					mEventStartText = EventsQuery.this.mContext.getResources().getString(R.string.event_comment);
				}
			} 
			else if (eventType.equals("rate")) 
			{
				mResourceId = R.drawable.event_like;
				mEventType = EventType.RATE;
				mEventStartText = EventsQuery.this.mContext.getResources().getString(R.string.event_rate);
			} 
			else if (eventType.equals("go_to_meeting")) 
			{
				mResourceId = R.drawable.event_going_to_meet;
				mEventType = EventType.GO_TO_MEETING;
				mEventStartText = EventsQuery.this.mContext.getResources().getString(R.string.event_going_to_meeting);
			} 
			else if (eventType.equals("create_meeting")) 
			{
				mResourceId = R.drawable.arranged_to_meet;
				mEventType = EventType.CREATE_MEETING;
				mEventStartText = EventsQuery.this.mContext.getResources().getString(R.string.event_create_meeting);
			} 
			else if (eventType.equals("friendship")) 
			{
				mResourceId = R.drawable.became_friend;
				mEventType = EventType.FRIENDSHIP;
				mEventStartText = EventsQuery.this.mContext.getResources().getString(R.string.event_become_friend);
			}
		}

		void setTargetType(String targetType) 
		{
			if (targetType == null) return;

			if (targetType.equals("Bars")) 
			{
				mTargetType = TargetType.BARS;
			} 
			else if (targetType.equals("Drinks")) 
			{
				mTargetType = TargetType.DRINKS;
			} 
			else if (targetType.equals("Cocktails")) 
			{
				mTargetType = TargetType.COCKTAILS;
			} 
			else if (targetType.equals("Brands")) 
			{
				mTargetType = TargetType.BRANDS;
			} 
			else if (targetType.equals("Meetings")) 
			{
				mTargetType = TargetType.MEETINGS;
			} 
			else if (targetType.equals("BlogPosts")) 
			{
				mTargetType = TargetType.BLOGPOSTS;
			}
		}
	}
}
