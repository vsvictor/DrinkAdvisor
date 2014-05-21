package com.drink.activity;

import com.drink.ControlApplication;
import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.query.AddBarQuery;
import com.drink.query.BarsQuery;
import com.drink.query.CitiesListQuery;
import com.drink.query.WebQuery;
import com.drink.utils.location.LocationService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class RecommendBarActivity extends BasicActivity
{
	private final int CHOOSE_CITY = 0;
	
	GoogleMap 				mGoogleMap;

	CitiesListQuery.Data	data;

	ArrayAdapter<String> 	adapter;
	TextView				tv_city;
	int 					city_id;
	
	EditText				etBarTitle;
	EditText				etBarAddress;
	EditText				etBarInfo;

	ScrollView 				scrollView;
	Marker 					marker;
	
	double lat = 2.01;
	double lng = 3.01;

	
	public RecommendBarActivity() 
	{
		super(R.layout.activity_recommend_bar);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        etBarTitle = (EditText) findViewById(R.id.edit_title);
        etBarTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
        etBarAddress = (EditText) findViewById(R.id.edit_address);
        etBarAddress.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
        etBarInfo = (EditText) findViewById(R.id.edit_info);
        etBarInfo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));

        LinearLayout ll_city = (LinearLayout) findViewById(R.id.ll_city);
        ll_city.setOnClickListener(new OnClickListener() 
        {
			@Override
			public void onClick(View arg0) 
			{
				Intent intent = new Intent(RecommendBarActivity.this, FilterCitiesActivity.class);
				intent.putExtra("mode", FilterCitiesActivity.MODE_RecommendBar);
				
				startActivityForResult(intent, CHOOSE_CITY);
			}
		});
        
        tv_city = (TextView) ll_city.findViewById(R.id.tv_city);
        tv_city.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
        tv_city.setText(appSettings.cityName + ", " + appSettings.countryName);
        
        TextView tv_map_title = (TextView) findViewById(R.id.tv_map_title);
        tv_map_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
        
        TextView tv_info_title = (TextView) findViewById(R.id.tv_info_title);
        tv_info_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
        
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        ImageView transparentImageView = (ImageView) scrollView.findViewById(R.id.imageView123);
        transparentImageView.setOnTouchListener(new View.OnTouchListener() 
	        {
	            @Override
	            public boolean onTouch(View v, MotionEvent event) 
	            {
	                int action = event.getAction();
	                switch (action) 
	                {
	                   case MotionEvent.ACTION_DOWN:
	                        // Disallow ScrollView to intercept touch events.
	                        scrollView.requestDisallowInterceptTouchEvent(true);
	                        // Disable touch on transparent view
	                        return false;
	
	                   case MotionEvent.ACTION_UP:
	                        // Allow ScrollView to intercept touch events.
	                        scrollView.requestDisallowInterceptTouchEvent(false);
	                        return true;
	
	                   case MotionEvent.ACTION_MOVE:
	                        scrollView.requestDisallowInterceptTouchEvent(true);
	                        return false;
	
	                   default: 
	                        return true;
	                }   
	            }
	        });
        
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mGoogleMap = mapFragment.getMap();
		mGoogleMap.setMyLocationEnabled(false);
		mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
		mGoogleMap.getUiSettings().setCompassEnabled(true);
		mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
		mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
		mGoogleMap.setTrafficEnabled(true);
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
		mGoogleMap.setOnMapClickListener(new OnMapClickListener() 
			{
				@Override
				public void onMapClick(LatLng pos) 
				{
					lat = pos.latitude;
					lng = pos.longitude;
					
					if (marker != null)
					{
						marker.remove();
					}
					
					BitmapDescriptor image = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bars_map_marker));
					
					marker = mGoogleMap.addMarker(new MarkerOptions().position(pos).icon(image));
				}
			});

		LatLng point = new LatLng(appSettings.lat, appSettings.lng);

		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));

		Button btn_send = (Button) findViewById(R.id.btn_send);
		btn_send.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btn_send.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					String title = etBarTitle.getText().toString();
					if (title.length() == 0)
					{
						CommonHelper.showMessageBox(RecommendBarActivity.this, getString(R.string.error_dialog_title), getString(R.string.enter_bar_name), false);
						return;
					}
					
					new AddBarTask().execute();
				}
			});

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
        city_id = appSettings.cityId;
        new CityCoordinatesTask().execute(city_id);
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
	
	private class CityCoordinatesTask extends QueryTask<Integer, Void, BarsQuery> 
	{
		@Override
		protected BarsQuery doInBackground(Integer... params) 
		{
			BarsQuery query = new BarsQuery(appSettings, params[0], "3", 0, 1, null);
			query.getResponse(WebQuery.GET);

			return query;
		}

		@Override
		protected void onPostExecute(BarsQuery query) 
		{
			if (query.getResult()) 
			{
				Double lat = Double.parseDouble(query.getData().bars.get(0).lat);
				Double lng = Double.parseDouble(query.getData().bars.get(0).lon);

				LatLng pos = new LatLng(lat, lng);
				mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 12));
			}
			
			super.onPostExecute(query);
		}
	}
	
	private class AddBarTask extends QueryTask<Void, Void, AddBarQuery>
	{
		@Override
		protected AddBarQuery doInBackground(Void... params) 
		{
			String title = etBarTitle.getText().toString();
			String address = etBarAddress.getText().toString();
			String info = etBarInfo.getText().toString();

			AddBarQuery query = new AddBarQuery(appSettings, title, city_id, lat, lng, address, info);
			query.getResponse(WebQuery.GET);

			return query;
		}

		@Override
		protected void onPostExecute(AddBarQuery query) 
		{
			if (query.getResult()) 
			{
				setResult(RESULT_OK);

				CommonHelper.showMessageBox(RecommendBarActivity.this, "Drink Advisor", RecommendBarActivity.this.getString(R.string.information_sent), true);
			}
			
			super.onPostExecute(query);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		switch (resultCode)
		{
		case RESULT_OK:
			if (requestCode == CHOOSE_CITY)
			{
				tv_city.setText(intent.getStringExtra("name"));

				city_id = intent.getIntExtra("id", -1);
				new CityCoordinatesTask().execute(city_id);
			}
			break;
		}

		super.onActivityResult(requestCode, resultCode, intent);
	}
}
