
package com.drink.activity;

import com.drink.ControlApplication;
import com.drink.R;
import com.drink.helpers.StatisticsHelper;
import com.drink.imageloader.ImagesStore;
import com.drink.imageloader.WebImageView2;
import com.drink.query.CocktailsQuery;
import com.drink.query.CocktailsQuery.Data;
import com.drink.query.WebQuery;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout.LayoutParams;

public class CocktailsActivity extends BasicActivity 
{
	static private final int SELECT_DRINK = 20;
	
	private ListView listView;
	private CocktailsListAdapter adapter;
	private String ID;

	private boolean loading = false;
	private boolean isEmpty = false;

	public CocktailsActivity()
	{
		super(R.layout.activity_cocktails, false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		mCaption.setText(R.string.cocktails);
		
		Bundle bundle = getIntent().getExtras();

		ID = bundle.getString("ID");
		
		// add cocktail basis button
		Button btnBasis = new Button(this);
		btnBasis.setBackgroundResource(R.drawable.img_navigation_button);
		btnBasis.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		btnBasis.setTextSize(15.0f);
		btnBasis.setText(R.string.basis);
		btnBasis.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent();
				intent.setClass(CocktailsActivity.this,	FilterCoctailsActivity.class);
				
				startActivityForResult(intent, SELECT_DRINK);
			}
		});
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btnBasis.setLayoutParams(params);
		
		try 
		{
		    XmlResourceParser parser = getResources().getXml(R.color.button_login);
		    ColorStateList colors = ColorStateList.createFromXml(getResources(), parser);
		    btnBasis.setTextColor(colors);
		} 
		catch (Exception e) {}
		
		RelativeLayout rlNavigationBottom = (RelativeLayout) findViewById(R.id.rl_navigation_bottom);
		rlNavigationBottom.addView(btnBasis);

		listView = (ListView) findViewById(R.id.listView);
		adapter = new CocktailsListAdapter(getApplicationContext());
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new OnScrollListener() 
			{
				private boolean needLoading = false;
			
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
				{
					if ((totalItemCount > 0) && !loading)
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
		getMenuInflater().inflate(R.menu.activity_drinks, menu);
	
		return true;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		if (ControlApplication.isFirstCocktails())
		{
			StatisticsHelper.sendCocktailsEvent(this);
			
			ControlApplication.resetFirstCocktails();
		}
	}
	
	private class CocktailsListAdapter extends BaseAdapter 
	{
		CocktailsQuery.Data data;
		private LayoutInflater layoutInflater;

		public CocktailsListAdapter(Context context) 
		{
			layoutInflater = LayoutInflater.from(context);
		}

		public void addData(CocktailsQuery.Data data) 
		{
			if (this.data == null) 
			{
				this.data = data;
			}
			else
			{
				this.data.cocktails.addAll(data.cocktails);
			}
			
			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			if (this.data == null) return 0;
			
			return this.data.cocktails.size();
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) 
		{
			View row = view;
			ViewHolder viewHolder;
			
			if (row == null) 
			{
				row = this.layoutInflater.inflate(R.layout.listitem_cocktails, null);
				
				viewHolder = new ViewHolder();
				
				viewHolder.picture = (WebImageView2) row.findViewById(R.id.picture);
				viewHolder.picture.setImagesStore(mImagesStore);
				viewHolder.picture.setCacheDir(appSettings.getCacheDirImage());
				viewHolder.picture.setHeight(123);

				viewHolder.name = (TextView) row.findViewById(R.id.title);
				viewHolder.name.setTypeface(Typeface.createFromAsset(CocktailsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

				row.setTag(viewHolder);
			} 
			else 
			{
				viewHolder = (ViewHolder) row.getTag();
			}
			
			final int c_id = data.cocktails.get(i).id;
			final String drink_id = data.cocktails.get(i).coctail_drink_id;
			final String c_name = data.cocktails.get(i).name;
			row.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						StatisticsHelper.sendCocktailEvent(CocktailsActivity.this, String.valueOf(c_id), c_name);

						Intent intent = new Intent();
						intent.putExtra("ID", c_id);
						intent.putExtra("d_id", drink_id);
						intent.putExtra("c_name", c_name);
						intent.setClass(CocktailsActivity.this,	CocktailActivity.class);
						
						startActivity(intent);
					}
				});

			viewHolder.picture.setImageURL(data.cocktails.get(i).picture, "123");
			
			viewHolder.name.setText(c_name);

			return row;
		}

		@Override
		public Object getItem(int position) 
		{
			if (this.data == null) return null;
			if (position < this.data.cocktails.size()) return this.data.cocktails.get(position);
			
			return null;
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		private class ViewHolder 
		{
			public WebImageView2 picture;
			public TextView name;
		}
	}

	public class LoadTask extends QueryTask<Void, Void, CocktailsQuery> 
	{
		private final int LIMIT = 25;
		
		@Override
		protected CocktailsQuery doInBackground(Void... params) 
		{
			mImagesStore = new ImagesStore(appSettings.getCacheDirImage());
			CocktailsQuery cocktailsQuery = new CocktailsQuery(appSettings, ID, adapter.getCount(), 25);
			cocktailsQuery.getResponse(WebQuery.GET);
			
			return cocktailsQuery;
		}

		@Override
		protected void onPostExecute(CocktailsQuery query) 
		{
			if (checkResult(query)) 
			{
				Data data = query.getData();
				if (data.cocktails.size() > 0) 
				{
					adapter.addData(query.getData());
				}
				
				isEmpty = (data.cocktails.size() < LIMIT);
			}
			
			loading = false;
			
			super.onPostExecute(query);
		}
	}

	@Override
	protected void onDestroy() 
	{
		mImagesStore.recycle();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if ((resultCode == RESULT_OK) && (requestCode == SELECT_DRINK)) 
		{
			Bundle bundle = data.getExtras();
			ID = bundle.getString("ID");
			
			adapter = new CocktailsListAdapter(getApplicationContext());
			listView.setAdapter(adapter);

			loading = true;
			new LoadTask().execute();
		}
		else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
