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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drink.R;
import com.drink.activity.FriendFollowedActivity.LoadUnsubscribe;
import com.drink.constants.Constants;
import com.drink.imageloader.WebImageView2;
import com.drink.query.FriendFollowing;
import com.drink.query.FriendFollowing.Data;
import com.drink.query.FriendFollowing.Friend;
import com.drink.query.FriendFollowed;
import com.drink.query.FriendSubscribe;
import com.drink.query.FriendUnsubsrcibe;
import com.drink.query.FriendsQuery;
import com.drink.query.FriendsQueryMain;
import com.drink.query.FriendsSearchQuery;
import com.drink.query.ProfileQuery;
import com.drink.query.ProfileQuery.UserProfile;
import com.drink.query.WebQuery;
import com.drink.types.BaseObjectList;
import com.drink.types.FilteredAdapter;

public class FriendFollowingActivity extends ActivityWithMenu implements View.OnClickListener{
	private RelativeLayout rlRoot;
	private EditText edSearch;
	private ListView lvFriends;
	private Button rlBack;
	private ImageView ivUpdate;
	private ImageView ivLeftMenu;
	private LinearLayout llHint;
	private FriendsFollowingAdapter adapter;
	private ActivityWithMenu ac;
	private BaseObjectList allFriends;
	private TextView tvNoUsers;
	private LinearLayout llList;
	public FriendFollowingActivity() {
		super(R.layout.activity_friends_following);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		ac = this;
//		TextView tv = (TextView) this.findViewById(R.id.tvYourFriends);
		TextView tv = (TextView) this.findViewById(R.id.tv_caption);
		tv.setText(ac.getResources().getString(R.string.your_friends));
		tv.setTypeface(Typeface.createFromAsset(FriendFollowingActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		//tv = (TextView) this.findViewById(R.id.tvBack);
		tv.setTypeface(Typeface.createFromAsset(FriendFollowingActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv = (TextView) this.findViewById(R.id.tvSearchText);
		tv.setTypeface(Typeface.createFromAsset(FriendFollowingActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

		rlRoot = (RelativeLayout) this.findViewById(R.id.rlRoot);
		llHint = (LinearLayout) this.findViewById(R.id.llHint);
		edSearch = (EditText) this.findViewById(R.id.edSearch);
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
				adapter = new FriendsFollowingAdapter(allFriends);
				adapter.getFilter().filter(edSearch.getText().toString());
				lvFriends.setAdapter(adapter);
				if(lvFriends.getCount()<=0)	noUsersFound(true);
				else noUsersFound(false);
			}
			@Override
			public void afterTextChanged(Editable s) {
				adapter = new FriendsFollowingAdapter(allFriends);
				adapter.getFilter().filter(edSearch.getText().toString());
				lvFriends.setAdapter(adapter);
				if(lvFriends.getCount()<=0)	noUsersFound(true);
				else noUsersFound(false);
			}
		  });
		
		lvFriends = (ListView) this.findViewById(R.id.lvFriends);
		lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent();
				i.putExtra(Constants.KEY_USER_ID, String.valueOf(id));
				i.setClass(FriendFollowingActivity.this, UserProfileActivity.class);
				startActivity(i);
			}
		});
		rlBack = (Button) this.findViewById(R.id.btn_back);
		rlBack.requestFocus();
//		ivUpdate = (ImageView) this.findViewById(R.id.ivUpdate);
//		ivLeftMenu = (ImageView) this.findViewById(R.id.ivLeftMenu);
		rlBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ac.finish();
			}
		});
		tvNoUsers = (TextView) this.findViewById(R.id.tvNoUserFollowing);
		llList = (LinearLayout) this.findViewById(R.id.llList);
		new LoadFriendsFollowing().execute();
	}
	@Override
	public void onResume(){
		super.onResume();
		edSearch.clearFocus();
		rlRoot.requestFocus();
	}
	private void noUsersFound(boolean visible){
		if(visible){
			llList.setVisibility(View.INVISIBLE);
			tvNoUsers.setVisibility(View.VISIBLE);
		}
		else{
			llList.setVisibility(View.VISIBLE);
			tvNoUsers.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_back: {this.finish();break;}
		}
	}
	public class FriendsFollowingAdapter extends FilteredAdapter{
		private LayoutInflater inflater;
		public FriendsFollowingAdapter(BaseObjectList list) {
			super(list);
			inflater = (LayoutInflater) ac.getSystemService(Context.LAYOUT_INFLATER_SERVICE);			
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
			View v = inflater.inflate(R.layout.friend_item, parent, false);
			final FriendFollowing.Friend item = (Friend) getData().get(position);
			WebImageView2 picture = (WebImageView2) v.findViewById(R.id.ivAvatar);
			picture.setCircle(true);
			picture.setMaxWidth(80);
			picture.setMaxHeight(80);
			picture.setImagesStore(mImagesStore);
			picture.setCacheDir(appSettings.getCacheDirImage());
			picture.setImageURL(item.getPicture(), "123");
			picture.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.putExtra(Constants.KEY_USER_ID, String.valueOf(item.getID()));
					i.setClass(FriendFollowingActivity.this, UserProfileActivity.class);
					startActivity(i);
				}
			});

			TextView tvName = (TextView) v.findViewById(R.id.tvName);
			tvName.setTypeface(Typeface.createFromAsset(FriendFollowingActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
			tvName.setText(item.getName());
			tvName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.putExtra(Constants.KEY_USER_ID, String.valueOf(item.getID()));
					i.setClass(FriendFollowingActivity.this, UserProfileActivity.class);
					startActivity(i);
				}
			});
			TextView tvButton = (TextView) v.findViewById(R.id.tvWatch);
			tvButton.setTypeface(Typeface.createFromAsset(FriendFollowingActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
			tvButton = (TextView) v.findViewById(R.id.tvDisable);
			tvButton.setTypeface(Typeface.createFromAsset(FriendFollowingActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
			tvButton = (TextView) v.findViewById(R.id.tvRequestedFollowing);
			tvButton.setTypeface(Typeface.createFromAsset(FriendFollowingActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));

			RelativeLayout rlDisabled = (RelativeLayout) v.findViewById(R.id.rlDisable);
			RelativeLayout rlWatch = (RelativeLayout) v.findViewById(R.id.rlWatch); 
			RelativeLayout rlRequested = (RelativeLayout) v.findViewById(R.id.rlRequested);
			rlDisabled.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Long l = new Long(item.getID());
					new LoadSubscribe().execute(l);
				}
			});
			rlWatch.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Long l = new Long(item.getID());
					new LoadUnsubscribe().execute(l);
				}
			});
			rlRequested.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Long l = new Long(item.getID());
					new LoadUnsubscribe().execute(l);
				}
			});
			rlDisabled.setVisibility(item.getWatched()==0?View.VISIBLE:View.INVISIBLE);
			rlWatch.setVisibility(item.getWatched()==1?View.VISIBLE:View.INVISIBLE);
			rlRequested.setVisibility(item.getWatched()==2?View.VISIBLE:View.INVISIBLE);
			return v;
		}
	}

	public class LoadFriendsFollowing extends QueryTask<Void, Void, FriendFollowing>{

		@Override
		protected FriendFollowing doInBackground(Void... params) {
			FriendFollowing friendsQuery = new FriendFollowing(appSettings);
			friendsQuery.getResponse(WebQuery.GET);
			return friendsQuery;
		}
		@Override
		protected void onPostExecute(FriendFollowing query) 
		{
			if (checkResult(query))
			{
				FriendFollowing.Data data = (FriendFollowing.Data) query.getData();
				for(int i = 0; i < data.friends.size();i++){
					FriendFollowing.Friend f = (FriendFollowing.Friend) data.friends.get(i);
					ProfileQuery pq = new ProfileQuery(appSettings, String.valueOf(f.getID()));
					pq.getResponse(WebQuery.GET);
					ProfileQuery.UserProfile p = pq.getData();
					int ii = 0;
					ii++;
				}
				allFriends = data.friends;
				adapter = new FriendsFollowingAdapter(allFriends);				
				//adapter.notifyDataSetChanged();
				lvFriends.setAdapter(adapter);
				if(lvFriends.getCount()<=0)	noUsersFound(true);
				else noUsersFound(false);

			}
			super.onPostExecute(query);
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
			FriendFollowing.Friend f = (FriendFollowing.Friend) allFriends.find(id);
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
			final FriendFollowing.Friend f = (FriendFollowing.Friend) allFriends.find(id);
			FriendSubscribe.Data d = (FriendSubscribe.Data) query.getData();
			if(d.getAllow()) {f.setWatched(1); adapter.notifyDataSetChanged();}
			else{
				AlertDialog.Builder b = new AlertDialog.Builder(ac);
				b.setTitle(ac.getResources().getString(R.string.alert))
				.setMessage(ac.getResources().getString(R.string.this_prifile))
				.setNegativeButton(ac.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						f.setWatched(2);
						adapter.notifyDataSetChanged();
					}
				});
				AlertDialog dialog = b.create();
				dialog.show();
			}
			adapter.notifyDataSetChanged();
		}
	
	}	
}
