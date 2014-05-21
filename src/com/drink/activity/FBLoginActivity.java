package com.drink.activity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import com.drink.R;
import com.drink.helpers.Defines;
import com.drink.query.LoginQuery;
import com.drink.query.WebQuery;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class FBLoginActivity extends BasicActivity {
	private GraphUser userData;
	String access_token;
	boolean login = false;
	Session mCurrentSession = null;

	private StatusCallback statusCallback = new StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state == SessionState.CLOSED_LOGIN_FAILED) {
				setResult(RESULT_CANCELED);
				finish();
			} else if (session.isOpened()) {
				Request.executeMeRequestAsync(mCurrentSession,
						new Request.GraphUserCallback() {
							@Override
							public void onCompleted(GraphUser user,
									Response response) {
								onCompleteRequest(user);
							}
						});
			}
		}
	};

	private void onCompleteRequest(GraphUser user) {
		access_token = mCurrentSession.getAccessToken();
		userData = user;

		if (access_token != null) {
			if (!login) {
				Intent intent = new Intent();
				intent.putExtra("access_token", access_token);
				intent.putExtra("user_id", userData.getId());

				setResult(RESULT_OK, intent);
				finish();
			} else {

				new LoginTask().execute();
			}
		} else {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	public FBLoginActivity() {
		super(R.layout.activity_fblogin);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		login = getIntent().getBooleanExtra("login", false);

		List<String> permissions = new ArrayList<String>();
		permissions.add("email");
		permissions.add("user_birthday");
		permissions.add("user_about_me");
		permissions.add("user_hometown");
		permissions.add("user_website");

		try {
			PackageInfo info = getPackageManager().getPackageInfo("com.drink",
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
		} catch (NoSuchAlgorithmException e) {
		}

		mCurrentSession = new Session.Builder(getBaseContext())
				.setApplicationId(getString(R.string.app_id)).build();
		Session.setActiveSession(mCurrentSession);

		if (!mCurrentSession.isOpened()) {
			Session.OpenRequest openRequest = new Session.OpenRequest(
					FBLoginActivity.this);
			openRequest.setDefaultAudience(SessionDefaultAudience.FRIENDS);
			openRequest.setPermissions(permissions);
			openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
			openRequest.setCallback(statusCallback);
			mCurrentSession.openForRead(openRequest);
		} else {
			Request.executeMeRequestAsync(mCurrentSession,
					new Request.GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							onCompleteRequest(user);
						}
					});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		mCurrentSession.onActivityResult(this, requestCode, resultCode, data);

	}

	private class LoginTask extends QueryTask<GraphUser, Void, LoginQuery> {
		@Override
		protected LoginQuery doInBackground(GraphUser... params) {
			LoginQuery loginQuery = new LoginQuery(appSettings,
					Defines.TYPE_FACEBOOK, access_token, userData.getId());
			loginQuery.getResponse(WebQuery.POST);

			return loginQuery;
		}

		@Override
		protected void onPostExecute(LoginQuery query) {
			LoginQuery.Data data = query.getData();

			if (data.error == -1) {
				appSettings.setUserAccount(null, null,
						Integer.toString(Defines.TYPE_FACEBOOK));

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
			} else {
				Intent intent = new Intent();

				intent.putExtra("type", Defines.TYPE_FACEBOOK);
				intent.putExtra("user_name", userData.getName());
				intent.putExtra("access_token", access_token);
				intent.putExtra("user_id", userData.getId());
				intent.putExtra("email",
						userData.getProperty("email") != null ? userData
								.getProperty("email").toString() : "");

				SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy",
						Locale.getDefault());
				Date birthday = Calendar.getInstance().getTime();
				try {
					birthday = sdf.parse((String) userData
							.getProperty("birthday"));
				} catch (ParseException e) {
					e.printStackTrace();
				}

				intent.putExtra("birthday", birthday.getTime());
				intent.putExtra("gender", userData.getProperty("gender")
						.toString());

				setResult(Defines.RESULT_NEED_REGISTRATION, intent);
			}

			finish();

			super.onPostExecute(query);
		}
	}
}
