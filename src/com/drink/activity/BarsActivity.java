
package com.drink.activity;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.drink.ControlApplication;
import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.helpers.StatisticsHelper;
import com.drink.imageloader.WebImageView2;
import com.drink.query.BarsQuery;
import com.drink.query.CanCommentQuery;
import com.drink.query.CommentsQuery;
import com.drink.query.FavBarsQuery;
import com.drink.query.BarsQuery.Bar;
import com.drink.query.FavBarsQuery.Data;
import com.drink.query.WebQuery;
import com.drink.settings.AppSettings;
import com.drink.swipelistview.BaseSwipeListViewListener;
import com.drink.swipelistview.SwipeListView;
import com.drink.utils.location.IGPSLocationPostProcess;
import com.drink.utils.location.MyLocationService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class BarsActivity extends BasicActivity implements IGPSLocationPostProcess
{
	public static final int MODE_BarsList = 1;
	public static final int MODE_SelectMeetingBar = 2;
	
	private final int FILTER_INITIAL = 0;
	private final int ADD_BAR = 1;
	private final int CREATE_MEETING = 2;
	
	private static final int ADD_POSITIVE_COMMENT = 10;
	private static final int ADD_NEGATIVE_COMMENT = 11;

	private static final int LOGIN_REQUIED_FOR_RATING_DISLIKE = 6;
	private static final int LOGIN_REQUIED_FOR_INVITE_FRIENDS = 4;
		
	private SwipeListView listView;
	private BarsListAdapter listAdapter;
	private Location location;

	private ArrayList<String> ids = new ArrayList<String>();
	private ArrayList<String> names = new ArrayList<String>();
	private ArrayList<String> lat = new ArrayList<String>();
	private ArrayList<String> lon = new ArrayList<String>();

	private boolean loading = false;
	private boolean isEmpty = false;

	private int mode = MODE_BarsList;

	private String filter;

	private ArrayList<BarsQuery.Bar> bars;
	private LinearLayout llMap;
	private GoogleMap mGoogleMap;
	
	private String sCurrentId;

	private OnClickListener listenerShowBar = new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				ViewHolder viewHolder = (ViewHolder) v.getTag();
				String id = String.valueOf(viewHolder.id);
				if (!id.equals("-1")) 
				{
					switch (mode)
					{
						case MODE_BarsList:
						{
							String name = viewHolder.title.getText().toString();
							StatisticsHelper.sendBarEvent(BarsActivity.this, id, name);
								
							Intent intent = new Intent(BarsActivity.this, BarActivity.class);
							intent.putExtra("ID", id);
							intent.putExtra("name", name);
							
							startActivity(intent);
						}
						break;
						case MODE_SelectMeetingBar:
						{
							Intent intent = new Intent(BarsActivity.this, InviteFriendsActivity.class);
							intent.putExtra("mode", InviteFriendsActivity.MODE_CreateMeeting);
							intent.putExtra("meeting_title", getIntent().getStringExtra("meeting_title"));
							intent.putExtra("meeting_date", getIntent().getLongExtra("meeting_date", 0));
							intent.putExtra("meeting_comment", getIntent().getStringExtra("meeting_comment"));
							intent.putExtra("bar_id", id);
							
							startActivityForResult(intent, CREATE_MEETING);
						}
						break;
					}
				} 
			}
		};

	public BarsActivity()
	{
		super(R.layout.activity_bars, true, false, false, true);
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		mode = getIntent().getIntExtra("mode", MODE_BarsList);
		
		switch (mode)
		{
		case MODE_BarsList:
			break;
		case MODE_SelectMeetingBar:
			cancel = true;
			back = true;
			break;
		}

		super.onCreate(savedInstanceState);

		switch (mode)
		{
		case MODE_BarsList:
			mCaption.setText(getString(R.string.bars));

			cityId = AppSettings.cityId;
			cityName = AppSettings.cityName;
			countryName = AppSettings.countryName;
			cityLat = AppSettings.lat;
			cityLng = AppSettings.lng;
			break;
		case MODE_SelectMeetingBar:
			mCaption.setText(getString(R.string.select_bar));

			cityId = getIntent().getIntExtra("city_id", -1);
			cityName = getIntent().getStringExtra("city_name");
			cityLat = getIntent().getDoubleExtra("city_lat", 0.0);
			cityLng = getIntent().getDoubleExtra("city_lng", 0.0);
			break;
		}
		
		// add filter button
		ImageButton btnFilter = new ImageButton(this);
		btnFilter.setBackgroundResource(R.drawable.button_filter);
		btnFilter.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showFilter();
			}
		});
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btnFilter.setLayoutParams(params);
		
		RelativeLayout rlNavigationTop = (RelativeLayout) findViewById(R.id.rl_navigation_up);
		rlNavigationTop.addView(btnFilter);

		listAdapter = new BarsListAdapter(getApplicationContext());

		listView = (SwipeListView) findViewById(R.id.lv_bars);
		listView.setAdapter(listAdapter);
		listView.setSwipeListViewListener(new BaseSwipeListViewListener(){
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
                View v = (View) listView.getChildAt(position - listView.getFirstVisiblePosition());
				LinearLayout llBack = (LinearLayout) v.findViewById(R.id.ll_buttons_bars);
                llBack.setVisibility(View.INVISIBLE); 
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            	//listView.closeOpenedItems();
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
            	listView.closeOpenedItems();
                View v = (View) listView.getChildAt(position - listView.getFirstVisiblePosition());
				LinearLayout llBack = (LinearLayout) v.findViewById(R.id.ll_buttons_bars);
                llBack.setVisibility(View.VISIBLE);
            }
            @Override
            public void onStartClose(int position, boolean right) {
            }

            @Override
            public void onClickFrontView(int position) 
            {
				BarsQuery.Bar bar = bars.get(position);
				String id = Integer.toString(bar.id);

				switch (mode)
				{
					case MODE_BarsList:
					{
						StatisticsHelper.sendBarEvent(BarsActivity.this, id, bar.title);
							
						Intent intent = new Intent(BarsActivity.this, BarActivity.class);
						intent.putExtra("ID", id);
						intent.putExtra("name", bar.title);
						
						startActivity(intent);
					}
					break;
					case MODE_SelectMeetingBar:
					{
						Intent intent = new Intent(BarsActivity.this, InviteFriendsActivity.class);
						intent.putExtra("mode", InviteFriendsActivity.MODE_CreateMeeting);
						intent.putExtra("meeting_title", getIntent().getStringExtra("meeting_title"));
						intent.putExtra("meeting_date", getIntent().getLongExtra("meeting_date", 0));
						intent.putExtra("meeting_comment", getIntent().getStringExtra("meeting_comment"));
						intent.putExtra("bar_id", id);
						
						startActivityForResult(intent, CREATE_MEETING);
					}
					break;
				}
            }

            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
            }
		});
		listView.setOnScrollListener(new OnScrollListener() 
			{
				private boolean needLoading = false;
			
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
				{
					listView.closeOpenedItems();
					if ((totalItemCount > 0) && (!loading))
					{
						needLoading = (firstVisibleItem + visibleItemCount + 5 >= totalItemCount);
					}
				}
	
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) 
				{
					if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) 
					{
						if (!isEmpty && needLoading && !loading)
						{
							loading = true;
							needLoading = false;
							
							new Load4ListTask().execute(location);
						}
					}
				}
			});

		llMap = (LinearLayout) findViewById(R.id.ll_map);
		
		mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mGoogleMap.setMyLocationEnabled(true);
		mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
		mGoogleMap.getUiSettings().setCompassEnabled(true);
		mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
		mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
		mGoogleMap.setTrafficEnabled(false);
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
		mGoogleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() 
		{
			@Override
			public void onInfoWindowClick(Marker marker) 
			{
				for (int i = 0; i < bars.size(); ++i)
				{
					Bar bar = bars.get(i);
					
					if (bar.title.equalsIgnoreCase(marker.getTitle()))
					{
						Intent intent = new Intent(BarsActivity.this, BarActivity.class);
						intent.putExtra("ID", Integer.toString(bar.id));
						intent.putExtra("name", bar.title);
						
						startActivity(intent);
					}
				}
			}
		}); 

		if (cityId == -1)
		{
			// need to choose city
			Intent intent = new Intent();
			intent = new Intent(BarsActivity.this, FilterCitiesActivity.class);
			
			startActivityForResult(intent, FILTER_INITIAL);
		} 
		else 
		{
			refresh();
		}
	}

	private void refresh()
	{
		if (AppSettings.barsView == AppSettings.View.List)
		{
			listView.setVisibility(View.VISIBLE);
			llMap.setVisibility(View.GONE);
			
			listAdapter.clearData();
			
			loading = true;
			isEmpty = false;
		
			if (AppSettings.barsFilter == AppSettings.Filter.By_Distance)
			{
				MyLocationService.getInstance(this).checkLocation(this, this);
			}
			else
			{
				location = null;
				new Load4ListTask().execute(location);
			}
		}
		else
		{
			listView.setVisibility(View.GONE);
			llMap.setVisibility(View.VISIBLE);
			
			mGoogleMap.clear();
			
			new Load4MapTask().execute();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == RESULT_OK) 
		{
			if (requestCode == ADD_BAR)
			{
				CommonHelper.showMessageBox(BarsActivity.this, "", getString(R.string.information_sent), false);
			}
			else if (requestCode == CREATE_MEETING)
			{
				setResult(RESULT_OK);
				finish();
			}
			else
			{
				switch (requestCode)
				{
				case LOGIN_REQUIED_FOR_RATING_DISLIKE:
					new CheckAddCommentTask(false, sCurrentId).execute();
					break;
				case FILTER_INITIAL: 
					AppSettings.cityId = intent.getIntExtra("id", -1);
					AppSettings.cityName = intent.getStringExtra("name");
					AppSettings.countryName = intent.getStringExtra("country");
					AppSettings.lat = intent.getDoubleExtra("lat", 0.0);
					AppSettings.lng = intent.getDoubleExtra("lng", 0.0);
					
					if (AppSettings.cityId == appSettings.getUserCityId())
					{
						// set sorting by distance for user city
						AppSettings.barsFilter = AppSettings.Filter.By_Distance;
					}
					else
					{
						// set sorting by rating for other city
						AppSettings.barsFilter = AppSettings.Filter.By_Rating;
					}
					
					refresh();
					
					break;
				}
			}
		}
		else if (resultCode == RESULT_INTERNAL_CANCEL)
		{
			setResult(RESULT_INTERNAL_CANCEL);
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_bars, menu);
		
		return true;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		if (ControlApplication.isFirstBars())
		{
			StatisticsHelper.sendBarsEvent(this);
			
			ControlApplication.resetFirstBars();
		}
	}
	
	private class Load4ListTask extends QueryTask<Location, Void, BarsQuery>
	{
		private final int LIMIT = 25;
		
		@Override
		protected BarsQuery doInBackground(Location... params) 
		{
			int offset = (bars != null ? bars.size() : 0);
			String sort = (AppSettings.barsFilter == AppSettings.Filter.By_Distance ? "1" : "3");
			
			BarsQuery barsQuery = new BarsQuery(appSettings, cityId, sort, offset, LIMIT, filter);
			if (params[0] != null)
			{
				barsQuery.setCoord(params[0]);
			}
			barsQuery.getResponse(WebQuery.GET);

			return barsQuery;
		}

		@Override
		protected void onPostExecute(BarsQuery query) 
		{
			if (checkResult(query)) 
			{
				BarsQuery.Data data = query.getData();
				if (data.bars.size() > 0) 
				{
					listAdapter.addData(data);
				} 
				
				isEmpty = (data.bars.size() < LIMIT); 
			}

			loading = false;
			
			super.onPostExecute(query);
		}
	}

	
	private class Load4MapTask extends QueryTask<Void, Void, BarsQuery>
	{
		@Override
		protected BarsQuery doInBackground(Void... params) 
		{
			BarsQuery barsQuery = new BarsQuery(appSettings, cityId,"1", 0, 1000, filter);
			barsQuery.getResponse(WebQuery.GET);

			return barsQuery;
		}

		@Override
		protected void onPostExecute(BarsQuery query) 
		{
			if (checkResult(query)) 
			{
				BarsQuery.Data data = query.getData();
				bars = data.bars;
				
				BitmapDescriptor image = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bars_map_marker));

				LatLngBounds.Builder builder = LatLngBounds.builder();
				for (int i = 0; i < data.bars.size(); ++i) 
				{
					final Bar bar = data.bars.get(i);
					
					LatLng pos = new LatLng(Double.valueOf(bar.lat), Double.valueOf(bar.lon));

					builder.include(pos);
					
					MarkerOptions marker = new MarkerOptions();
					marker.position(pos);
					marker.icon(image);
					marker.title(bar.title);
					mGoogleMap.addMarker(marker);
				}
				
				mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), CommonHelper.getScreenWidth(), CommonHelper.getScreenHeight() * 4 / 5, 20));
			}
			
			super.onPostExecute(query);
		}
	}

	private class BarsListAdapter extends BaseAdapter 
	{
		private LayoutInflater mInflater;

		public BarsListAdapter(Context context) 
		{
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void addData(BarsQuery.Data data) 
		{
			if (bars == null) 
			{
				bars = data.bars;
			}
			else
			{
				bars.addAll(data.bars);
			}

			for (BarsQuery.Bar bar : data.bars) 
			{
				ids.add(Integer.toString(bar.id));
				names.add(bar.title);
				lat.add(bar.lat);
				lon.add(bar.lon);
			}
			
			notifyDataSetChanged();
		}

		public void clearData() 
		{
			bars = null;
			
			ids.clear();
			names.clear();
			lat.clear();
			lon.clear();
			
			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			if (bars == null) return 0;
			
			return bars.size();
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) 
		{
			ViewHolder viewHolder = null;
			View row = view;
			if (row == null) 
			{
				viewHolder = new ViewHolder();
				
				row = mInflater.inflate(R.layout.listitem_bars, null);
				
				TextView tx = (TextView) row.findViewById(R.id.tv_btn_meeting_bars);
				tx.setTypeface(Typeface.createFromAsset(BarsActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				tx = (TextView) row.findViewById(R.id.tv_btn_rate_bars);
				tx.setTypeface(Typeface.createFromAsset(BarsActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				tx = (TextView) row.findViewById(R.id.tv_btn_favorite_bars);
				tx.setTypeface(Typeface.createFromAsset(BarsActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				
				viewHolder.id = bars.get(i).id;
				viewHolder.title = (TextView) row.findViewById(R.id.title);
				viewHolder.title.setTypeface(Typeface.createFromAsset(BarsActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				viewHolder.distance = (TextView) row.findViewById(R.id.tv_distance);
				viewHolder.distance.setTypeface(Typeface.createFromAsset(BarsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				viewHolder.rating = (TextView) row.findViewById(R.id.tv_rating);
				viewHolder.rating.setTypeface(Typeface.createFromAsset(BarsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				viewHolder.opened = (TextView) row.findViewById(R.id.tv_opened);
				viewHolder.opened.setTypeface(Typeface.createFromAsset(BarsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				
				viewHolder.picture = (WebImageView2) row.findViewById(R.id.picture);
				viewHolder.picture.setImagesStore(mImagesStore);
				viewHolder.picture.setCacheDir(appSettings.getCacheDirImage());
				viewHolder.picture.setHeight(123);
								
				row.setTag(viewHolder);
// by Demo - prevent double handling
//				row.setOnClickListener(listenerShowBar);
				

				final String sID = String.valueOf(viewHolder.id);
				LinearLayout llMeeting = (LinearLayout) row.findViewById(R.id.llMeetingBars);
				LinearLayout llRate = (LinearLayout) row.findViewById(R.id.llRateBars);
				LinearLayout llFav = (LinearLayout) row.findViewById(R.id.llFavoriteBar);

				llMeeting.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
					    sCurrentId = sID;
						if (checkLogin(LOGIN_REQUIED_FOR_INVITE_FRIENDS)){
							inviteFriends(sID);
						}
					}
				});
				llRate.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) 
					{
						sCurrentId = sID;
						if (checkLogin(LOGIN_REQUIED_FOR_RATING_DISLIKE)){
							new CheckAddCommentTask(false, sID).execute();
						}
					}
				});
				final int ii = i;
				llFav.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						sCurrentId = sID;
							 if (!bars.get(ii).favotites) {
								 LoadTaskFav tf = new LoadTaskFav(cityId, sID);
							    tf.execute();
							   } else {
								   LoadTaskUnFav tuf = new LoadTaskUnFav(cityId, sID);
								   tuf.execute();
							   }
						}
				});
			} 
			else 
			{
				viewHolder = (ViewHolder) row.getTag();
			}
			
			Bar bar = bars.get(i);

			// set bar id
			viewHolder.id = bar.id;
			// set bar title
			viewHolder.title.setText(bar.title);
			// set bar distance
			String dist = getString(R.string.dist_c);
			double distance = bar.dist;
			if (distance > 0.001)
			{
				if (dist.equals("mi")) 
				{
					distance = distance / 1609.0;
				} 
				else 
				{
					distance = distance / 1000.0;
				}
				distance = (double) (int) (distance * 100.0) / 100.0;
				
				viewHolder.distance.setVisibility(View.VISIBLE);
				viewHolder.distance.setText(String.valueOf(distance) + " " + dist);
			}
			else
			{
				viewHolder.distance.setVisibility(View.GONE);
			}
			
			
			switch (bar.status)
			{
			case Closed_Today:
				viewHolder.opened.setText(R.string.closed_today);
				break;
			case Opened_Tonight:
				viewHolder.opened.setText(R.string.opened_tonight);
				break;
			case Opened_Today:
				viewHolder.opened.setText(R.string.opened_today);
				break;
			}
			
			// set bar rating
			viewHolder.rating.setText(String.valueOf(bar.rating) + "%");
			// set bar picture					
			if (bar.picture != null)
			{
				viewHolder.picture.setImageURL(bar.picture, "123");
			}

			return row;
		}

		@Override
		public BarsQuery.Bar getItem(int position) 
		{
			if (bars == null) return null;

			return bars.get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}
	}

	private class ViewHolder 
	{
		public int id = -1;
		public TextView title;
		public WebImageView2 picture;
		public TextView distance;
		public TextView rating;
		public TextView opened;
		RelativeLayout rlMainInfo;
	}

	@Override
	public void onGPSLocationLoad(Location location) 
	{
		this.location = location;
		new Load4ListTask().execute(location);
	}
	
	@Override
	protected void onApplyFilter()
	{
		filter = AppSettings.filter;
		AppSettings.filter = null;

		refresh();
	}
	private class CheckAddCommentTask extends QueryTask<Void, Void, CanCommentQuery> 
	{
		private boolean like = true;
		private String ID = null;
		
		CheckAddCommentTask(boolean like, String ID)
		{
			this.like = like;
			this.ID = ID;
		}
		
		@Override
		protected CanCommentQuery doInBackground(Void... params) 
		{
			CanCommentQuery query = new CanCommentQuery(appSettings, ID);
			query.getResponse(WebQuery.GET);
	
			return query;
		}

		@Override
		protected void onPostExecute(CanCommentQuery query) 
		{
			if (checkResult(query)) 
			{
				CanCommentQuery.Data data = query.getData();
				if (data.mCanComment)
				{
					if (like)
					{
						makeRatingLike(this.ID);
					}
					else
					{
						makeRatingDislike(this.ID);
					}
				}
				else
				{
					showErrorBox(CommonHelper.getRemainingTimePhrase(data.mMinutes), false);
				}
			}

			super.onPostExecute(query);
		}
	}
	
	void makeRatingLike(String ID) 
	{
		Intent intent = new Intent();
		intent.putExtra("id", ID);
		intent.putExtra("like", true);
		intent.putExtra("type", CommentsQuery.BARS);
		intent.putExtra("bar_name", getIntent().getStringExtra("name"));
		intent.setClass(BarsActivity.this, AddCommentsActivity.class);
		
		startActivityForResult(intent, ADD_POSITIVE_COMMENT);
	}
	
	void makeRatingDislike(String ID)
	{
		Intent intent = new Intent();
		intent.putExtra("id", ID);
		intent.putExtra("like", false);
		intent.putExtra("type", CommentsQuery.BARS);
		intent.putExtra("bar_name", getIntent().getStringExtra("name"));
		intent.setClass(BarsActivity.this, AddCommentsActivity.class);
		
		startActivityForResult(intent, ADD_NEGATIVE_COMMENT);
	}
	void inviteFriends(String ID)
	{
		Intent intent = new Intent();
		intent.setClass(this, CreateMeetingInfoActivity.class);
		intent.putExtra("ID", ID);
		startActivity(intent);
	}
	private class LoadTaskFav extends QueryTask<Void, Void, Data> {
		private int idCity;
		private String idBar;
		public LoadTaskFav(int idCity, String idBar){
			this.idCity = idCity;
			this.idBar = idBar;
		}
		@Override
		protected Data doInBackground(Void... params) {
			FavBarsQuery barsQuery = new FavBarsQuery(appSettings, this.idCity,	BarsActivity.this, this.idBar, "POST");
			barsQuery.getData();
			return barsQuery.getData();
		}

		protected void onPostExecute(Data dataRet) {
			if (dataRet != null) {
				if(dataRet.success)
				{
//					tv_favorites.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.icon_light_white_favorites, 0, 0);
//					data.favorite = true;
				}
			}
			super.onPostExecute(dataRet);
		}
	}

	private class LoadTaskUnFav extends QueryTask<Void, Void, Data> {
		private int idCity;
		private String idBar;
		
		public LoadTaskUnFav(int idCity, String idBar){
			this.idCity = idCity;
			this.idBar = idBar;
		}
		@Override
		protected Data doInBackground(Void... params) {

			FavBarsQuery barsQuery = new FavBarsQuery(appSettings, this.idCity,	BarsActivity.this, this.idBar, "DELETE");
			barsQuery.getData();

			return barsQuery.getData();
		}

		protected void onPostExecute(Data dataRet) {
			if (dataRet != null) {
				if(dataRet.success)
				{
//					tv_favorites.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.icon_light_blue_favorites, 0, 0);
//					data.favorite = false;
				}
			}
			super.onPostExecute(dataRet);
		}
	}
	
}
