package com.drink.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.imageloader.WebImageView;
import com.drink.query.BlogPostQuery;
import com.drink.query.CanCommentQuery;
import com.drink.query.CommentsQuery;
import com.drink.query.WebQuery;
import com.drink.types.Comment;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class ReviewActivity extends BasicActivity 
{
	private final int NEED_LOGIN = 0;
	private final int POST_COMMENT = 1;
	
	private String ID;
	BlogPostQuery.Data data;
	
	private ListView listView;
	private CommentsAdapter adapter;
	private LinearLayout header;
	
	private TextView tvTitle;
	private TextView tvMonth;
	private TextView tvDay;
	private TextView tvComment;
	private WebView wvText;
	private WebImageView picture;
	
	private boolean empty = false;
	private boolean loading = false;
	
	public ReviewActivity() 
	{
		super(R.layout.activity_review);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		ID = getIntent().getStringExtra("ID");
		
		listView = (ListView)findViewById(R.id.listView);
		adapter = new CommentsAdapter(getApplicationContext());
		header = (LinearLayout) getLayoutInflater().inflate(R.layout.header_review, (ViewGroup) findViewById(R.id.ll_content));
		header.setVisibility(View.GONE);
		listView.addHeaderView(header, null, false);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new OnScrollListener() 
		{
			private boolean needLoading = false;
		
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
			{
				if ((totalItemCount > 0) && (!loading))
				{
					needLoading = (firstVisibleItem + visibleItemCount >= totalItemCount);
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) 
			{
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) 
				{
					if (!empty && needLoading && !loading)
					{
						loading = true;
						needLoading = false;
						
						new LoadCommentsTask().execute();
					}
				}
			}
		});

		tvTitle = (TextView) findViewById(R.id.title);
		tvTitle.setTypeface(Typeface.createFromAsset(ReviewActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

		tvMonth = (TextView)findViewById(R.id.month);
		tvMonth.setTypeface(Typeface.createFromAsset(ReviewActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));

		tvDay = (TextView)findViewById(R.id.number);
		tvDay.setTypeface(Typeface.createFromAsset(ReviewActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));

		picture = (WebImageView) findViewById(R.id.picture);
		picture.setImagesStore(mImagesStore);
		picture.setCacheDir(appSettings.getCacheDirImage());
		
		int px = (int) (100.0f * getResources().getDisplayMetrics().density);
		picture.setBoundBox(getWindowManager().getDefaultDisplay().getWidth() - px);

		wvText = (WebView) findViewById(R.id.text);
		
		tvComment = (TextView) findViewById(R.id.tv_comment);
		tvComment.setVisibility(View.GONE);
		
		Button add_comment = (Button)findViewById(R.id.add_comment);
		add_comment.setTypeface(Typeface.createFromAsset(ReviewActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		add_comment.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (checkLogin(NEED_LOGIN))
				{
					new CheckAddCommentTask().execute();
				} 
			}
		});

		new LoadTask().execute();
	}

	private void updateView() 
	{
		tvTitle.setText(data.title);
		picture.setImageURL(data.picture, "123");
		
		wvText.loadDataWithBaseURL("file:///android_asset/", data.text, "text/html", "utf-8", null);
		
		String dtStart = data.date;
		long timestamp = Integer.valueOf(data.date);
		timestamp *= 1000;		
		if (!dtStart.equals("")) 
		{
			Date date = new Date ();
			date.setTime((long) timestamp);
			if (date != null)
			{
				tvMonth.setText(new SimpleDateFormat("MMMM", Locale.getDefault()).format(date));
				tvDay.setText(String.valueOf(date.getDate()).toUpperCase());
			}
		}
		
		tvComment.setVisibility(data.comments.size() > 0 ? View.VISIBLE : View.GONE);
		
		adapter.notifyDataSetChanged();
	}

	private class LoadTask extends QueryTask<Void, Void, BlogPostQuery> 
	{
		@Override
		protected BlogPostQuery doInBackground(Void... params)
		{
			BlogPostQuery query = new BlogPostQuery(ReviewActivity.this, ID);

			return query;
		}

		@Override
		protected void onPostExecute(BlogPostQuery query) 
		{
			if (checkResult(query))
			{
				data = query.getData();
				if (data.comments.size() == 3)
				{
					data.comments.remove(2);
					empty = false;
				}
				else
				{
					empty = true;
				}
				updateView();

				header.setVisibility(View.VISIBLE);
			}
			
			super.onPostExecute(query);
		}
	}
	
	private class LoadCommentsTask extends QueryTask<Void, Void, CommentsQuery> 
	{
		private final int LIMIT = 25;
		
		@Override
		protected CommentsQuery doInBackground(Void... params)
		{
			CommentsQuery query = new CommentsQuery(appSettings, ID, CommentsQuery.BLOG_POSTS, data.comments.size(), LIMIT);
			query.getResponse(WebQuery.GET);

			return query;
		}

		@Override
		protected void onPostExecute(CommentsQuery query) 
		{
			if (checkResult(query))
			{
				CommentsQuery.Data _data = query.getData();
				
				data.comments.addAll(_data.comments);
				adapter.notifyDataSetChanged();
				
				empty = (_data.comments.size() < LIMIT);
			}
			
			loading = false;
			
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
					intent.setClass(ReviewActivity.this, AddTextCommentActivity.class);
					intent.putExtra("ID", ID);
					intent.putExtra("type", CommentsQuery.BLOG_POSTS);
					
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

	private class CommentsAdapter extends BaseAdapter 
	{
		LayoutInflater layoutInflater;		

		public CommentsAdapter(Context context) 
		{
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() 
		{
			if (data == null || data.comments == null) return 0;
			
			return data.comments.size();
		}

		@Override
		public Object getItem(int arg0) 
		{
			return arg0;
		}

		@Override
		public long getItemId(int arg0) 
		{
			return arg0;
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) 
		{
			ViewHolder viewHolder;
			View row = view;
			if (row == null) 
			{
				row = this.layoutInflater.inflate(R.layout.listitem_comment, parent, false);
				
				viewHolder = new ViewHolder();
				viewHolder.logo = (WebImageView) row.findViewById(R.id.iv_avatar);
				viewHolder.name = (TextView) row.findViewById(R.id.tv_user_name);
				viewHolder.time = (TextView) row.findViewById(R.id.tv_time);
				viewHolder.text = (TextView) row.findViewById(R.id.tv_comment_text);
				
				row.findViewById(R.id.iv_like).setVisibility(View.GONE);
				row.findViewById(R.id.tv_translate).setVisibility(View.GONE);
				
				row.setTag(viewHolder);
			} 
			else 
			{
				viewHolder = (ViewHolder) row.getTag();
			}			
			
			Comment comment = data.comments.get(i);
				
			viewHolder.name.setTypeface(Typeface.createFromAsset(ReviewActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
			viewHolder.name.setText(comment.user_name);

			viewHolder.time.setTypeface(Typeface.createFromAsset(ReviewActivity.this.getAssets(), "fonts/HelveticaNeue.otf"));
			viewHolder.time.setText(comment.created_at);

			viewHolder.text.setTypeface(Typeface.createFromAsset(ReviewActivity.this.getAssets(), "fonts/HelveticaNeue.otf"));
			viewHolder.text.setText(comment.text);

			if(!comment.user_picture.equals(""))
			{
				viewHolder.logo.setImagesStore(mImagesStore);
				viewHolder.logo.setCacheDir(appSettings.getCacheDirImage());
				viewHolder.logo.setImageURL(comment.user_picture, "123");
			}
			
			return row;
		}
		
		private class ViewHolder 
		{
			public WebImageView logo;
			public TextView name;
			public TextView time;
			public TextView text;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) 
		{
			if (requestCode == NEED_LOGIN) 
			{
				new CheckAddCommentTask().execute();
			}
			else if (requestCode == POST_COMMENT)
			{
				new LoadTask().execute();
			}
		}
	}
}
