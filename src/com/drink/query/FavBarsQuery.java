package com.drink.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.drink.activity.BarActivity;
import com.drink.activity.BarsActivity;
import com.drink.activity.FavouritesBarsActivity;
import com.drink.db.DataBase;
import com.drink.settings.AppSettings;
import com.drink.types.Bar;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

public class FavBarsQuery extends WebQuery {
	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	private JSONObject jsonObj;
	private JSONParser jsonParser;
	private List<NameValuePair> params;
	private DataBase db;
	private Activity act;
	private String ID = "";
	private String typeVal = "";
	private static String post = "POST";
	private static String del = "DELETE";
	private String delStr;

	public FavBarsQuery(AppSettings appSettings, int cityId, String sort,
			int offset, int limit, FavouritesBarsActivity activity) {
		act = activity;
		jsonParser = new JSONParser();

		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(DEVICE_TOKEN, appSettings
				.getContentToken()));
		params.add(new BasicNameValuePair(USER_TOKEN, appSettings
				.getUserToken()));
		params.add(new BasicNameValuePair("user_id", appSettings.getUserId()));
	}

	public FavBarsQuery(AppSettings appSettings, int cityId,
			BarActivity activity, String id, String type) {
		act = activity;
		ID = id;
		jsonParser = new JSONParser();

		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(DEVICE_TOKEN, appSettings
				.getContentToken()));
		params.add(new BasicNameValuePair(USER_TOKEN, appSettings
				.getUserToken()));
		params.add(new BasicNameValuePair("user_id", appSettings.getUserId()));
		params.add(new BasicNameValuePair("bar_id", id));
		typeVal = type;
		delStr = DEVICE_TOKEN + "=" + appSettings.getContentToken() + "&"
				+ USER_TOKEN + "=" + appSettings.getUserToken() + "&user_id="
				+ appSettings.getUserId() + "&bar_id=" + id;

	}
	public FavBarsQuery(AppSettings appSettings, int cityId,
			BarsActivity activity, String id, String type) {
		act = activity;
		ID = id;
		jsonParser = new JSONParser();

		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(DEVICE_TOKEN, appSettings
				.getContentToken()));
		params.add(new BasicNameValuePair(USER_TOKEN, appSettings
				.getUserToken()));
		params.add(new BasicNameValuePair("user_id", appSettings.getUserId()));
		params.add(new BasicNameValuePair("bar_id", id));
		typeVal = type;
		delStr = DEVICE_TOKEN + "=" + appSettings.getContentToken() + "&"
				+ USER_TOKEN + "=" + appSettings.getUserToken() + "&user_id="
				+ appSettings.getUserId() + "&bar_id=" + id;

	}
	public FavBarsQuery(AppSettings appSettings, int cityId,
			FavouritesBarsActivity activity, String id, String type) {
		act = activity;
		ID = id;
		jsonParser = new JSONParser();

		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(DEVICE_TOKEN, appSettings
				.getContentToken()));
		params.add(new BasicNameValuePair(USER_TOKEN, appSettings
				.getUserToken()));
		params.add(new BasicNameValuePair("user_id", appSettings.getUserId()));
		params.add(new BasicNameValuePair("bar_id", id));
		typeVal = type;
		delStr = DEVICE_TOKEN + "=" + appSettings.getContentToken() + "&"
				+ USER_TOKEN + "=" + appSettings.getUserToken() + "&user_id="
				+ appSettings.getUserId() + "&bar_id=" + id;

	}

	public void setCoord(Location loc) {

		params.add(new BasicNameValuePair("lat", String.valueOf(loc
				.getLatitude())));
		params.add(new BasicNameValuePair("long", String.valueOf(loc
				.getLongitude())));

	}

	/*
	 * public void setRadius(String string) { mURLBuilder.setParameter("radius",
	 * string); }
	 * 
	 * public void setCacheId(String string) {
	 * mURLBuilder.setParameter("cache_id", "1"); }
	 */
	@Override
	public Data getData() {
		Data data = new Data();
		data.bars = new ArrayList<Bar>();

		if (ID.length() > 0 && typeVal.length() > 0) {

			if (typeVal.equalsIgnoreCase(post)) {

				try {
					String path_url = mURLBuilder.PROTOCOL_HTTP
							+ mURLBuilder.DEFAULT_DOMAIN + "/userBarFavorites";

					jsonObj = jsonParser.makeHttpRequest(path_url, "POST",
							params);

				} catch (IOException e) {

					e.printStackTrace();
				}

				if (jsonObj != null && jsonObj.opt("error_code") == null
						&& jsonObj.opt("code") == null) {
					if(jsonObj.has("success"))
					{
						data.success = true;
					}
				}

			}
			if (typeVal.equalsIgnoreCase(del)) {
				try {
					String path_url = mURLBuilder.PROTOCOL_HTTP
							+ mURLBuilder.DEFAULT_DOMAIN + "/userBarFavorites/rem";

					jsonObj = jsonParser.makeHttpRequest(path_url, "POST",
							params);

				} catch (IOException e) {

					e.printStackTrace();
				}

				if (jsonObj != null && jsonObj.opt("error_code") == null
						&& jsonObj.opt("code") == null) {
					if(jsonObj.has("success"))
					{
						data.success = true;
					}
					
				
				}

			}

		}

		else {
			String path_url = mURLBuilder.PROTOCOL_HTTP
					+ mURLBuilder.DEFAULT_DOMAIN + "/userBarFavorites/sync";

			try {
				jsonObj = jsonParser.makeHttpRequest(path_url, "POST", params);

			} catch (IOException e) {

				e.printStackTrace();
			}

			if (jsonObj != null && jsonObj.opt("error_code") == null
					&& jsonObj.opt("code") == null) {
				try {
					db = new DataBase(act);
					JSONArray barsArray = jsonObj.getJSONArray("bars");
					for (int i = 0; i < barsArray.length(); ++i) {
						JSONObject obj = (JSONObject) barsArray.get(i);

						Bar bar = new Bar();
						bar.id = obj.optInt("id", -1);
						bar.title = obj.optString("title", "null");
						bar.rating = obj.optInt("rating", 0);
						bar.dist = obj.optDouble("dist", 0);
						bar.picture = obj.optString("picture");
						bar.picture_addr = obj.optString("picture");
						bar.lat = obj.optString("latitude");
						bar.lon = obj.optString("longitude");
						bar.name = obj.optString("name");
						bar.info = obj.optString("common_info");
						bar.city_id = obj.optInt("city_id");
						bar.city = obj.optString("city");
						bar.address = obj.optString("address");
						bar.phone = obj.optString("phone");
						bar.timezone_id = obj.optString("timezone_id");
						JSONObject workingTime = obj
								.getJSONObject("work_hours");
						bar.workingTimeList1.add(workingTime
								.optString("working_time_day_sun"));
						bar.workingTimeList1.add(workingTime
								.optString("working_time_day_mon"));
						bar.workingTimeList1.add(workingTime
								.optString("working_time_day_tue"));
						bar.workingTimeList1.add(workingTime
								.optString("working_time_day_wed"));
						bar.workingTimeList1.add(workingTime
								.optString("working_time_day_thu"));
						bar.workingTimeList1.add(workingTime
								.optString("working_time_day_fri"));
						bar.workingTimeList1.add(workingTime
								.optString("working_time_day_sat"));
						db.addNewBar(bar);
						data.bars.add(bar);
					}
					db.close();
				} catch (Exception e) {
					Log.e("error", e.toString());
				}
			}

		}

		return data;
	}

	public Bar getNewbar() {
		return new Bar();
	}

	public class Data extends WebQueryData {
		public int total = 0;
		public boolean success = false;
		public ArrayList<Bar> bars;
	}

	public class JSONParser {
		public JSONParser() {

		}

		public JSONObject makeHttpRequest(String url, String method,
				List<NameValuePair> params) throws IOException {

			try {
				if (method == "DELETE") {
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpDelete httpDel = new HttpDelete(url);

					httpDel.setHeader(HTTP.CONTENT_TYPE, "text/xml");
					HttpResponse httpResponse = httpClient.execute(httpDel);
					HttpEntity httpEntity = httpResponse.getEntity();
					is = httpEntity.getContent();

				}

				if (method == "POST") {
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(url);
					httpPost.setEntity(new UrlEncodedFormEntity(params));
					HttpResponse httpResponse = httpClient.execute(httpPost);
					HttpEntity httpEntity = httpResponse.getEntity();
					is = httpEntity.getContent();

				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (Exception ex) {
				Log.d("Networking", ex.getLocalizedMessage());
				throw new IOException("Error connecting");
			}

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "utf-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				json = sb.toString();
				is.close();
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}

			try {
				jObj = null;

				jObj = new JSONObject(json);
			} catch (JSONException e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}

			return jObj;

		}
	}

}
