
package com.drink.activity;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.drink.R;
import com.drink.imageloader.WebImageView;
import com.drink.query.PlacesQuery;
import com.drink.query.PlacesQuery.PlaceBar;
import com.drink.query.WebQuery;

public class PlacesActivity extends BasicActivity 
{
	static final int PLACES_QUERY_LIMIT = 20;

	TextView tvNoPlaces;
	ListView mPlacesListView;
	int mOffset = 0;
	
	ArrayList<PlaceBar> mPlaces;
	PlacesAdapter mAdapter;
	int mPlacesCount;

	public PlacesActivity()
	{
		super(R.layout.activity_places, false);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		tvNoPlaces = (TextView) findViewById(R.id.no_places);
		tvNoPlaces.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		tvNoPlaces.setVisibility(View.GONE);
		
		mPlacesListView = (ListView) findViewById(R.id.places_list);
		mPlacesListView.setVisibility(View.GONE);
		
		mAdapter = new PlacesAdapter();
		mPlacesListView.setAdapter(mAdapter);
		
		mPlacesListView.setClickable(true);
		mPlacesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) 
				{
					Intent intent = new Intent();
					intent.putExtra("ID", mPlaces.get(position).mId);
					intent.setClass(PlacesActivity.this, BarActivity.class);

					startActivity(intent);
				}
			});
		
		new PlacesLoader().execute();
	}

	class PlacesLoader extends QueryTask<Void, Void, PlacesQuery> 
	{
		@Override
		protected PlacesQuery doInBackground(Void... params) 
		{
			PlacesQuery query = new PlacesQuery(getBaseContext());
			query.setLimit(PLACES_QUERY_LIMIT);
			query.setOffset(mOffset);
			query.getResponse(WebQuery.GET);
			
			return query;
		}

		@Override
		protected void onPostExecute(PlacesQuery query) 
		{
			if (query.getResult())
			{
				if (mPlaces == null)
				{
					mPlaces = query.getData().mBarPlaces;
				}
				else
				{
					mPlaces.addAll(query.getData().mBarPlaces);
				}
				mAdapter.notifyDataSetChanged();
				
				if (mPlaces.size() > 0)
				{
					mPlacesListView.setVisibility(View.VISIBLE);
				}
				else
				{
					tvNoPlaces.setVisibility(View.VISIBLE);
				}
			}
			else
			{
				if ((mPlaces == null) || (mPlaces.size() == 0))
				{
					tvNoPlaces.setVisibility(View.VISIBLE);
				}
			}
			
			super.onPostExecute(query);
		}

	}

	class PlacesAdapter extends BaseAdapter 
	{
		LayoutInflater mInflater;

		public PlacesAdapter() 
		{
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() 
		{
			if (mPlaces == null) return 0;

			return mPlaces.size();
		}

		@Override
		public Object getItem(int arg0) 
		{
			return null;
		}

		@Override
		public long getItemId(int arg0) 
		{
			return 0;
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) 
		{
			ViewHolder viewHolder = null;
			View row = view;
			if (row == null) 
			{
				row = mInflater.inflate(R.layout.activity_places_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mText = (TextView) row.findViewById(R.id.places_bar_name);
				viewHolder.mImage = (WebImageView) row.findViewById(R.id.places_event_avatar);
				viewHolder.mImage.setImagesStore(mImagesStore);
				viewHolder.mImage.setCacheDir(appSettings.getCacheDirImage());
				viewHolder.mAddress = (TextView) row.findViewById(R.id.place_bar_address);
				viewHolder.mTime = (TextView) row.findViewById(R.id.place_bar_time);
				viewHolder.mRatingText = (TextView) row.findViewById(R.id.place_rating_text);
				viewHolder.mRating =  (ImageView)row.findViewById(R.id.places_rating);
				
				row.setTag(viewHolder);
			} 
			else 
			{
				viewHolder = (ViewHolder) row.getTag();
			}
			
			PlaceBar bar = mPlaces.get(i);
			if (bar.mImage != null) 
			{
				viewHolder.mImage.setImageURL(bar.mImage, "123");
			}

			viewHolder.mText.setText(bar.mName);
			viewHolder.mAddress.setText(bar.mAddress);
			viewHolder.mTime.setText(bar.mDate);
			viewHolder.mRatingText.setText(String.valueOf(bar.mRating) + "%");
			Bitmap ratingBitmap = getRatingBitmap(bar.mRating);
			viewHolder.mRating.setImageBitmap(ratingBitmap);

			if (i == mPlaces.size() - 1 && i < mPlacesCount - 1) 
			{
				mOffset = mPlaces.size();
			}

			return row;
		}

		class ViewHolder 
		{
			WebImageView mImage;
			TextView mText;
			TextView mTime;
			TextView mAddress;
			TextView mRatingText;
			ImageView mRating;
			ImageView mCheckinIcon;
		}
		
		private Bitmap getRatingBitmap(int rating)
		{
			Bitmap bmpSrc = BitmapFactory.decodeResource(getResources(), R.drawable.bar_one_drink);

			Bitmap bmpBG = BitmapFactory.decodeResource(getResources(), R.drawable.bar_one_drink_bg);
			int x = bmpBG.getWidth() - bmpBG.getWidth();
			int y = bmpBG.getHeight() - (int) (bmpBG.getHeight() * Integer.valueOf(rating) / 100);

			Bitmap bmpSrcResized = Bitmap.createBitmap(bmpBG, x, y, bmpBG.getWidth(), bmpBG.getHeight() - y);
			int y_height = bmpSrc.getHeight() - bmpBG.getHeight();
			Bitmap target = Bitmap.createBitmap(bmpSrc.getWidth(), bmpSrc.getHeight(), Bitmap.Config.ARGB_8888);

			Canvas c = new Canvas(target);
			c.setDensity(Bitmap.DENSITY_NONE);
			c.drawBitmap(bmpSrc, 0, 0, null);
			c.drawBitmap(bmpSrcResized, x, y + y_height, null);
			
			return target;
		}
	}
}
