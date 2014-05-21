package com.drink.utils.location;

import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class LocationService extends PhoneStateListener 
{
	private static volatile LocationService instance;

	private Context context;

	public static final int GPS_PROVIDER = 1;
	public static final int WIFI_PROVIDER = 2;
	public static final int GSM_PROVIDER = 3;

	private int providerType = GPS_PROVIDER;
	private int satellitesCount;

	private LocationManager locationManager;
	private LocationListener locListener;
	private GpsStatus.Listener gpsListener;

	private WifiManager wifiManager;

	private TelephonyManager telephonyManager;
	private BroadcastReceiver receiver;

	private Location location;

	public static LocationService getInstance(Context context) 
	{
		if (instance == null) 
		{
			synchronized (LocationService.class) 
			{
				if (instance == null) 
				{
					instance = new LocationService(context);
				}
			}
		}
		
		return instance;
	}

	public LocationService(Context context) 
	{
		this.context = context;

		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		locListener = new MyLocationListener();
		gpsListener = new MyGpsStatusListener();
	}

	public void startGPSUpdate()
	{
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
		}
		
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 20, locListener);
		}
	}
	
	public void stopGPSUpdate()
	{
		locationManager.removeUpdates(locListener);
	}
	
	public void startGSM() 
	{
		CellLocation.requestLocationUpdate();

		context.registerReceiver(receiver, new IntentFilter("android.intent.action.TIME_TICK"));
		// register listener
		telephonyManager.listen(this, PhoneStateListener.LISTEN_CELL_LOCATION);
	}

	public void stopGSM() 
	{
		// clean listener
		telephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);
	}

	public void startGPS() 
	{
		locationManager.addGpsStatusListener(gpsListener);
	}

	public void stopGPS() 
	{
	}

	public void startWifi() 
	{
	}

	public void stopWifi() 
	{
	}

	public Location computeMyLocation() 
	{
		if (location == null) 
		{
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if (location == null) 
		{
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		return location;
	}

	public class MyGpsStatusListener implements GpsStatus.Listener 
	{
		@Override
		public void onGpsStatusChanged(int event) 
		{
			GpsStatus gpsStatus = locationManager.getGpsStatus(null);
			switch (event) 
			{
			case GpsStatus.GPS_EVENT_STARTED:
				// Toast.makeText(context, "GPS started",
				// Toast.LENGTH_SHORT).show();
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				// Toast.makeText(context, "time to first fix in ms = " +
				// gpsStatus.getTimeToFirstFix(), Toast.LENGTH_SHORT).show();
				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
				int satellitesCount = 0;
				Iterator<GpsSatellite> satI = satellites.iterator();
				while (satI.hasNext()) 
				{
					satellitesCount++;
					satI.next();
				}
				
				if (satellitesCount != LocationService.this.satellitesCount) 
				{
					if (satellitesCount == 0) 
					{
						if (wifiManager.getConnectionInfo() != null) 
						{
							setProviderType(WIFI_PROVIDER);
						} 
						else 
						{
							setProviderType(GSM_PROVIDER);
						}
					} 
					else 
					{
						setProviderType(GPS_PROVIDER);
					}
				}
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				break;
			}
		}
	}

	public class MyLocationListener implements LocationListener 
	{
		@Override
		public void onLocationChanged(Location loc) 
		{
			location = loc;
		}

		@Override
		public void onProviderDisabled(String provider) 
		{
			// Toast.makeText( context, "Определено по GPS", 3000).show();
			if (wifiManager.getConnectionInfo() != null) 
			{
				setProviderType(WIFI_PROVIDER);
			} 
			else 
			{
				setProviderType(GSM_PROVIDER);
			}
		}

		@Override
		public void onProviderEnabled(String provider) 
		{
			// Toast.makeText( context, "Определено по GPS", 3000).show();
			setProviderType(GPS_PROVIDER);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) 
		{
		}
	}

	public void stop() 
	{
		switch (providerType) 
		{
		case GPS_PROVIDER:
			stopGPS();
			break;
		case WIFI_PROVIDER:
			stopWifi();
			break;
		case GSM_PROVIDER:
			stopGSM();
			break;
		}
	}

	public void start() 
	{
		switch (providerType) 
		{
		case GPS_PROVIDER:
			startGPS();
			break;
		case WIFI_PROVIDER:
			startWifi();
			break;
		case GSM_PROVIDER:
			startGSM();
			break;
		}
	}

	public void setProviderType(int providerType) 
	{
		if (this.providerType != providerType) 
		{
			stop();
			this.providerType = providerType;
			start();
		}
	}

	public int getProviderType() 
	{
		return providerType;
	}

	public Location getMyLocation() 
	{
		return location;
	}
}
