
package com.drink.activity;

import java.util.ArrayList;
import java.util.Locale;

import com.drink.R;
import com.drink.query.CitiesListQuery;
import com.drink.query.WebQuery;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class FilterCitiesActivity extends BasicActivity implements OnItemClickListener 
{
	public static final int MODE_SelectUserCity = 1; 
	public static final int MODE_SelectMeetingCity = 2;
	public static final int MODE_ChangeCity = 3;
	public static final int MODE_RecommendBar = 4;
	
	private static final int SELECT_BAR = 1;
	
	SearchView searchView;
	ListView listView;
	ListAdapter adapter;
	
	private int	mode;
	
	private ArrayList<CitiesListQuery.City> cities;
	private ArrayList<CitiesListQuery.City> allCities;

	public FilterCitiesActivity() 
	{
		super(R.layout.activity_filter_cities);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		mode = getIntent().getIntExtra("mode", MODE_SelectUserCity);
		
		super.onCreate(savedInstanceState);
		
		adapter = new ListAdapter(getApplicationContext());

		mCaption.setText(R.string.select_city);
		
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		listView.setAdapter(adapter);		
		
		searchView = (SearchView) findViewById(R.id.search);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() 
			{
				@Override
				public boolean onQueryTextSubmit(String query) 
				{
					return true;
				}
				
				@Override
				public boolean onQueryTextChange(String newText) 
				{
					applyFilter(newText);
					
					return true;
				}
			});
		
		new CitiesTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_filter_cities, menu);
		
		return true;
	}

	private class ListAdapter extends BaseAdapter 
	{
		private ViewHolder viewHolder;
		private LayoutInflater layoutInflater;

		public ListAdapter(Context context) 
		{
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() 
		{
			if (cities == null ) return 0;
			
			return cities.size();
		}

		@Override
		public CitiesListQuery.City getItem(int position) 
		{
			return cities.get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) 
		{
			CitiesListQuery.City city = cities.get(position);
			if (city.id == -1)
			{
				View row = this.layoutInflater.inflate(R.layout.listitem_filter_country, null);
				TextView name = (TextView) row.findViewById(R.id.name);
				name.setTypeface(Typeface.createFromAsset(FilterCitiesActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				name.setText(city.name.toUpperCase());
				
				return row;
			}
			
			View row = view;
			if ((row == null) || (row.getTag() == null)) 
			{
				row = this.layoutInflater.inflate(R.layout.listitem_filter_city, null);
				
				viewHolder = new ViewHolder();
				viewHolder.name = (TextView) row.findViewById(R.id.firstline);
				viewHolder.name.setTypeface(Typeface.createFromAsset(FilterCitiesActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

				row.setTag(viewHolder);
			}
			else
			{
				viewHolder = (ViewHolder) row.getTag();
			}
			viewHolder.name.setText(cities.get(position).name);
			
			return row;
		}
	}
	
	private class ViewHolder 
	{
		public TextView name;
	}
	
	private void applyFilter(String filter)
	{
		if (allCities == null) return;
		
		if ((filter == null) || (filter.length() == 0))
		{
			cities = allCities;
		}
		else
		{
			cities = new ArrayList<CitiesListQuery.City>();
			
			int lastCountry = -1;
			boolean country = false;
			for (int i = 0; i < allCities.size(); ++i)
			{
				CitiesListQuery.City city = allCities.get(i);
				if (city.id == -1)
				{
					lastCountry = i;
					country = false;
				}
				else if (city.name.toLowerCase(Locale.getDefault()).contains(filter.toLowerCase(Locale.getDefault())))
				{
					if (!country)
					{
						cities.add(allCities.get(lastCountry));
						country = true;
					}
					
					cities.add(city);
				}
			}
		}
		
		adapter.notifyDataSetChanged();
	}
	
	private class CitiesTask extends QueryTask<Void, Void, CitiesListQuery> 
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

			listView.setVisibility(View.GONE);
		}

		@Override
		protected CitiesListQuery doInBackground(Void... params) 
		{
			CitiesListQuery citiesListQuery = new CitiesListQuery();
			citiesListQuery.getResponse(WebQuery.GET);

			return citiesListQuery;
		}

		@Override
		protected void onPostExecute(CitiesListQuery query) 
		{
			if (checkResult(query)) 
			{
				allCities = query.getData().cities;
				cities = allCities;
				
				adapter.notifyDataSetChanged();
			}
			
			listView.setVisibility(View.VISIBLE);
			
			super.onPostExecute(query);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		if (cities == null) return;
		
		CitiesListQuery.City city = cities.get(position);
		if (city.id == -1) return;
		
		switch (mode)
		{
			case MODE_SelectUserCity:
			case MODE_ChangeCity:
			case MODE_RecommendBar:
			{
				Intent intent = new Intent();
				intent.putExtra("id", city.id);
				intent.putExtra("name", city.name);
				intent.putExtra("country", city.country);
				intent.putExtra("lat", city.lat);
				intent.putExtra("lng", city.lng);
				
				setResult(RESULT_OK, intent);
				
				finish();	
			}
			break;
			case MODE_SelectMeetingCity:
			{
				Intent intent = new Intent(FilterCitiesActivity.this, BarsActivity.class);
				intent.putExtra("mode", BarsActivity.MODE_SelectMeetingBar);
				intent.putExtra("meeting_title", getIntent().getStringExtra("meeting_title"));
				intent.putExtra("meeting_date", getIntent().getLongExtra("meeting_date", 0));
				intent.putExtra("meeting_comment", getIntent().getStringExtra("meeting_comment"));
				intent.putExtra("city_id", city.id);
				intent.putExtra("city_name", city.name);
				intent.putExtra("city_lat", city.lat);
				intent.putExtra("city_lng", city.lng);
				
				startActivityForResult(intent, SELECT_BAR);
			}
			break;
		}
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		super.onActivityResult(requestCode, resultCode, intent);
	
		switch (resultCode)
		{
			case RESULT_OK:
			{
				switch (requestCode)
				{
				case SELECT_BAR:
					setResult(RESULT_OK);
					finish();
					break;
				}
			}
			break;
			case RESULT_INTERNAL_CANCEL:
			{
				setResult(RESULT_INTERNAL_CANCEL);
				finish();
			}
			break;
		}
	}
}
