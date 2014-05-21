
package com.drink.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.drink.R;
import com.drink.helpers.Defines;
import com.drink.query.LoginQuery;
import com.drink.query.WebQuery;
import com.drink.twitter.TwitterApp;
import com.drink.twitter.TwitterApp.TwDialogListener;

public class TWLoginActivity extends BasicActivity 
{
	public static final int ID = 0xAA01;
	public static final int SUCCESS = 1;
	public static final int FAIL = -1;

	private boolean login = false;

	private TwitterApp mTwitter;

	private enum FROM 
	{
		TWITTER_POST, TWITTER_LOGIN
	};

	private enum MESSAGE 
	{
		SUCCESS, DUPLICATE, FAILED, CANCELLED
	};

	private void onCompleateRequest()
	{
		if (!login) 
		{
			Intent intent = new Intent();
			intent.putExtra("access_token", mTwitter.getAccessToken().getToken());
			intent.putExtra("user_id", mTwitter.getUserID());
			
			setResult(RESULT_OK, intent);
			finish();
		} 
		else 
		{
			new LoginTask().execute();
		}
	}
	
	public TWLoginActivity() 
	{
		super(R.layout.activity_twlogin);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		login = getIntent().getBooleanExtra("login", false);

		mTwitter = new TwitterApp(this, getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret), mTwLoginDialogListener);
		if (mTwitter.hasAccessToken() == true) 
		{
			onCompleateRequest();
		} 
		else 
		{
			mTwitter.authorize();
		}
	}

	private void postAsToast(FROM twitterPost, MESSAGE success) 
	{
		switch (twitterPost) 
		{
		case TWITTER_LOGIN:
			switch (success) 
			{
			case SUCCESS:
				Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG)	.show();
				break;
			case FAILED:
				Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
			break;
		case TWITTER_POST:
			switch (success) 
			{
			case SUCCESS:
				Toast.makeText(this, "Posted Successfully", Toast.LENGTH_LONG).show();
				break;
			case FAILED:
				Toast.makeText(this, "Posting Failed", Toast.LENGTH_LONG).show();
				break;
			case DUPLICATE:
				Toast.makeText(this, "Posting Failed because of duplicate message...", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
			break;
		}
	}

	private TwDialogListener mTwLoginDialogListener = new TwDialogListener() 
	{
		public void onError(String value) 
		{
			postAsToast(FROM.TWITTER_LOGIN, MESSAGE.FAILED);
			mTwitter.resetAccessToken();

			finish();
		}

		public void onComplete(String value) 
		{
			if (value == null) 
			{
				mTwitter.resetAccessToken();
				
				setResult(RESULT_CANCELED);
				finish();
			} 
			else 
			{
				try 
				{
					onCompleateRequest();
				} 
				catch (Exception e) 
				{
					if (e.getMessage().toString().contains("duplicate")) 
					{
						postAsToast(FROM.TWITTER_POST, MESSAGE.DUPLICATE);
					}
					e.printStackTrace();
					
					setResult(RESULT_CANCELED);
					finish();
				}
			}
		}
	};

	private class LoginTask extends AsyncTask<Void, Void, LoginQuery> 
	{
		@Override
		protected LoginQuery doInBackground(Void... params) 
		{
			LoginQuery loginQuery = new LoginQuery(appSettings, Defines.TYPE_TWITTER, mTwitter.getAccessToken().getToken(), mTwitter.getUserID());
			loginQuery.getResponse(WebQuery.POST);

			return loginQuery;
		}

		@Override
		protected void onPostExecute(LoginQuery query) 
		{
			LoginQuery.Data data = query.getData();
			
			if (data.error == -1) 
			{
				appSettings.setUserAccount(null, null, Integer.toString(Defines.TYPE_TWITTER));
				
				appSettings.setUserToken(data.user_token);
				appSettings.setUserId(data.user_id);
				appSettings.setUserName(data.user_name);
				
				
				appSettings.setConnectFacebook(data.connect_facebook);
				appSettings.setConnectTwitter(data.connect_twitter);

				appSettings.setPublishOnFacebook(data.publish_on_facebook);
				appSettings.setPublishOnTwitter(data.publish_on_twitter);

				appSettings.setEventsFeedCount(data.events_feed);
				appSettings.setEventsFriendsCount(data.events_friends);
				appSettings.setEventsMeetingsCount(data.events_meetings);

				setResult(RESULT_OK);
			} 
			else 
			{
				Intent intent = new Intent();

				intent.putExtra("type", Defines.TYPE_TWITTER);
				intent.putExtra("user_name", mTwitter.getUsername());
				
				intent.putExtra("access_token", mTwitter.getAccessToken().getToken());
				//new value
				intent.putExtra("access_secret", mTwitter.getAccessToken().getTokenSecret());
				intent.putExtra("user_id", mTwitter.getUserID());
				intent.putExtra("email", (String) null);
				intent.putExtra("birthday", (String) null);
				intent.putExtra("gender", (String) null);

				setResult(Defines.RESULT_NEED_REGISTRATION, intent);
			}
			
			finish();

			super.onPostExecute(query);
		}
	}
}