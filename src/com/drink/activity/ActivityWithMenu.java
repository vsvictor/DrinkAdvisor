
package com.drink.activity;

import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.helpers.Defines;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public abstract class ActivityWithMenu extends BasicActivity
{
	static final int LOGIN_REQUIED_FOR_CHECKIN = 1000;
	static final int LOGIN_REQUIED_FOR_MEETINGS = 1001;
	static final int LOGIN_REQUIED_FOR_FEED = 1002;
	static final int LOGIN_REQUIED_FOR_FRIENDS = 1003;
	static final int LOGIN_REQUIED_FOR_PROFILE = 1004;
	static final int LOGIN_REQUIED_FOR_CREATE_MEETING = 1005;
	static final int LOGIN_REQUIED_FOR_FORCE_SWITCH = 1006;
	static final int REQUEST_BAR = 1007;
	
	private TextView tv_user_name;
	private TextView tv_friends_count;
	private TextView tv_meetings_count;
	private TextView tv_place_count;
	private TextView tv_feed_count;
	private TextView tv_settings;
	private TextView tv_register;
	private TextView tv_logout;
	private TextView tv_login;

	private LinearLayout ll_register;
	
	private TableRow tr_user_name;
	private TableRow tr_settings;
	private TableRow tr_register;
	private TableRow tr_logout;
	private TableRow tr_login;

	protected ActivityWithMenu(int id)
	{
		super(id);
	}
	
	protected ActivityWithMenu(int id, boolean back)
	{
		super(id, back);
	}
	
	@Override
	public void onBackPressed() 
	{
/*		if (menu.isMenuShowing()) 
		{
			menu.showContent();
		} 
		else 
		{
			super.onBackPressed();
		}*/
	}

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
//		createMenu();
	}
/*	
	private void createMenu() 
	{
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menu.setBehindOffset((int) (getWindowManager().getDefaultDisplay().getWidth() * 0.33f));
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		menu.setMinimumWidth(20);

		// create layout
		LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
		RelativeLayout layout = (RelativeLayout) layoutInflater.inflate(R.layout.sliding_menu, null);

		// MAIN MENU
		TextView tv_menu = (TextView) layout.findViewById(R.id.tv_main_menu);
		tv_menu.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		// Bars
		TextView tv_bars = (TextView) layout.findViewById(R.id.tv_bars);
		tv_bars.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		TableRow tr_bars = (TableRow) layout.findViewById(R.id.tr_bars);
		tr_bars.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					startActivity(new Intent(ActivityWithMenu.this, BarsActivity.class));
					finish();
				}
			});
		
		// Drinks
		TextView tv_drinks = (TextView) layout.findViewById(R.id.tv_drinks);
		tv_drinks.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		TableRow tr_drinks = (TableRow) layout.findViewById(R.id.tr_drinks);
		tr_drinks.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					startActivity(new Intent(ActivityWithMenu.this, DrinksActivity.class));
					finish();
				}
			});
		
		// Cocktails
		TextView tv_cocktails = (TextView) layout.findViewById(R.id.tv_cocktails);
		tv_cocktails.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		TableRow tr_cocktails = (TableRow) layout.findViewById(R.id.tr_cocktails);
		tr_cocktails.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					startActivity(new Intent(ActivityWithMenu.this, CocktailsActivity.class).putExtra("ID", ""));
					finish();
				}
			});
		
		// Reviews
		TextView tv_blog = (TextView) layout.findViewById(R.id.tv_reviews);
		tv_blog.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		TableRow tr_blog = (TableRow) layout.findViewById(R.id.tr_reviews);
		tr_blog.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					startActivity(new Intent(ActivityWithMenu.this, BlogActivity.class));
					finish();
				}
			});

		// Home
		TextView tv_home = (TextView) layout.findViewById(R.id.tv_home);
		tv_home.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		TableRow tr_home = (TableRow) layout.findViewById(R.id.tr_home);
		tr_home.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					startActivity(new Intent(ActivityWithMenu.this, HomeActivity.class));
					finish();
				}
			});

		// PERSONAL PAGES
		TextView tv_section = (TextView) layout.findViewById(R.id.tv_personal_pages);
		tv_section.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		// Friends
		TextView tv_friends = (TextView) layout.findViewById(R.id.tv_friends);
		tv_friends.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_friends_count = (TextView) layout.findViewById(R.id.tv_friends_count);
		tv_friends_count.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		int eventCount = appSettings.getEventsFriendsCount();
		if (eventCount > 0) 
		{
			tv_friends_count.setText(String.valueOf(eventCount));
		} 
		else
		{
			tv_friends_count.setVisibility(View.INVISIBLE);
		}
		
		TableRow tr_friends = (TableRow) layout.findViewById(R.id.tr_friends);
		tr_friends.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					if (checkLogin(LOGIN_REQUIED_FOR_FRIENDS))
					{
						startActivity(new Intent(ActivityWithMenu.this, FriendsActivity.class));
					}
					menu.showContent();
				}
			});
		
		// Meetings
		TextView tv_meetings = (TextView) layout.findViewById(R.id.tv_meetings);
		tv_meetings.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_meetings_count = (TextView) layout.findViewById(R.id.tv_meetings_count);
		tv_meetings_count.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		eventCount = appSettings.getEventsMeetingsCount();
		if (eventCount > 0) 
		{
			tv_meetings_count.setText(String.valueOf(eventCount));
		}
		else
		{
			tv_meetings_count.setVisibility(View.INVISIBLE);
		}
		
		TableRow tr_meetings = (TableRow) layout.findViewById(R.id.tr_meetings);
		tr_meetings.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					if (checkLogin(LOGIN_REQUIED_FOR_MEETINGS))
					{
						startActivity(new Intent(ActivityWithMenu.this, MeetingsActivity.class));
					}
					menu.showContent();
				}
			});
		
		// My places
		TextView tv_place = (TextView) layout.findViewById(R.id.tv_places);
		tv_place.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_place_count = (TextView) layout.findViewById(R.id.tv_places_count);
		tv_place_count.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		TableRow tr_place = (TableRow) layout.findViewById(R.id.tr_places);
		tr_place.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					if (checkLogin(LOGIN_REQUIED_FOR_CHECKIN))
					{
						startActivity(new Intent(ActivityWithMenu.this, PlacesActivity.class));
					}
					menu.showContent();
				}
			});
		
		// Activity
		TextView tv_feed = (TextView) layout.findViewById(R.id.tv_activity);
		tv_feed.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_feed_count = (TextView) layout.findViewById(R.id.tv_activity_count);
		tv_feed_count.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		eventCount = appSettings.getEventsFeedCount();
		if (eventCount > 0) 
		{
			tv_feed_count.setText("+" + String.valueOf(eventCount));
			tv_friends_count.setBackgroundResource(R.drawable.menu_count_bg);
		}
		
		TableRow tr_feed = (TableRow) layout.findViewById(R.id.tr_activity);
		tr_feed.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					if (checkLogin(LOGIN_REQUIED_FOR_FEED))
					{
						startActivity(new Intent(ActivityWithMenu.this, FeedActivity.class));
					}
					menu.showContent();
				}
			});

		// PROFILE
		TextView tv_account = (TextView) layout.findViewById(R.id.tv_profile);
		tv_account.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		// [user name]
		tr_user_name = (TableRow) layout.findViewById(R.id.tr_user_name);
		tr_user_name.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				startActivity(new Intent(ActivityWithMenu.this, UserProfileActivity.class));
				menu.showContent();
			}
		});
		
		tv_user_name = (TextView) tr_user_name.findViewById(R.id.tv_user_name);
		tv_user_name.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		ll_register = (LinearLayout) layout.findViewById(R.id.ll_register);
		
		// Settings (login state)
		tr_settings = (TableRow) layout.findViewById(R.id.tr_settings);
		tr_settings.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					startActivity(new Intent(ActivityWithMenu.this, ProfileActivity.class));
					menu.showContent();
				}
			});
		
		tv_settings = (TextView) tr_settings.findViewById(R.id.tv_settings);
		tv_settings.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		// Register (logout state)
		tr_register = (TableRow) layout.findViewById(R.id.tr_register);
		tr_register.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent();
					intent.putExtra("type", Defines.TYPE_USUAL);
					intent.setClass(ActivityWithMenu.this, RegisterActivity.class);
					startActivity(intent);

					finish();
				}
			});

		tv_register = (TextView) tr_register.findViewById(R.id.tv_register);
		tv_register.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		// Logout (login state)
		tr_logout = (TableRow) layout.findViewById(R.id.tr_logout);
		tr_logout.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					appSettings.clearAuthData();
					CommonHelper.clearUserToken(ActivityWithMenu.this);
					
					startActivity(new Intent(ActivityWithMenu.this, LoginActivity.class));
					finish();
				}
			});

		tv_logout = (TextView) tr_logout.findViewById(R.id.tv_logout);
		tv_logout.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		// Login (logout state)
		tr_login = (TableRow) layout.findViewById(R.id.tr_login);
		tr_login.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					startActivity(new Intent(ActivityWithMenu.this, LoginActivity.class));
					finish();
				}
			});

		tv_login = (TextView) tr_login.findViewById(R.id.tv_login);
		tv_login.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));

		// ACTIONS
		TextView tv_actions = (TextView) layout.findViewById(R.id.tv_actions);
		tv_actions.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		// create meeting
		TextView tv_meeting_add = (TextView) layout.findViewById(R.id.tv_meeting_add);
		tv_meeting_add.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		TableRow tr_meeting_add = (TableRow) layout.findViewById(R.id.tr_meeting_add);
		tr_meeting_add.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					if (checkLogin(LOGIN_REQUIED_FOR_CREATE_MEETING))
					{
						Intent intent = new Intent();
						intent.putExtra("select_bar", "");
						intent.setClass(ActivityWithMenu.this, BarsActivity.class);

						startActivityForResult(intent, REQUEST_BAR);
					}
					menu.showContent();
				}
			});

		updateMenu();
		
		menu.setMenu(layout);
	}

	protected void updateMenu()
	{
		if ((userToken != null) && (userToken.length() > 0))
		{
			tv_user_name.setText(appSettings.getUserName());
			tr_user_name.setVisibility(View.VISIBLE);
			ll_register.setVisibility(View.VISIBLE);
			
			tr_settings.setVisibility(View.VISIBLE);
			tr_register.setVisibility(View.GONE);
			
			tr_logout.setVisibility(View.VISIBLE);
			tr_login.setVisibility(View.GONE);
		}
		else
		{
			tr_user_name.setVisibility(View.GONE);
			ll_register.setVisibility(View.GONE);
			
			tr_settings.setVisibility(View.GONE);
			tr_register.setVisibility(View.VISIBLE);
			
			tr_logout.setVisibility(View.GONE);
			tr_login.setVisibility(View.VISIBLE);
		}
	}
	*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
/*		
		if (resultCode == RESULT_OK) 
		{
			switch (requestCode)
			{
			case LOGIN_REQUIED_FOR_CHECKIN:
				startActivity(new Intent(this, PlacesActivity.class));
				break;
			case LOGIN_REQUIED_FOR_MEETINGS:
				startActivity(new Intent(this, MeetingsActivity.class));
				break;
			case LOGIN_REQUIED_FOR_FEED:
				startActivity(new Intent(this, FeedActivity.class));
				break;
			case LOGIN_REQUIED_FOR_FRIENDS:
				startActivity(new Intent(this, FriendsActivity.class));
				break;
			case LOGIN_REQUIED_FOR_PROFILE:
				startActivity(new Intent(this, UserProfileActivity.class));
				break;
			case LOGIN_REQUIED_FOR_CREATE_MEETING:
				{
					Intent intent = new Intent();
					intent.putExtra("select_bar", "");
					intent.setClass(ActivityWithMenu.this, BarsActivity.class);
	
					startActivityForResult(intent, REQUEST_BAR);
				}
				break;
			case REQUEST_BAR:
				{
					Intent intent = new Intent();
					intent.setClass(this, CreateMeetingActivity.class);
					intent.putExtra("ID", data.getStringExtra("bar_id"));
					
					startActivity(intent);
				}
				break;
			}
		}*/
	}
}
