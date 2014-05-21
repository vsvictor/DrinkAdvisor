
package com.drink.activity;

import java.util.ArrayList;
import com.drink.ControlApplication;
import com.drink.R;
import com.drink.helpers.StatisticsHelper;
import com.drink.imageloader.WebImageView2;
import com.drink.query.DrinksQuery;
import com.drink.query.WebQuery;
import com.drink.types.Drink;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DrinksActivity extends BasicActivity 
{
	private ListView listView;
	private DrinksListAdapter listAdapter;

	public DrinksActivity()
	{
		super(R.layout.activity_drinks, false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	
		mCaption.setText(getString(R.string.drinks));
		
		listAdapter = new DrinksListAdapter(getApplicationContext());
		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(listAdapter);

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
		
		if (ControlApplication.isFirstDrinks())
		{
			StatisticsHelper.sendDrinksEvent(this);
			
			ControlApplication.resetFirstDrinks();
		}
	}
	
	private class LoadTask extends QueryTask<Void, Void, DrinksQuery> 
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

			listView.setVisibility(View.GONE);
		}

		@Override
		protected DrinksQuery doInBackground(Void... params) 
		{
			DrinksQuery drinksQuery = new DrinksQuery(appSettings);
			drinksQuery.getResponse(WebQuery.GET);

			return drinksQuery;
		}

		@Override
		protected void onPostExecute(DrinksQuery query) 
		{
			if (checkResult(query)) 
			{
				DrinksQuery.Data data = query.getData();

				if (data.count > 0) 
				{
					listAdapter.addData(data);
				}
			}

			listView.setVisibility(View.VISIBLE);

			super.onPostExecute(query);
		}
	}

	private class DrinksListAdapter extends BaseAdapter 
	{
		private LayoutInflater layoutInflater;
		private ArrayList<Drink> drinks;

		public DrinksListAdapter(Context context) 
		{
			layoutInflater = LayoutInflater.from(context);
		}

		public void addData(DrinksQuery.Data data) 
		{
			if (drinks == null)
			{
				drinks = data.drinks;
			}
			else
			{
				drinks.addAll(data.drinks);
			}

			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			if (drinks == null)	return 0;
			
			return drinks.size();
		}

		@Override
		public Drink getItem(int position) 
		{
			if (drinks == null) return null;

			return drinks.get(position);
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) 
		{
			final String id = drinks.get(i).id;
			final String name = drinks.get(i).name;
			
			view = this.layoutInflater.inflate(R.layout.listitem_drinks, null);
			view.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					StatisticsHelper.sendDrinkEvent(DrinksActivity.this, id, name);

					Intent intent = new Intent();
					intent.putExtra("ID", id);
					intent.putExtra("name", name);
					intent.setClass(DrinksActivity.this, DrinkActivity.class);
					startActivity(intent);
				}
			});
			
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.picture = (WebImageView2) view.findViewById(R.id.picture);
			viewHolder.name = (TextView) view.findViewById(R.id.title);
			viewHolder.name.setTypeface(Typeface.createFromAsset(DrinksActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
			viewHolder.name.setText(drinks.get(i).name);

			viewHolder.picture.setImagesStore(mImagesStore);
			viewHolder.picture.setCacheDir(appSettings.getCacheDirImage());
			viewHolder.picture.setHeight(123);
			viewHolder.picture.setImageURL(drinks.get(i).picture, "123");

			return view;
		}

		private class ViewHolder 
		{
			public WebImageView2 picture;
			public TextView name;
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}
	}
}
