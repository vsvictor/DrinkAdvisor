package com.drink.types;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Meeting implements Parcelable
{
	static public final int GO = 0;
	static public final int DONT_GO = 1;
	static public final int DONT_KNOW = 2;

	public Meeting()
	{
		super();
	}
	
	private Meeting(Parcel parcel) 
	{
		super();

		readFromParcel(parcel);
	}

	public static final Parcelable.Creator<Meeting> CREATOR = new Parcelable.Creator<Meeting>() 
	{
	    public Meeting createFromParcel(Parcel in) 
	    {
	      return new Meeting(in);
	    }

	    public Meeting[] newArray(int size) 
	    {
	    	return new Meeting[size];
	    }
	};

	static public Meeting parse(JSONObject object)
	{
		try 
		{
			Meeting meeting = new Meeting();
			
			meeting.id = object.getString("id");
			if (object.has("porp"))
			{
				meeting.title = object.getString("porp");
			}
			else
			{
				meeting.title = object.getString("purpose_of_meeting");
			}
			meeting.date = object.optString("date");
			meeting.address = object.optString("adr");
			meeting.user_id = object.optString("user_id");
			meeting.user_name = object.optString("user_name");
			meeting.user_picture = object.optString("user_picture");
			meeting.bar_id = object.optString("bar_id");
			meeting.bar_name = object.optString("bar_name");
			meeting.bar_picture = object.optString("bar_picture");
			meeting.description = object.optString("comment_creator");
			meeting.lat = object.optDouble("lat");
			meeting.lng = object.optDouble("long");
			meeting.invited_count = object.optInt("invited_count");
			meeting.will_go_count = object.optInt("will_go_count");
			meeting.owner = object.optBoolean("isowner");
			meeting.view = object.optBoolean("isView");
			if (!meeting.owner)
			{
				meeting.state = object.optInt("was_in_meeting");
			}
			if ((meeting.invited_count > 0) && object.has("invited"))
			{
				JSONArray array = object.optJSONArray("invited");
				for (int i = 0; i < array.length(); ++i) 
				{
					meeting.invited.add(Invited.parse(array.getJSONObject(i)));
				}
			}
			
			return meeting;
		} 
		catch (JSONException ex) {}
		
		return null;
	}

	@Override
	public int describeContents() 
	{
		return 0;
	}
	
	public void readFromParcel(Parcel parcel)
	{
	    id = parcel.readString();
	    title = parcel.readString();
	    date = parcel.readString();
	    address = parcel.readString();
	    user_id = parcel.readString();
	    user_name = parcel.readString();
	    user_picture = parcel.readString();
	    bar_id = parcel.readString();
	    bar_name = parcel.readString();
	    bar_picture = parcel.readString();
	    description = parcel.readString();
	    lat = parcel.readDouble();
	    lng = parcel.readDouble();
	    invited_count = parcel.readInt();
	    will_go_count = parcel.readInt();
	    owner = parcel.readString().equalsIgnoreCase("yes");
	    view = parcel.readString().equalsIgnoreCase("yes");
	    state = parcel.readInt();

	    int size = parcel.readInt();
	    for (int i = 0; i < size; ++i)
	    {
	    	invited.add(new Invited(parcel));
	    }
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) 
	{
		parcel.writeString(id);
		parcel.writeString(title);
		parcel.writeString(date);
		parcel.writeString(address);
		parcel.writeString(user_id);
		parcel.writeString(user_name);
		parcel.writeString(user_picture);
		parcel.writeString(bar_id);
		parcel.writeString(bar_name);
		parcel.writeString(bar_picture);
		parcel.writeString(description);
		parcel.writeDouble(lat);
		parcel.writeDouble(lng);
		parcel.writeInt(invited_count);
		parcel.writeInt(will_go_count);
		parcel.writeString(owner ? "yes" : "no");
		parcel.writeString(view ? "yes" : "no");
		parcel.writeInt(state);

	    for (int i = 0; i < invited_count; ++i)
	    {
	    	invited.get(i).writeToParcel(parcel, 0);
	    }
	}
	
	public String id;
	public String title;
	public String date;
	public String address;
	public String user_id;
	public String user_name;
	public String user_picture;
	public String bar_id;
	public String bar_name;
	public String bar_picture;
	public String description;
	public double lat = 0.0;
	public double lng = 0.0;
	public int invited_count;
	public int will_go_count;
	public boolean owner = false;
	public boolean view = true;
	public int state = DONT_KNOW;
	public ArrayList<Invited> invited = new ArrayList<Invited>();
}
