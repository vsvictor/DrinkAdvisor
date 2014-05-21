package com.drink.helpers;

import java.util.HashMap;
import java.util.Map;

import com.appsflyer.AppsFlyerLib;
import com.drink.R;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.GoogleAnalytics;

import android.content.Context;

public class StatisticsHelper 
{
	static public void sendBarsEvent(Context context)
	{
		sendAppsFlyerEvent(context, "BarListView");
		sendFlurryEvent(context, "BarListView");
		sendGoogleAnalyticsEvent(context, "BarListView");
	}
	
	static public void sendBarEvent(Context context, String id, String name)
	{
		sendAppsFlyerEvent(context, "OneBarView_" + id);
		sendFlurryEvent(context, "OneBarView", "id", id, "name", name);
		sendGoogleAnalyticsEvent(context, "OneBarView", "id", id, "name", name);
	}
	
	static public void sendReviewsEvent(Context context)
	{
		sendAppsFlyerEvent(context, "ReviewsListView");
		sendFlurryEvent(context, "ReviewsListView");
		sendGoogleAnalyticsEvent(context, "ReviewsListView");
	}
	
	static public void sendReviewEvent(Context context, String id, String name)
	{
		sendAppsFlyerEvent(context, "OneReviewView_" + id);
		sendFlurryEvent(context, "OneReviewView", "id", id, "name", name);
		sendGoogleAnalyticsEvent(context, "OneReviewView", "id", id, "name", name);
	}
	
	static public void sendBrandsEvent(Context context)
	{
		sendAppsFlyerEvent(context, "BrandsListView");
		sendFlurryEvent(context, "BrandsListView");
		sendGoogleAnalyticsEvent(context, "BrandsListView");
	}
	
	static public void sendBrandEvent(Context context, String id, String name)
	{
		sendAppsFlyerEvent(context, "OneBrandView_" + id);
		sendFlurryEvent(context, "OneBrandView", "id", id, "name", name);
		sendGoogleAnalyticsEvent(context, "OneBrandView", "id", id, "name", name);
	}
	
	static public void sendCheckinEvent(Context context, String bar_id)
	{
		sendAppsFlyerEvent(context, "CrCheckInBar_" + bar_id);
		sendFlurryEvent(context, "CrCheckInBar", "Bar_id", bar_id);
		sendGoogleAnalyticsEvent(context, "CrCheckInBar", "Bar_id", bar_id);
	}

	static public void sendMeetingEvent(Context context, String bar_id)
	{
		sendAppsFlyerEvent(context, "CrMeetingInBar_" + bar_id);
		sendFlurryEvent(context, "CrMeetingInBar", "Bar_id", bar_id);
		sendGoogleAnalyticsEvent(context, "CrMeetingInBar", "Bar_id", bar_id);
	}

	static public void sendRegisterEvent(Context context, int type)
	{
		switch (type)
		{
		case Defines.TYPE_FACEBOOK:
			sendAppsFlyerEvent(context, "FacebookRegistration");
			sendFlurryEvent(context, "FacebookRegistration");
			sendGoogleAnalyticsEvent(context, "FacebookRegistration");
			break;
		case Defines.TYPE_TWITTER:
			sendAppsFlyerEvent(context, "TwitterRegistration");
			sendFlurryEvent(context, "TwitterRegistration");
			sendGoogleAnalyticsEvent(context, "TwitterRegistration");
			break;
		case Defines.TYPE_GOOGLE:
			sendAppsFlyerEvent(context, "GoogleRegistration");
			sendFlurryEvent(context, "GoogleRegistration");
			sendGoogleAnalyticsEvent(context, "GoogleRegistration");
			break;
		case Defines.TYPE_USUAL:
			sendAppsFlyerEvent(context, "UsualRegistration");
			sendFlurryEvent(context, "UsualRegistration");
			sendGoogleAnalyticsEvent(context, "UsualRegistration");
			break;
		}
	}

	static public void sendCocktailsEvent(Context context)
	{
		sendAppsFlyerEvent(context, "CoctailListView");
		sendFlurryEvent(context, "CoctailListView");
		sendGoogleAnalyticsEvent(context, "CoctailListView");
	}
	
	static public void sendCocktailEvent(Context context, String id, String name)
	{
		sendAppsFlyerEvent(context, "OneCoctailView_" + id);
		sendFlurryEvent(context, "OneCoctailView", "id", id, "name", name);
		sendGoogleAnalyticsEvent(context, "OneCoctailView", "id", id, "name", name);
	}

	static public void sendDrinksEvent(Context context)
	{
		sendAppsFlyerEvent(context, "DrinkListView");
		sendFlurryEvent(context, "DrinkListView");
		sendGoogleAnalyticsEvent(context, "DrinkListView");
	}
	
	static public void sendDrinkEvent(Context context, String id, String name)
	{
		sendAppsFlyerEvent(context, "OneDrinkView_" + id);
		sendFlurryEvent(context, "OneDrinkView", "id", id, "name", name);
		sendGoogleAnalyticsEvent(context, "OneDrinkView", "id", id, "name", name);
	}

	static private void sendAppsFlyerEvent(Context context, String event)
	{
		AppsFlyerLib.sendTrackingWithEvent(context, context.getString(R.string.apps_flyer_id), event, "USD0.99");
	}

	static private void sendFlurryEvent(Context context, String event, String... params)
	{
		if (params.length == 0)
		{
			FlurryAgent.logEvent(event);
		}
		else
		{
			Map<String, String> val = new HashMap<String, String>();
			for (int i = 0; i < params.length; i = i + 2)
			{
				val.put(params[i], params[i + 1]);
			}
			
			FlurryAgent.logEvent(event, val);
		}
	}

	static private void sendGoogleAnalyticsEvent(Context context, String event, String... params)
	{
		Map<String, String> val = new HashMap<String, String>();
		val.put("event", event);
		for (int i = 0; i < params.length; i = i + 2)
		{
			val.put(params[i], params[i + 1]);
		}

		GoogleAnalytics.getInstance(context).getTracker(context.getString(R.string.ga_trackingId)).send(val);
	}
}
