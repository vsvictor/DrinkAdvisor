package com.drink.activity;

import java.util.ArrayList;

import com.drink.R;
import com.drink.db.DataBase;
import com.drink.helpers.CommonHelper;
import com.drink.query.BarQuery;
import com.drink.query.CanCheckinQuery;
import com.drink.query.CanCommentQuery;
import com.drink.query.CommentsQuery;
import com.drink.query.FavBarsQuery;
import com.drink.query.WebQueryData;
import com.drink.query.BarQuery.Status;
import com.drink.query.FavBarsQuery.Data;
import com.drink.query.WebQuery;
import com.drink.query.WrongInfoQuery;
import com.drink.types.Bar;
import com.drink.utils.location.IGPSLocationPostProcess;
import com.drink.utils.location.MyLocationService;
import com.drink.widgets.ImagesViewer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class FavouritesBarActivity extends BasicActivity implements
		IGPSLocationPostProcess, OnClickListener {
	// private static final int LOGIN_REQUIED_FOR_LIKE = 0;
	private static final int LOGIN_REQUIED_FOR_CHECKIN = 1;
	// private static final int LOGIN_REQUIED_FOR_REVIEW = 2;
	private static final int LOGIN_REQUIED_FOR_PHOTO = 3;
	private static final int LOGIN_REQUIED_FOR_INVITE_FRIENDS = 4;
	private static final int LOGIN_REQUIED_FOR_RATING_LIKE = 5;
	private static final int LOGIN_REQUIED_FOR_RATING_DISLIKE = 6;
	private static final int CHECKIN = 7;
	private static final int ADD_PHOTO = 8;
	private static final int SEND_WRONG_INFO = 9;

	private static final int ADD_POSITIVE_COMMENT = 10;
	private static final int ADD_NEGATIVE_COMMENT = 11;

	private int checkinMode = CHECKIN;

	private LinearLayout llWeekday;
	private LinearLayout llBarInfo;

	private TextView mBarInfo;
	private TextView mBarRating;
	private TextView mBarAddress;
	private TextView mBarStatus;

	private TextView mWorkingTime1;
	private TextView mWorkingTime2;
	private TextView mWeekDay;
	private TextView mThisIsToday;
	private TextView tv_favorites;
	Button btnLike;
	Button btnDislike;
	Button btnCall;

	private double lat;
	private double lng;
	private String ID;

	private String info;
	private String[] pictures;

	private DataFav data;

	ArrayList<String> arrUrlPictures;
	ImagesViewer viewer;

	ScrollView mScrollView;

	boolean hasPhoto = false;

	public FavouritesBarActivity() {
		super(R.layout.activity_bar_fav);
	}

	@SuppressLint("CutPasteId")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		LinearLayout llButtons = (LinearLayout) findViewById(R.id.ll_buttons);
		llButtons.setVisibility(View.GONE);
		LinearLayout feedbackData = (LinearLayout) findViewById(R.id.feedbackData);
		feedbackData.setVisibility(View.GONE);
		LinearLayout llButtons2 = (LinearLayout) findViewById(R.id.ll_buttons2);
		llButtons2.setVisibility(View.GONE);
		// tune bar name
		mCaption.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Bold.otf"));
		mCaption.setText(getIntent().getStringExtra("name"));
		mCaption.setTextColor(Color.WHITE);

		mScrollView = (ScrollView) findViewById(R.id.scrollView1);
		mScrollView.setVisibility(View.GONE);
		tv_favorites = (TextView) findViewById(R.id.tv_favorites);
		tv_favorites.setOnClickListener(this);

		ID = getIntent().getStringExtra("ID");

		arrUrlPictures = new ArrayList<String>();

		TextView tvShowOnMap = (TextView) findViewById(R.id.tv_show_on_map);
		tvShowOnMap.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Bold.otf"));
		tvShowOnMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(FavouritesBarActivity.this,
						BarOnMapActivity.class);
				intent.putExtra("name", mCaption.getText());
				intent.putExtra("lat", lat);
				intent.putExtra("lng", lng);

				startActivity(intent);
			}
		});

		viewer = (ImagesViewer) findViewById(R.id.viewer);
		viewer.setImagesStore(mImagesStore);
		viewer.setHeight(160);
		/*
		 * picGallery.setOnItemLongClickListener(new OnItemLongClickListener() {
		 * public boolean onItemLongClick(AdapterView<?> parent, View v, int
		 * position, long id) { return true; } });
		 * 
		 * picGallery.setOnItemClickListener(new OnItemClickListener() { public
		 * void onItemClick(AdapterView<?> parent, View v, int position, long
		 * id) { if (arrUrlPictures.size() > 0) { Intent intent = new Intent();
		 * intent.setClass(BarActivity.this, PicturesActivity.class);
		 * intent.putExtra("index", picGallery.getSelectedItemPosition());
		 * intent.putStringArrayListExtra("urls", (ArrayList<String>)
		 * arrUrlPictures);
		 * 
		 * startActivity(intent); } } });
		 */
		llWeekday = (LinearLayout) findViewById(R.id.ll_weekday);
		llWeekday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showWeekdayDialog(FavouritesBarActivity.this);
			}
		});

		TextView tvWorkingHours = (TextView) findViewById(R.id.tv_working_hours);
		tvWorkingHours.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Light.otf"));

		TextView tvStupidDescription = (TextView) findViewById(R.id.tv_stupid_description);
		tvStupidDescription.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Light.otf"));

		// history section
		llBarInfo = (LinearLayout) findViewById(R.id.include_bar_info);

		TextView tvTitle = (TextView) llBarInfo.findViewById(R.id.tv_title);
		tvTitle.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Light.otf"));
		tvTitle.setText(getString(R.string.about_bar));

		mBarInfo = (TextView) llBarInfo.findViewById(R.id.tv_text);
		mBarInfo.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Reg.otf"));
		mBarInfo.setMaxLines(999);

		LinearLayout llReadMore = (LinearLayout) llBarInfo
				.findViewById(R.id.ll_read_more);
		llReadMore.setVisibility(View.GONE);

		// tune bar rating
		mBarRating = (TextView) findViewById(R.id.rating);
		mBarRating.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Bold.otf"));

		// tune bar address
		mBarAddress = (TextView) findViewById(R.id.tv_address);
		mBarAddress.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Reg.otf"));

		// tune bar status
		mBarStatus = (TextView) findViewById(R.id.tv_status);
		mBarStatus.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Bold.otf"));

		mWorkingTime1 = (TextView) findViewById(R.id.tv_working_time_1);
		mWorkingTime1.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Bold.otf"));

		mWorkingTime2 = (TextView) findViewById(R.id.tv_working_time_2);
		mWorkingTime2.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Bold.otf"));

		mWeekDay = (TextView) findViewById(R.id.tv_weekday);
		mWeekDay.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Bold.otf"));

		mThisIsToday = (TextView) findViewById(R.id.tv_this_is_today);
		mThisIsToday.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Bold.otf"));

		// like image listener
		btnLike = (Button) findViewById(R.id.btn_like);
		btnLike.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Reg.otf"));
		btnLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkLogin(LOGIN_REQUIED_FOR_RATING_LIKE)) {
					new CheckAddCommentTask(true).execute();
				}
			}
		});

		// dislike image listener
		btnDislike = (Button) findViewById(R.id.btn_dislike);
		btnDislike.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Reg.otf"));
		btnDislike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkLogin(LOGIN_REQUIED_FOR_RATING_DISLIKE)) {
					new CheckAddCommentTask(false).execute();
				}
			}
		});

		btnCall = (Button) findViewById(R.id.btn_call);
		btnCall.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Reg.otf"));
		btnCall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + btnCall.getText().toString()));

				startActivity(intent);
			}
		});

		// add photo button
		TextView tvAddPhoto = (TextView) findViewById(R.id.tv_add_photo);
		tvAddPhoto.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Reg.otf"));
		tvAddPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (checkLogin(LOGIN_REQUIED_FOR_PHOTO)) {
					addPhoto();
				}
			}
		});

		// check-in button
		TextView tvCheckin = (TextView) findViewById(R.id.tv_checkin);
		tvCheckin.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Reg.otf"));
		tvCheckin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (checkLogin(LOGIN_REQUIED_FOR_CHECKIN)) {
					checkinMode = CHECKIN;
					MyLocationService.getInstance(FavouritesBarActivity.this)
							.checkLocation(FavouritesBarActivity.this,
									FavouritesBarActivity.this);
				}
			}
		});

		// users photo button
		TextView tvPhotos = (TextView) findViewById(R.id.tv_photos);
		tvPhotos.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Reg.otf"));
		tvPhotos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!hasPhoto) {
					CommonHelper.showMessageBox(FavouritesBarActivity.this, "",
							getString(R.string.bar_no_photos), false);
					return;
				}

				Intent intent = new Intent();
				intent.putExtra("ID", ID);
				intent.putExtra("Name", mCaption.getText());
				intent.setClass(FavouritesBarActivity.this,
						UserPhotoActivity.class);

				startActivity(intent);
			}
		});

		// invite friends button
		TextView tvMeeting = (TextView) findViewById(R.id.tv_meeting);
		tvMeeting.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Reg.otf"));
		tvMeeting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkLogin(LOGIN_REQUIED_FOR_INVITE_FRIENDS)) {
					inviteFriends();
				}
			}
		});

		// bar reviews button
		TextView tvReviews = (TextView) findViewById(R.id.tv_reviews);
		tvReviews.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Reg.otf"));
		tvReviews.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("ID", ID);
				intent.putExtra("MODEL", CommentsQuery.BARS);
				intent.putExtra("bar_name", getIntent().getStringExtra("name"));
				intent.setClass(FavouritesBarActivity.this,
						CommentsActivity.class);

				startActivity(intent);
			}
		});

		// wrong info button
		TextView tvWrongInfo = (TextView) findViewById(R.id.tv_wrong_info);
		tvWrongInfo.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ProximaNova-Reg.otf"));
		tvWrongInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						FavouritesBarActivity.this);
				builder.setTitle(R.string.wrong_info_title);
				builder.setItems(R.array.wrong_info_items,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
								case 1:
								case 2:
								case 3:
								case 4:
									new SendInfoTask(which + 1, "no_text")
											.execute();
									break;
								default:
									Intent intent = new Intent();
									intent.putExtra("ID", ID);
									intent.setClass(FavouritesBarActivity.this,
											SendWrongInfoActivity.class);

									startActivityForResult(intent,
											SEND_WRONG_INFO);
									break;
								}
							}
						});
				builder.setNegativeButton(R.string.dlg_btn_cancel, null);
				builder.create().show();
			}
		});
		DataBase db = new DataBase(FavouritesBarActivity.this);
		Bar bar = db.getBarById(ID);
		DataFav data = new DataFav();
		data.name = bar.name;
		data.rating = String.valueOf(bar.rating);
		data.info = bar.info;
		data.address = bar.address;
		data.lat = Double.parseDouble(bar.lat);
		data.lng = Double.parseDouble(bar.lon);
		data.phone = bar.phone;
		data.cityName = bar.city;
		data.picList.add(bar.picture);
		data.workingTimeList1.addAll(bar.workingTimeList1);
		updateData(data);
		db.close();
		// new LoadTask().execute();
	}

	public class DataFav extends WebQueryData {

		public String name;
		public String rating;
		public String info;
		public String address;
		public double lat;
		public double lng;
		public String checkins_count;
		public String reviews_count;
		public String phone;
		public int likes_count = 0;
		public int pictures_count = 0;
		public int positiveRatingCount = 0;
		public int negativeRatingCount = 0;
		public ArrayList<String> picList = new ArrayList<String>();
		public ArrayList<String> comList = new ArrayList<String>();
		public boolean state_like = false;
		public boolean hasPhoto = false;
		public ArrayList<String> workingTimeList1 = new ArrayList<String>();
		public ArrayList<String> workingTimeList2 = new ArrayList<String>();
		public Status status = Status.Closed_Today;
		public int workingDay = 0;
		public boolean like = false;
		public boolean favorite = false;
		public String cityName;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_bar, menu);

		return true;
	}

	private void updateData(final DataFav data) {

		this.data = data;

		arrUrlPictures.clear();
		// update pictures list
		for (String url : data.picList) {
			arrUrlPictures.add(url);
		}
		viewer.setURLs(arrUrlPictures);

		// update bar rating
		mBarRating.setText(data.rating + "%");
		if (data.favorite) {

			tv_favorites.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.icon_light_white_favorites, 0, 0);
		} else {

			tv_favorites.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.icon_light_blue_favorites, 0, 0);

		}
		// update likes count
		btnLike.setText(String.valueOf(data.positiveRatingCount));
		// update dislikes count
		btnDislike.setText(String.valueOf(data.negativeRatingCount));
		// update bar phone
		btnCall.setText(data.phone);
		// update bar pictures
		pictures = new String[data.picList.size()];
		pictures = data.picList.toArray(pictures);
		// photos count
		hasPhoto = data.hasPhoto;
		// update bar info
		info = data.info;
		if (data.info.length() < 1) {
			mBarInfo.setVisibility(View.GONE);
		} else {
			while (info.length() > 0) {
				if (info.charAt(0) == '<') {
					int pos = 1;
					while ((pos < info.length()) && (info.charAt(pos) != '>'))
						++pos;

					if (pos < info.length()) {
						info = info.substring(pos + 1);
					}
				} else if (info.charAt(0) == '\r') {
					info = info.substring(1);
				} else if (info.charAt(0) == '\n') {
					info = info.substring(1);
				} else if (info.charAt(0) == '\t') {
					info = info.substring(1);
				} else if (info.charAt(0) == ' ') {
					info = info.substring(1);
				} else {
					break;
				}
			}

			mBarInfo.setText(Html.fromHtml(info));
		}
		// update bar address
		mBarAddress.setText(Html.fromHtml(data.address));
		// update bar status
		switch (data.status) {
		case Closed_Today:
			mBarStatus.setText(R.string.closed_today);
			break;
		case Opened_Tonight:
			mBarStatus.setText(R.string.opened_tonight);
			break;
		case Opened_Today:
			mBarStatus.setText(R.string.opened_today);
			break;
		}

		lat = data.lat;
		lng = data.lng;

		mScrollView.setVisibility(View.VISIBLE);

		// updateWorkingTime(data.workingDay);
	}

	public class PicAdapter extends BaseAdapter {
		// use the default gallery background image
		int defaultItemBackground;
		// gallery context
		private Context galleryContext;
		// array to store bitmaps to display
		private ArrayList<Bitmap> imageBitmaps;

		public PicAdapter(Context c) {
			galleryContext = c;

			imageBitmaps = new ArrayList<Bitmap>();
			TypedArray styleAttrs = galleryContext
					.obtainStyledAttributes(R.styleable.PicGallery);

			defaultItemBackground = styleAttrs.getResourceId(
					R.styleable.PicGallery_android_galleryItemBackground, 0);
			styleAttrs.recycle();
		}

		public int getCount() {
			return imageBitmaps.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = new ImageView(galleryContext);
			imageView.setImageBitmap(imageBitmaps.get(position));

			int width = imageBitmaps.get(position).getWidth();
			float xScale = 1;
			if (imageBitmaps.size() > 1) {
				xScale = ((float) (getWindowManager().getDefaultDisplay()
						.getWidth() * 0.9f)) / width;

				imageView.setLayoutParams(new Gallery.LayoutParams(
						(int) (xScale * width), (int) ((xScale * width) / 2)));
			}
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setBackgroundResource(defaultItemBackground);
			if (imageBitmaps.size() == 1) {
				imageView.setPadding(0, 0, 0, 0);
			}

			return imageView;
		}

		public void clear() {
			imageBitmaps.clear();
		}

		public void addPic(Bitmap newPic) {
			imageBitmaps.add(newPic);
		}

		public Bitmap getPic(int posn) {
			return imageBitmaps.get(posn);
		}
	}

	private class LoadTask extends QueryTask<Void, Void, BarQuery> {
		@Override
		protected BarQuery doInBackground(Void... params) {
			BarQuery query = new BarQuery(appSettings, ID);
			query.getResponse(WebQuery.GET);

			return query;
		}

		protected void onPostExecute(BarQuery query) {
			if (checkResult(query)) {
				// updateData(query.getData());
			}

			super.onPostExecute(query);
		}
	}

	class CanCheckinTask extends QueryTask<Location, Void, CanCheckinQuery> {
		private int mode = CHECKIN;
		private Location location;

		public CanCheckinTask(int mode) {
			super();

			this.mode = mode;
		}

		@Override
		protected CanCheckinQuery doInBackground(Location... params) {
			this.location = params[0];

			CanCheckinQuery query = new CanCheckinQuery(appSettings, ID);
			query.getResponse(WebQuery.GET);

			return query;
		}

		@Override
		protected void onPostExecute(CanCheckinQuery query) {
			if (checkResult(query, true)) {
				switch (mode) {
				case CHECKIN:
					if (query.getData().mCanCheckin) {
						Location locBar = new Location("reverseGeocoded");
						locBar.setLatitude(lat);
						locBar.setLongitude(lng);

						if (CommonHelper.getDistanceFromLatLonInKm(location,
								locBar) < 40000.0) {
							Intent intent = new Intent();
							intent.putExtra("ID", ID);
							intent.setClass(FavouritesBarActivity.this,
									CheckinActivity.class);

							startActivityForResult(intent, CHECKIN);
						} else {
							CommonHelper.showMessageBox(
									FavouritesBarActivity.this, "",
									getString(R.string.bar_too_far), false);
						}
					} else {
						CommonHelper.showMessageBox(FavouritesBarActivity.this,
								"", getString(R.string.bar_checkin_disable),
								false);
					}
					break;
				case ADD_PHOTO: {
					Intent intent = new Intent();
					intent.putExtra("bar_id", ID);
					intent.setClass(FavouritesBarActivity.this,
							AddPhotoActivity.class);

					startActivityForResult(intent, ADD_PHOTO);
				}

					break;
				}
			}

			super.onPostExecute(query);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case LOGIN_REQUIED_FOR_RATING_LIKE:
				new CheckAddCommentTask(true).execute();
				break;
			case LOGIN_REQUIED_FOR_RATING_DISLIKE:
				new CheckAddCommentTask(false).execute();
				break;
			case LOGIN_REQUIED_FOR_PHOTO:
				checkinMode = ADD_PHOTO;
				MyLocationService.getInstance(this).checkLocation(this, this);
				break;
			// case LOGIN_REQUIED_FOR_LIKE:
			// makeLike();
			// break;
			case LOGIN_REQUIED_FOR_CHECKIN:
				checkinMode = CHECKIN;
				MyLocationService.getInstance(this).checkLocation(this, this);
				break;
			case LOGIN_REQUIED_FOR_INVITE_FRIENDS:
				inviteFriends();
				break;
			case ADD_POSITIVE_COMMENT:
			case ADD_NEGATIVE_COMMENT:
				CommonHelper.showMessageBox(FavouritesBarActivity.this, "",
						getString(R.string.information_sent), false);
				break;
			case CHECKIN:
			case ADD_PHOTO:
				if (!hasPhoto) {
					hasPhoto = data.getBooleanExtra("hasPhoto", false);
				}
				break;
			case SEND_WRONG_INFO: {
				String text = data.getStringExtra("text");
				new SendInfoTask(WrongInfoQuery.WRONG_OTHER, text).execute();
			}
				break;
			}
		}
	}

	void makeRatingLike() {
		Intent intent = new Intent();
		intent.putExtra("id", ID);
		intent.putExtra("like", true);
		intent.putExtra("type", CommentsQuery.BARS);
		intent.putExtra("bar_name", getIntent().getStringExtra("name"));
		intent.setClass(FavouritesBarActivity.this, AddCommentsActivity.class);

		startActivityForResult(intent, ADD_POSITIVE_COMMENT);
	}

	void makeRatingDislike() {
		Intent intent = new Intent();
		intent.putExtra("id", ID);
		intent.putExtra("like", false);
		intent.putExtra("type", CommentsQuery.BARS);
		intent.putExtra("bar_name", getIntent().getStringExtra("name"));
		intent.setClass(FavouritesBarActivity.this, AddCommentsActivity.class);

		startActivityForResult(intent, ADD_NEGATIVE_COMMENT);
	}

	void inviteFriends() {
		Intent intent = new Intent();
		intent.setClass(this, CreateMeetingInfoActivity.class);
		intent.putExtra("ID", ID);

		startActivity(intent);
	}

	void addPhoto() {
		checkinMode = ADD_PHOTO;
		MyLocationService.getInstance(this).checkLocation(this, this);
	}

	private class CheckAddCommentTask extends
			QueryTask<Void, Void, CanCommentQuery> {
		private boolean like = true;

		CheckAddCommentTask(boolean like) {
			this.like = like;
		}

		@Override
		protected CanCommentQuery doInBackground(Void... params) {
			CanCommentQuery query = new CanCommentQuery(appSettings, ID);
			query.getResponse(WebQuery.GET);

			return query;
		}

		@Override
		protected void onPostExecute(CanCommentQuery query) {
			if (checkResult(query)) {
				CanCommentQuery.Data data = query.getData();
				if (data.mCanComment) {
					if (like) {
						makeRatingLike();
					} else {
						makeRatingDislike();
					}
				} else {
					showErrorBox(
							CommonHelper.getRemainingTimePhrase(data.mMinutes),
							false);
				}
			}

			super.onPostExecute(query);
		}
	}

	private class SendInfoTask extends QueryTask<Void, Void, WrongInfoQuery> {
		private int type;
		private String text;

		public SendInfoTask(int type, String text) {
			this.type = type;
			this.text = text;
		}

		@Override
		protected WrongInfoQuery doInBackground(Void... params) {
			WrongInfoQuery query = new WrongInfoQuery(appSettings, ID, type,
					text);
			query.getResponse(WebQuery.POST);

			return query;
		}

		@Override
		protected void onPostExecute(WrongInfoQuery query) {
			if (checkResult(query)) {
				WrongInfoQuery.Data data = query.getData();
				if (data.success) {
					String title = getString(R.string.wrong_info_reply_header);
					String message = getString(R.string.wrong_info_reply_message);
					CommonHelper.showMessageBox(FavouritesBarActivity.this,
							title, message, false);
				} else {
					showErrorBox(data.error, false);
				}
			}

			super.onPostExecute(query);
		}
	}

	@Override
	public void onGPSLocationLoad(Location location) {
		if (location != null) {
			new CanCheckinTask(checkinMode).execute(location);
		} else {
			showErrorBox("GPS error!", false);
		}
	}

	private void updateWorkingTime(int day) {
		if ((data.workingTimeList1.get(day) == null)
				|| data.workingTimeList1.get(day).equalsIgnoreCase("null")) {
			mWorkingTime1.setVisibility(View.GONE);
		} else {
			mWorkingTime1.setVisibility(View.VISIBLE);
			mWorkingTime1.setText(data.workingTimeList1.get(day));
		}

		mWorkingTime2.setText(data.workingTimeList2.get(day));

		switch (day) {
		case 0:
			mWeekDay.setText(R.string.sunday);
			break;
		case 1:
			mWeekDay.setText(R.string.monday);
			break;
		case 2:
			mWeekDay.setText(R.string.tuesday);
			break;
		case 3:
			mWeekDay.setText(R.string.wednesday);
			break;
		case 4:
			mWeekDay.setText(R.string.thursday);
			break;
		case 5:
			mWeekDay.setText(R.string.friday);
			break;
		case 6:
			mWeekDay.setText(R.string.saturday);
			break;
		}

		if (data.workingDay == day) {
			mThisIsToday.setVisibility(View.VISIBLE);
		} else {
			mThisIsToday.setVisibility(View.GONE);
		}
	}

	public void showWeekdayDialog(final Context context) {
		final Dialog dialog = new Dialog(context);
		dialog.setTitle(context.getString(R.string.select_weekday));

		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(LinearLayout.VERTICAL);

		Button b1 = new Button(context);
		b1.setText(context.getString(R.string.sunday));
		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateWorkingTime(0);
				dialog.dismiss();
			}
		});
		ll.addView(b1);

		Button b2 = new Button(context);
		b2.setText(context.getString(R.string.monday));
		b2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateWorkingTime(1);
				dialog.dismiss();
			}
		});
		ll.addView(b2);

		Button b3 = new Button(context);
		b3.setText(context.getString(R.string.tuesday));
		b3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateWorkingTime(2);
				dialog.dismiss();
			}
		});
		ll.addView(b3);

		Button b4 = new Button(context);
		b4.setText(context.getString(R.string.wednesday));
		b4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateWorkingTime(3);
				dialog.dismiss();
			}
		});
		ll.addView(b4);

		Button b5 = new Button(context);
		b5.setText(context.getString(R.string.thursday));
		b5.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateWorkingTime(4);
				dialog.dismiss();
			}
		});
		ll.addView(b5);

		Button b6 = new Button(context);
		b6.setText(context.getString(R.string.friday));
		b6.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateWorkingTime(5);
				dialog.dismiss();
			}
		});
		ll.addView(b6);

		Button b7 = new Button(context);
		b7.setText(context.getString(R.string.saturday));
		b7.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				updateWorkingTime(6);
				dialog.dismiss();
			}
		});
		ll.addView(b7);

		dialog.setContentView(ll);
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_favorites:
			if (!data.favorite) {

				// new LoadTaskFav().execute();

			} else {

				// new LoadTaskUnFav().execute();
			}

			break;

		}

	}

}
