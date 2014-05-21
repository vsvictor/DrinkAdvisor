package com.drink.activity;

import com.drink.R;
import com.drink.activity.BasicActivity.QueryTask;
import com.drink.helpers.CommonHelper;
import com.drink.imageloader.WebImageView;
import com.drink.imageloader.WebImageView2;
import com.drink.imageloader.WebImageView3;
import com.drink.query.BarQuery;
import com.drink.query.CanCheckinQuery;
import com.drink.query.UserPhotoQuery;
import com.drink.query.WebQuery;
import com.drink.query.WrongInfoQuery;
import com.drink.utils.location.MyLocationService;

import android.location.Location;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserPhotoActivity extends BasicActivity 
{
	private static final int ADD_PHOTO = 8;
	
	private Gallery gallery;
	private ImageView ivPhotoUser;
	private ImageAdapter adapter;
	
	private String ID;
	private String Name;
	private boolean loading = false;
	private boolean isEmpty = false;
	boolean hasPhoto = false;
	
	public UserPhotoActivity() 
	{
		super(R.layout.activity_user_photo);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();

		ID = bundle.getString("ID");
		Name = getIntent().getStringExtra("Name");
		
		TextView tvCaption = (TextView) this.findViewById(R.id.tv_caption);
		tvCaption.setTypeface(Typeface.createFromAsset(UserPhotoActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		tvCaption.setText(Name);
		
		gallery = (Gallery) this.findViewById(R.id.gallery);
		
		adapter = new ImageAdapter(this);
		gallery.setAdapter(adapter);

		gallery.setOnItemSelectedListener(new OnItemSelectedListener()
	        {
	            @Override
	            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
	            {
					if (!isEmpty && !loading)
					{
						loading = true;
						new LoadTask().execute();
					}
	            }
	
				@Override
				public void onNothingSelected(AdapterView<?> arg0) 
				{
				}            
	        });
		TextView tvAddPhoto = (TextView) findViewById(R.id.tvAddBarPhoto);
		tvAddPhoto.setTypeface(Typeface.createFromAsset(UserPhotoActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		ivPhotoUser = (ImageView) this.findViewById(R.id.ivPhotoUser);
		ivPhotoUser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("bar_id", ID);
				intent.setClass(UserPhotoActivity.this, AddPhotoActivity.class);
				startActivityForResult(intent, ADD_PHOTO);
			}
		});
		loading = true;
		new LoadTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_user_photo, menu);
		return true;
	}
	public class ImageAdapter extends BaseAdapter
	{
		UserPhotoQuery.Data data;
		private ViewHolder viewHolder;		
	    private Context context;

	    public ImageAdapter(Context context)
	    {
	        this.context = context;
	    }

		public void clearData() 
		{
			this.data = null;
			notifyDataSetChanged();
		}
		
		public void addData(UserPhotoQuery.Data data) 
		{
			if (this.data == null) 
			{
				this.data = data;
			}
			else
			{
				this.data.photos.addAll(data.photos);
			}

			notifyDataSetChanged();
		}

	    @Override
	    public int getCount()
	    {
			if (this.data == null) return 0;
			
			return this.data.photos.size();
	    }

	    @Override
		public Object getItem(int position) 
	    {
			if (this.data == null) return null;
			if (position < this.data.photos.size()) return this.data.photos.get(position);
			
			return null;
		}

	    @Override
		public long getItemId(int position) 
	    {
			return position;
		}

	    @Override
	    public View getView( int i, View view, ViewGroup parent )
	    {
			View row = view;
			if (row == null) 
			{
				LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        //row = (RelativeLayout) inflater.inflate(R.layout.listitem_user_photo, null );
		        row = (RelativeLayout) inflater.inflate(R.layout.user_photo_new, null );
		        //row.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.MATCH_PARENT, Gallery.LayoutParams.MATCH_PARENT));
				
		        viewHolder = new ViewHolder();
				viewHolder.picture = (WebImageView3) row.findViewById(R.id.iv_user_photo);

				viewHolder.picture.setImagesStore(mImagesStore);
				viewHolder.picture.setCacheDir(appSettings.getCacheDirImage());
				viewHolder.picture.setSizeInDp(360, 300);
				
				//viewHolder.avatar = (WebImageView) row.findViewById(R.id.iv_avatar);
				viewHolder.avatar = (WebImageView2) row.findViewById(R.id.ivUserPictureAvatar);
				viewHolder.avatar.setImagesStore(mImagesStore);
				viewHolder.avatar.setCircle(true);
				viewHolder.avatar.setCacheDir(appSettings.getCacheDirImage());

				//viewHolder.name = (TextView) row.findViewById(R.id.tv_user_name);
				viewHolder.name = (TextView) row.findViewById(R.id.tvUserPhotoName);
				viewHolder.name.setTypeface(Typeface.createFromAsset(UserPhotoActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

				//viewHolder.comment = (TextView) row.findViewById(R.id.tv_comment_text);
				viewHolder.comment = (TextView) row.findViewById(R.id.tvUserPhotoTextComments);
				viewHolder.comment.setTypeface(Typeface.createFromAsset(UserPhotoActivity.this.getAssets(), "fonts/HelveticaNeue.otf"));

				viewHolder.oneOf = (TextView) row.findViewById(R.id.tv_one_of);
				viewHolder.oneOf.setTypeface(Typeface.createFromAsset(UserPhotoActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));

				row.setTag(viewHolder);
				TextView tvComm = (TextView) row.findViewById(R.id.tvUserPhotoComments);
				tvComm.setTypeface(Typeface.createFromAsset(UserPhotoActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));
			} 
			else 
			{
				viewHolder = (ViewHolder) row.getTag();
			}
	    	
			if (data.photos.get(i).picture != null)
			{
				viewHolder.picture.setImageURL(data.photos.get(i).picture, "123");
			}

			if (data.photos.get(i).avatar != null)
			{
				viewHolder.avatar.setImageURL(data.photos.get(i).avatar, "123");
			}

			viewHolder.name.setText(data.photos.get(i).user_name);
			
			if (data.photos.get(i).comment != null)
			{
				viewHolder.comment.setText(data.photos.get(i).comment);
			}
			else
			{
				viewHolder.comment.setVisibility(View.GONE);
			}
			
			viewHolder.oneOf.setText(Integer.toString(i + 1) + " " + "/" + " " + Integer.toString(data.photos.size()));
			
	        return row;
	    }
	
		private class ViewHolder 
		{
			public WebImageView3 picture;
			public WebImageView2 avatar;
			public TextView name;
			public TextView comment;
			public TextView oneOf;
		}
	}
	public class LoadTask extends QueryTask<Void, Void, UserPhotoQuery> 
	{
		private final int LIMIT = 10;
		
		@Override
		protected UserPhotoQuery doInBackground(Void... params) 
		{
			UserPhotoQuery photosQuery = new UserPhotoQuery(appSettings, ID, adapter.getCount(), LIMIT);
			photosQuery.getResponse(WebQuery.GET);
			
			return photosQuery;
		}

		@Override
		protected void onPostExecute(UserPhotoQuery query) 
		{
			if (checkResult(query))
			{
				UserPhotoQuery.Data data = query.getData();
				if (data.error_code == -1)
				{
					if (data.photos.size() > 0)
					{
						adapter.addData(data);
					}

					isEmpty = (data.photos.size() < LIMIT);
				}
				else
				{
					showErrorBox(data.error_code, (adapter.getCount() == 0));
				}
			}
			
			loading = false;
			
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
			case ADD_PHOTO:
				if (!hasPhoto)
				{
					hasPhoto = data.getBooleanExtra("hasPhoto", false);
					new LoadTask().execute();
				}
				break;
			}
		}
	}

}
