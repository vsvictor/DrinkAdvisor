package com.drink.activity;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import com.appsflyer.AppsFlyerLib;
import com.drink.ForceSwitchManager;
import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.query.CityByCoordQuery;
import com.drink.query.ClientsQuery;
import com.drink.query.LangQuery;
import com.drink.query.UserInfoQuery;
import com.drink.query.WebQuery;
import com.drink.settings.AppSettings;
import com.drink.utils.location.IGPSLocationPostProcess;
import com.drink.utils.location.MyLocationService;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class LoadingActivity extends BasicActivity implements IGPSLocationPostProcess
{
	private static final int STATE_GET_LOCATION = 0;
	private static final int STATE_GET_CITY = 1;
	private static final int STATE_GET_LANGUAGE = 2;
	private static final int STATE_GET_CONTENT_TOKEN = 3;
	private static final int STATE_LOGIN = 4;
	private static final int UPDATE_STATE = 5;
	
	private int state = STATE_GET_LOCATION; 
	
	private Location location;
	private TextView mTxtDots;
	private long mStartTime;

	private Timer mTimer = new Timer();
	private Handler mHandler = new Handler();
	
	final Runnable UpdateDotsRunnable = new Runnable() 
	{
	  	private long mDots = 0; 
	
	  	public void run() 
		{
            long timeInMilliseconds = SystemClock.uptimeMillis() - mStartTime;
            timeInMilliseconds /= 500;
            long dots = timeInMilliseconds % 4;
            
            if (mDots != dots)
            {
	            String str = new String();
	            for (int i = 0; i < dots; ++i)
	            {
	            	str += ".";
	            }
	            
				mTxtDots.setText(str);
	            mDots = dots;
            }
	    }
	};
	
	public LoadingActivity() 
	{
		super(R.layout.activity_loading);
	}
	 
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		AppsFlyerLib.sendTracking(getApplicationContext());

		super.onCreate(savedInstanceState);
		
		TextView txtLoading = (TextView) findViewById(R.id.txt_loading);
		txtLoading.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		
		mTxtDots = (TextView) findViewById(R.id.txt_dots);
		mTxtDots.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		mStartTime = SystemClock.uptimeMillis();
		
		Intent intent = getIntent();
		String action = intent.getAction();
		if (action.compareToIgnoreCase("android.intent.action.VIEW") == 0)
		{
			Uri uri = intent.getData();
			if (uri != null)
			{
				if (ForceSwitchManager.sharedInstance().parseURI(uri))
				{
					// switch to main menu
					Intent _intent = new Intent();
					_intent.setAction(Intent.ACTION_MAIN);
					_intent.setClass(LoadingActivity.this, HomeActivity.class);
					startActivity(_intent);
					
					finish();
					return;
				}
			}
		}

		String cacheDir;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) 
		{
			cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.cache_path);
		} 
		else 
		{
			cacheDir = getCacheDir().getAbsolutePath();
		}

// DEBUG ONLY!!! CLEAR CACHE!!!
//		File dir = new File(cacheDir + "/images");
//		String[] children = dir.list();
//		for (int i = 0; i < children.length; i++) 
//		{
//			new File(dir, children[i]).delete();
//		}
// DEBUG ONLY!!! CLEAR CACHE!!!
		
		appSettings.setCacheDirImage(cacheDir + "/images");

		// AppsFlyer
		AppsFlyerLib.setAppsFlyerKey(getString(R.string.apps_flyer_id));
		// Google Analytics
		GoogleAnalytics.getInstance(this).getTracker(getString(R.string.ga_trackingId));
		
		MyLocationService.getInstance(this).checkLocation(this, this);
		
		mTimer.schedule(new TimerTask() 
		{
	         @Override
	         public void run() 
	         {
	        	 mHandler.post(UpdateDotsRunnable);
	         }
	    }, 0, 100);
	}
	
	@Override
	public void onPause()
	{
		mTimer.cancel();
		
		super.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_splash_screen, menu);
		
		return true;
	}

	private void updateState()
	{
		switch (state)
		{
		case STATE_GET_CITY:
			new GetCityTask().execute(location);
			break;
		case STATE_GET_LANGUAGE:
			new LangTask().execute();
			break;
		case STATE_GET_CONTENT_TOKEN:
			new ClientsTask().execute();
			break;
		}
	}
	
	public void showErrorBox() 
	{
		//
		startActivityForResult(new Intent(this, NoConnectionActivity.class),UPDATE_STATE);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == UPDATE_STATE) {
				updateState();
			} 

		}
	}
	
	private class GetCityTask extends QueryTask<Location, Void, CityByCoordQuery> 
	{
		private Location location;
		
		@Override
		protected CityByCoordQuery doInBackground(Location... params) 
		{
			location = params[0];
			
			CityByCoordQuery cityByCoord = new CityByCoordQuery();
			cityByCoord.setCoord(location);
			cityByCoord.getResponse(WebQuery.GET);

			return cityByCoord;
		}

		@Override
		protected void onPostExecute(CityByCoordQuery query) 
		{
			if (query.getResult())
			{
				CityByCoordQuery.Data data = query.getData();
				
				appSettings.setUserCityAndCountry(data.city_id, data.country_id);
				if (data.city_id != -1)
				{
					AppSettings.cityId = data.city_id;
					AppSettings.cityName = data.city_name;
					AppSettings.countryName = data.country_name;
					AppSettings.lat = location.getLatitude();
					AppSettings.lng = location.getLongitude();
					// set sorting by distance
					AppSettings.barsFilter = AppSettings.Filter.By_Distance;
				}
				else
				{
					AppSettings.cityId = -1;
					AppSettings.cityName = "";
					AppSettings.countryName = "";
					AppSettings.lat = 0.0;
					AppSettings.lng = 0.0;
					// set sorting by rating
					AppSettings.barsFilter = AppSettings.Filter.By_Rating;
				}
	
				state = STATE_GET_LANGUAGE;
				new LangTask().execute();
			}
			else
			{
				showErrorBox();
			}
			
			super.onPostExecute(query);
		}
	}

	private class LangTask extends QueryTask<Void, Void, LangQuery> 
	{
		@Override
		protected LangQuery doInBackground(Void... params) 
		{
			LangQuery langQuery = new LangQuery();
			langQuery.getResponse(WebQuery.GET);

			return langQuery;
		}

		private void receiveRegistrationId() 
		{
		    new AsyncTask<Void, Void, String>() 
		    {
		        @Override
		        protected String doInBackground(Void... params) 
		        {
		            try 
		            {
		            	GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(LoadingActivity.this);
		                String regid = gcm.register("527286405815");

		                // save registration id
		                AppSettings appSettings = new AppSettings(LoadingActivity.this);
		                appSettings.setGCMIdentifier(regid);
		                appSettings.setGCMVersion(getAppVersion(LoadingActivity.this));
		                
		                return regid;
		            } 
		            catch (IOException ex) 
		            {
						return "1";
		            }
		        }

		        @Override
		        protected void onPostExecute(String regid) 
		        {
		        	new ClientsTask().execute(regid);
		        }
		    }.execute();
		}
		
		@Override
		protected void onPostExecute(LangQuery query) 
		{
			if (checkResult(query))
			{
				LangQuery.Data data = query.getData();
	
				appSettings.setLANGID(data.string);
				
				state = STATE_GET_CONTENT_TOKEN;
				
				String id = getRegistrationId(LoadingActivity.this);
				if (id.isEmpty())
				{
					if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(LoadingActivity.this) == ConnectionResult.SUCCESS)
					{
						receiveRegistrationId();
					}
					else
					{
						new ClientsTask().execute("1");
					}
				}
				else
				{
					new ClientsTask().execute(id);
				}
			}
			else
			{
				showErrorBox();
			}
			
			super.onPostExecute(query);
		}
	}

	private class ClientsTask extends QueryTask<String, Void, ClientsQuery> 
	{
		@Override
		protected ClientsQuery doInBackground(String... params) 
		{
			String regId = params[0];
			
			Log.i("GCM", regId);
			
			ClientsQuery clientsQuery = new ClientsQuery();
			
			String contentToken = CommonHelper.readContentToken();
			if (contentToken != null)
			{
				clientsQuery.setOldContentToken(contentToken);
			}
			
			clientsQuery.setLangId(appSettings.getLANGId());
			clientsQuery.setDeviceToken(regId);
			clientsQuery.setCityId(String.valueOf(appSettings.getUserCityId()));
			clientsQuery.setCountryId(String.valueOf(appSettings.getUserCountryId()));
			String device_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

			clientsQuery.setDeviceID(device_id);
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);

			clientsQuery.setDeviceType(metrics);
			clientsQuery.getResponse(WebQuery.POST);

			return clientsQuery;
		}

		@Override
		protected void onPostExecute(ClientsQuery query) 
		{
			if (checkResult(query))
			{
				appSettings.setContentToken(query.getData().content_token);
				// save current content token
				CommonHelper.saveContentToken(query.getData().content_token);
	
				state = STATE_LOGIN;

				if (CommonHelper.loadUserToken())
				{
					new CheckLoginTask().execute();
				}
				else
				{
					Intent intent = new Intent();
					intent.setClass(LoadingActivity.this, LoginActivity.class);
					startActivity(intent);
				
					finish();
				}
			}	
			else
			{
				showErrorBox();
			}
			
			super.onPostExecute(query);
		}
	}

	private class CheckLoginTask extends QueryTask<Void, Void, UserInfoQuery> 
	{
		@Override
		protected UserInfoQuery doInBackground(Void... params) 
		{
			UserInfoQuery query = new UserInfoQuery(appSettings);
			query.getResponse(WebQuery.POST);

			return query;
		}

		@Override
		protected void onPostExecute(UserInfoQuery query) 
		{
			if (checkResult(query))
			{
				UserInfoQuery.Data data = query.getData();
				if (data.error == -1)
				{
					appSettings.setUserToken(data.user_token);
					appSettings.setUserId(String.valueOf(data.user_id));
					appSettings.setUserName(data.user_name);
					appSettings.setUserAvatar(data.user_picture);
					
					appSettings.setEventsFeedCount(data.events_feed);
					appSettings.setEventsFriendsCount(data.events_friends);
					appSettings.setEventsMeetingsCount(data.events_meetings);
					
					appSettings.setConnectFacebook(data.connect_facebook);
					appSettings.setConnectTwitter(data.connect_twitter);

					appSettings.setPublishOnFacebook(data.publish_on_facebook);
					appSettings.setPublishOnTwitter(data.publish_on_twitter);

					// switch to main menu
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_MAIN);
					intent.setClass(LoadingActivity.this, HomeActivity.class);
					startActivity(intent);
					
					finish();
					
					super.onPostExecute(query);
					
					return;
				}
			}	
			
			CommonHelper.clearUserToken(LoadingActivity.this);
			
			Intent intent = new Intent();
			intent.setClass(LoadingActivity.this, LoginActivity.class);
			startActivity(intent);
		
			finish();
			
			super.onPostExecute(query);
		}
	}

	@Override
	public void onGPSLocationLoad(Location _location) 
	{
		if (_location == null)
		{
			_location = new Location("geodecoding");
			_location.setLatitude(0.0);
			_location.setLongitude(0.0);
		}
		
		location = _location;
		
		state = STATE_GET_CITY;
		new GetCityTask().execute(location);
	}

	private static int getAppVersion(Context context) 
	{
	    try 
	    {
	        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } 
	    catch (NameNotFoundException e) 
	    {
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	private String getRegistrationId(Context context) 
	{
		AppSettings appSettings = new AppSettings(context);
		String registrationId = appSettings.getGCMIdentifier();
		if (registrationId.isEmpty()) return "";
		
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = appSettings.getGCMVersion();
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) return ""; 

	    return registrationId;
	}
}
