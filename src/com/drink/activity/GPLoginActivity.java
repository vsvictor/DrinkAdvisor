package com.drink.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.drink.google.SharedPreferencesCredentialStore;
import com.drink.helpers.Defines;
import com.drink.query.LoginQuery;
import com.drink.query.WebQuery;
import com.drink.settings.AppSettings;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

public class GPLoginActivity extends Activity 
{
	private AppSettings appSettings;

	private SharedPreferences prefs;
	private ProgressDialog mProgressDlg;

	private String accessToken;
	private String userId;
	private String userName;
	private String userEmail;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	
		appSettings = new AppSettings(getApplicationContext());
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		mProgressDlg = new ProgressDialog(this);
		mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onResume() 
	{
		super.onResume();
	
		WebView webview = new WebView(this);
		webview.setVisibility(View.VISIBLE);
		webview.getSettings().setJavaScriptEnabled(true);
		setContentView(webview);

		String googleAuthorizationRequestUrl = new GoogleAuthorizationRequestUrl(
				SharedPreferencesCredentialStore.CLIENT_ID,
				SharedPreferencesCredentialStore.REDIRECT_URI,
				SharedPreferencesCredentialStore.SCOPE).build();

		webview.setWebViewClient(new WebViewClient() 
			{
				@Override
				public void onPageFinished(WebView view, String url) 
				{
					if (url.startsWith(SharedPreferencesCredentialStore.REDIRECT_URI)) 
					{
						if (url.indexOf("code=") != -1) 
						{
							view.setVisibility(View.INVISIBLE);
							new LoadToken().execute(url);
						} 
						else if (url.indexOf("error=") != -1) 
						{
							view.setVisibility(View.INVISIBLE);
							SharedPreferencesCredentialStore.getInstance(prefs).clearCredentials();
							finish();
						}
	
					}
				}
			});

		webview.loadUrl(googleAuthorizationRequestUrl);
	}

	protected void onPause() 
	{
		super.onPause();
	
		if (mProgressDlg.isShowing())
		{
			mProgressDlg.dismiss();
		}
	}

	private class LoginTask extends AsyncTask<Void, Void, LoginQuery> 
	{
		@Override
		protected LoginQuery doInBackground(Void... params) 
		{
			LoginQuery query = new LoginQuery(appSettings, Defines.TYPE_GOOGLE, accessToken, userId);
			query.getResponse(WebQuery.POST);

			return query;
		}

		@Override
		protected void onPostExecute(LoginQuery query) 
		{
			mProgressDlg.dismiss();
			
			LoginQuery.Data data = query.getData();
			
			if (data.error == -1) 
			{
				appSettings.setUserAccount(null, null, Integer.toString(Defines.TYPE_GOOGLE));
				
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

				intent.putExtra("type", Defines.TYPE_GOOGLE);
				intent.putExtra("user_name", userName);
				intent.putExtra("access_token", accessToken);
				intent.putExtra("user_id", userId);
				intent.putExtra("email", userEmail);
				intent.putExtra("birthday", (String) null);
				intent.putExtra("gender", (String) null);
				
				setResult(Defines.RESULT_NEED_REGISTRATION, intent);
			}

			finish();
			
			super.onPostExecute(query);
		}
	}

	class LoadToken extends AsyncTask<String, Void, Void> 
	{
	    private String getParameter(String jsonResponse, String parameter) throws JSONException 
	    {
	        JSONObject profile = new JSONObject(jsonResponse);
	        return profile.getString(parameter);
	    }

	    private String readResponse(InputStream is) throws IOException 
	    {
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        byte[] data = new byte[2048];
	        int len = 0;
	        while ((len = is.read(data, 0, data.length)) >= 0) 
	        {
	            bos.write(data, 0, len);
	        }
	        return new String(bos.toByteArray(), "UTF-8");
	    }

	    @Override
		protected Void doInBackground(String... params) 
		{
			String url = params[0];
			try 
			{
				String code = url.substring(SharedPreferencesCredentialStore.REDIRECT_URI.length() + 7, url.length());

				AccessTokenResponse accessTokenResponse = new GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant(
						new NetHttpTransport(), new JacksonFactory(),
						SharedPreferencesCredentialStore.CLIENT_ID,
						SharedPreferencesCredentialStore.CLIENT_SECRET, code,
						SharedPreferencesCredentialStore.REDIRECT_URI).execute();

				SharedPreferencesCredentialStore credentialStore = SharedPreferencesCredentialStore.getInstance(prefs);
				credentialStore.write(accessTokenResponse);
				
				accessToken = accessTokenResponse.accessToken;

		        URL _url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);
				
		        HttpURLConnection con = (HttpURLConnection) _url.openConnection();
		        int sc = con.getResponseCode();
		        if (sc == 200) 
		        {
		          InputStream is = con.getInputStream();
		          String responce = readResponse(is);
		          
		          userId = getParameter(responce, "id");
		          userName = getParameter(responce, "name");
		          userEmail = getParameter(responce, "email");
		          
		          is.close();
		        } 
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) 
		{
			if ((accessToken == null) || (userId == null))
			{
				setResult(RESULT_CANCELED);
				finish();
			}
			else
			{
				mProgressDlg.setMessage("Wait ...");
				mProgressDlg.show();
	
				new LoginTask().execute();
			}

			super.onPostExecute(result);
		}
	}
}
