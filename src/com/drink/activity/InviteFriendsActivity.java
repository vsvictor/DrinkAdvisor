package com.drink.activity;

import java.util.ArrayList;
import java.util.Locale;

import com.drink.R;
import com.drink.imageloader.WebImageView2;
import com.drink.query.FriendsNewQuery;
import com.drink.query.WebQuery;
import com.drink.types.Friend;
import com.drink.types.Invited;
import com.drink.types.Meeting;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class InviteFriendsActivity extends BasicActivity
{
	public static final int MODE_CreateMeeting = 1;
	public static final int MODE_Checkin = 2;
	public static final int MODE_AddFriends = 3;
	
	private static final int CREATE_MEETING = 1;
	
	SearchView searchView;
	ListView listView;
	ListAdapter adapter;
	
	LinearLayout llButtons;
	Button btnInviteAll;
	Button btnSelectedOnly;
	
	private ArrayList<Friend> followingAll;
	private ArrayList<Friend> following = new ArrayList<Friend>();
	private ArrayList<Friend> followersAll;
	private ArrayList<Friend> followers = new ArrayList<Friend>();
	private ArrayList<Friend> selected = new ArrayList<Friend>();
	private ArrayList<Invited> invited = new ArrayList<Invited>();

	private int mode = 0;
	
	public InviteFriendsActivity() 
	{
		super(R.layout.activity_invite_friends, true, true);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		mode = getIntent().getIntExtra("mode", 0);

		if (mode == MODE_Checkin)
		{
			selected = getIntent().getParcelableArrayListExtra("selected");
			cancel = false;
		}
		else if (mode == MODE_AddFriends)
		{
			invited = getIntent().getParcelableArrayListExtra("invited");
			cancel = false;
		}
		
		super.onCreate(savedInstanceState);
		
		adapter = new ListAdapter(getApplicationContext());

		mCaption.setText(R.string.select_invite_friends_meeting);
		
		listView = (ListView) findViewById(R.id.listView);
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
		
		llButtons = (LinearLayout) findViewById(R.id.ll_buttons);
		llButtons.setVisibility(View.GONE);
		
		btnInviteAll = (Button) findViewById(R.id.btn_invite_all);
		if (mode == MODE_Checkin)
		{
			btnInviteAll.setText(R.string.check_in_all);
		}
		btnInviteAll.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnInviteAll.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				selected.clear();
				
				for (int i = 0; i < followingAll.size(); ++i)
				{
					selected.add(followingAll.get(i));
				}
				
				for (int i = 0; i < followersAll.size(); ++i)
				{
					if (findSelected(followersAll.get(i)) == -1)
					{
						selected.add(followersAll.get(i));
					}
				}
				
				switch (mode)
				{
				case MODE_CreateMeeting:
					doNext();
					break;
				case MODE_Checkin:
				case MODE_AddFriends:
					doSelect();
					break;
				}
			}
		});
		
		btnSelectedOnly = (Button) findViewById(R.id.btn_invite_selected);
		btnSelectedOnly.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnSelectedOnly.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				switch (mode)
				{
				case MODE_CreateMeeting:
					doNext();
					break;
				case MODE_Checkin:
				case MODE_AddFriends:
					doSelect();
					break;
				}
			}
		});

		new FriendsTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_filter_cities, menu);
		
		return true;
	}

	private void doNext()
	{
		Intent intent = new Intent(this, CreateMeetingResumeActivity.class);
		intent.putExtra("meeting_title", getIntent().getStringExtra("meeting_title"));
		intent.putExtra("meeting_date", getIntent().getLongExtra("meeting_date", 0));
		intent.putExtra("meeting_comment", getIntent().getStringExtra("meeting_comment"));
		intent.putExtra("bar_id", getIntent().getStringExtra("bar_id"));
		intent.putExtra("selected", selected);
		
		startActivityForResult(intent, CREATE_MEETING);
	}

	private void doSelect()
	{
		Intent intent = new Intent();
		intent.putExtra("selected", selected);
		
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private int findSelected(Friend friend)
	{
		for (int i = 0; i < selected.size(); ++i)
		{
			if (selected.get(i).id.compareTo(friend.id) == 0)
			{
				return i;
			}
		}
		
		return -1;
	}

	private int findInvited(Friend friend)
	{
		for (int i = 0; i < invited.size(); ++i)
		{
			if (invited.get(i).id.compareTo(friend.id) == 0)
			{
				return i;
			}
		}
		
		return -1;
	}

	private class ListAdapter extends BaseAdapter 
	{
		private ViewHolder viewHolder;
		private LayoutInflater layoutInflater;
		
		private boolean isFollowing()
		{
			return (following.size() > 0);
		}
		
		private boolean isFollowers()
		{
			return (followers.size() > 0);
		}

		private int getFollowingIndex()
		{
			if (!isFollowing()) return -1;
			
			return 0;
		}
		
		private int getFollowersIndex()
		{
			if (!isFollowers()) return -1;
			if (!isFollowing()) return 0;
			
			return following.size() + 1;
		}
		
		public ListAdapter(Context context) 
		{
			layoutInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() 
		{
			int size  = 0;
			
			if (isFollowing()) 
			{
				size += following.size() + 1;
			}
			if (isFollowers()) 
			{
				size += followers.size() + 1;
			}
			
			return size;
		}

		@Override
		public Friend getItem(int position) 
		{
			if ((position == getFollowingIndex()) || (position == getFollowersIndex())) return null;
			
			if (isFollowing() && (position <= following.size())) return following.get(position - 1);
			
			int ind = position;
			if (isFollowing())
			{
				ind -= following.size() + 1;
			}
			
			return followers.get(ind - 1);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) 
		{
			if (position == getFollowingIndex())
			{
				View row = this.layoutInflater.inflate(R.layout.listitem_invite_friends_following, null);
				TextView tvTitle = (TextView) row.findViewById(R.id.tv_title);
				tvTitle.setTypeface(Typeface.createFromAsset(InviteFriendsActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));
				
				return row;
			}
			else if (position == getFollowersIndex())
			{
				View row = this.layoutInflater.inflate(R.layout.listitem_invite_friends_followers, null);
				TextView tvTitle = (TextView) row.findViewById(R.id.tv_title);
				tvTitle.setTypeface(Typeface.createFromAsset(InviteFriendsActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));
				
				return row;
			}

			Friend friend = getItem(position);

			View row = view;
			if ((row == null) || (row.getTag() == null)) 
			{
				row = this.layoutInflater.inflate(R.layout.listitem_invite_friends, null);
				
				viewHolder = new ViewHolder();
				viewHolder.avatar = (WebImageView2) row.findViewById(R.id.wiv_avatar);
				viewHolder.avatar.setCircle(true);
				viewHolder.avatar.setImagesStore(mImagesStore);
				viewHolder.avatar.setCacheDir(appSettings.getCacheDirImage());
				
				viewHolder.name = (TextView) row.findViewById(R.id.friend_name);
				viewHolder.name.setTypeface(Typeface.createFromAsset(InviteFriendsActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				
				viewHolder.button = (Button) row.findViewById(R.id.button);
				viewHolder.button.setOnClickListener(new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						Friend friend = (Friend) v.getTag();
						Button button = (Button) v;
						
						int ind = findSelected(friend); 
						if (ind != -1)
						{
							button.setSelected(false);
							if (mode == MODE_Checkin)
							{
								button.setText(R.string._check_in);
							}
							else
							{
								button.setText(R.string._invite);
							}
							selected.remove(ind);
						}
						else
						{
							button.setSelected(true);
							if (mode == MODE_Checkin)
							{
								button.setText(R.string.checked_in_one);
							}
							else
							{
								button.setText(R.string.invited_one);
							}
							selected.add(friend);
						}
						
						notifyDataSetChanged();
						
						if (selected.size() > 0)
						{
							llButtons.setVisibility(View.VISIBLE);
						}
						else
						{
							llButtons.setVisibility(View.GONE);
						}
					}
				});

				row.setTag(viewHolder);
			}
			else
			{
				viewHolder = (ViewHolder) row.getTag();
			}
			
			viewHolder.avatar.setImageURL(friend.avatar, "123");
			viewHolder.name.setText(friend.name);
			viewHolder.button.setTag(friend);
			
			if (mode == MODE_AddFriends)
			{
				int ind = findInvited(friend); 
				if (ind != -1)
				{
					viewHolder.button.setEnabled(false);
					switch (invited.get(ind).state)
					{
					case Meeting.GO:
						viewHolder.button.setText(R.string.go);
						break;
					case Meeting.DONT_GO:
						viewHolder.button.setText(R.string.dont_go);
						break;
					case Meeting.DONT_KNOW:
						viewHolder.button.setText(R.string.dont_know);
						break;
					}
				}
				else
				{
					viewHolder.button.setEnabled(true);
				}
			}
			
			if (viewHolder.button.isEnabled())
			{
				if (findSelected(friend) != -1)
				{
					viewHolder.button.setSelected(true);
					if (mode == MODE_Checkin)
					{
						viewHolder.button.setText(R.string.checked_in_one);
					}
					else
					{
						viewHolder.button.setText(R.string.invited_one);
					}
				}
				else
				{
					viewHolder.button.setSelected(false);
					if (mode == MODE_Checkin)
					{
						viewHolder.button.setText(R.string._check_in);
					}
					else
					{
						viewHolder.button.setText(R.string._invite);
					}
				}
			}
			
			return row;
		}
	}
	
	private class ViewHolder 
	{
		public WebImageView2 avatar;
		public TextView name;
		public Button button;
	}
	
	private void applyFilter(String filter)
	{
		following.clear();
		followers.clear();

		if (followingAll != null)
		{
			for (int i = 0; i < followingAll.size(); ++i)
			{
				Friend friend = followingAll.get(i);
				if ((filter == null) || 
					(filter.length() == 0) || 
					(friend.name.toLowerCase(Locale.getDefault()).contains(filter.toLowerCase(Locale.getDefault()))))
				{
					following.add(friend);
				}
			}
		}
		
		if (followersAll != null)
		{
			for (int i = 0; i < followersAll.size(); ++i)
			{
				Friend friend = followersAll.get(i);
				if ((filter == null) || 
					(filter.length() == 0) || 
					(friend.name.toLowerCase(Locale.getDefault()).contains(filter.toLowerCase(Locale.getDefault()))))
				{
					followers.add(friend);
				}
			}
		}
		
		adapter.notifyDataSetChanged();
	}
	
	private class FriendsTask extends QueryTask<Void, Void, FriendsNewQuery> 
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

			listView.setVisibility(View.GONE);
		}

		@Override
		protected FriendsNewQuery doInBackground(Void... params) 
		{
			FriendsNewQuery query = new FriendsNewQuery(appSettings);
			query.getResponse(WebQuery.GET);

			return query;
		}

		@Override
		protected void onPostExecute(FriendsNewQuery query) 
		{
			if (checkResult(query)) 
			{
				followingAll = query.getData().following;
				followersAll = query.getData().followers;
				
				applyFilter(null);
				
				adapter.notifyDataSetChanged();
			}
			
			listView.setVisibility(View.VISIBLE);
			
			if (selected.size() > 0)
			{
				llButtons.setVisibility(View.VISIBLE);
			}
			
			super.onPostExecute(query);
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
				case CREATE_MEETING:
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
