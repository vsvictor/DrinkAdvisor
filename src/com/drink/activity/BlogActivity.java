
package com.drink.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.drink.ControlApplication;
import com.drink.R;
import com.drink.helpers.StatisticsHelper;
import com.drink.imageloader.WebImageView;
import com.drink.query.BlogQuery;
import com.drink.query.WebQuery;

public class BlogActivity extends BasicActivity 
{
	ListView listView;
	BlogListAdapter adapter;

	private boolean loading = false;
	private boolean isEmpty = false;

	public BlogActivity()
	{
		super(R.layout.activity_blog, false);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		listView = (ListView) findViewById(R.id.listView);
		adapter = new BlogListAdapter(getApplicationContext());
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new OnScrollListener() 
			{
				private boolean needLoading = false;
			
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
				{
					if ((totalItemCount > 0) && (!loading))
					{
						needLoading = (firstVisibleItem + visibleItemCount + 3 >= totalItemCount);
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
		
		loading = true;
		new LoadTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_blog, menu);
		return true;
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if (ControlApplication.isFirstBlog())
		{
			StatisticsHelper.sendReviewsEvent(this);
			
			ControlApplication.resetFirstBlog();
		}
	}
	
	private class BlogListAdapter extends BaseAdapter 
	{
		LayoutInflater layoutInflater;
		BlogQuery.Data data;
		private ViewHolder viewHolder;

		public BlogListAdapter(Context context) 
		{
			layoutInflater = LayoutInflater.from(context);
		}

		public void addData(BlogQuery.Data data) 
		{
			if (this.data == null)
			{
				this.data = data;
			}
			else 
			{
				this.data.posts.addAll(data.posts);
			}

			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			if (data == null) return 0;
			
			return data.posts.size();
		}

		@Override
		public BlogQuery.Post getItem(int arg0) 
		{
			return data.posts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) 
		{
			return arg0;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) 
		{
			View row = view;
			if (row == null) 
			{
				row = this.layoutInflater.inflate(R.layout.listitem_blog, null);
				
				viewHolder = new ViewHolder();
				viewHolder.picture = (WebImageView) row.findViewById(R.id.picture);
				viewHolder.picture.setImagesStore(mImagesStore);
				viewHolder.picture.setCacheDir(appSettings.getCacheDirImage());
				
				int px = (int) (110.0f * getResources().getDisplayMetrics().density);
				viewHolder.picture.setBoundBox(getWindowManager().getDefaultDisplay().getWidth() - px);
				
				viewHolder.day = (TextView) row.findViewById(R.id.number);
				viewHolder.month = (TextView) row.findViewById(R.id.month);
				viewHolder.text = (TextView) row.findViewById(R.id.text);
				viewHolder.title = (TextView) row.findViewById(R.id.title);
				
				row.setTag(viewHolder);
			} 
			else 
			{
				viewHolder = (ViewHolder) row.getTag();
			}		

			final BlogQuery.Post post = getItem(i);		
			long timestamp = Integer.valueOf(post.date);
			timestamp *= 1000;		
			
			if(!post.date.equals("")) 
			{
				Date date = new Date ();
				date.setTime((long)timestamp);
				if (date != null)
				{
					viewHolder.day.setText(String.valueOf(date.getDate()).toUpperCase(Locale.getDefault()));
					viewHolder.day.setTypeface(Typeface.createFromAsset(BlogActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
					viewHolder.month.setText(new SimpleDateFormat("MMMM", Locale.getDefault()).format(date));
					viewHolder.month.setTypeface(Typeface.createFromAsset(BlogActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				}
			}
			
			viewHolder.picture.setImageURL(post.picture, "123");

			String stText = "";
			int len = post.text.indexOf(".");
			if((len > 0) && ((len+1) <= post.text.length()))
			{
				stText = post.text.substring(0, len+1);
			}

        	while (stText.length() > 0)
        	{
        		if (stText.charAt(0) == '<')
        		{
        			int pos = 1;
        			while ((pos < stText.length()) && (stText.charAt(pos) != '>')) ++pos;
        			
        			if (pos < stText.length())
        			{
        				stText = stText.substring(pos + 1);
        			}
        		}
        		else if (stText.charAt(0) == '\r')
        		{
        			stText = stText.substring(1);
        		}
        		else if (stText.charAt(0) == '\n')
        		{
        			stText = stText.substring(1);
        		}
        		else if (stText.charAt(0) == '\t')
        		{
        			stText = stText.substring(1);
        		}
        		else if (stText.charAt(0) == ' ')
        		{
        			stText = stText.substring(1);
        		}
        		else
        		{
        			break;
        		}
        	}

        	viewHolder.text.setText(Html.fromHtml(stText));
			viewHolder.text.setTypeface(Typeface.createFromAsset(BlogActivity.this.getAssets(), "fonts/HelveticaNeue.otf"));
			
			viewHolder.title.setText(Html.fromHtml(post.title));
			viewHolder.title.setTypeface(Typeface.createFromAsset(BlogActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

			final String id = post.id;
			row.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						StatisticsHelper.sendReviewEvent(BlogActivity.this, id, viewHolder.title.getText().toString());
						
						Intent intent = new Intent();
						intent.putExtra("ID", id);
						intent.setClass(BlogActivity.this, ReviewActivity.class);
						
						startActivity(intent);
					}
				});

			return row;
		}

		private class ViewHolder 
		{
			public WebImageView picture;
			public TextView title;
			public TextView day;
			public TextView month;
			public TextView text;
		}
	}

	private class LoadTask extends QueryTask<Void, Void, BlogQuery> 
	{
		private final int LIMIT = 25;
		
		@Override
		protected BlogQuery doInBackground(Void... params) 
		{
			BlogQuery query = new BlogQuery(appSettings, adapter.getCount(), LIMIT);
			query.getResponse(WebQuery.GET);

			return query;
		}

		@Override
		protected void onPostExecute(BlogQuery query) 
		{
			if (checkResult(query))
			{
				BlogQuery.Data data = query.getData();
				if (data.posts.size() > 0)
				{
					adapter.addData(query.getData());
				}

				isEmpty = (data.posts.size() < LIMIT);
			}
			
			loading = false;
			
			super.onPostExecute(query);
		}
	}
}
