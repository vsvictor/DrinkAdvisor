package com.drink.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.drink.R;
import com.drink.constants.Constants;
import com.drink.imageloader.WebImageView;
import com.drink.query.FriendsQuery;
import com.drink.query.WebQuery;

public class FriendsActivity extends BasicActivity 
{
	static private final int FRIENDSHIP_REQUESTS = 0;
	static private final int SEARCH_FRIENDS = 1;
	static private final int SHOW_USER_PROFILE = 2;
	
	private TextView tvNoFriends;
	private LinearLayout layoutSearch;
	private View searchView;
	private ListView listView;
	private FriendsListAdapter adapter;
	private RelativeLayout rrFrienshipRequest;
	private TextView tvReqCount;
	private int countSuggest;

	private ArrayList<FriendsQuery.Friend> allFriends;
	private ArrayList<FriendsQuery.Friend> friends;

	public FriendsActivity() 
	{
		super(R.layout.activity_friends);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		rrFrienshipRequest = (RelativeLayout) findViewById(R.id.rr_friendship_request);
		tvReqCount = (TextView) findViewById(R.id.new_friends_count);
		tvReqCount.setTypeface(Typeface.createFromAsset(FriendsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		tvNoFriends = (TextView) findViewById(R.id.no_friends);
		tvNoFriends.setVisibility(View.GONE);

		listView = (ListView) findViewById(R.id.listView);
		
		adapter = new FriendsListAdapter(getApplicationContext());
		listView.setAdapter(adapter);

		layoutSearch = (LinearLayout) findViewById(R.id.ll_search);
		
		searchView = SearchViewCompat.newSearchView(this);
		if (searchView != null)
		{
			int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
			TextView textView = (TextView) searchView.findViewById(id);
			textView.setTextColor(Color.WHITE);
			
			SearchViewCompat.setOnQueryTextListener(searchView, new OnQueryTextListenerCompat() 
				{
					  public boolean onQueryTextSubmit(String query)
					  {
						  return true;
					  }
					  public boolean onQueryTextChange(String newText)
					  {
						  applyFilter(newText);
						  
						  return true;
					  }
				});
	        layoutSearch.addView(searchView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		}

        Button btnFindFriends = (Button) findViewById(R.id.btn_find_friends);
		btnFindFriends.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnFindFriends.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					startActivityForResult(new Intent(FriendsActivity.this, FriendsSearchActivity.class), SEARCH_FRIENDS);
				}
			});
		
		Button btnRequests = (Button) findViewById(R.id.btn_requests);
		btnRequests.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnRequests.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					startActivityForResult(new Intent(FriendsActivity.this, FriendsSuggestionsActivity.class), FRIENDSHIP_REQUESTS);
				}
			});
		
		new LoadTaskReqFriends().execute();
		new LoadTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_frendlist, menu);
		
		return true;
	}

	private class FriendsListAdapter extends BaseAdapter 
	{
		private LayoutInflater layoutInflater;

		public FriendsListAdapter(Context context) 
		{
			this.layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() 
		{
			if (friends == null) return 0;
			
			return friends.size();
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) 
		{
			ViewHolder viewHolder;
			View row = view;
			if (row == null) 
			{
				row = this.layoutInflater.inflate(R.layout.listitem_frends_list, null);

				viewHolder = new ViewHolder();
				viewHolder.avatar = (WebImageView) row.findViewById(R.id.avatar);
				viewHolder.avatar.setImagesStore(mImagesStore);
				viewHolder.avatar.setCacheDir(appSettings.getCacheDirImage());
				viewHolder.name = (TextView) row.findViewById(R.id.name);

				row.setTag(viewHolder);
			} 
			else 
			{
				viewHolder = (ViewHolder) row.getTag();
			}

			FriendsQuery.Friend friend = getItem(i);
			if ((friends.get(i).picture != null)	&& !friends.get(i).picture.equals("")) 
			{
				viewHolder.avatar.setImageURL(friends.get(i).picture, "123");
			}
			viewHolder.name.setTypeface(Typeface.createFromAsset(FriendsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
			SpannableString content = new SpannableString(friend.name);
			content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
			viewHolder.name.setText(content);
			final String friend_id = friend.id;
			row.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						Intent intent = new Intent();
						intent.putExtra(Constants.KEY_USER_ID, friend_id);
						intent.setClass(FriendsActivity.this, UserProfileActivity.class);
						
						startActivityForResult(intent, SHOW_USER_PROFILE);
					}
				});

			return row;
		}

		@Override
		public FriendsQuery.Friend getItem(int position) 
		{
			if (friends == null) return null;
			if (position < friends.size())
			{
				return friends.get(position);
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
			public WebImageView avatar;
			public TextView name;
		}

	}

	public class LoadTask extends QueryTask<Void, Void, FriendsQuery> 
	{
		@Override
		protected FriendsQuery doInBackground(Void... params) 
		{
			FriendsQuery friendsQuery = new FriendsQuery(appSettings);
			friendsQuery.getResponse(WebQuery.GET);
			
			return friendsQuery;
		}

		@Override
		protected void onPostExecute(FriendsQuery query) 
		{
			if (checkResult(query))
			{
				FriendsQuery.Data data = query.getData();
				allFriends = data.friends;
				friends = allFriends;
				
				adapter.notifyDataSetChanged();
				
				if (listView.getCount() == 0)
				{
					tvNoFriends.setVisibility(View.VISIBLE);
				}
				else
				{
					tvNoFriends.setVisibility(View.GONE);
				}
			}

			super.onPostExecute(query);
		}
	}

	public class LoadTaskReqFriends extends QueryTask<Void, Void, FriendsQuery> 
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

			rrFrienshipRequest.setVisibility(View.GONE);
		}

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
			if (checkResult(query))
			{
				FriendsQuery.Data data = query.getData();
				countSuggest = data.count;
				if (countSuggest > 0) 
				{
					rrFrienshipRequest.setVisibility(View.VISIBLE);
					String countText = (String) getText(R.string.friends_list_text_offers_count);
					countText = countText.replace("#", String.valueOf(countSuggest));
					tvReqCount.setText(countText);
				} 
				else
				{
					rrFrienshipRequest.setVisibility(View.GONE);
				}
			}
			
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
			case SHOW_USER_PROFILE:
				new LoadTask().execute();
				break;
			case FRIENDSHIP_REQUESTS:
				new LoadTask().execute();
				new LoadTaskReqFriends().execute();
				break;
			}
		}
	}

	private void applyFilter(String filter)
	{
		if (allFriends == null) return;
		
		if ((filter == null) || (filter.length() == 0))
		{
			friends = allFriends;
		}
		else
		{
			friends = new ArrayList<FriendsQuery.Friend>();
			
			for (int i = 0; i < allFriends.size(); ++i)
			{
				FriendsQuery.Friend friend = allFriends.get(i);
				if (friend.name.toLowerCase().contains(filter.toLowerCase()))
				{
					friends.add(friend);
				}
			}
		}
		
		adapter.notifyDataSetChanged();
	}
}
