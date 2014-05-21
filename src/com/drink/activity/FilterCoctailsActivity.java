
package com.drink.activity;

import com.drink.R;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FilterCoctailsActivity extends BasicActivity 
{
	private ListView listView;
	private DrinksListAdapter listAdapter;

	public FilterCoctailsActivity() 
	{
		super(R.layout.activity_filter_cocktails);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mCaption.setText(R.string.basis);
		
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
		private DrinksQuery.Data data;
		private ViewHolder viewHolder;

		public void addData(DrinksQuery.Data data) 
		{
			if (this.data == null) 
			{
				this.data = new DrinksQuery(appSettings).getData();
				Drink drinkAny = new Drink();
				drinkAny.name = getString(R.string.coctail_filter_str_any);
				drinkAny.id = "";
				this.data.drinks.add(drinkAny);
				
				for (Drink drink : data.drinks)
				{
					if(drink.cocktails_drink)
					{
						this.data.drinks.add(drink);
					}
				}
			}
			else
			{
				for (Drink drink : data.drinks) 
				{
					if (drink.cocktails_drink)
					{
						this.data.drinks.add(drink);
					}
				}
			}
			
			notifyDataSetChanged();
		}

		public DrinksListAdapter(Context context) 
		{
			this.layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() 
		{
			if (this.data == null) return 0;
			
			return this.data.drinks.size();
		}

		@Override
		public Drink getItem(int position) 
		{
			if (this.data == null) return null;
			if (position < this.data.drinks.size())
			{
				return this.data.drinks.get(position);
			}
			
			return null;
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) 
		{
			final String id = data.drinks.get(i).id;
			view = this.layoutInflater.inflate(R.layout.listitem_filter_city, null);

			view.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						Intent intent = new Intent();
						intent.putExtra("ID", id);
						setResult(RESULT_OK, intent);
						
						finish();
					}
				});
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) view.findViewById(R.id.firstline);
			viewHolder.name.setTypeface(Typeface.createFromAsset(FilterCoctailsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
			viewHolder.name.setText(data.drinks.get(i).name);

			return view;
		}

		private class ViewHolder 
		{
			public TextView name;
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}
	}
}
