
package com.drink.activity;

import java.util.ArrayList;
import org.w3c.dom.Document;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import com.drink.ControlApplication;
import com.drink.GMapV2GetRouteDirection;
import com.drink.R;
import com.drink.settings.AppSettings;
import com.drink.utils.location.IGPSLocationPostProcess;
import com.drink.utils.location.LocationService;
import com.drink.utils.location.MyLocationService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class BarOnMapActivity extends BasicActivity implements IGPSLocationPostProcess
{
	GoogleMap mGoogleMap;
	GMapV2GetRouteDirection v2GetRouteDirection;
	LatLng pointTo;
	
	public BarOnMapActivity() 
	{
		super(R.layout.activity_bar_on_map);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		// tune bar name
		mCaption.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		mCaption.setText(getIntent().getStringExtra("name"));
		
		mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mGoogleMap.setMyLocationEnabled(true);
		mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
		mGoogleMap.getUiSettings().setCompassEnabled(true);
		mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
		mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
		mGoogleMap.setTrafficEnabled(false);
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

		Bundle bundle = getIntent().getExtras();
		String name = bundle.getString("name");
		pointTo = new LatLng(bundle.getDouble("lat"), bundle.getDouble("lng"));

		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointTo, 15));

		BitmapDescriptor image = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bars_map_marker));
		
		Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(pointTo).icon(image).title(name));
		marker.showInfoWindow();
		
		AppSettings appSettings = new AppSettings(getApplicationContext());
		if (AppSettings.cityId == appSettings.getUserCityId())
		{
			v2GetRouteDirection = new GMapV2GetRouteDirection();
			
			int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
			switch (resultCode) 
			{
			case ConnectionResult.SUCCESS:
				MyLocationService.getInstance(this).checkLocation(this, this);
				break;
			case ConnectionResult.SERVICE_MISSING:
			case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
			case ConnectionResult.SERVICE_DISABLED:
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1);
				dialog.show();
				break;
			}
		}
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		
		ControlApplication.onActivityStop(this);
		LocationService.getInstance(this).stopGPSUpdate();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		ControlApplication.onActivityStart(this);
		LocationService.getInstance(this).startGPSUpdate();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		com.facebook.AppEventsLogger.activateApp(this, getString(R.string.app_id));
	}

	private class GetRouteTask extends AsyncTask<Location, Void, String> 
	{
		private ProgressDialog Dialog;
		private String response = "";
		private Document document;

		@Override
		protected void onPreExecute() 
		{
			Dialog = new ProgressDialog(BarOnMapActivity.this);
			Dialog.setMessage("Loading route...");
			Dialog.show();
		}

		@Override
		protected String doInBackground(Location... params) 
		{
			Location location = params[0];
			
			document = v2GetRouteDirection.getDocument(new LatLng(location.getLatitude(), location.getLongitude()), pointTo, GMapV2GetRouteDirection.MODE_DRIVING);
			response = "Success";
			
			return response;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			ArrayList<LatLng> list = v2GetRouteDirection.getDirection(document);
			
			Display display = getWindowManager().getDefaultDisplay();
			LatLngBounds.Builder builder = LatLngBounds.builder();
			
			PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
			for (int z = 0; z < list.size(); z++) 
			{
			    options.add(list.get(z));
			    builder.include(list.get(z));
			}
			mGoogleMap.addPolyline(options);

			mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), display.getWidth(), display.getHeight() * 4 / 5, 20));

			Dialog.dismiss();
			
			super.onPostExecute(result);
		}
	}

	@Override
	public void onGPSLocationLoad(Location location) 
	{
		if (location != null)
		{
			new GetRouteTask().execute(location);
		}
	}
}
