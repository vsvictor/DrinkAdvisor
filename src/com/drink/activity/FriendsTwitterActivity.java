package com.drink.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drink.R;
import com.drink.activity.BasicActivity.QueryTask;
import com.drink.activity.FriendFollowedActivity.LoadEnable;
import com.drink.activity.FriendFollowedActivity.LoadFriendsFollowed;
import com.drink.activity.FriendFollowedActivity.LoadFriendsFollowing;
import com.drink.activity.FriendFollowedActivity.LoadSubscribe;
import com.drink.activity.FriendFollowedActivity.LoadUnsubscribe;
import com.drink.activity.FriendFollowedActivity.SignersAdapter;
import com.drink.activity.FriendFollowingActivity.FriendsFollowingAdapter;
import com.drink.constants.Constants;
import com.drink.constants.Constants.FriendType;
import com.drink.imageloader.WebImageView2;
import com.drink.query.CreateFriendshipOfferQuery;
import com.drink.query.FriendFollowed;
import com.drink.query.FriendFollowing;
import com.drink.query.FriendSubscribe;
import com.drink.query.FriendUnsubsrcibe;
import com.drink.query.FriendsSearchQuery;
import com.drink.query.FriendsTwitter;
import com.drink.query.ProfileQuery;
import com.drink.query.WebQuery;
import com.drink.query.FriendsTwitter.Friend;
import com.drink.twitter.TwitterApp;
import com.drink.twitter.TwitterApp.TwDialogListener;
import com.drink.types.BaseObject;
import com.drink.types.BaseObjectList;
import com.drink.types.FilteredAdapter;

public class FriendsTwitterActivity extends BasicActivity{
	static private final int LOGIN_TWITTER = 1;
	BaseObjectList friends;
	BaseObjectList following;
	BaseObjectList followed;
	ListView listView;
	TwitterAdapter adapter;
	FriendsTwitterActivity ac;
	
	private EditText edSearch;
	private LinearLayout llHint;
	private Button rlBack;
	private LinearLayout llSelector;	
	private double llSelectorHeight;
	private TextView tvNoUsers;
	private LinearLayout llTwitter;
	
	public FriendsTwitterActivity() {
		super(R.layout.activity_friends_twitter);
		friends = new BaseObjectList();
	}
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
	public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		ac = this;
		TextView tv = (TextView) findViewById(R.id.tv_caption);
		tv.setTypeface(Typeface.createFromAsset(FriendsTwitterActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv.setText(this.getResources().getString(R.string.your_friends_in_twitter));
		tv = (TextView) this.findViewById(R.id.tvTWSearchText);
		tv.setTypeface(Typeface.createFromAsset(FriendsTwitterActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

		llHint = (LinearLayout) this.findViewById(R.id.llTWHint);
		llSelector = (LinearLayout) this.findViewById(R.id.llTWFooter);
		llSelector.getLayoutParams().height = 0;
		float scale = this.getResources().getDisplayMetrics().density;
		llSelectorHeight = 50*scale;
		edSearch = (EditText) this.findViewById(R.id.edTWSearch);
		edSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus && edSearch.getText().toString().isEmpty()){
					llHint.setVisibility(View.VISIBLE);
				}
				else{
					llHint.setVisibility(View.INVISIBLE);
				}
			}
		});
		edSearch.addTextChangedListener(new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				adapter = new TwitterAdapter(friends);
				adapter.getFilter().filter(edSearch.getText().toString());
				listView.setAdapter(adapter);
				if(listView.getCount() <= 0) noUsersFound(true);
				else noUsersFound(false);
			}
			@Override
			public void afterTextChanged(Editable s) {
				adapter = new TwitterAdapter(friends);
				adapter.getFilter().filter(edSearch.getText().toString());
				listView.setAdapter(adapter);
				if(listView.getCount() <= 0) noUsersFound(true);
				else noUsersFound(false);
			}
		  });

		rlBack = (Button) this.findViewById(R.id.btn_back);
		rlBack.requestFocus();
		rlBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ac.finish();
			}
		});

		getTWFriends();
		listView = (ListView) this.findViewById(R.id.lvTWFriends);
		adapter = new TwitterAdapter(friends);
		listView.setAdapter(adapter);
		tvNoUsers = (TextView) this.findViewById(R.id.tvNoUserTwitter);
		llTwitter = (LinearLayout) this.findViewById(R.id.llTWList);
	}
	private void noUsersFound(boolean visible){
		if(visible){
			llTwitter.setVisibility(View.INVISIBLE);
			tvNoUsers.setVisibility(View.VISIBLE);
		}
		else{
			llTwitter.setVisibility(View.VISIBLE);
			tvNoUsers.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
			case LOGIN_TWITTER:
				TwitterApp twitterApp = new TwitterApp(this, getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret), twitterDialogListener);
				new GetTwitterIDsTask(twitterApp).execute();
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
	public class TwitterAdapter extends FilteredAdapter{

		private LayoutInflater inflater;
		private int visibled;
		private TwitterAdapter ad;

		public TwitterAdapter(BaseObjectList list) {
			super(list);
			inflater = (LayoutInflater) ac.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			visibled = R.id.rlTWDisableSigner;
			ad = this;
		}
		@Override
		public int getCount() {
			return data.size();
		}
		@Override
		public Object getItem(int position) {
			return data.get(position);
		}
		@Override
		public long getItemId(int position) {
			return data.get(position).getID();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = inflater.inflate(R.layout.twitter_item, parent, false);

			final FriendsTwitter.Friend item = (FriendsTwitter.Friend) getData().get(position);
			WebImageView2 picture = (WebImageView2) v.findViewById(R.id.ivTWAvatarSigner);
			picture.setCircle(true);
			picture.setMaxWidth(80);
			picture.setMaxHeight(80);
			picture.setImagesStore(mImagesStore);
			picture.setCacheDir(appSettings.getCacheDirImage());
			picture.setImageURL(item.getPicture(), "123");
			TextView tvName = (TextView) v.findViewById(R.id.tvTWNameSigner);
			tvName.setText(item.getName());
			tvName.setTypeface(Typeface.createFromAsset(FriendsTwitterActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
			TextView tvButton = (TextView) v.findViewById(R.id.tvTWWatchSigner);
			tvButton.setTypeface(Typeface.createFromAsset(FriendsTwitterActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
			tvButton = (TextView) v.findViewById(R.id.tvTWDisableSigner);
			RelativeLayout rlDisabled = (RelativeLayout) v.findViewById(R.id.rlTWDisableSigner);
			RelativeLayout rlWatch = (RelativeLayout) v.findViewById(R.id.rlTWWatchSigner);
			rlDisabled.setVisibility(View.INVISIBLE);
			rlWatch.setVisibility(View.INVISIBLE);
			if(this.visibled == R.id.rlTWWatchSigner){
				rlDisabled.setVisibility(item.getWatched()==0?View.VISIBLE:View.INVISIBLE);
				rlWatch.setVisibility(item.getWatched()==1?View.VISIBLE:View.INVISIBLE);
			}
			rlWatch.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new LoadUnsubscribe().execute(new Long(item.getID()));
				}
			});
			rlDisabled.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new LoadSubscribe().execute(new Long(item.getID()));					
				}
			});
			rlDisabled.setVisibility(item.getWatched()==0?View.VISIBLE:View.INVISIBLE);
			rlWatch.setVisibility(item.getWatched()==1?View.VISIBLE:View.INVISIBLE);
			return v;
		}
	}
	public class LoadUnsubscribe extends QueryTask<Long, Void, FriendUnsubsrcibe>{
		private int id;
		@Override
		protected FriendUnsubsrcibe doInBackground(Long... params) {
			id = params[0].intValue();
			FriendUnsubsrcibe friendsQuery = new FriendUnsubsrcibe(appSettings, params[0]);
			friendsQuery.getResponse(WebQuery.GET);
			return friendsQuery;
		}
		@Override
		protected void onPostExecute(FriendUnsubsrcibe query) 
		{
			super.onPostExecute(query);
			FriendsTwitter.Friend f = (FriendsTwitter.Friend) friends.find(id);
			f.setWatched(0);
			adapter.notifyDataSetChanged();
		}
	
	}
	public class LoadSubscribe extends QueryTask<Long, Void, FriendSubscribe>{

		private int id;
		@Override
		protected FriendSubscribe doInBackground(Long... params) {
			id = params[0].intValue();
			FriendSubscribe friendsQuery = new FriendSubscribe(appSettings, params[0]);
			friendsQuery.getResponse(WebQuery.GET);
			return friendsQuery;
		}
		@Override
		protected void onPostExecute(FriendSubscribe query) 
		{
			super.onPostExecute(query);
			final FriendsTwitter.Friend f = (FriendsTwitter.Friend) friends.find(id);
			f.setWatched(1);
			adapter.notifyDataSetChanged();
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
			long[] ids = twitterApp.getFriendIds();
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
			FriendsSearchQuery friendsSearchQuery = new FriendsSearchQuery(FriendsTwitterActivity.this);
			if (type == FriendType.TWITTER)
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
					for(int i = 0; i<data.friends.size();i++){
						int id = Integer.parseInt(data.friends.get(i).mAppId);
						int status = data.friends.get(i).status;
						FriendsTwitter.Friend f = new FriendsTwitter.Friend(id, data.friends.get(i).name, data.friends.get(i).mPicture);
						f.setWatched(status);
						friends.add(f);
					}
				}
				adapter.notifyDataSetChanged();
				if(listView.getCount() <= 0) noUsersFound(true);
				else noUsersFound(false);
			}
			super.onPostExecute(query);
		}
	}
}
