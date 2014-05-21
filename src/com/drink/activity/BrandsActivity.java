
package com.drink.activity;

import java.util.ArrayList;
import com.drink.ControlApplication;
import com.drink.R;
import com.drink.helpers.StatisticsHelper;
import com.drink.imageloader.WebImageView2;
import com.drink.query.BrandsQuery;
import com.drink.query.WebQuery;
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

public class BrandsActivity extends BasicActivity 
{
	private ListView listView;
	private BrandsAdapter listAdapter;

	private String ID;

	public BrandsActivity() 
	{
		super(R.layout.activity_brands);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mCaption.setText(getIntent().getStringExtra("drink_name"));

		listAdapter = new BrandsAdapter(getApplicationContext());
		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(listAdapter);

		Bundle bundle = getIntent().getExtras();
		ID = bundle.getString("ID");
		
		new LoadTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_brands, menu);
		return true;
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if (ControlApplication.isFirstBrands())
		{
			StatisticsHelper.sendBrandsEvent(this);
			
			ControlApplication.resetFirstBrands();
		}
	}
	
	public class BrandsAdapter extends BaseAdapter 
	{
		private LayoutInflater layoutInflater;
		private ArrayList<BrandsQuery.Brand> brands;

		public BrandsAdapter(Context context) 
		{
			this.layoutInflater = LayoutInflater.from(context);
		}

		public void addData(BrandsQuery.Data data) 
		{
			if (brands == null)
			{
				brands = data.brands;
			}
			else 
			{
				brands.addAll(data.brands);
			}

			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			if (brands == null) return 0;

			return brands.size();
		}

		@Override
		public BrandsQuery.Brand getItem(int arg0) 
		{
			if (brands == null) return null;
			
			return brands.get(arg0);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int i, View view, ViewGroup arg2) 
		{
			final int id = brands.get(i).id;
			final String name = brands.get(i).name;
			
			view = this.layoutInflater.inflate(R.layout.listitem_brands, null);
			view.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					StatisticsHelper.sendBrandEvent(BrandsActivity.this, String.valueOf(id), name);

					Intent intent = new Intent();
					intent.putExtra("ID", id);
					intent.putExtra("name", name);
					intent.setClass(BrandsActivity.this, BrandActivity.class);
					startActivity(intent);
				}
			});
			
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.picture = (WebImageView2) view.findViewById(R.id.picture);
			viewHolder.name = (TextView) view.findViewById(R.id.title);
			viewHolder.name.setTypeface(Typeface.createFromAsset(BrandsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
			viewHolder.name.setText(brands.get(i).name);

			viewHolder.picture.setImagesStore(mImagesStore);
			viewHolder.picture.setCacheDir(appSettings.getCacheDirImage());
			viewHolder.picture.setHeight(123);
			viewHolder.picture.setImageURL(brands.get(i).pic, "123");

			return view;
		}

		private class ViewHolder 
		{
			public WebImageView2 picture;
			public TextView name;
		}
	}

	public class LoadTask extends QueryTask<Void, Void, BrandsQuery> 
	{
		@Override
		protected BrandsQuery doInBackground(Void... params) 
		{
			BrandsQuery brandsQuery = new BrandsQuery(ID);
			brandsQuery.setDeviceToken(appSettings.getContentToken());
			brandsQuery.setUserToken(appSettings.getUserToken());
			brandsQuery.getResponse(WebQuery.GET);

			return brandsQuery;
		}

		@Override
		protected void onPostExecute(BrandsQuery query) 
		{
			if (checkResult(query))
			{
				BrandsQuery.Data data = query.getData();
				listAdapter.addData(data);
			}

			super.onPostExecute(query);
		}
	}
}
