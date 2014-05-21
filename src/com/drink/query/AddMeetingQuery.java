package com.drink.query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.http.message.BasicNameValuePair;

import com.drink.settings.AppSettings;

public class AddMeetingQuery extends WebQuery 
{
	public AddMeetingQuery(AppSettings appSettings, String bar_id, Date date, String title, String comment, boolean facebook, boolean twitter) 
	{
		mURLBuilder.setPath("meetings");

		nameValuePairs.add(new BasicNameValuePair("device_token", appSettings.getContentToken()));
		nameValuePairs.add(new BasicNameValuePair("user_token",	appSettings.getUserToken()));
		nameValuePairs.add(new BasicNameValuePair("user_id", appSettings.getUserId()));

		String _time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
		String _date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);

		nameValuePairs.add(new BasicNameValuePair("bar_id", bar_id));
		nameValuePairs.add(new BasicNameValuePair("date", _date));
		nameValuePairs.add(new BasicNameValuePair("time", _time));
		nameValuePairs.add(new BasicNameValuePair("purpose_of_meeting", title));

		if ((comment != null) && (comment.length() > 0))
		{
			nameValuePairs.add(new BasicNameValuePair("comment_creator", comment));
		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair("comment_creator", "no"));
		}

		nameValuePairs.add(new BasicNameValuePair("publish_on_facebook", facebook ? "true" : "false"));
		nameValuePairs.add(new BasicNameValuePair("publish_on_twitter",	twitter ? "true" : "false"));
		nameValuePairs.add(new BasicNameValuePair("publish_on_google", "false"));
	}

	public void setInvitedToMeetings(ArrayList<String> invited_to_meetings) 
	{
		if (invited_to_meetings.size() == 0) return;

		String invited_to_meetings_string = "";
		for (int i = 0; i < invited_to_meetings.size(); i++) 
		{
			if (i > 0)
			{
				invited_to_meetings_string += ",";
			}
			invited_to_meetings_string += invited_to_meetings.get(i);
		}

		nameValuePairs.add(new BasicNameValuePair("invited_to_meetings", invited_to_meetings_string));
	}

	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			data.string = mJSONResponse.toString();
		}
		return data;
	}

	public class Data extends WebQueryData 
	{
		public String string;
	}
}
