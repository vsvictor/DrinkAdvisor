package com.drink.constants;

public class Constants 
{
	public static final String KEY_USER_ID = "user_id";
	public static final int LOGIN_RESULT_CODE = 3;

	public enum UserProfileRequest 
	{
		NON(0), USER(1), EVENTS(2), ADD_FRIEND(3), DELETE_FRIEND(4), USER_ONLY(5);
		private int typeValue;

		private UserProfileRequest(int type) 
		{
			typeValue = type;
		}

		public int getTypeValue() 
		{
			return typeValue;
		}
	}

	public enum EventType 
	{
		NON(0), LIKE(1), GO_TO_BAR(2), CHECKIN(3), COMMENT(4), RATE(5), GO_TO_MEETING(6), CREATE_MEETING(7), FRIENDSHIP(8);
		private int typeValue;

		private EventType(int type) 
		{
			typeValue = type;
		}

		public int getTypeValue() 
		{
			return typeValue;
		}
	}

	public enum TargetType 
	{
		NON(0), COCKTAILS(1), DRINKS(2), BARS(3), BRANDS(4), MEETINGS(5), BLOGPOSTS(6);
		private int typeValue;

		private TargetType(int type) 
		{
			typeValue = type;
		}

		public int getTypeValue() 
		{
			return typeValue;
		}
	}

	public enum EventsHolder 
	{
		NON(0), PROFILE(1), MAIN(2);
		private int typeValue;

		private EventsHolder(int type) 
		{
			typeValue = type;
		}

		public int getTypeValue() 
		{
			return typeValue;
		}
	}

	public enum FriendType 
	{
		NONE(0), NATIVE(1), FACEBOOK(2), TWITTER(3);
		private int typeValue;

		private FriendType(int type) 
		{
			typeValue = type;
		}

		public int getTypeValue() 
		{
			return typeValue;
		}
	}

}
