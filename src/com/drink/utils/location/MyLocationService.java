package com.drink.utils.location;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

public class MyLocationService 
{
	private static MyLocationService instance;

	private Location currentLocation;
	private int repeatingCount;
	private Activity activity;
	private IGPSLocationPostProcess gpsPostProcess;

	private MyLocationService(Context context) 
	{
	}

	public static MyLocationService getInstance(Context context) 
	{
		if (instance == null) 
		{
			instance = new MyLocationService(context);
		}
		
		return instance;
	}

	public void checkLocation(IGPSLocationPostProcess locationPostProcess, Activity activity) 
	{
		this.gpsPostProcess = locationPostProcess;
		this.activity = activity;
		
		new MyLocationThread();
	}

	class MyLocationThread extends Thread 
	{
		public MyLocationThread() 
		{
			super();
			
			start();
		}

		@Override
		public void run() 
		{
			Looper.prepare();
			final LocationService locationService = LocationService.getInstance(activity);
			repeatingCount = 0;
			final Timer timer = new Timer();
			timer.schedule(new TimerTask() 
			{
				@Override
				public void run() 
				{
					System.out.println("Start location...");
					repeatingCount++;

					Location location = locationService.computeMyLocation();
					if (location == null) 
					{
						System.out.println("Location is null");
						if (repeatingCount > 10) 
						{
							timer.cancel();
							timer.purge();
							activity.runOnUiThread(new Runnable() 
							{
								@Override
								public void run() 
								{
									gpsPostProcess.onGPSLocationLoad(null);									
								}
							});
						}
					} 
					else 
					{
						currentLocation = location;
						timer.cancel();
						timer.purge();
						activity.runOnUiThread(new Runnable() 
						{
							@Override
							public void run() 
							{
								// WARNING!!! TEST ONLY!!!
								currentLocation.setLatitude(55.733279781857);
								currentLocation.setLongitude(37.644347245432);
								
								gpsPostProcess.onGPSLocationLoad(currentLocation);								
							}
						});
					}
				}
			}, 0, 500);
			Looper.loop();
		}
	}
	
	public String getDistanceToMe(Location currentLocation, double lat, double lng) 
	{
		Double dist = computeDistanceToMe(currentLocation, lat, lng);
		if (dist < 0) return "";
		
		Integer km = dist.intValue() / 1000;
		Integer metr = dist.intValue() % 1000;
		Integer decim = metr / 10;
		if (metr % 10 > 4)
		{
			decim++;
		}
		String distanse = km.toString() + "," + decim.toString() + " км";

		return distanse;
	}

	public double computeDistanceToMe(Location currentLocation, double lat, double lng) 
	{
		double pk = (double) (180 / Math.PI);
		Location location = currentLocation;
		if (location == null) return -1;
		
		double a1 = location.getLatitude() / pk;
		double a2 = location.getLongitude() / pk;

		double b1 = lat / pk;
		double b2 = lng / pk;

		double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
		double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
		double t3 = Math.sin(a1) * Math.sin(b1);
		double tt = Math.acos(t1 + t2 + t3);
		double dist = 6366000 * tt;

		return dist;
	}
}
