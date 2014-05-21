package com.drink.types;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Invited implements Parcelable
{
	public Invited()
	{
		super();
	}
	
	public Invited(Parcel parcel) 
	{
		super();

		readFromParcel(parcel);
	}

	public static final Parcelable.Creator<Invited> CREATOR = new Parcelable.Creator<Invited>() 
	{
	    public Invited createFromParcel(Parcel in) 
	    {
	      return new Invited(in);
	    }

	    public Invited[] newArray(int size) 
	    {
	    	return new Invited[size];
	    }
	};

	static public Invited parse(JSONObject object)
	{
		try 
		{
			Invited invited = new Invited();
			
			invited.id = object.getString("id");
			invited.name = object.getString("name");
			invited.avatar = object.optString("avatar");
			invited.friend = object.optBoolean("is_friend");
			invited.state = object.optInt("was_in_meeting", Meeting.DONT_KNOW);

			return invited;
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
	    name = parcel.readString();
	    avatar = parcel.readString();
	    friend = parcel.readString().equalsIgnoreCase("yes");
	    state = parcel.readInt();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(avatar);
		dest.writeString(friend ? "yes" : "no");
		dest.writeInt(state);
	}

	public String 	id;
	public String 	name;
	public String 	avatar;
	public boolean 	friend = false;
	public int		state = Meeting.DONT_KNOW;
}
