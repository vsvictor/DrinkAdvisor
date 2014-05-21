package com.drink.activity;

import com.drink.R;
import com.drink.imageloader.WebImageView;
import com.drink.query.AcceptFriendshipOffer;
import com.drink.query.FriendsQuery;
import com.drink.query.RejectFriendshipOffer;
import com.drink.query.WebQuery;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class FriendsSuggestionsActivity extends BasicActivity 
{
	private ListView listView;
	private FriendsListAdapter adapter;

	private boolean needUpdate = false;
	
	public FriendsSuggestionsActivity() 
	{
		super(R.layout.activity_friends_suggestions);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		Button btnFindFriends = (Button) findViewById(R.id.btn_find_friends);
		btnFindFriends.setTypeface(Typeface.createFromAsset(FriendsSuggestionsActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnFindFriends.setOnClickListener(new OnClickListener() 
			{			
				@Override
				public void onClick(View arg0) 
				{
					Intent intent = new Intent();
					intent.setClass(FriendsSuggestionsActivity.this, FriendsSearchActivity.class);
					
					startActivity(intent);
					finish();
				}
			});
		
		final ToggleButton btnFriendOffer = (ToggleButton) findViewById(R.id.tgl_requests);
		btnFriendOffer.setTypeface(Typeface.createFromAsset(FriendsSuggestionsActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnFriendOffer.setChecked(true);
		btnFriendOffer.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					btnFriendOffer.setChecked(true);
				}
			});
		
		listView = (ListView) findViewById(R.id.listView);
		adapter = new FriendsListAdapter(getApplicationContext());
		listView.setAdapter(adapter);

		new LoadTask().execute();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_friends_suggestions, menu);
		
		return true;
	}
	
	@Override
	public void onBackPressed()  
	{
		if (needUpdate)
		{
			setResult(RESULT_OK);
		}
		
		super.onBackPressed();
	}
	
	private class FriendsListAdapter extends BaseAdapter 
	{
		private LayoutInflater layoutInflater;
		private FriendsQuery.Data data;

		public FriendsListAdapter(Context context) 
		{
			this.layoutInflater = LayoutInflater.from(context);
		}

		public void addData(FriendsQuery.Data data) 
		{
			if (this.data == null)
			{
				this.data = data;
			}
			else
			{
				this.data.friends.addAll(data.friends);
			}
			
			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			if (this.data == null) return 0;
			
			return data.friends.size();
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) 
		{
			ViewHolder viewHolder;
			View row = view;
			if (row == null) 
			{
				row = this.layoutInflater.inflate(R.layout.listitem_friend_suggestions, null);

				viewHolder = new ViewHolder();
				viewHolder.id =	data.friends.get(i).id;
				viewHolder.avatar = (WebImageView) row.findViewById(R.id.avatar);
				viewHolder.name = (TextView) row.findViewById(R.id.name);
				viewHolder.btn_ok = (TextView) row.findViewById(R.id.btn_ok);
				viewHolder.btn_cancel = (TextView) row.findViewById(R.id.btn_cancel);
				
				row.setTag(viewHolder);
			} 
			else 
			{
				viewHolder = (ViewHolder) row.getTag();
			}

			FriendsQuery.Friend friend = getItem(i);
			
			if(data.friends.get(i).picture != null && !data.friends.get(i).picture.equals(""))
			{
				viewHolder.avatar.setImagesStore(mImagesStore);
				viewHolder.avatar.setCacheDir(appSettings.getCacheDirImage());
				viewHolder.avatar.setImageURL(data.friends.get(i).picture, "123");
			}
			viewHolder.name.setTypeface(Typeface.createFromAsset(FriendsSuggestionsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
			SpannableString content = new SpannableString(friend.name);
			content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
			viewHolder.name.setText(content);
			
			final String ID = viewHolder.id;

			viewHolder.btn_ok.setTypeface(Typeface.createFromAsset(FriendsSuggestionsActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
			viewHolder.btn_ok.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View arg0) 
					{
						new AcceptFriendRequest(ID).execute();
						new LoadTask().execute();	
					}
				});		
			
			viewHolder.btn_cancel.setTypeface(Typeface.createFromAsset(FriendsSuggestionsActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
			viewHolder.btn_cancel.setOnClickListener(new OnClickListener() 
				{	
					@Override
					public void onClick(View arg0) 
					{	
						new RejectFriendRequest(ID).execute();
						new LoadTask().execute();	
					}
				});

			return row;
		}

		@Override
		public FriendsQuery.Friend getItem(int position) 
		{
			if (this.data == null) return null;
			if (position < this.data.friends.size())
			{
				return data.friends.get(position);
			}
			
			return null;
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		private class ViewHolder 
		{
			public String id;
			public WebImageView avatar;
			public TextView name;
			public TextView btn_ok;
			public TextView btn_cancel;
		}
	}

	public class LoadTask extends QueryTask<Void, Void, FriendsQuery> 
	{
		@Override
		protected FriendsQuery doInBackground(Void... params) 
		{
			FriendsQuery friendsQuery = new FriendsQuery(appSettings);
			friendsQuery.setOnlyReq();
			friendsQuery.getResponse(WebQuery.GET);
			
			return friendsQuery;
		}

		@Override
		protected void onPostExecute(FriendsQuery query) 
		{
			FriendsQuery.Data data = query.getData();

			if (data.count > 0)
			{
				adapter.addData(data);
			}
			else
			{
				adapter.data = null;
				adapter.notifyDataSetChanged();
			}
			
			super.onPostExecute(query);
		}
	}
	
	public class AcceptFriendRequest extends QueryTask<Void, Void, Void> 
	{
		private String friend_user_id;

		public AcceptFriendRequest(String friend_user_id) 
		{
			this.friend_user_id = friend_user_id;
		}

		@Override
		protected Void doInBackground(Void... params) 
		{
			new AcceptFriendshipOffer(FriendsSuggestionsActivity.this, friend_user_id);
			needUpdate = true;

			return null;
		}
	}
	
	public class RejectFriendRequest extends QueryTask<Void, Void, Void> 
	{
		private String friend_user_id;

		public RejectFriendRequest(String friend_user_id) 
		{
			this.friend_user_id = friend_user_id;
		}

		@Override
		protected Void doInBackground(Void... params) 
		{
			new RejectFriendshipOffer(FriendsSuggestionsActivity.this, friend_user_id);
			needUpdate = true;

			return null;
		}
	}
}
