package com.drink.activity;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.drink.ControlApplication;
import com.drink.R;
import com.drink.activity.BasicActivity.QueryTask;
import com.drink.db.DataBase;
import com.drink.helpers.CommonHelper;
import com.drink.helpers.StatisticsHelper;
import com.drink.imageloader.WebImageView2;
import com.drink.query.BarsQuery;
import com.drink.query.CanCommentQuery;
import com.drink.query.CommentsQuery;
import com.drink.query.FavBarsQuery;
import com.drink.query.FavBarsQuery.Data;
import com.drink.query.WebQuery;
import com.drink.settings.AppSettings;
import com.drink.swipelistview.BaseSwipeListViewListener;
import com.drink.swipelistview.SwipeListView;
import com.drink.types.Bar;
import com.drink.utils.location.IGPSLocationPostProcess;
import com.drink.utils.location.MyLocationService;

public class FavouritesBarsActivity extends BasicActivity implements
		IGPSLocationPostProcess, OnClickListener {
	public static final int MODE_BarsList = 1;
	public static final int MODE_SelectMeetingBar = 2;

	private final int FILTER_INITIAL = 0;
	private final int ADD_BAR = 1;
	private final int CREATE_MEETING = 2;

	private static final int LOGIN_REQUIED_FOR_RATING_DISLIKE = 6;
	private static final int LOGIN_REQUIED_FOR_INVITE_FRIENDS = 4;
	
	private static final int ADD_POSITIVE_COMMENT = 10;
	private static final int ADD_NEGATIVE_COMMENT = 11;


	private SwipeListView listView;
	private BarsListAdapter listAdapter;
	private Location location;

	private ArrayList<String> ids = new ArrayList<String>();
	private ArrayList<String> names = new ArrayList<String>();
	private ArrayList<String> lat = new ArrayList<String>();
	private ArrayList<String> lon = new ArrayList<String>();
	private Button btn_back;
	private boolean loading = false;
	private boolean isEmpty = false;
	private boolean isOnline = false;
	private int mode = MODE_BarsList;
	private ArrayList<Bar> bars;
	private String sCurrentId;
	
	private OnClickListener listenerShowBar = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ViewHolder viewHolder = (ViewHolder) v.getTag();
			String id = String.valueOf(viewHolder.id);
			if (!id.equals("-1")) {
				switch (mode) {
				case MODE_BarsList: {
					String name = viewHolder.title.getText().toString();
					StatisticsHelper.sendBarEvent(FavouritesBarsActivity.this, id, name);
					if (!isOnline) {
						Intent intent = new Intent(FavouritesBarsActivity.this,	FavouritesBarActivity.class);

						intent.putExtra("ID", id);
						intent.putExtra("name", name);

						startActivity(intent);
					} else {
						Intent intent = new Intent(FavouritesBarsActivity.this,
								BarActivity.class);

						intent.putExtra("ID", id);
						intent.putExtra("name", name);

						startActivity(intent);
					}
				}
					break;
				case MODE_SelectMeetingBar: {
					Intent intent = new Intent(FavouritesBarsActivity.this,
							InviteFriendsActivity.class);
					intent.putExtra("mode",
							InviteFriendsActivity.MODE_CreateMeeting);
					intent.putExtra("meeting_title", getIntent()
							.getStringExtra("meeting_title"));
					intent.putExtra("meeting_date",
							getIntent().getLongExtra("meeting_date", 0));
					intent.putExtra("meeting_comment", getIntent()
							.getStringExtra("meeting_comment"));
					intent.putExtra("bar_id", id);

					startActivityForResult(intent, CREATE_MEETING);
				}
					break;
				}
			}
		}
	};

	public FavouritesBarsActivity()

	{
		super(R.layout.activity_bars_fav, true, false, false, true);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mode = getIntent().getIntExtra("mode", MODE_BarsList);
		// btn_back = (Button) findViewById(R.id.btn_back);
		// btn_back.setOnClickListener(this);
		switch (mode) {
		case MODE_BarsList:
			break;
		case MODE_SelectMeetingBar:
			cancel = true;
			back = true;
			break;
		}

		super.onCreate(savedInstanceState);

		switch (mode) {
		case MODE_BarsList:
			mCaption.setText(getString(R.string.bars));

			cityId = AppSettings.cityId;
			cityName = AppSettings.cityName;
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
		btnFilter.setVisibility(View.GONE);
		btnFilter.setBackgroundResource(R.drawable.button_filter);
		btnFilter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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

		listView = (SwipeListView) findViewById(R.id.lv_bars_fav);
		listView.setAdapter(listAdapter);
		listView.setSwipeListViewListener(new BaseSwipeListViewListener(){
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
                View v = (View) listView.getChildAt(position - listView.getFirstVisiblePosition());
				LinearLayout llBack = (LinearLayout) v.findViewById(R.id.ll_buttons_fav);
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
				LinearLayout llBack = (LinearLayout) v.findViewById(R.id.ll_buttons_fav);
                llBack.setVisibility(View.VISIBLE);
            }
            @Override
            public void onStartClose(int position, boolean right) {
            }

            @Override
            public void onClickFrontView(int position) 
            {
				Bar bar = bars.get(position);
				String id = Integer.toString(bar.id);

				switch (mode)
				{
					case MODE_BarsList:
					{
						StatisticsHelper.sendBarEvent(FavouritesBarsActivity.this, id, bar.title);
							
						Intent intent = new Intent(FavouritesBarsActivity.this, BarActivity.class);
						intent.putExtra("ID", id);
						intent.putExtra("name", bar.title);
						
						startActivity(intent);
					}
					break;
					case MODE_SelectMeetingBar:
					{
						Intent intent = new Intent(FavouritesBarsActivity.this, InviteFriendsActivity.class);
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
		listView.setOnScrollListener(new OnScrollListener() {
			private boolean needLoading = false;
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            	listView.closeOpenedItems();
				if ((totalItemCount > 0) && (!loading)) {
					needLoading = (firstVisibleItem + visibleItemCount + 5 >= totalItemCount);
				}
			}
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (!isEmpty && needLoading && !loading) {
						loading = true;
						needLoading = false;
							new LoadTask().execute(location);
					}
				}
			}
		});

		if (cityId == -1) {
			Intent intent = new Intent();
			intent = new Intent(FavouritesBarsActivity.this,FilterCitiesActivity.class);
			startActivityForResult(intent, FILTER_INITIAL);
		} else {
			refresh();
		}
	}

	private void refresh() {
		listAdapter.clearData();

		loading = true;
		isEmpty = false;

		if (AppSettings.barsFilter == AppSettings.Filter.By_Distance) {
			MyLocationService.getInstance(this).checkLocation(this, this);
		} else {
			location = null;
			new LoadTask().execute(location);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == RESULT_OK) {
			if (requestCode == ADD_BAR) {
				CommonHelper.showMessageBox(FavouritesBarsActivity.this, "",
						getString(R.string.information_sent), false);
			} else if (requestCode == CREATE_MEETING) {
				setResult(RESULT_OK);
				finish();
			} else {
				switch (requestCode) {
				case FILTER_INITIAL:
					AppSettings.cityId = intent.getIntExtra("id", -1);
					AppSettings.cityName = intent.getStringExtra("name");
					AppSettings.lat = intent.getDoubleExtra("lat", 0.0);
					AppSettings.lng = intent.getDoubleExtra("lng", 0.0);

					if (AppSettings.cityId == appSettings.getUserCityId()) {

						AppSettings.barsFilter = AppSettings.Filter.By_Distance;
					} else {

						AppSettings.barsFilter = AppSettings.Filter.By_Rating;
					}

					refresh();

					break;
				}
			}
		} else if (resultCode == RESULT_INTERNAL_CANCEL) {
			setResult(RESULT_INTERNAL_CANCEL);
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_bars, menu);

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (ControlApplication.isFirstBars()) {
			StatisticsHelper.sendBarsEvent(this);

			ControlApplication.resetFirstBars();
		}
	}

	private class LoadTask extends QueryTask<Location, Void, Data> {
		private final int LIMIT = 25;

		@Override
		protected Data doInBackground(Location... params) {
			int offset = (bars != null ? bars.size():0);
			String filter = (AppSettings.barsFilter == AppSettings.Filter.By_Distance ? "1"	: "3");
			FavBarsQuery barsQuery = new FavBarsQuery(appSettings, cityId,	filter, offset, LIMIT, FavouritesBarsActivity.this);
			if (params[0] != null) {
				barsQuery.setCoord(params[0]);
			}

			barsQuery.getData();

			return barsQuery.getData();
		}

		@Override
		protected void onPostExecute(Data query) {
			if (query.bars.size() > 0) {
				isOnline = true;
				FavBarsQuery.Data data = query;
				if (data.bars.size() > 0) {
					listAdapter.addData(data.bars);
				}
				isEmpty = (data.bars.size() < LIMIT);
			} else {
				isOnline = false;
				DataBase db = new DataBase(FavouritesBarsActivity.this);
				ArrayList<Bar> bars = db.getAllBars();

				if (bars.size() > 0) {
					listAdapter.addData(bars);

				}
				isEmpty = (bars.size() < LIMIT);
				db.close();
			}

			loading = false;

			super.onPostExecute(query);
		}
	}

	private class BarsListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public BarsListAdapter(Context context) {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void addData(ArrayList<Bar> bars2) {
			if (bars == null) {
				bars = bars2;
			} else {
				bars.addAll(bars2);
			}

			for (Bar bar : bars2) {
				ids.add(Integer.toString(bar.id));
				names.add(bar.title);
				lat.add(bar.lat);
				lon.add(bar.lon);
			}

			notifyDataSetChanged();
		}

		public void clearData() {
			bars = null;

			ids.clear();
			names.clear();
			lat.clear();
			lon.clear();

			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (bars == null)
				return 0;

			return bars.size();
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) {
			ViewHolder viewHolder = null;
			View row = view;
			if (row == null) {
				viewHolder = new ViewHolder();

				row = mInflater.inflate(R.layout.listitem_bars_fav, null);

				TextView tx = (TextView) row.findViewById(R.id.tv_btn_meeting_fav);
				tx.setTypeface(Typeface.createFromAsset(FavouritesBarsActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				tx = (TextView) row.findViewById(R.id.tv_btn_rate_fav);
				tx.setTypeface(Typeface.createFromAsset(FavouritesBarsActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				tx = (TextView) row.findViewById(R.id.tv_btn_favorite_fav);
				tx.setTypeface(Typeface.createFromAsset(FavouritesBarsActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));

				viewHolder.id = bars.get(i).id;
				viewHolder.title = (TextView) row.findViewById(R.id.title);
				viewHolder.title.setTypeface(Typeface.createFromAsset(FavouritesBarsActivity.this.getAssets(),"fonts/ProximaNova-Bold.otf"));
				viewHolder.city = (TextView) row.findViewById(R.id.tv_opened);
				viewHolder.city.setTypeface(Typeface.createFromAsset(FavouritesBarsActivity.this.getAssets(),"fonts/ProximaNova-Reg.otf"));
				viewHolder.distance = (TextView) row.findViewById(R.id.tv_distance);
				viewHolder.distance.setTypeface(Typeface.createFromAsset(FavouritesBarsActivity.this.getAssets(),"fonts/ProximaNova-Reg.otf"));
				viewHolder.picture = (WebImageView2) row.findViewById(R.id.picture);
				viewHolder.picture.setImagesStore(mImagesStore);
				viewHolder.picture.setCacheDir(appSettings.getCacheDirImage());
				viewHolder.picture.setHeight(123);
				row.setOnClickListener(listenerShowBar);
				row.setTag(viewHolder);
				final String sID = String.valueOf(viewHolder.id);
				LinearLayout llMeeting = (LinearLayout) row.findViewById(R.id.llMeetingFav);
				LinearLayout llRate = (LinearLayout) row.findViewById(R.id.llRateFav);
				LinearLayout llFav = (LinearLayout) row.findViewById(R.id.llFavoriteFav);

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
					   LoadTaskUnFav tuf = new LoadTaskUnFav(cityId, sID);
					   tuf.execute();
					}
				});

			} else {
				viewHolder = (ViewHolder) row.getTag();
			}

			Bar bar = bars.get(i);

			// set bar id
			viewHolder.id = bar.id;
			// set bar title
			viewHolder.title.setText(bar.title);
			// set bar distance
			String dist = getString(R.string.dist_c);

			viewHolder.city.setText(bar.city);
			double distance = bar.dist;
			if (distance > 0.001) {
				if (dist.equals("mi")) {
					distance = distance / 1609.0;
				} else {
					distance = distance / 1000.0;
				}
				distance = (double) (int) (distance * 100.0) / 100.0;

				viewHolder.distance.setVisibility(View.VISIBLE);
				viewHolder.distance.setText(String.valueOf(distance) + " "
						+ dist);
			} else {
				viewHolder.distance.setVisibility(View.GONE);
			}
			// set bar rating
			// viewHolder.rating.setText(String.valueOf(bar.rating) + "%");
			// set bar picture
			if (bar.picture != null) {
				viewHolder.picture.setImageURL(bar.picture, "123");
			}
			return row;
		}

		@Override
		public Bar getItem(int position) {
			if (bars == null)
				return null;
			return bars.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	private class ViewHolder {
		public int id = -1;
		public TextView title;
		public WebImageView2 picture;
		public TextView distance;
		public TextView city;
	}

	@Override
	public void onGPSLocationLoad(Location location) {
		this.location = location;
		new LoadTask().execute(location);
	}

	@Override
	protected void onApplyFilter() {
		refresh();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_back:
			FavouritesBarsActivity.this.finish();

			break;

		}

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
			FavBarsQuery barsQuery = new FavBarsQuery(appSettings, this.idCity,	FavouritesBarsActivity.this, this.idBar, "POST");
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

			FavBarsQuery barsQuery = new FavBarsQuery(appSettings, this.idCity,	FavouritesBarsActivity.this, this.idBar, "DELETE");
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
		intent.setClass(FavouritesBarsActivity.this, AddCommentsActivity.class);
		
		startActivityForResult(intent, ADD_POSITIVE_COMMENT);
	}
	
	void makeRatingDislike(String ID)
	{
		Intent intent = new Intent();
		intent.putExtra("id", ID);
		intent.putExtra("like", false);
		intent.putExtra("type", CommentsQuery.BARS);
		intent.putExtra("bar_name", getIntent().getStringExtra("name"));
		intent.setClass(FavouritesBarsActivity.this, AddCommentsActivity.class);
		
		startActivityForResult(intent, ADD_NEGATIVE_COMMENT);
	}
	
}
