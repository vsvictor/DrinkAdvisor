package com.drink.query;

import java.util.ArrayList;

import com.drink.settings.AppSettings;
import com.drink.types.Friend;

public class AddInvitedQuery extends WebQuery 
{
	public AddInvitedQuery(AppSettings appSettings, String meetingId, ArrayList<Friend> friends) 
	{
		mURLBuilder.setPath("meetings/inviteFriends");
		
		mURLBuilder.setParameter("device_token", appSettings.getContentToken());
		mURLBuilder.setParameter("user_token", appSettings.getUserToken());
		mURLBuilder.setParameter("user_id",	appSettings.getUserId());
		
		mURLBuilder.setParameter("meeting_id", meetingId);
		
		String _friends = "";
		for (int i = 0; i < friends.size(); ++i)
		{
			if (_friends.length() > 0)
			{
				_friends += ",";
			}
			_friends += friends.get(i).id;
		}
		
		mURLBuilder.setParameter("friends", _friends);
	}
	
	@Override
	public Data getData() 
	{
		Data data = new Data();
		if (getResult()) 
		{
			try 
			{
			} 
			catch (Exception e) {}
		}

		return data;
	}

	public class Data extends WebQueryData 
	{
	}
}
