package com.drink.activity;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.drink.R;
import com.drink.constants.Constants.FriendType;
import com.drink.imageloader.WebImageView;
import com.drink.query.CreateFriendshipOfferQuery;
import com.drink.query.FriendsSearchQuery;
import com.drink.query.FriendsSearchQuery.Friend;
import com.drink.query.WebQuery;
import com.drink.twitter.TwitterApp;
import com.drink.twitter.TwitterApp.TwDialogListener;
import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class FriendsSearchActivity extends BasicActivity 
{
	static private final int LOGIN_FACEBOOK = 0;
	static private final int LOGIN_TWITTER = 1;
	static private final int PICK_CONTACT = 2;

	private LinearLayout layoutSearch;
	private View searchView;
	
	ListView listView;
	FriendsAdapter adapter;
	LinearLayout login_screen;
	Context context;

	ArrayList<Friend> allFriends;
	ArrayList<Friend> friends;

	ImageView mFacebookFind;
	ImageView mTwitterFind;
	TextView mTotalFriends;
	
	LinearLayout	mAllFriends;

	TwDialogListener twitterDialogListener = new TwDialogListener() 
	{
		public void onError(String value) 
		{
			showErrorBox("error", false);
		}

		public void onComplete(String value) 
		{
			showErrorBox("compleate", false);
		}
	};

	public FriendsSearchActivity() 
	{
		super(R.layout.activity_friends_search);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		context = this;
		listView = (ListView) findViewById(R.id.listView);

		LinearLayout footer = (LinearLayout) getLayoutInflater().inflate(R.layout.listitem_frends_panel, (ViewGroup) findViewById(R.id.ll_root));
		listView.addFooterView(footer, null, false);
		
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

        mTotalFriends = (TextView) footer.findViewById(R.id.friends_total);
		
		Button button = (Button) footer.findViewById(R.id.btn_invite);
		button.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				inviteFriends();
			}
		});
		
		button = (Button) footer.findViewById(R.id.btn_sms);
		button.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
				intent.setType(Phone.CONTENT_TYPE);
				startActivityForResult(intent, PICK_CONTACT);
			}
		});
		
		adapter = new FriendsAdapter(getApplicationContext());
		listView.setAdapter(adapter);
		listView.setVisibility(View.GONE);
		layoutSearch.setVisibility(View.GONE);

		login_screen = (LinearLayout) findViewById(R.id.login_screen);

		mFacebookFind = (ImageView) findViewById(R.id.facebook);
		mFacebookFind.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					getFBFriends();
				}
			});
		
		mTwitterFind = (ImageView) findViewById(R.id.twitter);
		mTwitterFind.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					getTWFriends();
				}
			});

		Button btnFriendsOffer = (Button) findViewById(R.id.btn_requests);
		btnFriendsOffer.setTypeface(Typeface.createFromAsset(FriendsSearchActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnFriendsOffer.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					Intent intent = new Intent();
					intent.setClass(FriendsSearchActivity.this, FriendsSuggestionsActivity.class);
					
					startActivity(intent);
					finish();
	
				}
			});

		final ToggleButton btnFindFriends = (ToggleButton) findViewById(R.id.tgl_find_friends);
		btnFindFriends.setTypeface(Typeface.createFromAsset(FriendsSearchActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnFindFriends.setChecked(true);
		btnFindFriends.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					btnFindFriends.setChecked(true);
				}
			});
		
		mAllFriends = (LinearLayout) findViewById(R.id.ll_all_friends);
		mAllFriends.setVisibility(View.GONE);
		
		button = (Button) mAllFriends.findViewById(R.id.btn_all_friends);
		button.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (friends == null) return;
				
				for (int i = 0; i < friends.size(); ++i)
				{
					Friend friend = friends.get(i);
					if (!friend.mIsFriendOrRequestSent && !friend.mSentRequest)
					{
						friend.mSentRequest = true;
						
						new SendFriendRequest(friend, "").execute();
					}
				}
				
				adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_friends_search, menu);
		
		return true;
	}

	private void getFBFriends() 
	{
		Session session = Session.getActiveSession();

		if (session == null) 
		{
			startActivityForResult(new Intent(this, FBLoginActivity.class).putExtra("login", false), LOGIN_FACEBOOK);
		} 
		else if (session.getState().isOpened()) 
		{
			Request fRequest = Request.newMyFriendsRequest(session, new GraphUserListCallback() 
				{
					@Override
					public void onCompleted(List<GraphUser> users, Response response) 
					{
						ArrayList<String> ids = new ArrayList<String>();
						for (int i = 0; i < users.size(); ++i) 
						{
							ids.add(users.get(i).getId());
						}

						new FindFriendsTask(FriendType.FACEBOOK, ids).execute();
					}
				});
			
			Bundle params = new Bundle();
			params.putString("fields", "id, name, picture");
			fRequest.setParameters(params);
			fRequest.executeAsync();
		}
	}
	private void getTWFriends() 
	{
		TwitterApp twitterApp = new TwitterApp(this, getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret), twitterDialogListener);
		if (twitterApp.getAccessToken() == null) 
		{
			startActivityForResult(new Intent(this, TWLoginActivity.class).putExtra("login", false), LOGIN_TWITTER);
		} 
		else 
		{
			new GetTwitterIDsTask(twitterApp).execute();
		}
	}
	private class GetTwitterIDsTask extends QueryTask<Void, Void, String> 
	{
		private TwitterApp twitterApp;
		private ArrayList<String> ids = new ArrayList<String>();

		public GetTwitterIDsTask(TwitterApp twitterApp) 
		{
			super();
			
			this.twitterApp = twitterApp;
		}
		
		@Override
		protected String doInBackground(Void... params) 
		{
			long[] ids = twitterApp.getids();
			for (int i = 0; i < ids.length; ++i) 
			{
				this.ids.add(String.valueOf(ids[i]));
			}
			
			return "";
		}

		@Override
		protected void onPostExecute(String str) 
		{
			new FindFriendsTask(FriendType.TWITTER, ids).execute();
			
			super.onPostExecute(str);
		}
	}

	private class FindFriendsTask extends QueryTask<Void, Void, FriendsSearchQuery> 
	{
		public FriendType type;
		public ArrayList<String> ids;

		public FindFriendsTask(FriendType type, ArrayList<String> ids) 
		{
			super();
			
			this.type = type;
			this.ids = ids;
		}

		@Override
		protected FriendsSearchQuery doInBackground(Void... params) 
		{
			FriendsSearchQuery friendsSearchQuery = new FriendsSearchQuery(FriendsSearchActivity.this);
			if (type == FriendType.FACEBOOK)
			{
				friendsSearchQuery.setNetworkType("facebook");
			}
			else if (type == FriendType.TWITTER)
			{
				friendsSearchQuery.setNetworkType("twitter");
			}
			
			friendsSearchQuery.setSocIds(ids);
			friendsSearchQuery.getResponse(WebQuery.GET);
			
			return friendsSearchQuery;
		}

		@Override
		protected void onPostExecute(FriendsSearchQuery query) 
		{
			if (checkResult(query))
			{
				FriendsSearchQuery.Data data = query.getData();
				if (data != null) 
				{
					allFriends = data.friends;
					friends = allFriends;
				}
				adapter.notifyDataSetChanged();
				
				mAllFriends.setVisibility(View.VISIBLE);
				listView.setVisibility(View.VISIBLE);
				layoutSearch.setVisibility(View.VISIBLE);
				login_screen.setVisibility(View.GONE);
				
				String text = Integer.toString(ids.size() - friends.size());
				text += " ";
				text += getString(R.string.friends_on_site_1);
				text += " ";
				text += Integer.toString(ids.size());
				text += " ";
				text += getString(R.string.friends_on_site_2);
				mTotalFriends.setText(text);
			}
			
			super.onPostExecute(query);
		}
	}

	private class FriendsAdapter extends BaseAdapter 
	{
		LayoutInflater layoutInflater;

		public FriendsAdapter(Context context) 
		{
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() 
		{
			if (friends == null) return 0;
			
			return friends.size();
		}

		@Override
		public Object getItem(int position) 
		{
			if (friends == null) return null;
			
			if (position < friends.size())
			{
				return friends.get(position);
			}
			
			return null;
		}

		@Override
		public long getItemId(int arg0) 
		{
			return arg0;
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) 
		{
			View row = view;
			ViewHolder viewHolder = null;
			if (row == null) 
			{
				row = layoutInflater.inflate(R.layout.listitem_friendship, null);
				
				viewHolder = new ViewHolder();
				viewHolder.avatar = (WebImageView) row.findViewById(R.id.avatar);
				viewHolder.avatar.setImagesStore(mImagesStore);
				viewHolder.avatar.setCacheDir(appSettings.getCacheDirImage());
				viewHolder.name = (TextView) row.findViewById(R.id.name);
				viewHolder.name.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
				viewHolder.mAddFriend = (Button) row.findViewById(R.id.addfriend_button);
				viewHolder.mFriends = (Button) row.findViewById(R.id.friend_button);
				viewHolder.mFriends.setEnabled(false);

				row.setTag(viewHolder);
			} 
			else 
			{
				viewHolder = (ViewHolder) row.getTag();
			}

			final Friend currentFriend = friends.get(i);

			if (currentFriend.mIsFriendOrRequestSent)
			{
				viewHolder.mAddFriend.setVisibility(View.GONE);
				viewHolder.mFriends.setVisibility(View.VISIBLE);
			}
			else
			{
				viewHolder.mAddFriend.setVisibility(View.VISIBLE);
				viewHolder.mAddFriend.setEnabled(!currentFriend.mSentRequest);
				viewHolder.mFriends.setVisibility(View.GONE);
			}

			if (currentFriend.mPicture != null && !currentFriend.mPicture.equals("")) 
			{
				viewHolder.avatar.setImageURL(currentFriend.mPicture, "123");
			}
			else
			{
				viewHolder.avatar.setVisibility(View.INVISIBLE);
			}
			
			SpannableString content = new SpannableString(currentFriend.name);
			content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
			viewHolder.name.setText(content);

			final ViewHolder _viewHolder = viewHolder;
			viewHolder.mAddFriend.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						_viewHolder.mAddFriend.setEnabled(false);
						currentFriend.mSentRequest = true;
						
						new SendFriendRequest(currentFriend, "").execute();
					}
				});

			return row;
		}
		private class ViewHolder 
		{
			public WebImageView avatar;
			public TextView name;
			public Button mAddFriend;
			public Button mFriends;
		}
	}
	public class SendFriendRequest extends QueryTask<Void, Void, Void> 
	{
		Friend mFriend;
		String mMessage;
		
		public SendFriendRequest(Friend friend, String message) 
		{
			mFriend = friend;
			mMessage = message;
		}

		@Override
		protected Void doInBackground(Void... params) 
		{
			new CreateFriendshipOfferQuery(FriendsSearchActivity.this, mFriend.mAppId, mMessage);
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
			
			mFriend.mIsDialogVisible = false;
			adapter.notifyDataSetChanged();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
			case LOGIN_FACEBOOK:
				Session session = Session.getActiveSession();
				if (session.getState().isOpened()) 
				{
					Request fRequest = Request.newMyFriendsRequest(session,	new GraphUserListCallback() 
						{
							@Override
							public void onCompleted(List<GraphUser> users, Response response) 
							{
								ArrayList<String> ids = new ArrayList<String>();
								for (int i = 0; i < users.size(); ++i) 
								{
									ids.add(users.get(i).getId());
								}

								new FindFriendsTask(FriendType.FACEBOOK, ids).execute();
							}
						});
					
					Bundle params = new Bundle();
					params.putString("fields", "id, name, picture");
					fRequest.setParameters(params);
					fRequest.executeAsync();
				}
				break;
			case LOGIN_TWITTER:
				TwitterApp twitterApp = new TwitterApp(this, getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret), twitterDialogListener);
				new GetTwitterIDsTask(twitterApp).execute();
				break;
			case PICK_CONTACT:
			     Uri contactData = data.getData();
			     Cursor c =  managedQuery(contactData, null, null, null, null);
			     if (c.moveToFirst()) 
			     {
		            // Get the URI that points to the selected contact
		            Uri contactUri = data.getData();
		            // We only need the NUMBER column, because there will be only one row in the result
		            String[] projection = {Phone.NUMBER};

		            // Perform the query on the contact to get the NUMBER column
		            // We don't need a selection or sort order (there's only one result for the given URI)
		            // CAUTION: The query() method should be called from a separate thread to avoid blocking
		            // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
		            // Consider using CursorLoader to perform the query.
		            Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
		            cursor.moveToFirst();

		            // Retrieve the phone number from the NUMBER column
		            int column = cursor.getColumnIndex(Phone.NUMBER);
		            String number = cursor.getString(column);

		            Uri uri = Uri.parse("smsto:" + number); 
		            Intent it = new Intent(Intent.ACTION_SENDTO, uri); 
		            it.putExtra("sms_body", getString(R.string.sms_text)); 
		            startActivity(it); 
			     }

				break;
			default:
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	private void inviteFriends() 
	{
		Bundle params = new Bundle();
		params.putString("message", "invite");

		WebDialog.Builder builder = new com.facebook.widget.WebDialog.Builder(this, Session.getActiveSession(), "apprequests", params);

		builder.setOnCompleteListener(new OnCompleteListener() 
		{
			@Override
			public void onComplete(Bundle values, FacebookException error) 
			{
			}
		});

		WebDialog webDialog = builder.build();
		webDialog.show();
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
			friends = new ArrayList<FriendsSearchQuery.Friend>();
			
			for (int i = 0; i < allFriends.size(); ++i)
			{
				FriendsSearchQuery.Friend friend = allFriends.get(i);
				if (friend.name.toLowerCase().contains(filter.toLowerCase()))
				{
					friends.add(friend);
				}
			}
		}
		
		adapter.notifyDataSetChanged();
	}
}