package com.drink.query;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.drink.settings.AppSettings;

import android.content.Context;

public class AboutMeetingQuery extends WebQuery {

	public AboutMeetingQuery(Context context, String id) {

		mURLBuilder.setPath("meetings/" + id);
		mURLBuilder.setParameter("device_token",
				new AppSettings(context).getContentToken());
		mURLBuilder.setParameter("user_token",
				new AppSettings(context).getUserToken());
		mURLBuilder.setParameter("user_id",
				new AppSettings(context).getUserId());
	}

	@Override
	public Data getData() {
		Data data = new Data();
		if (getResult()) {
			try {

				data.id = mJSONResponse.optString("id");
				data.date = mJSONResponse.optString("date");
				data.user_id = mJSONResponse.optString("user_id");
				data.user_picture = mJSONResponse.optString("user_picture");
				data.user_name = mJSONResponse.optString("user_name");
				data.bar_id = mJSONResponse.optString("bar_id");
				data.bar_name = mJSONResponse.optString("bar_name");
				data.bar_picture = mJSONResponse.optString("bar_picture");
				data.invited_count = mJSONResponse.optString("invited_count");
				data.will_go_count = mJSONResponse.optString("will_go_count");
				data.will_go_count = mJSONResponse.optString("will_go_count");
				data.purpose_of_meeting = mJSONResponse.optString("purpose_of_meeting");
				data.comment_creator = mJSONResponse.optString("comment_creator");
				
				JSONArray array = mJSONResponse.optJSONArray("comments");
				for (int i = 0; i < array.length(); i++) {
					JSONObject  object = array.getJSONObject(i);
					Comment comment = new Comment();
					
					comment.id = object.optString("id");
					comment.text = object.optString("text");
					comment.created_at = object.optString("created_at");
					comment.user_id = object.optString("user_id");
					comment.user_picture = object.optString("user_picture");
					
					data.comments.add(comment);
				}

			} catch (Exception e) {

			}
		}
		return data;
	}

	public class Data extends WebQueryData {
		public String id;
		public String date;
		public String user_id;
		public String bar_id;
		public String bar_name;
		public String user_picture;
		public String user_name;
		public String bar_picture;
		public String invited_count;
		public String will_go_count;
		public String purpose_of_meeting;
		public String comment_creator;
		public ArrayList<Comment> comments = new ArrayList<AboutMeetingQuery.Comment>();
	}

	public class Comment {
		public String id;
		public String text;
		public String created_at;
		public String user_id;
		public String user_name;
		public String user_picture;
	}

}
