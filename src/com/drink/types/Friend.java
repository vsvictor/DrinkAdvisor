package com.drink.types;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Friend implements Parcelable
{
	public static final int STATUS_DontFollow = 0;
	public static final int STATUS_Follow = 1;
	public static final int STATUS_Request = 2;
	
	public String id;
	public String name;
	public String avatar;
	public int status = STATUS_DontFollow;
	
	public Friend()
	{
		super();
	}
	
	public Friend(Parcel parcel) 
	{
		super();
		
		readFromParcel(parcel);
	}

	public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>() 
	{
	    public Friend createFromParcel(Parcel in) 
	    {
	      return new Friend(in);
	    }

	    public Friend[] newArray(int size) 
	    {
	    	return new Friend[size];
	    }
	};

	static public Friend parse(JSONObject object)
	{
		try 
		{
			Friend friend = new Friend();
			
			friend.id = object.getString("id");
			friend.name = object.getString("name");
			friend.avatar = object.getString("avatar");
			friend.status = object.getInt("status");

			return friend;
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
	    status = parcel.readInt();
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) 
	{
		parcel.writeString(id);
		parcel.writeString(name);
		parcel.writeString(avatar);
		parcel.writeInt(status);
	}
}
