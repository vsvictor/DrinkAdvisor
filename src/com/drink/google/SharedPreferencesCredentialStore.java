package com.drink.google;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;

/**
 * Created with IntelliJ IDEA.
 * User: AS
 * Date: 14.01.13
 * Time: 1:52
 * To change this template use File | Settings | File Templates.
 */

public class SharedPreferencesCredentialStore {
	
	public static final String CLIENT_ID = "527286405815-bkf91kro42pbfja5ducc5on91t4g7n4p.apps.googleusercontent.com";
	public static final String CLIENT_SECRET = "";
	
	
	public static final String SCOPE = "https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile";
	public static final String REDIRECT_URI = "http://localhost";

	private static final String ACCESS_TOKEN = "access_token";
	private static final String EXPIRATION_TIME = "token_expiration_perion";
	private static final String REFRESH_TOKEN = "refresh_token";
	private static final String SCOPE_STRING = "scope";

	private SharedPreferences prefs;

	private static SharedPreferencesCredentialStore store;

	private SharedPreferencesCredentialStore(SharedPreferences prefs) {
		this.prefs = prefs;
	}

	public static SharedPreferencesCredentialStore getInstance(SharedPreferences prefs) {
		if (store == null)
			store = new SharedPreferencesCredentialStore(prefs);

		return store;
	}

	public AccessTokenResponse read() {
		AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
		accessTokenResponse.accessToken = prefs.getString(ACCESS_TOKEN, "");
		accessTokenResponse.expiresIn = prefs.getLong(EXPIRATION_TIME, 0);
		accessTokenResponse.refreshToken = prefs.getString(REFRESH_TOKEN, "");
		accessTokenResponse.scope = prefs.getString(SCOPE_STRING, "");
		return accessTokenResponse;
	}

	public void write(AccessTokenResponse accessTokenResponse) {
		Editor editor = prefs.edit();
		editor.putString(ACCESS_TOKEN, accessTokenResponse.accessToken);
		editor.putLong(EXPIRATION_TIME, accessTokenResponse.expiresIn);
		editor.putString(REFRESH_TOKEN, accessTokenResponse.refreshToken);
		editor.putString(SCOPE_STRING, accessTokenResponse.scope);
		editor.commit();
	}

	public void clearCredentials() {
		Editor editor = prefs.edit();
		editor.remove(ACCESS_TOKEN);
		editor.remove(EXPIRATION_TIME);
		editor.remove(REFRESH_TOKEN);
		editor.remove(SCOPE_STRING);
		editor.commit();
	}
}
