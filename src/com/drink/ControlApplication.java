
package com.drink;

import java.util.Calendar;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class ControlApplication extends Application 
{
	private static ControlApplication sApplication;
	private static Context sContext;
	private static long time = 0;
	private static boolean firstBars = true;
	private static boolean firstBlog = true;
	private static boolean firstBrands = true;
	private static boolean firstCocktails = true;
	private static boolean firstDrinks = true;
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		
		sApplication = this;
		sContext = getApplicationContext();
	}

	static public void onActivityStop(Activity activity)
	{
		FlurryAgent.onEndSession(activity);
		EasyTracker.getInstance(activity).activityStop(activity);

		time = Calendar.getInstance().getTimeInMillis();
	}
	
	static public void onActivityStart(Activity activity)
	{
		FlurryAgent.onStartSession(activity, activity.getString(R.string.flurry_id));
		EasyTracker.getInstance(activity).activityStart(activity);

		boolean first = (Calendar.getInstance().getTimeInMillis() - time > 60000);
		
		if (!firstBars)
		{
			firstBars = first;
		}
		
		if (!firstBlog)
		{
			firstBlog = first;
		}

		if (!firstBrands)
		{
			firstBrands = first;
		}

		if (!firstCocktails)
		{
			firstCocktails = first;
		}

		if (!firstDrinks)
		{
			firstDrinks = first;
		}
	}
	
	public static Context getContext()
	{
		return sContext;
	}
	
	static public boolean isFirstBars()
	{
		return firstBars;
	}
	
	static public void resetFirstBars()
	{
		firstBars = false;
	}
	
	static public boolean isFirstBlog()
	{
		return firstBlog;
	}
	
	static public void resetFirstBlog()
	{
		firstBlog = false;
	}
	
	static public boolean isFirstBrands()
	{
		return firstBrands;
	}
	
	static public void resetFirstBrands()
	{
		firstBrands = false;
	}

	static public boolean isFirstCocktails()
	{
		return firstCocktails;
	}
	
	static public void resetFirstCocktails()
	{
		firstCocktails = false;
	}

	static public boolean isFirstDrinks()
	{
		return firstDrinks;
	}
	
	static public void resetFirstDrinks()
	{
		firstDrinks = false;
	}

	static public ControlApplication getApp()
	{
		return sApplication;
	}
}
