package com.drink.query;

import java.security.NoSuchAlgorithmException;

import org.apache.http.message.BasicNameValuePair;

import com.drink.helpers.SHA1Helper;

public class RegQuery extends WebQuery {
	public static String USUAL = "usual";
	public static String FACEBOOK = "facebook";
	public static String TWITTER = "twitter";
	public static String GOOGLE = "google";

	public RegQuery() {
		mURLBuilder.setPath("registration/create");
	}

	public void setTypeReg(String string) {
		nameValuePairs.add(new BasicNameValuePair("reg_type", string));
	}

	public void setAccessToken(String string) {
		nameValuePairs
				.add(new BasicNameValuePair("social_access_token", string));
	}

	// new method
	public void setAccessSecret(String access_secret) {
		nameValuePairs.add(new BasicNameValuePair("social_access_secret",
				access_secret));

	}

	public void setSocialIdentifier(String string) {
		nameValuePairs.add(new BasicNameValuePair("social_identifier", string));
	}

	public void setPhotoUrl(String string) {
		nameValuePairs.add(new BasicNameValuePair("picture", string));
	}

	public void setName(String string) {
		nameValuePairs.add(new BasicNameValuePair("name", string));
	}

	public void setPassword(String string) {
		String sha1 = "";
		try {
			sha1 = SHA1Helper.sha1(string);
		} catch (NoSuchAlgorithmException e) {
		}

		nameValuePairs.add(new BasicNameValuePair("password", sha1));
	}

	public void setMail(String string) {
		nameValuePairs.add(new BasicNameValuePair("email", string));
	}

	public void setIDCity(String string) {
		nameValuePairs.add(new BasicNameValuePair("id_city", string));
	}

	public void setSex(String string) {
		nameValuePairs.add(new BasicNameValuePair("sex", string));
	}

	public void setDateBirth(String string) {
		nameValuePairs.add(new BasicNameValuePair("date_birth", string));
	}

	public void setDeviceToken(String string) {
		nameValuePairs.add(new BasicNameValuePair("device_token", string));
	}

	@Override
	public Data getData() {
		Data data = new Data();
		if (getResult()) {
			try {
				data.user_token = mJSONResponse.optString("token");
				if (mJSONResponse.has("user_secret")) {
					data.user_secret = mJSONResponse.optString("user_secret");
				} else
					data.user_secret = "";
				data.user_id = mJSONResponse.optString("user_id");
			} catch (Exception e) {
			}
		}

		return data;
	}

	public class Data extends WebQueryData {
		public String user_token;
		public String user_secret;
		public String user_id;
	}

}
