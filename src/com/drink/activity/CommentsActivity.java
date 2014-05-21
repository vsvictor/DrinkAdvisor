package com.drink.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.imageloader.ImagesStore;
import com.drink.imageloader.WebImageView;
import com.drink.imageloader.WebImageView2;
import com.drink.query.CommentsQuery;
import com.drink.query.CanCommentQuery;
import com.drink.query.TranslatedCommentQuery;
import com.drink.query.WebQuery;
import com.drink.types.Comment;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class CommentsActivity extends BasicActivity 
{
	private final int NEED_LOGIN = 0;
	private final int POST_COMMENT = 1;
	
	ListView listView;
	CommentsListAdapter listAdapter;
	TextView tvNoReviews;

	String ID;
	String type;

	private boolean loading = false;
	private boolean isEmpty = false;

	public CommentsActivity() 
	{
		super(R.layout.activity_comments);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		ID = bundle.getString("ID");
		type = bundle.getString("type");
		
		String name = bundle.getString("bar_name");
		if (name != null)
		{
			mCaption.setText(name);
			mCaption.setTextColor(Color.WHITE);
			mCaption.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		}

		if (type == null) 
		{
			type = CommentsQuery.BARS;
		}

		listView = (ListView) findViewById(R.id.listView);
		
		LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.header_reviews, null);
		listView.addHeaderView(header, null, false);
		
		listAdapter = new CommentsListAdapter(getApplicationContext());
		listView.setAdapter(listAdapter);
		listView.setOnScrollListener(new OnScrollListener() 
		{
			private boolean needLoading = false;
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
			{
				if ((totalItemCount > 0) && (!loading))
				{
					needLoading = firstVisibleItem + visibleItemCount + 5 >= totalItemCount;
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
						
						new LoadTask().execute();
					}
				}
			}
		});

		tvNoReviews = (TextView) findViewById(R.id.tv_no_reviews);
		tvNoReviews.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tvNoReviews.setVisibility(View.GONE);
		
		Button btnAddComment = (Button) findViewById(R.id.btn_add_review);
		btnAddComment.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnAddComment.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					if (checkLogin(NEED_LOGIN))
					{
						new CheckAddCommentTask().execute();
					}
				}
			});
		
		loading = true;
		new LoadTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_comments, menu);
		return true;
	}

	@Override
	protected void onDestroy() 
	{
		mImagesStore.recycle();
		super.onDestroy();
	}

	private class CommentsListAdapter extends BaseAdapter 
	{
		private LayoutInflater layoutInflater;
		private CommentsQuery.Data data;
		private ViewHolder viewHolder;

		public CommentsListAdapter(Context context) 
		{
			this.layoutInflater = LayoutInflater.from(context);
		}

		public void addData(CommentsQuery.Data data) 
		{
			if (this.data == null)
			{
				this.data = data;
			}
			else
			{
				this.data.comments.addAll(data.comments);
			}
			
			notifyDataSetChanged();
		}

		public void clearData() 
		{
			this.data = null;
			
			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			if (this.data == null) return 0;
			
			return this.data.comments.size();
		}

		@Override
		public Comment getItem(int arg0) 
		{
			return data.comments.get(arg0);
		}

		@Override
		public long getItemId(int arg0) 
		{
			return arg0;
		}

		@Override
		public View getView(int i, View view, ViewGroup parrent) 
		{
			View row = view;
			if (row == null) 
			{
				row = this.layoutInflater.inflate(R.layout.listitem_comment, null);

				viewHolder = new ViewHolder();
				viewHolder.logo = (WebImageView2) row.findViewById(R.id.iv_avatar);
				viewHolder.logo.setCircle(true);
				viewHolder.logo.setImagesStore(mImagesStore);
				viewHolder.logo.setCacheDir(appSettings.getCacheDirImage());
				viewHolder.name = (TextView) row.findViewById(R.id.tv_user_name);
				viewHolder.name.setTypeface(Typeface.createFromAsset(CommentsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				viewHolder.time = (TextView) row.findViewById(R.id.tv_time);
				viewHolder.time.setTypeface(Typeface.createFromAsset(CommentsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				viewHolder.text = (TextView) row.findViewById(R.id.tv_comment_text);
				viewHolder.text.setTypeface(Typeface.createFromAsset(CommentsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				viewHolder.like = (ImageView) row.findViewById(R.id.iv_like);

				viewHolder.translate = (TextView) row.findViewById(R.id.tv_translate);
				viewHolder.translate.setTypeface(Typeface.createFromAsset(CommentsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				viewHolder.translate.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						Integer ind = (Integer) viewHolder.translate.getTag();
						new TranslateTask(ind).execute();
					}
				});
				
				row.setTag(viewHolder);
			} 
			else 
			{
				viewHolder = (ViewHolder) row.getTag();
			}

			Comment comment = getItem(i);

			if (!comment.translated && !comment.id.equals("")) 
			{
				if (!comment.text_lang.equals(Locale.getDefault().getLanguage())) 
				{
					viewHolder.translate.setTag(i);
					viewHolder.translate.setVisibility(View.VISIBLE);
				} 
				else 
				{
					viewHolder.translate.setVisibility(View.GONE);
				}
			} 
			else 
			{
				viewHolder.translate.setVisibility(View.GONE);
			}

			viewHolder.name.setText(comment.user_name);

			if (comment.created_at != null && !comment.created_at.equals("")) 
			{
				String stDateTime = "";
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
				Date date = null;
				try 
				{
					date = df.parse(comment.created_at);
				} 
				catch (ParseException e) 
				{
					e.printStackTrace();
				}
				
				if (date != null) 
				{
					SimpleDateFormat dateformatDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
					SimpleDateFormat dateformatTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
					dateformatDate.format(date);
					Date dateNow = new Date();
					if (dateNow.getYear() == date.getYear() && dateNow.getMonth() == date.getMonth() && dateNow.getDate() == date.getDate()) 
					{
						stDateTime = getText(R.string.reviews_text_write)
								+ " " + getText(R.string.reviews_text_write_time)
								+ " " + dateformatTime.format(date);
					} 
					else 
					{
						stDateTime = getText(R.string.reviews_text_write)
								+ " " + dateformatDate.format(date)
								+ " " + getText(R.string.reviews_text_write_time)
								+ " " + dateformatTime.format(date);
					}

					viewHolder.time.setText(stDateTime);
				} 
				else 
				{
					viewHolder.time.setVisibility(View.GONE);
				}
			} 
			else 
			{
				viewHolder.time.setVisibility(View.GONE);
			}

			viewHolder.text.setText(comment.text);

			if (!comment.user_picture.equals("")) 
			{
				viewHolder.logo.setImageURL(comment.user_picture, "123");
			}

			if (comment.bar_rating.equals("1")) 
			{
				viewHolder.like.setImageResource(R.drawable.img_report_like);
			} 
			else 
			{
				viewHolder.like.setImageResource(R.drawable.img_report_dislike);
			}

			return row;
		}

		private class ViewHolder 
		{
			public WebImageView2 logo;
			public ImageView like;
			public TextView name;
			public TextView time;
			public TextView text;
			public TextView translate;
		}
	}

	private class LoadTask extends QueryTask<Void, Void, CommentsQuery> 
	{
		private final int LIMIT = 25;

		@Override
		protected CommentsQuery doInBackground(Void... params) 
		{
			mImagesStore = new ImagesStore(appSettings.getCacheDirImage());

			CommentsQuery commentsQuery = new CommentsQuery(appSettings, ID, type, listAdapter.getCount(), LIMIT);
			commentsQuery.getResponse(WebQuery.GET);
			
			return commentsQuery;
		}

		@Override
		protected void onPostExecute(CommentsQuery query) 
		{
			if (checkResult(query)) 
			{
				CommentsQuery.Data data = query.getData();
				if (data.comments.size() > 0) 
				{
					listAdapter.addData(query.getData());
				}

				isEmpty = (data.comments.size() < LIMIT);
			}

			loading = false;
			
			if (listAdapter.getCount() == 0)
			{
				tvNoReviews.setVisibility(View.VISIBLE);
			}
			
			super.onPostExecute(query);
		}
	}

	private class CheckAddCommentTask extends QueryTask<Void, Void, CanCommentQuery> 
	{
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
					Intent intent = new Intent();
					intent.putExtra("id", ID);
					intent.putExtra("MODEL", type);
					intent.putExtra("bar_name", getIntent().getStringExtra("bar_name"));
					intent.setClass(CommentsActivity.this, AddCommentsActivity.class);
					
					startActivityForResult(intent, POST_COMMENT);
				}
				else
				{
					showErrorBox(CommonHelper.getRemainingTimePhrase(data.mMinutes), false);
				}
			}
			
			super.onPostExecute(query);
		}
	}

	private class TranslateTask extends QueryTask<Void, Void, TranslatedCommentQuery> 
	{
		private int ind;

		public TranslateTask(int ind) 
		{
			super();
			
			this.ind = ind;
		}

		@Override
		protected TranslatedCommentQuery doInBackground(Void... arg0) 
		{
			Comment comment = listAdapter.getItem(ind);
			
			TranslatedCommentQuery query = new TranslatedCommentQuery(appSettings, comment.id);
			query.getResponse(WebQuery.GET);

			return query;
		}

		@Override
		protected void onPostExecute(TranslatedCommentQuery query) 
		{
			if (checkResult(query))
			{
				listAdapter.data.comments.get(ind).text = query.getData().text;
				listAdapter.data.comments.get(ind).translated = true;
				listAdapter.notifyDataSetChanged();
			}
			
			super.onPostExecute(query);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) 
		{
			switch (requestCode)
			{
			case NEED_LOGIN:
				new CheckAddCommentTask().execute();
				break;
			case POST_COMMENT:
				tvNoReviews.setVisibility(View.GONE);
				listAdapter.clearData();
				loading = true;
				new LoadTask().execute();
				break;
			}
		}
	}
}
