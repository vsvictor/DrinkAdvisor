package com.drink.query;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import com.drink.helpers.Defines;

import android.util.Log;

public abstract class WebQuery {
	protected static String DEVICE_TOKEN = "device_token";
	protected static String USER_TOKEN = "user_token";
	protected static String USER_ID = "id_user";
	protected static String LIST_LIMIT = "limit";
	protected static String LIST_OFFSET = "offset";

	private static final String DEBUG_TAG = "DrinkAdvisor";

	private static final int TIMEOUT = 30000;

	protected URLBuilder mURLBuilder;

	protected JSONObject mJSONResponse;

	protected WebQueryError mError;

	protected List<NameValuePair> nameValuePairs;

	public WebQuery() {
		super();

		mURLBuilder = new URLBuilder();
		nameValuePairs = new ArrayList<NameValuePair>();
	}

	private static void logMsg(String msg) {
		Log.d(DEBUG_TAG, msg);
	}

	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";

	public WebQuery getResponse(String method) {
		String url = mURLBuilder.getURLString();
		String fullQuery = getFullQuery();
		logMsg("WebQuery: QUERY - " + fullQuery);
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		StringBuilder stringBuilder = new StringBuilder("");
		String response = null;

		logMsg("WebQuery: TRY TO GET FROM WEB");

		if (method.equals(GET)) {
			inputStream = getFromWebGet(url);
		} else if (method.equals(POST)) {
			inputStream = getFromWebPost(url);
		} else if (method.equals(PUT)) {
			inputStream = getFromWebPut(url);
		}

		if (inputStream == null)
			return this;

		try {
			inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			int ch;
			while ((ch = inputStreamReader.read()) != -1) {
				stringBuilder.appendCodePoint(ch);
			}
			response = stringBuilder.toString();
			inputStreamReader.close();
			inputStream.close();

			try {
				mJSONResponse = new JSONObject(response);
			} catch (JSONException Ex) {
				try {
					response = "{ \"array\":" + response + "}";
					mJSONResponse = new JSONObject(response);
				} catch (Exception e) {
					setError(Defines.ERROR_JSON, Ex.getMessage(),
							"Internal Error");
					return this;
				}
			}
		} catch (Exception Ex) {
			setError(Defines.ERROR_CONNECTION, Ex.getMessage(),
					"Internal Error");
			return this;
		}

		logMsg("WebQuery: RESPONSE - " + response);

		return this;

	}

	public boolean getResult() {
		if (mJSONResponse == null)
			return false;
		if ((mJSONResponse.opt("error_code") == null)
				&& (mJSONResponse.opt("code") == null))
			return true;

		if (!mJSONResponse.optString("description").equals("")) {
			setError(1, mJSONResponse.optString("description"),
					mJSONResponse.optString("title"));
		} else {
			setError(1, mJSONResponse.optString("message"),
					mJSONResponse.optString("title"));
		}

		return false;
	}

	private InputStream getFromWebGet(String url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url)
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setConnectTimeout(TIMEOUT);
			connection.connect();

			return connection.getInputStream();
		} catch (Exception Ex) {
			setError(Defines.ERROR_CONNECTION, Ex.getMessage(),
					"Internal Error");
		}

		return null;
	}

	private InputStream getFromWebPost(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);

		try {
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);

			for (int i = 0; i < nameValuePairs.size(); i++) {
				/*
				 * if
				 * (nameValuePairs.get(i).getName().equalsIgnoreCase("qqfile"))
				 * { File file = new File(nameValuePairs.get(i).getValue());
				 * FileBody fileBody = new FileBody(file, "image/jpg");
				 * FormBodyPart bodyPart = new FormBodyPart("qqfile", fileBody);
				 * entity.addPart(bodyPart); } else
				 */
				{
					Charset chars = Charset.forName("UTF-8");
					entity.addPart(nameValuePairs.get(i).getName(),
							new StringBody(nameValuePairs.get(i).getValue(),
									chars));
				}
			}

			httpPost.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(httpPost,
					localContext);

			return httpResponse.getEntity().getContent();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	private InputStream getFromWebPut(String url) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpPut httpPost = new HttpPut(url);

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse httpResponse = httpClient.execute(httpPost);

			return httpResponse.getEntity().getContent();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	protected String getFullQuery() {
		StringBuilder sb = new StringBuilder(mURLBuilder.getURLString());

		for (int i = 0; i < nameValuePairs.size(); i++) {
			if (i != 0) {
				sb.append('&');
			}

			sb.append(nameValuePairs.get(i).getName()).append('=')
					.append(nameValuePairs.get(i).getValue());
		}

		return sb.toString();
	}

	protected void setError(int code, String message, String title) {
		if (mError == null) {
			mError = new WebQueryError();
		}

		mError.code = code;
		mError.message = message;
		mError.title = title;
		if (code == Defines.ERROR_CONNECTION) {
			mError.message = "neeed Wi-Fi.";
		}

		logMsg("WebQuery: ERROR - [" + code + "] " + message);
	}

	public abstract WebQueryData getData();

	public WebQueryError getError() {
		if (mError != null) {
			return mError;
		} else if (mJSONResponse == null) {
			setError(Defines.ERROR_UNKNOWN, "Unknown error", "Unknown error");
		} else {
			try {
				setError(mJSONResponse.getInt("errorcode"),
						mJSONResponse.getString("error"),
						mJSONResponse.getString("title"));
			} catch (JSONException Ex) {
				setError(Defines.ERROR_JSON, Ex.getMessage(), "Internal Error");
			}
		}
		return mError;
	}

	protected class URLBuilder {
		public static final String PROTOCOL_HTTP = "http://";
		public static final String DEFAULT_DOMAIN = "www.drinkadvisor.com/api";
		public static final String DEFAULT_PATH = "/index.php";

		private String mProtocol = PROTOCOL_HTTP;
		private String mDomain = DEFAULT_DOMAIN;
		private String mPath = DEFAULT_PATH;

		private ArrayList<Parameter> mParametersList = new ArrayList<Parameter>();

		public void setPath(String path) {
			if (path.startsWith("/") || (path.startsWith("http")))
				mPath = path;
			else
				mPath = "/" + path;
		}

		public String getParameter(String name) {
			for (Parameter p : mParametersList)
				if (p.name.equals(name)) {
					return p.value;
				}

			return "";
		}

		public void setParameter(String name, String value) {
			for (Parameter p : mParametersList)
				if (p.name.equals(name)) {
					p.set(value);
					return;
				}

			mParametersList.add(new Parameter(name, value));
		}

		protected String getURLString() {
			StringBuilder stringBuilder = null;

			if (mPath.startsWith("http")) {
				stringBuilder = new StringBuilder(mPath);
			} else {
				stringBuilder = new StringBuilder(mProtocol);
				stringBuilder.append(mDomain);
				stringBuilder.append(mPath);
			}
			stringBuilder.append("?");
			// Add expires date.

			int cnt = 0;
			for (Parameter p : mParametersList)
				if (!p.value.equals("")) {
					if (cnt != 0)
						stringBuilder.append("&");
					stringBuilder.append(p.name).append("=").append(p.value);
					++cnt;
				}
			Log.d("+++++++", "+++++++");
			Log.d("URL TO SERVER", stringBuilder.toString());
			Log.d("+++++++", "+++++++");

			return stringBuilder.toString();
		}

		@Override
		public String toString() {
			return getURLString();
		}

		private class Parameter {

			public String name;
			public String value;

			public Parameter(String name, String value) {
				super();

				this.name = name;
				set(value);
			}

			public void set(String value) {
				if (value == null) {
					this.value = "";
					return;
				}
				this.value = URLEncoder.encode(value.trim());
			}
		}
	}
}
