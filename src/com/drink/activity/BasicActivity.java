package com.drink.activity;

import com.drink.ControlApplication;
import com.drink.R;
import com.drink.helpers.BlurAnimation;
import com.drink.helpers.CommonHelper;
import com.drink.helpers.Defines;
import com.drink.helpers.TurnAnimation;
import com.drink.imageloader.ImagesStore;
import com.drink.imageloader.WebImageView2;
import com.drink.query.SNLoginQuery;
import com.drink.query.WebQuery;
import com.drink.settings.AppSettings;
import com.drink.utils.location.LocationService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public abstract class BasicActivity extends FragmentActivity 
{
	static final int LOGIN_REQUIED_FOR_CHECKIN 			= 1000;
	static final int LOGIN_REQUIED_FOR_MEETINGS 		= 1001;
	static final int LOGIN_REQUIED_FOR_FEED 			= 1002;
	static final int LOGIN_REQUIED_FOR_FRIENDS 			= 1003;
	static final int LOGIN_REQUIED_FOR_PROFILE 			= 1004;
	static final int LOGIN_REQUIED_FOR_CREATE_MEETING 	= 1005;
	static final int LOGIN_REQUIED_FOR_FORCE_SWITCH 	= 1006;
	static final int LOGIN_REQUIED_FOR_MENU 			= 1007;
	static final int REQUEST_BAR 						= 1008;
	static final int CHANGE_CITY						= 1009;

	static final int RESULT_INTERNAL_CANCEL = 1;
	
	static protected final int CONNECT_FACEBOOK = 100;
	static protected final int CONNECT_TWITTER = 101;

	private static final float MENU_SHOW_TIME = 0.40f;
	private static final float MENU_HIDE_TIME = 0.25f;
	private static final float MENU_ANGLE =0;//180.0f / 5.0f;
	private static final float MENU_SCALE = 20.0f;
	
	// menu
	private TextView tv_user_name;
	private TextView tv_friends_count;
	private TextView tv_meetings_count;
	private WebImageView2 avatar;
	private LinearLayout ll_user_data;
	private Button btn_login;

	// filter
	private TextView tv_city;
	private TextView tv_view_list;
	private TextView tv_view_map;
	private TextView tv_filter_by_distance;
	private TextView tv_filter_by_rating;
	private TextView tv_opened_now;
	private TextView tv_show_all;
	private SearchView search;
	
	private enum State
	{
		Showed, Hiden, Showing, Hiding
	};
	
	private State mStateMenu = State.Hiden;
	private State mStateFilter = State.Hiden;
	
	protected LayoutInflater inflater;
	
	protected AppSettings appSettings;
	protected String userToken = "";

	protected ImagesStore mImagesStore;
	private ProgressBar mProgress;
	protected TextView	mCaption;

	RelativeLayout mRoot;
	RelativeLayout mMenu;
	RelativeLayout mFilter;
	View mContent;
	
	int mContentID = -1;
	
	protected boolean menu = true;
	protected boolean back = true;
	protected boolean cancel = false;
	protected boolean filter = true;
	
	protected int cityId = -1;
	protected String cityName;
	protected String countryName;
	protected double cityLat;
	protected double cityLng;
	
	protected BasicActivity(int id)
	{
		super();
		
		mContentID = id;
		
		menu = true;
		back = true;
		cancel = false;
		filter = false;
	}
	
	protected BasicActivity(int id, boolean back)
	{
		super();

		mContentID = id;
		
		this.menu = true;
		this.back = back;
		this.cancel = false;
		this.filter = false;
	}
	
	protected BasicActivity(int id, boolean back, boolean cancel)
	{
		super();

		mContentID = id;
		
		this.menu = true;
		this.back = back;
		this.cancel = cancel;
		this.filter = false;
	}
	
	protected BasicActivity(int id, boolean menu, boolean back, boolean cancel, boolean filter)
	{
		super();

		mContentID = id;
		
		this.menu = menu;
		this.back = back;
		this.cancel = cancel;
		this.filter = filter;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		appSettings = new AppSettings(getApplicationContext());
		
		userToken = appSettings.getUserToken();

		mImagesStore = new ImagesStore(appSettings.getCacheDirImage());

		// create root layout
		mRoot = new RelativeLayout(this);

		// create content view
		mContent = inflater.inflate(mContentID, null);
		mContent.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mContent.setOnTouchListener(new OnTouchListener() 
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				return true;
			}
		});
		mRoot.addView(mContent);

		if (cancel)
		{
			// cancel button
			Button btnCancel = new Button(this);
			btnCancel.setBackgroundResource(R.drawable.img_navigation_button);
			btnCancel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
			btnCancel.setTextSize(15.0f);
			btnCancel.setText(R.string.cancel);
			btnCancel.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					setResult(RESULT_INTERNAL_CANCEL);
					finish();
				}
			});
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			btnCancel.setLayoutParams(params);
			
			try 
			{
			    XmlResourceParser parser = getResources().getXml(R.color.button_login);
			    ColorStateList colors = ColorStateList.createFromXml(getResources(), parser);
			    btnCancel.setTextColor(colors);
			} 
			catch (Exception e) {}
			
			RelativeLayout rlNavigationBottom = (RelativeLayout) mContent.findViewById(R.id.rl_navigation_bottom);
			rlNavigationBottom.addView(btnCancel);
		}
		
		if (menu)
		{
			// create menu
			mMenu = (RelativeLayout) inflater.inflate(R.layout.activity_menu, null);
			mMenu.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mMenu.setVisibility(View.GONE);
			mMenu.setOnTouchListener(new OnTouchListener() 
			{
				@Override
				public boolean onTouch(View v, MotionEvent event) 
				{
					return true;
				}
			});
			mRoot.addView(mMenu);
	
			LinearLayout llHide= (LinearLayout) mMenu.findViewById(R.id.ll_hide);
			llHide.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					hideMenu();
				}
			});
			
			// connect menu
			connectMenu();
		}
		
		if (filter)
		{
			// create menu
			mFilter = (RelativeLayout) inflater.inflate(R.layout.activity_filter, null);
			mFilter.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mFilter.setVisibility(View.GONE);
			mFilter.setOnTouchListener(new OnTouchListener() 
			{
				@Override
				public boolean onTouch(View v, MotionEvent event) 
				{
					return true;
				}
			});
			mRoot.addView(mFilter);
	
			LinearLayout llHide= (LinearLayout) mFilter.findViewById(R.id.ll_hide);
			llHide.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					hideFilter();
				}
			});
			
			// connect filter
			connectFilter();
		}

		// create progress bar
		mProgress = new ProgressBar(this);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mProgress.setLayoutParams(params);
		mProgress.setVisibility(View.GONE);
		mRoot.addView(mProgress);

		setContentView(mRoot);
		
		Button btnBack = (Button) findViewById(R.id.btn_back);
		if (btnBack != null)
		{
			if (back)
			{
				btnBack.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
				btnBack.setOnClickListener(new OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
							onBackPressed();
						}
					});
			}
			else
			{
				btnBack.setVisibility(View.INVISIBLE);
			}
		}
		
		ImageButton btnMenu = (ImageButton) findViewById(R.id.btn_menu);
		if (btnMenu != null)
		{
			btnMenu.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					showMenu();
				}
			});
		}

		mCaption = (TextView) findViewById(R.id.tv_caption);
		if (mCaption != null)
		{
			mCaption.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
			mCaption.setText("");
		}
	}

	@Override
	public void onBackPressed() 
	{
		if (mStateMenu == State.Showed) 
		{
			hideMenu();
		} 
		else if (mStateFilter == State.Showed)
		{
			hideFilter();
		}
		else 
		{
			super.onBackPressed();
		}
	}

	protected void showMenu()
	{
		if (!menu) return;
		if (mStateMenu != State.Hiden) return;
        
        updateMenu();
		
		mRoot.setBackgroundResource(R.drawable.img_background_right);
		mStateMenu = State.Showing;
		
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		
		float k = (float) size.x / (float) size.y;
        TurnAnimation rAnim = new TurnAnimation(MENU_ANGLE,  -k,  -k * 0.1f, k * 0.7f, true, true);
        rAnim.setStartOffset(0);
        rAnim.setDuration((int) (MENU_SHOW_TIME * 1000.0));
        rAnim.setFillAfter(true);
        rAnim.setFillEnabled(true);
        rAnim.setAnimationListener(new AnimationListener() 
        {
			@Override
			public void onAnimationStart(Animation animation) 
			{
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) 
			{
			}
			
			@Override
			public void onAnimationEnd(Animation animation) 
			{
				mStateMenu = State.Showed;
			}
		});
        mContent.startAnimation(rAnim);
        
        BlurAnimation bAnim = new BlurAnimation(mMenu, MENU_SCALE, true);
        bAnim.setStartOffset(0);
        bAnim.setDuration((int) (MENU_SHOW_TIME * 1000.0));
        bAnim.setFillAfter(true);
        bAnim.setFillEnabled(true);
        mMenu.startAnimation(bAnim);
        
        mMenu.setVisibility(View.VISIBLE);
	}
	
	protected void hideMenu()
	{
		if (!menu) return;
		if (mStateMenu != State.Showed) return;
		
		mStateMenu = State.Hiding;
		
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		
		float k = (float) size.x / (float) size.y;
        TurnAnimation rAnim = new TurnAnimation(MENU_ANGLE, -k, -k * 0.4f, k * 0.5f, true, false);
        rAnim.setStartOffset(0);
        rAnim.setDuration((int) (MENU_HIDE_TIME * 1000.0));
        rAnim.setFillAfter(true);
        rAnim.setFillEnabled(true);
        rAnim.setAnimationListener(new AnimationListener() 
        {
			@Override
			public void onAnimationStart(Animation animation) 
			{
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) 
			{
			}
			
			@Override
			public void onAnimationEnd(Animation animation) 
			{
				mStateMenu = State.Hiden;
				
				mMenu.setVisibility(View.GONE);
				
				mContent.clearAnimation();
				mMenu.clearAnimation();
			}
		});
        mContent.startAnimation(rAnim);
        
        BlurAnimation bAnim = new BlurAnimation(mMenu, MENU_SCALE, false);
        bAnim.setStartOffset(0);
        bAnim.setDuration((int) (MENU_HIDE_TIME * 1000.0));
        bAnim.setFillAfter(true);
        bAnim.setFillEnabled(true);
        mMenu.startAnimation(bAnim);
	}
	

	protected void showFilter()
	{
		if (!filter) return;
		if (mStateFilter != State.Hiden) return;
	
		updateFilter();
		
		mRoot.setBackgroundResource(R.drawable.img_background_left);
		mStateFilter = State.Showing;
		
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		
		float k = (float) size.x / (float) size.y;
        TurnAnimation rAnim = new TurnAnimation(MENU_ANGLE, -k, -k * -0.2f, -k * 0.7f, false, true);
        rAnim.setStartOffset(0);
        rAnim.setDuration((int) (MENU_SHOW_TIME * 1000.0));
        rAnim.setFillAfter(true);
        rAnim.setFillEnabled(true);
        rAnim.setAnimationListener(new AnimationListener() 
        {
			@Override
			public void onAnimationStart(Animation animation) 
			{
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) 
			{
			}
			
			@Override
			public void onAnimationEnd(Animation animation) 
			{
				mStateFilter = State.Showed;
			}
		});
        mContent.startAnimation(rAnim);
        
        BlurAnimation bAnim = new BlurAnimation(mFilter, MENU_SCALE, true);
        bAnim.setStartOffset(0);
        bAnim.setDuration((int) (MENU_SHOW_TIME * 1000.0));
        bAnim.setFillAfter(true);
        bAnim.setFillEnabled(true);
        mFilter.startAnimation(bAnim);
        
        mFilter.setVisibility(View.VISIBLE);
	}
	
	protected void hideFilter()
	{
		if (!filter) return;
		if (mStateFilter != State.Showed) return;
		
		mStateFilter = State.Hiding;
		
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		
		float k = (float) size.x / (float) size.y;
        TurnAnimation rAnim = new TurnAnimation(MENU_ANGLE, -k, -k * 0.4f, -k * 0.5f, false, false);
        rAnim.setStartOffset(0);
        rAnim.setDuration((int) (MENU_HIDE_TIME * 1000.0));
        rAnim.setFillAfter(true);
        rAnim.setFillEnabled(true);
        rAnim.setAnimationListener(new AnimationListener() 
        {
			@Override
			public void onAnimationStart(Animation animation) 
			{
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) 
			{
			}
			
			@Override
			public void onAnimationEnd(Animation animation) 
			{
				mStateFilter = State.Hiden;
				
				mFilter.setVisibility(View.GONE);
				
				mContent.clearAnimation();
				mFilter.clearAnimation();
			}
		});
        mContent.startAnimation(rAnim);
        
        BlurAnimation bAnim = new BlurAnimation(mFilter, MENU_SCALE, false);
        bAnim.setStartOffset(0);
        bAnim.setDuration((int) (MENU_HIDE_TIME * 1000.0));
        bAnim.setFillAfter(true);
        bAnim.setFillEnabled(true);
        mFilter.startAnimation(bAnim);
	}
	
	protected void showProgressBar()
	{
		if (mProgress == null) return;

		mProgress.setVisibility(View.VISIBLE);
	}	
	protected void hideProgressBar()
	{
		if (mProgress == null) return;
		
		mProgress.setVisibility(View.GONE);
	}
	
	protected boolean checkInternetConnection()
	{
		if (!CommonHelper.checkInternetConnection())
		{
			startActivity(new Intent(this, NoConnectionActivity.class));
			
			//showErrorBox(Defines.ERROR_CONNECTION, false);
			return false;
		}
		
		return true;
	}
	

	boolean checkLogin(int code)
	{
		if (appSettings.getUserToken().length() > 0) return true; 
		
		startActivityForResult(new Intent(this, LoginReqquiredActivity.class), code);
		
		return false;
	}
	
	boolean checkConnectFacebook()
	{
		if (appSettings.getConnectFacebook()) return true;

		startActivityForResult(new Intent(this, FBLoginActivity.class).putExtra("login", false), CONNECT_FACEBOOK);
		
		return false;
	}
	
	boolean checkConnectTwitter()
	{
		if (appSettings.getConnectTwitter()) return true;
		
		startActivityForResult(new Intent(this, TWLoginActivity.class).putExtra("login", false), CONNECT_TWITTER);
		
		return true;
	}
	
	protected void showErrorBox(String message, boolean finish)
	{
		CommonHelper.showMessageBox(this, getString(R.string.error_dialog_title), message, finish);
	}
	
	protected void showErrorBox(int code, boolean finish)
	{
		String message = getString(R.string.err_other);
		
		switch (code)
		{
		case Defines.ERROR_CONNECTION:
			
			message = getString(R.string.err_connection);
			break;
		case Defines.ERROR_JSON:
			message = getString(R.string.err_json);
			break;
		case Defines.ERROR_BAD_NAME:
			message = getString(R.string.err_bad_name);
			break;
		case Defines.ERROR_MISSING_NAME:
			message = getString(R.string.err_missing_name);
			break;
		case Defines.ERROR_BAD_LOGIN:
			message = getString(R.string.err_bad_login);
			break;
		case Defines.ERROR_MISSING_LOGIN:
			message = getString(R.string.err_missing_login);
			break;
		case Defines.ERROR_BAD_EMAIL:
			message = getString(R.string.err_bad_email);
			break;
		case Defines.ERROR_MISSING_EMAIL:
			message = getString(R.string.err_missing_email);
			break;
		case Defines.ERROR_BAD_PASSWORD:
			message = getString(R.string.err_bad_password);
			break;
		case Defines.ERROR_TOO_SHORT_PASSWORD:
			message = getString(R.string.err_too_short_password);
			break;
		}

		showErrorBox(message, finish);
	}
	
	protected void showAuthDialog() 
	{
		AlertDialog.Builder dialog = new AlertDialog.Builder(BasicActivity.this);
		dialog.setTitle(getString(R.string.dlg_autorization_text));
		dialog.setPositiveButton(getText(R.string.dlg_btn_ok), new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					Intent intent = new Intent();
					intent.setClass(BasicActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			});
		dialog.setNegativeButton(getText(R.string.dlg_btn_cancel), new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.cancel();
				}
			});
		dialog.show();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
	
		if (resultCode == RESULT_OK)
		{
			switch (requestCode) 
			{
			case CONNECT_FACEBOOK: 
			{
				String accessToken = data.getStringExtra("access_token");
				String userId = data.getStringExtra("user_id");

				new SNLoginTask("facebook", accessToken, userId).execute();
				
				break;
			}
			case CONNECT_TWITTER: 
			{
				String accessToken = data.getStringExtra("access_token");
				String userId = data.getStringExtra("user_id");

				new SNLoginTask("twitter", accessToken, userId).execute();
				
				break;
			}
			case LOGIN_REQUIED_FOR_CHECKIN:
				startActivity(new Intent(this, FavouritesBarsActivity.class));
				break;
			case LOGIN_REQUIED_FOR_MEETINGS:
				startActivity(new Intent(this, MeetingsActivity.class));
				break;
			case LOGIN_REQUIED_FOR_FEED:
				startActivity(new Intent(this, FeedActivity.class));
				break;
			case LOGIN_REQUIED_FOR_FRIENDS:
				startActivity(new Intent(this, FriendsMainActivity.class));
				break;
			case LOGIN_REQUIED_FOR_PROFILE:
				startActivity(new Intent(this, UserProfileActivity.class));
				break;
			case LOGIN_REQUIED_FOR_CREATE_MEETING:
				{
					Intent intent = new Intent(this, BarsActivity.class);
					intent.putExtra("select_bar", "");
	
					startActivityForResult(intent, REQUEST_BAR);
				}
				break;
			case LOGIN_REQUIED_FOR_MENU:
				hideMenu();
				break;
			case CHANGE_CITY:
				cityId = data.getIntExtra("id", -1);
				cityName = data.getStringExtra("name");
				countryName = data.getStringExtra("country");
				cityLat = data.getDoubleExtra("lat",  0.0);
				cityLng = data.getDoubleExtra("lng", 0.0);

				tv_city.setText(cityName + ", " + countryName);
				
				if (appSettings.getUserCityId() != cityId)
				{
					tv_filter_by_distance.setVisibility(View.GONE);
					tv_filter_by_rating.setSelected(true);
					
					AppSettings.barsFilter = AppSettings.Filter.By_Rating;
				}
				else
				{
					tv_filter_by_distance.setVisibility(View.VISIBLE);
					tv_filter_by_distance.setSelected(AppSettings.barsFilter == AppSettings.Filter.By_Distance);
					tv_filter_by_rating.setSelected(AppSettings.barsFilter == AppSettings.Filter.By_Rating);
				}

				break;
			case REQUEST_BAR:
				{
					Intent intent = new Intent(this, CreateMeetingInfoActivity.class);
					intent.putExtra("ID", data.getStringExtra("bar_id"));
					
					startActivity(intent);
				}
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		
		ControlApplication.onActivityStop(this);
		LocationService.getInstance(this).stopGPSUpdate();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		ControlApplication.onActivityStart(this);
		LocationService.getInstance(this).startGPSUpdate();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		com.facebook.AppEventsLogger.activateApp(this, getString(R.string.app_id));
	}
	
	private class SNLoginTask extends QueryTask<Void, Void, SNLoginQuery>
	{
		private String type;
		private String accessToken;
		private String userId;
		
		public SNLoginTask(String type, String accessToken, String userId)
		{
			super();
			
			this.type = type;
			this.accessToken = accessToken;
			this.userId = userId;
		}

		@Override
		protected SNLoginQuery doInBackground(Void... arg0) 
		{
			SNLoginQuery query = new SNLoginQuery(appSettings, type, accessToken, userId);
			query.getResponse(WebQuery.GET);
			
			return query;
		}
		
		@Override
		protected void onPostExecute(SNLoginQuery query) 
		{
			if (query.getData().success)
			{
				if (type.equals("facebook"))
				{
					appSettings.setConnectFacebook(true);
				}
				else if (type.equals("twitter"))
				{
					appSettings.setConnectTwitter(true);
				}
			}
			else
			{
				showErrorBox(getString(R.string.SN_connect_error), false);
			}
			
			super.onPostExecute(query);
		}
	}

	protected abstract class QueryTask<P, S, Q> extends AsyncTask<P, S, Q>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			showProgressBar();
		}

		@Override
		protected void onPostExecute(Q result) 
		{
			hideProgressBar();
			
			super.onPostExecute(result);
		}
	
		boolean checkResult(WebQuery query)
		{
			return checkResult(query, false);
		}
		
		boolean checkResult(WebQuery query, boolean finish)
		{
			if (!query.getResult())
				
			{
				startActivity(new Intent(BasicActivity.this, NoConnectionActivity.class));
				//showErrorBox(Defines.ERROR_CONNECTION, finish);

				return false;
			}
			
			return true;
		}

		boolean checkResult2(WebQuery query)
		{
			if (!query.getResult())
			{
				showErrorBox(query.getError().message, false);

				return false;
			}
			
			return true;
		}
	}

	private void connectMenu() 
	{
		// MAIN MENU
		TextView tv_menu = (TextView) mMenu.findViewById(R.id.tv_main_menu);
		tv_menu.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		// Bars
		TextView tv_bars = (TextView) mMenu.findViewById(R.id.tv_bars);
		tv_bars.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_bars.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					startActivity(new Intent(BasicActivity.this, BarsActivity.class));
				}
			});
		
		// Drinks
		TextView tv_drinks = (TextView) mMenu.findViewById(R.id.tv_drinks);
		tv_drinks.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_drinks.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					startActivity(new Intent(BasicActivity.this, DrinksActivity.class));
				}
			});
		
		// Cocktails
		TextView tv_cocktails = (TextView) mMenu.findViewById(R.id.tv_cocktails);
		tv_cocktails.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_cocktails.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					startActivity(new Intent(BasicActivity.this, CocktailsActivity.class).putExtra("ID", ""));
				}
			});
		
		// Reviews
		TextView tv_reviews = (TextView) mMenu.findViewById(R.id.tv_reviews);
		tv_reviews.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_reviews.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					startActivity(new Intent(BasicActivity.this,  ReviewMainActivity.class));
					finish();
				}
			});

		// Home
		TextView tv_home = (TextView) mMenu.findViewById(R.id.tv_home);
		tv_home.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_home.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					startActivity(new Intent(BasicActivity.this, HomeActivity.class));
					finish();
				}
			});

		// PRIVATE SECTIONS
		TextView tv_personal_sections = (TextView) mMenu.findViewById(R.id.tv_private_sections);
		tv_personal_sections.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		// Friends
		TextView tv_friends = (TextView) mMenu.findViewById(R.id.tv_friends);
		tv_friends.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_friends_count = (TextView) mMenu.findViewById(R.id.tv_friends_count);
		tv_friends_count.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		int eventCount = appSettings.getEventsFriendsCount();
		if (eventCount > 0) 
		{
			tv_friends_count.setText(String.valueOf(eventCount));
		} 
		else
		{
			tv_friends_count.setVisibility(View.INVISIBLE);
		}
		
		tv_friends.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					if (checkLogin(LOGIN_REQUIED_FOR_FRIENDS))
					{
						startActivity(new Intent(BasicActivity.this, FriendsMainActivity.class));
					}
				}
			});
		
		// Meetings
		TextView tv_meetings = (TextView) mMenu.findViewById(R.id.tv_meetings);
		tv_meetings.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_meetings_count = (TextView) mMenu.findViewById(R.id.tv_meetings_count);
		tv_meetings_count.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		eventCount = appSettings.getEventsMeetingsCount();
		if (eventCount > 0) 
		{
			tv_meetings_count.setText(String.valueOf(eventCount));
		}
		else
		{
			tv_meetings_count.setVisibility(View.INVISIBLE);
		}
		
		tv_meetings.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					if (checkLogin(LOGIN_REQUIED_FOR_MEETINGS))
					{
						startActivity(new Intent(BasicActivity.this, MeetingsActivity.class));
					}
				}
			});
		
		// Favorites
		TextView tv_favorites = (TextView) mMenu.findViewById(R.id.tv_favorites);
		tv_favorites.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_favorites.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (checkLogin(LOGIN_REQUIED_FOR_CHECKIN))
				{
					startActivity(new Intent(BasicActivity.this, FavouritesBarsActivity.class));
					//startActivity(new Intent(HomeActivity.this, PlacesActivity.class));
				}
			}
		});
		
		TextView tv_settings = (TextView) mMenu.findViewById(R.id.tv_settings);
		tv_settings.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_settings.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (checkLogin(LOGIN_REQUIED_FOR_FEED))
				{
					startActivity(new Intent(BasicActivity.this,  FeedActivity.class));
					//startActivity(new Intent(HomeActivity.this, PlacesActivity.class));
				}
			}
		});
		
		
		// ACTIONS
		TextView tv_actions = (TextView) mMenu.findViewById(R.id.tv_actions);
		tv_actions.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		// create meeting
		TextView tv_meeting_add = (TextView) mMenu.findViewById(R.id.tv_create_meeting);
		tv_meeting_add.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_meeting_add.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					if (checkLogin(LOGIN_REQUIED_FOR_CREATE_MEETING))
					{
						Intent intent = new Intent();
						intent.putExtra("select_bar", "");
						intent.setClass(BasicActivity.this, CreateMeetingInfoActivity.class);

						startActivityForResult(intent, REQUEST_BAR);
					}
				}
			});

		ll_user_data = (LinearLayout) mMenu.findViewById(R.id.ll_user_data);
		
		// user avatar
		avatar = (WebImageView2) ll_user_data.findViewById(R.id.avatar);
		avatar.setCircle(true);
		avatar.setImagesStore(mImagesStore);
		avatar.setCacheDir(appSettings.getCacheDirImage());
		avatar.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (checkLogin(LOGIN_REQUIED_FOR_PROFILE))
				{
					startActivity(new Intent(BasicActivity.this, UserProfileActivity.class));
				}
			}
		});
		// user name
		tv_user_name = (TextView) ll_user_data.findViewById(R.id.tv_user_name);
		tv_user_name.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		tv_user_name.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (checkLogin(LOGIN_REQUIED_FOR_PROFILE))
				{
					startActivity(new Intent(BasicActivity.this, UserProfileActivity.class));
				}
			}
		});
		btn_login = (Button) mMenu.findViewById(R.id.btn_login);
		btn_login.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		btn_login.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				startActivityForResult(new Intent(BasicActivity.this, LoginReqquiredActivity.class), LOGIN_REQUIED_FOR_MENU);			}
		});
	}

	protected void updateMenu()
	{
		if (CommonHelper.isAuthorized(appSettings))
		{
			avatar.setImageURL(appSettings.getUserAvatar(), "123");
			tv_user_name.setText(appSettings.getUserName());
			
			ll_user_data.setVisibility(View.VISIBLE);
			btn_login.setVisibility(View.INVISIBLE);
		}
		else
		{
			ll_user_data.setVisibility(View.INVISIBLE);
			btn_login.setVisibility(View.VISIBLE);
		}
	}


	private void connectFilter() 
	{
		search = (SearchView)  mFilter.findViewById(R.id.search);
		
		Button btn_recommend = (Button) mFilter.findViewById(R.id.btn_recommend);
		btn_recommend.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btn_recommend.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				startActivity(new Intent(BasicActivity.this, RecommendBarActivity.class));
			}
		});

		// City
		tv_city = (TextView) mFilter.findViewById(R.id.tv_city);
		tv_city.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_city.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent(BasicActivity.this, FilterCitiesActivity.class);
					intent.putExtra("mode", FilterCitiesActivity.MODE_ChangeCity);
					startActivityForResult(intent, CHANGE_CITY);
				}
			});

		// View
		TextView tv_view = (TextView) mFilter.findViewById(R.id.tv_view);
		tv_view.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		// List
		tv_view_list = (TextView) mFilter.findViewById(R.id.tv_bars_list);
		tv_view_list.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_view_list.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					tv_view_list.setSelected(true);
					tv_view_map.setSelected(false);
				}
			});
		
		// Map
		tv_view_map = (TextView) mFilter.findViewById(R.id.tv_bars_map);
		tv_view_map.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_view_map.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					tv_view_map.setSelected(true);
					tv_view_list.setSelected(false);
				}
			});
		
		// Filter
		TextView tv_filter = (TextView) mFilter.findViewById(R.id.tv_filter);
		tv_filter.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		// By distance
		tv_filter_by_distance = (TextView) mFilter.findViewById(R.id.tv_filter_by_distance);
		tv_filter_by_distance.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_filter_by_distance.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					tv_filter_by_distance.setSelected(true);
					tv_filter_by_rating.setSelected(false);
				}
			});
		
		// By rating
		tv_filter_by_rating = (TextView) mFilter.findViewById(R.id.tv_filter_by_rating);
		tv_filter_by_rating.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_filter_by_rating.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					tv_filter_by_rating.setSelected(true);
					tv_filter_by_distance.setSelected(false);
				}
			});

		// Working time
		TextView tv_working_time = (TextView) mFilter.findViewById(R.id.tv_working_time);
		tv_working_time.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		// Opened now
		tv_opened_now = (TextView) mFilter.findViewById(R.id.tv_opened_now);
		tv_opened_now.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_opened_now.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					tv_opened_now.setSelected(true);
					tv_show_all.setSelected(false);
				}
			});

		// Show all
		tv_show_all = (TextView) mFilter.findViewById(R.id.tv_show_all);
		tv_show_all.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv_show_all.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					tv_show_all.setSelected(true);
					tv_opened_now.setSelected(false);
				}
			});

		Button btn_apply = (Button) mFilter.findViewById(R.id.btn_apply);
		btn_apply.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btn_apply.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				AppSettings.cityId = cityId;
				AppSettings.cityName = cityName;
				AppSettings.countryName = countryName;
				AppSettings.lat = cityLat;
				AppSettings.lng = cityLng;
				
				if (search.getQuery().length() > 0)
				{
					AppSettings.filter = search.getQuery().toString();
				}
				
				AppSettings.barsView = (tv_view_list.isSelected() ? AppSettings.View.List : AppSettings.View.Map);
				AppSettings.barsFilter = (tv_filter_by_distance.isSelected() ? AppSettings.Filter.By_Distance : AppSettings.Filter.By_Rating);
				AppSettings.barsWorkingTime = (tv_opened_now.isSelected() ? AppSettings.WorkingTime.Opened_Now : AppSettings.WorkingTime.Show_All);
				
				onApplyFilter();

				hideFilter();
			}
		});
	}
	
	protected void updateFilter()
	{
		search.setQuery("", false);
		tv_city.setText(cityName + ", " + countryName);
		
		tv_view_list.setSelected(AppSettings.barsView == AppSettings.View.List);
		tv_view_map.setSelected(AppSettings.barsView == AppSettings.View.Map);
		if ((appSettings.getUserCityId() == -1) || (appSettings.getUserCityId() != AppSettings.cityId))
		{
			tv_filter_by_distance.setVisibility(View.GONE);
			tv_filter_by_rating.setSelected(true);
			
			AppSettings.barsFilter = AppSettings.Filter.By_Rating;
		}
		else
		{
			tv_filter_by_distance.setVisibility(View.VISIBLE);
			tv_filter_by_distance.setSelected(AppSettings.barsFilter == AppSettings.Filter.By_Distance);
			tv_filter_by_rating.setSelected(AppSettings.barsFilter == AppSettings.Filter.By_Rating);
		}
		tv_opened_now.setSelected(AppSettings.barsWorkingTime == AppSettings.WorkingTime.Opened_Now);
		tv_show_all.setSelected(AppSettings.barsWorkingTime == AppSettings.WorkingTime.Show_All);
	}

	protected void onApplyFilter()
	{
	}

	protected static int getWidth(Activity acty) {
		// 74 dp
		int h = 0;
		float k = acty.getResources().getDisplayMetrics().scaledDensity;
		float header = 74 * k;
		if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 13) {
			Display display = acty.getWindowManager().getDefaultDisplay();
			double height = display.getHeight();
			h = (int) Math.floor(height);
		} else {

			Display display = acty.getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			double height = size.y;
			h = (int) Math.floor(height);

		}

		h = (int) ((h - header) / (3 * k));
		return h;
	}
}
