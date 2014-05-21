package com.drink.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.drink.ControlApplication;
import com.drink.R;
import com.drink.helpers.StatisticsHelper;
import com.drink.imageloader.WebImageView;
import com.drink.imageloader.WebImageView2;
import com.drink.query.BlogQuery;
import com.drink.query.ReviewQuery;
import com.drink.query.WebQuery;

public class ReviewMainActivity extends BasicActivity implements
		OnClickListener {
	ListView listView;
	ReviewListAdapter adapter;

	private boolean loading = false;
	private boolean isEmpty = false;
	private Button btn_back;
	private TextView tv_caption;
	private int itemHeight;

	public ReviewMainActivity() {
		super(R.layout.activity_review, true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		btn_back = (Button) findViewById(R.id.btn_back);

		tv_caption = (TextView) findViewById(R.id.tv_caption);
		btn_back.setOnClickListener(this);
		itemHeight = getWidth(ReviewMainActivity.this);
		tv_caption.setText(getResources().getString(R.string.rew_caption));

		listView = (ListView) findViewById(R.id.listView);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.activity_review, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (ControlApplication.isFirstBlog()) {
			StatisticsHelper.sendReviewsEvent(this);
			ControlApplication.resetFirstBlog();

		}
		adapter = new ReviewListAdapter(getApplicationContext());
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new OnScrollListener() {
			private boolean needLoading = false;

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if ((totalItemCount > 0) && (!loading)) {
					needLoading = (firstVisibleItem + visibleItemCount + 3 >= totalItemCount);
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (!isEmpty && needLoading && !loading) {
						loading = true;
						needLoading = false;

						new LoadTask().execute();
					}
				}
			}
		});

		loading = true;
		new LoadTask().execute();
	}

	private class ReviewListAdapter extends BaseAdapter {
		LayoutInflater layoutInflater;
		ReviewQuery.Data data;
		private ViewHolder viewHolder;

		public ReviewListAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
		}

		public void addData(ReviewQuery.Data data) {
			if (this.data == null) {
				this.data = data;
			} else {
				this.data.posts.addAll(data.posts);
			}

			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (data == null)
				return 0;

			return data.posts.size();
		}

		@Override
		public ReviewQuery.Post getItem(int arg0) {
			return data.posts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			View row = view;
			if (row == null) {
				row = this.layoutInflater.inflate(R.layout.listitem_review,
						null);

				viewHolder = new ViewHolder();
				viewHolder.usrPicture = (WebImageView2) row
						.findViewById(R.id.user_picture);
				viewHolder.picture = (WebImageView2) row
						.findViewById(R.id.picture);

				viewHolder.isnew = (TextView) row
						.findViewById(R.id.tv_review_isnew);
				viewHolder.usrPicture.setImagesStore(mImagesStore);
				viewHolder.usrPicture.setCacheDir(appSettings
						.getCacheDirImage());

				// int px = (int) (110.0f *
				// getResources().getDisplayMetrics().density);

				// viewHolder.usrPicture.setBoundBox(getWindowManager().getDefaultDisplay().getWidth()
				// - px);
				viewHolder.usrPicture.setCircle(true);

				viewHolder.picture.setImagesStore(mImagesStore);
				viewHolder.picture.setCacheDir(appSettings.getCacheDirImage());
				viewHolder.picture.setHeight(itemHeight);
				int h = itemHeight;
				viewHolder.date = (TextView) row
						.findViewById(R.id.tv_review_date);
				viewHolder.date.setTypeface(Typeface.createFromAsset(
						ReviewMainActivity.this.getAssets(),
						"fonts/HelveticaNeue.otf"));

				viewHolder.name = (TextView) row
						.findViewById(R.id.tv_review_username);
				viewHolder.date.setTypeface(Typeface.createFromAsset(
						ReviewMainActivity.this.getAssets(),
						"fonts/HelveticaNeue.otf"));

				viewHolder.title = (TextView) row
						.findViewById(R.id.tv_review_theme);

				row.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) row.getTag();
			}

			final ReviewQuery.Post post = getItem(i);
			long timestamp = Integer.valueOf(post.date);
			timestamp *= 1000;

			if (!post.date.equals("")) {
				Date date = new Date();
				date.setTime((long) timestamp);

				if (date != null) {

					viewHolder.date.setText(new SimpleDateFormat("MMMM", Locale
							.getDefault()).format(date)
							+ " "
							+ new SimpleDateFormat("dd", Locale.getDefault())
									.format(date));
					viewHolder.date.setTypeface(Typeface.createFromAsset(
							ReviewMainActivity.this.getAssets(),
							"fonts/ProximaNova-Bold.otf"));

				}
			}

			viewHolder.usrPicture.setImageURL(post.authPic, "123");
			viewHolder.picture.setImageURL(post.picture, "123");
			boolean isRead = appSettings.getReviewsSaved(String
					.valueOf(post.id));
			if (isRead) {
				viewHolder.isnew.setVisibility(View.GONE);
			} else {
				viewHolder.isnew.setVisibility(View.VISIBLE);
			}

			viewHolder.name.setText(post.authName);
			viewHolder.title.setText(Html.fromHtml(post.title));
			viewHolder.title.setTypeface(Typeface.createFromAsset(
					ReviewMainActivity.this.getAssets(),
					"fonts/ProximaNova-Reg.otf"));

			final String id = post.id;
			row.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					StatisticsHelper.sendReviewEvent(ReviewMainActivity.this,
							id, viewHolder.title.getText().toString());
					appSettings.setReviewsSavedId(id);
					Intent intent = new Intent();
					intent.putExtra("ID", id);
					intent.setClass(ReviewMainActivity.this,
							ReviewOneActivity.class);

					startActivity(intent);
				}
			});

			return row;
		}

		private class ViewHolder {
			public int id;
			public WebImageView2 picture;
			public WebImageView2 usrPicture;
			public TextView name;
			public TextView title;
			public TextView date;
			public TextView isnew;

		}
	}

	private class LoadTask extends QueryTask<Void, Void, ReviewQuery> {
		private final int LIMIT = 25;

		@Override
		protected ReviewQuery doInBackground(Void... params) {
			ReviewQuery query = new ReviewQuery(appSettings,
					adapter.getCount(), LIMIT);
			query.getResponse(WebQuery.GET);

			return query;
		}

		@Override
		protected void onPostExecute(ReviewQuery query) {
			if (checkResult(query)) {
				ReviewQuery.Data data = query.getData();
				if (data.posts.size() > 0) {
					adapter.addData(query.getData());
				}

				isEmpty = (data.posts.size() < LIMIT);
			}

			loading = false;
			super.onPostExecute(query);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_back:
			ReviewMainActivity.this.finish();

			break;

		}

	}
}
