package com.drink.settings;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings 
{
	public enum View 
	{
		List, Map
	};

	public enum Filter 
	{
		By_Distance, By_Rating
	}

	public enum WorkingTime 
	{
		Opened_Now, Show_All
	}

	private static String PREF_FILE_NAME = "appsettings";
	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;

	public static View barsView = View.List;
	public static Filter barsFilter = Filter.By_Rating;
	public static WorkingTime barsWorkingTime = WorkingTime.Opened_Now;
	public static int cityId;
	public static String cityName;
	public static String countryName;
	public static double lat;
	public static double lng;
	public static String filter;

	public AppSettings(Context context) 
	{
		mPreferences = context.getSharedPreferences(PREF_FILE_NAME, Activity.MODE_PRIVATE);
		mEditor = mPreferences.edit();
	}

	public void clear() 
	{
		mEditor.clear().commit();
	}

	public void clearAuthData() {
		setUserAccount("", "", "");
		setUserAvatar("");
		setUserName("");
		setUserToken("");
		setUserId("");

		setConnectFacebook(false);
		setConnectTwitter(false);

		setPublishOnFacebook(false);
		setPublishOnTwitter(false);
	}

	// AUTH
	private static String USER_LOGIN = "USER_LOGIN";
	private static String USER_PASS = "USER_PASS";

	public String getLogin() {
		return mPreferences.getString(USER_LOGIN, null);
	}

	public String getPass() {
		return mPreferences.getString(USER_PASS, null);
	}

	public void setUserAccount(String login, String pass, String type) {
		mEditor.putString("AUTH_TYPE", type);
		if (login != null) {
			mEditor.putString(USER_LOGIN, login);
		}
		if (pass != null) {
			mEditor.putString(USER_PASS, pass);
		}

		mEditor.commit();
	}

	// user name
	private static String USER_NAME = "USER_NAME";

	public void setUserName(String userName) {
		mEditor.putString(USER_NAME, userName);
		mEditor.commit();
	}

	public String getUserName() {
		return mPreferences.getString(USER_NAME, "");
	}

	// user avatar
	private static String USER_AVATAR = "USER_AVATAR";

	public void setUserAvatar(String userAvatar) 
	{
		mEditor.putString(USER_AVATAR, userAvatar);
		mEditor.commit();
	}

	public String getUserAvatar() 
	{
		return mPreferences.getString(USER_AVATAR, "");
	}
	
	// social networks connection settings
	private static String CONNECT_FACEBOOK = "CONNECT_FACEBOOK";
	private static String CONNECT_TWITTER = "CONNECT_TWITTER";

	public boolean getConnectFacebook() {
		return mPreferences.getBoolean(CONNECT_FACEBOOK, false);
	}

	public void setConnectFacebook(boolean val) {
		mEditor.putBoolean(CONNECT_FACEBOOK, val);
		mEditor.commit();
	}

	public boolean getConnectTwitter() {
		return mPreferences.getBoolean(CONNECT_TWITTER, false);
	}

	public void setConnectTwitter(boolean val) {
		mEditor.putBoolean(CONNECT_TWITTER, val);
		mEditor.commit();
	}

	// social networks publishing settings
	private static String PUBLISH_ON_FACEBOOK = "PUBLISH_ON_FACEBOOK";
	private static String PUBLISH_ON_TWITTER = "PUBLISH_ON_TWITTER";

	public boolean getPublishOnFacebook() {
		return mPreferences.getBoolean(PUBLISH_ON_FACEBOOK, false);
	}

	public void setPublishOnFacebook(boolean val) {
		mEditor.putBoolean(PUBLISH_ON_FACEBOOK, val);
		mEditor.commit();
	}

	public boolean getPublishOnTwitter() {
		return mPreferences.getBoolean(PUBLISH_ON_TWITTER, false);
	}

	public void setPublishOnTwitter(boolean val) {
		mEditor.putBoolean(PUBLISH_ON_TWITTER, val);
		mEditor.commit();
	}

	// user city and country
	private static String CITY_ID = "CITY_ID";
	private static String COUNTRY_ID = "COUNTRY_ID";

	public int getUserCityId() {
		return mPreferences.getInt(CITY_ID, -1);
	}

	public int getUserCountryId() {
		return mPreferences.getInt(COUNTRY_ID, -1);
	}

	public void setUserCityAndCountry(int city, int country) {
		mEditor.putInt(CITY_ID, city);
		mEditor.putInt(COUNTRY_ID, country);
		mEditor.commit();
	}

	// user language
	private static String LANG_ID = "LANG_ID";

	public String getLANGId() {
		return mPreferences.getString(LANG_ID, "-1");
	}

	public void setLANGID(String id) {
		mEditor.putString(LANG_ID, id);
		mEditor.commit();
	}

	// content token
	private static String CONTENT_TOKEN = "CONTENT_TOKEN";

	public String getContentToken() {
		return mPreferences.getString(CONTENT_TOKEN, null);
	}

	public void setContentToken(String token) {
		mEditor.putString(CONTENT_TOKEN, token);
		mEditor.commit();
	}

	// user info
	private static String USER_TOKEN = "USER_TOKEN";
	private static String USER_SECRET = "USER_SECRET";
	private static String USER_ID = "USER_ID";
	private static String USER_EMAIL = "USER_EMAIL";

	public String getUserToken() {
		return mPreferences.getString(USER_TOKEN, null);
	}

	public void setUserToken(String token) {
		mEditor.putString(USER_TOKEN, token);
		mEditor.commit();
	}

	// new method for twitter
	public void setUserSecret(String user_secret) {

		mEditor.putString(USER_SECRET, user_secret);
		mEditor.commit();

	}

	public String getUserSecret() {
		return mPreferences.getString(USER_SECRET, null);
	}

	public String getUserId() {
		return mPreferences.getString(USER_ID, null);
	}

	public void setUserId(String id) {
		mEditor.putString(USER_ID, id);
		mEditor.commit();
	}

	public String getUserEmail() {
		return mPreferences.getString(USER_EMAIL, "");
	}

	public void setUserEmail(String email) {
		mEditor.putString(USER_EMAIL, email);
		mEditor.commit();
	}

	// cache images directory
	private static String CACHE_IMAGES = "CACHE_IMAGES";

	public String getCacheDirImage() {
		return mPreferences.getString(CACHE_IMAGES, "-1");
	}

	public void setCacheDirImage(String cacheImages) {
		mEditor.putString(CACHE_IMAGES, cacheImages);
		mEditor.commit();
	}

	// events count
	private static String EVENTS_FEED = "EVENTS_FEED";
	private static String EVENTS_MEETINGS = "EVENTS_MEETINGS";
	private static String EVENTS_FRIENDS = "EVENTS_FRIENDS";
	private static String REVIEW_ID = "REVIEW_ID";

	public int getEventsFeedCount() {
		return mPreferences.getInt(EVENTS_FEED, 0);
	}

	public void setEventsFeedCount(int count) {
		mEditor.putInt(EVENTS_FEED, count);
		mEditor.commit();
	}

	public int getEventsFriendsCount() {
		return mPreferences.getInt(EVENTS_FRIENDS, 0);
	}

	public void setEventsFriendsCount(int count) {
		mEditor.putInt(EVENTS_FRIENDS, count);
		mEditor.commit();
	}

	//
	public boolean getReviewsSaved(String id) {
		String reviewArr = mPreferences.getString(REVIEW_ID, "");
		return reviewArr.contains("\"reviewid:" + id + "\"");

	}

	public void setReviewsSavedId(String id) {
		try {
			JSONArray jsonArray = new JSONArray(mPreferences.getString(
					REVIEW_ID, "[]"));
			mEditor.putString(REVIEW_ID, "");
			mEditor.commit();
			jsonArray.put("reviewid:" + id);
			mEditor.putString(REVIEW_ID, jsonArray.toString());
			mEditor.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//
	public int getEventsMeetingsCount() {
		return mPreferences.getInt(EVENTS_MEETINGS, 0);
	}

	public void setEventsMeetingsCount(int count) {
		mEditor.putInt(EVENTS_MEETINGS, count);
		mEditor.commit();
	}

	// GCM identifier
	private static String GCM_ID = "GCM_ID";
	private static String GCM_VERSION = "GCM_VERSION";

	public String getGCMIdentifier() {
		return mPreferences.getString(GCM_ID, "");
	}

	public void setGCMIdentifier(String id) {
		mEditor.putString(GCM_ID, id);
		mEditor.commit();
	}

	public int getGCMVersion() {
		return mPreferences.getInt(GCM_VERSION, -1);
	}

	public void setGCMVersion(int version) {
		mEditor.putInt(GCM_VERSION, version);
		mEditor.commit();
	}

}
