
package com.drink.activity;

import com.drink.ForceSwitchManager;
import com.drink.R;
import com.drink.constants.Constants;
import com.drink.helpers.AppRater;
import com.drink.query.CommentsQuery;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends BasicActivity 
{
	private static boolean firstLaunch = true;
	
	private Button	mBtnBars;
	private Button	mBtnDrinks;
	private Button	mBtnCocktails;
	private Button	mBtnReviews;
	private Button	mBtnFriends;
	private Button	mBtnMeetings;
	private Button	mBtnFavorites;
	private Button	mBtnActivity;
	private Button	mBtnProfile;
	
	private TextView	mTxtFriends;
	private TextView	mTxtMeetings;
	
	public HomeActivity()
	{
		super(R.layout.activity_home, false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		mBtnBars = (Button) findViewById(R.id.btn_bars);
		mBtnBars.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mBtnBars.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				startActivity(new Intent(HomeActivity.this, BarsActivity.class));
			}
		});

		mBtnDrinks = (Button) findViewById(R.id.btn_drinks);
		mBtnDrinks.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mBtnDrinks.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				startActivity(new Intent(HomeActivity.this, DrinksActivity.class));
			}
		});

		mBtnCocktails = (Button) findViewById(R.id.btn_cocktails);
		mBtnCocktails.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mBtnCocktails.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				startActivity(new Intent(HomeActivity.this, CocktailsActivity.class).putExtra("ID", ""));
			}
		});

		mBtnReviews = (Button) findViewById(R.id.btn_reviews);
		mBtnReviews.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mBtnReviews.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				startActivity(new Intent(HomeActivity.this, ReviewMainActivity.class));
			}
		});

		mBtnFriends = (Button) findViewById(R.id.btn_friends);
		mBtnFriends.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mBtnFriends.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (checkLogin(LOGIN_REQUIED_FOR_CHECKIN))
				{
					startActivity(new Intent(HomeActivity.this, FriendsMainActivity.class));
				}
			}
		});

		mBtnMeetings = (Button) findViewById(R.id.btn_meetings);
		mBtnMeetings.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mBtnMeetings.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (checkLogin(LOGIN_REQUIED_FOR_MEETINGS))
				{
					startActivity(new Intent(HomeActivity.this, MeetingsActivity.class));
				}
			}
		});
		
		mBtnFavorites = (Button) findViewById(R.id.btn_favorites);
		mBtnFavorites.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mBtnFavorites.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (checkLogin(LOGIN_REQUIED_FOR_CHECKIN))
				{
					startActivity(new Intent(HomeActivity.this, FavouritesBarsActivity.class));
					//startActivity(new Intent(HomeActivity.this, PlacesActivity.class));
				}
			}
		});
		
		mBtnActivity = (Button) findViewById(R.id.btn_activity);
		mBtnActivity.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mBtnActivity.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (checkLogin(LOGIN_REQUIED_FOR_FEED))
				{
					startActivity(new Intent(HomeActivity.this, FeedActivity.class));
				}
			}
		});

		mBtnProfile = (Button) findViewById(R.id.btn_profile);
		mBtnProfile.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mBtnProfile.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (checkLogin(LOGIN_REQUIED_FOR_PROFILE))
				{
					startActivity(new Intent(HomeActivity.this, UserProfileActivity.class));
				}
			}
		});

		mTxtFriends = (TextView) findViewById(R.id.txt_friends);
		mTxtFriends.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));

		mTxtMeetings = (TextView) findViewById(R.id.txt_meetings);
		mTxtMeetings.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void checkForceSwitch()
	{
		if (ForceSwitchManager.sharedInstance().isNeedForceSwitch())
		{
			String model = ForceSwitchManager.sharedInstance().getParameter("model");
			assert(model != null);
			
			String id = ForceSwitchManager.sharedInstance().getParameter("id");
			assert(id != null);
			
			if (model.compareToIgnoreCase("drink") == 0)
			{
				Intent intent = new Intent();
				intent.putExtra("ID", id);
				intent.setClass(this, DrinkActivity.class);

				startActivity(intent);
			}
			else if (model.compareToIgnoreCase("cocktail") == 0)
			{
				Intent intent = new Intent();
				intent.putExtra("ID", id);
				intent.putExtra("DRINK_ID", "94");
				intent.setClass(this, CocktailActivity.class);

				startActivity(intent);
			}
			else if (model.compareToIgnoreCase("brand") == 0)
			{
				Intent intent = new Intent();
				intent.putExtra("ID", Integer.parseInt(id));
				intent.setClass(this, BrandActivity.class);
				
				startActivity(intent);
			}
			else if (model.compareToIgnoreCase("bar") == 0)
			{
				Intent intent = new Intent();
				intent.putExtra("ID", id);
				intent.setClass(this, BarActivity.class);
				
				startActivity(intent);
			}
			else if (model.compareToIgnoreCase("blog") == 0)
			{
				Intent intent = new Intent();
				intent.putExtra("ID", id);
				intent.setClass(this, ReviewActivity.class);
				
				startActivity(intent);
			}
			else if (checkLogin(LOGIN_REQUIED_FOR_FORCE_SWITCH))
			{
				if (model.compareToIgnoreCase("rate_bar") == 0)
				{
					String rate = ForceSwitchManager.sharedInstance().getParameter("rate");
					assert(rate != null);
					
					Intent intent = new Intent();
					intent.putExtra("id", id);
					intent.putExtra("type", CommentsQuery.BARS);
					intent.putExtra("like", rate.compareTo("1") == 0);
					intent.setClass(this, AddCommentsActivity.class);
					
					startActivity(intent);
				}
				else if (model.compareToIgnoreCase("user") == 0)
				{
					String suggest = ForceSwitchManager.sharedInstance().getParameter("suggest"); 
					if (suggest != null)
					{
						Intent intent = new Intent();
						intent.setClass(this, FriendsSuggestionsActivity.class);
						
						startActivity(intent);
					}
					else
					{
						String isfriend = ForceSwitchManager.sharedInstance().getParameter("isfriend");
						if (isfriend != null)
						{
							Intent intent = new Intent();
							intent.setClass(this, FriendsActivity.class);
							
							startActivity(intent);
						}
						else
						{
							Intent intent = new Intent();
							intent.putExtra(Constants.KEY_USER_ID, id);
							intent.setClass(this, UserProfileActivity.class);
							
							startActivity(intent);
						}
					}
				}
				else if (model.compareToIgnoreCase("meet") == 0)
				{
					if (id.compareTo("0") == 0)
					{
						Intent intent = new Intent();
						intent.setClass(this, MeetingsActivity.class);
						
						startActivity(intent);
					}
					else
					{
						Intent intent = new Intent();
						intent.putExtra("meeting_id", id);
						intent.setClass(this, MeetingActivity.class);
						
						startActivity(intent);
					}
				}
			}
			else
			{
				// skip force switch reset
				return;
			}
			
			ForceSwitchManager.sharedInstance().resetForceSwitch();
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();

		checkForceSwitch();
		
		if (firstLaunch)
		{
			AppRater.app_launched(this);
			
			firstLaunch = false;
		}
		
		if (appSettings.getEventsFriendsCount() > 0)
		{
			mTxtFriends.setText(Integer.toString(appSettings.getEventsFriendsCount()));
		}
		else
		{
			mTxtFriends.setVisibility(View.INVISIBLE);
		}

		if (appSettings.getEventsMeetingsCount() > 0)
		{
			mTxtMeetings.setText(Integer.toString(appSettings.getEventsMeetingsCount()));
		}
		else
		{
			mTxtMeetings.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == LOGIN_REQUIED_FOR_FORCE_SWITCH)
		{
			if (resultCode == RESULT_OK)
			{
				checkForceSwitch();
			}
			else
			{
				ForceSwitchManager.sharedInstance().resetForceSwitch();
			}
		}
	}
}
