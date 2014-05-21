package com.drink.activity;

import com.drink.R;

import com.drink.activity.BasicActivity.QueryTask;
import com.drink.activity.FriendFollowingActivity.FriendsFollowingAdapter;
import com.drink.activity.FriendFollowingActivity.LoadSubscribe;
import com.drink.activity.FriendFollowingActivity.LoadUnsubscribe;
import com.drink.constants.Constants;
import com.drink.imageloader.WebImageView2;
import com.drink.query.FriendEnableWatch;
import com.drink.query.FriendFollowed;
import com.drink.query.FriendFollowing;
import com.drink.query.FriendSubscribe;
import com.drink.query.FriendUnsubsrcibe;
import com.drink.query.WebQuery;
import com.drink.query.FriendFollowing.Friend;
import com.drink.types.BaseObjectList;
import com.drink.types.FilteredAdapter;

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
import android.widget.ScrollView;
import android.widget.TextView;

public class FriendFollowedActivity extends ActivityWithMenu{
	private RelativeLayout rlRoot;
	private RelativeLayout rlNews;
	
	private EditText edSearch;
	private ListView lvNewSigners;
	private ListView lvSigners;
	private Button rlBack;
	private ImageView ivUpdate;
	private ImageView ivLeftMenu;
	private LinearLayout llHint;
	private SignersAdapter adapterSigner;
	private SignersAdapter adapterNewSigner;	
	private BaseObjectList signers;
	private BaseObjectList newSigners;
	private BaseObjectList iFollow;
	private ActivityWithMenu ac;

	private ScrollView svLists;
	private ImageView ivPanel;
	private TextView tvNoUsers;
	
	public FriendFollowedActivity() {
		super(R.layout.activity_friends_followed);
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);		
		ac = this;
		TextView tv = (TextView) this.findViewById(R.id.tv_caption);
		tv.setText(ac.getResources().getString(R.string.your_followers));
		tv.setTypeface(Typeface.createFromAsset(FriendFollowedActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv = (TextView) this.findViewById(R.id.btn_back);
		tv.setTypeface(Typeface.createFromAsset(FriendFollowedActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv = (TextView) this.findViewById(R.id.tvSearchTextFollowed);
		tv.setTypeface(Typeface.createFromAsset(FriendFollowedActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv = (TextView) this.findViewById(R.id.tvSignersFollowed);
		tv.setTypeface(Typeface.createFromAsset(FriendFollowedActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));
		tv = (TextView) this.findViewById(R.id.tvSigners);
		tv.setTypeface(Typeface.createFromAsset(FriendFollowedActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));

		rlRoot = (RelativeLayout) this.findViewById(R.id.rlMainFollowed);
		rlNews = (RelativeLayout) findViewById(R.id.rlSubscribersFollowed);
		llHint = (LinearLayout) this.findViewById(R.id.llHintFollowed);
		edSearch = (EditText) this.findViewById(R.id.edSearchFollowed);
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
				adapterSigner = new SignersAdapter(signers, R.id.rlWatchSigner);
				adapterSigner.getFilter().filter(edSearch.getText().toString());
				lvSigners.setAdapter(adapterSigner);
				adapterNewSigner = new SignersAdapter(newSigners, R.id.rlEnabledSigner);
				adapterNewSigner.getFilter().filter(edSearch.getText().toString());
				lvNewSigners.setAdapter(adapterNewSigner);
				if(lvSigners.getCount()<=0 && lvNewSigners.getCount()<=0)	noUsersFound(true);
				else noUsersFound(false);
			}
			@Override
			public void afterTextChanged(Editable s) {
				adapterSigner = new SignersAdapter(signers, R.id.rlWatchSigner);
				adapterSigner.getFilter().filter(edSearch.getText().toString());
				lvSigners.setAdapter(adapterSigner);
				adapterNewSigner = new SignersAdapter(newSigners, R.id.rlEnabledSigner);
				adapterNewSigner.getFilter().filter(edSearch.getText().toString());
				lvNewSigners.setAdapter(adapterNewSigner);
				if(lvSigners.getAdapter().getCount()<=0 && lvNewSigners.getAdapter().getCount()<=0)	noUsersFound(true);
				else noUsersFound(false);
			}
		  });
		
		lvNewSigners = (ListView) this.findViewById(R.id.lvNewSigners);	
		lvNewSigners.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent();
				i.putExtra(Constants.KEY_USER_ID, String.valueOf(id));
				i.setClass(FriendFollowedActivity.this, UserProfileActivity.class);
				startActivity(i);
			}
		});
		lvSigners = (ListView) this.findViewById(R.id.lvSigners);
		lvSigners.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent();
				i.putExtra(Constants.KEY_USER_ID, String.valueOf(id));
				i.setClass(FriendFollowedActivity.this, UserProfileActivity.class);
				startActivity(i);
			}
		});

		rlBack = (Button) this.findViewById(R.id.btn_back);
		rlBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ac, FriendsMainActivity.class);
				startActivity(intent);
			}
		});
		
		svLists = (ScrollView) this.findViewById(R.id.scrollList);
		ivPanel = (ImageView) this.findViewById(R.id.ivSigners);
		tvNoUsers = (TextView) this.findViewById(R.id.tvNoUserFollowed);
		tvNoUsers.setVisibility(View.INVISIBLE);
		new LoadFriendsFollowed().execute();
	}
	@Override
	public void onResume(){
		super.onResume();
		edSearch.clearFocus();
		rlRoot.requestFocus();
		Scroller.setListViewHeightBasedOnChildren(lvSigners);
		Scroller.setListViewHeightBasedOnChildren(lvNewSigners);
		
	}
	private void noUsersFound(boolean visible){
		if(visible){
			svLists.setVisibility(View.INVISIBLE);
			ivPanel.setVisibility(View.INVISIBLE);
			tvNoUsers.setVisibility(View.VISIBLE);
		}
		else{
			svLists.setVisibility(View.VISIBLE);
			ivPanel.setVisibility(View.VISIBLE);
			tvNoUsers.setVisibility(View.INVISIBLE);
		}
	}
	public class SignersAdapter extends FilteredAdapter{

		private LayoutInflater inflater;
		private int visibled;
		public SignersAdapter(BaseObjectList list, int buttonDefault) {
			super(list);
			inflater = (LayoutInflater) ac.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			visibled = buttonDefault;
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
			View v = inflater.inflate(R.layout.signer_item, parent, false);
			final FriendFollowed.Friend item = (FriendFollowed.Friend) getData().get(position);
			WebImageView2 picture = (WebImageView2) v.findViewById(R.id.ivAvatarSigner);
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
					i.setClass(FriendFollowedActivity.this, UserProfileActivity.class);
					startActivity(i);
				}
			});
			
			TextView tvName = (TextView) v.findViewById(R.id.tvNameSigner);
			tvName.setText(item.getName());
			tvName.setTypeface(Typeface.createFromAsset(FriendFollowedActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
			tvName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.putExtra(Constants.KEY_USER_ID, String.valueOf(item.getID()));
					i.setClass(FriendFollowedActivity.this, UserProfileActivity.class);
					startActivity(i);
				}
			});
			
			TextView tvButton = (TextView) v.findViewById(R.id.tvWatchSigner);
			tvButton.setTypeface(Typeface.createFromAsset(FriendFollowedActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
			tvButton = (TextView) v.findViewById(R.id.tvDisableSigner);
			tvButton.setTypeface(Typeface.createFromAsset(FriendFollowedActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
			tvButton = (TextView) v.findViewById(R.id.tvEnabledSigner);
			tvButton.setTypeface(Typeface.createFromAsset(FriendFollowedActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
			tvButton = (TextView) v.findViewById(R.id.tvRequestedSigner);
			tvButton.setTypeface(Typeface.createFromAsset(FriendFollowedActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
			
			RelativeLayout rlDisabled = (RelativeLayout) v.findViewById(R.id.rlDisableSigner);
			final RelativeLayout rlEnabled = (RelativeLayout) v.findViewById(R.id.rlEnabledSigner);
			RelativeLayout rlWatch = (RelativeLayout) v.findViewById(R.id.rlWatchSigner);
			RelativeLayout rlRequested = (RelativeLayout) v.findViewById(R.id.rlRequestedSigner);
			rlDisabled.setVisibility(View.INVISIBLE);
			rlEnabled.setVisibility(View.INVISIBLE);
			rlWatch.setVisibility(View.INVISIBLE);
			rlRequested.setVisibility(View.INVISIBLE);
			
			RelativeLayout rlVisibled = (RelativeLayout) v.findViewById(this.visibled);
			rlVisibled.setVisibility(View.VISIBLE);
			if(this.visibled == R.id.rlWatchSigner){
				rlVisibled.setVisibility(View.INVISIBLE);
				rlDisabled.setVisibility(item.getWatched()==0?View.VISIBLE:View.INVISIBLE);
				rlWatch.setVisibility(item.getWatched()==1?View.VISIBLE:View.INVISIBLE);
				rlRequested.setVisibility(item.getWatched()==2?View.VISIBLE:View.INVISIBLE);
			}
/*			if(this.visibled == R.id.rlWatchSigner && (item.getWatched()==0)){
				rlDisabled.setVisibility(View.VISIBLE);
				rlWatch.setVisibility(View.INVISIBLE);
				rlRequested.setVisibility(View.INVISIBLE);
			}
			else if(this.visibled == R.id.rlWatchSigner && (item.getWatched()==1)){
				rlDisabled.setVisibility(View.INVISIBLE);
				rlWatch.setVisibility(View.VISIBLE);
				rlRequested.setVisibility(View.INVISIBLE);
			}
			else if(this.visibled == R.id.rlWatchSigner && (item.getWatched()==2)){
				rlDisabled.setVisibility(View.INVISIBLE);
				rlWatch.setVisibility(View.INVISIBLE);
				rlRequested.setVisibility(View.VISIBLE);
			}*/
			rlWatch.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Long l = new Long(item.getID());
					new LoadUnsubscribe().execute(l);
				}
			});
			rlDisabled.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Long l = new Long(item.getID());
					new LoadSubscribe().execute(l);
				}
			});
			rlRequested.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Long l = new Long(item.getID());
					new LoadUnsubscribe().execute(l);
				}
			});
			rlEnabled.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Long l = new Long(item.getID());
					new LoadEnable().execute(l);
					rlEnabled.setVisibility(View.INVISIBLE);
					new LoadFriendsFollowed().execute();
				}
			});
			return v;
		}
	}
	public class LoadFriendsFollowed extends QueryTask<Void, Void, FriendFollowed>{

		@Override
		protected FriendFollowed doInBackground(Void... params) {
			FriendFollowed friendsQuery = new FriendFollowed(appSettings);
			friendsQuery.getResponse(WebQuery.GET);
			return friendsQuery;
		}
		@Override
		protected void onPostExecute(FriendFollowed query) 
		{
			if (checkResult(query))
			{
				FriendFollowed.Data data = (FriendFollowed.Data) query.getData();
				signers = data.subscribers;
				adapterSigner = new SignersAdapter(signers, R.id.rlWatchSigner);				
				lvSigners.setAdapter(adapterSigner);
				newSigners = data.news;
				adapterNewSigner = new SignersAdapter(newSigners, R.id.rlEnabledSigner);
				lvNewSigners.setAdapter(adapterNewSigner);
				if(data.news.size() <=0)rlNews.getLayoutParams().height = 0;
				Scroller.setListViewHeightBasedOnChildren(lvNewSigners);
				Scroller.setListViewHeightBasedOnChildren(lvSigners);
				new LoadFriendsFollowing().execute();
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
			FriendFollowed.Friend f = (FriendFollowed.Friend) signers.find(id);
			f.setWatched(0);
			adapterSigner.notifyDataSetChanged();
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
			final FriendFollowed.Friend f = (FriendFollowed.Friend) signers.find(id);
			FriendSubscribe.Data d = (FriendSubscribe.Data) query.getData();			
			if(d.getAllow()) {f.setWatched(1); adapterSigner.notifyDataSetChanged();}
			else{
				AlertDialog.Builder b = new AlertDialog.Builder(ac);
				b.setTitle(ac.getResources().getString(R.string.alert))
				.setMessage(ac.getResources().getString(R.string.this_prifile))
				.setNegativeButton(ac.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						f.setWatched(2);
						adapterSigner.notifyDataSetChanged();
					}
				});
				AlertDialog dialog = b.create();
				dialog.show();
			}
		}
	}	
	public class LoadEnable extends QueryTask<Long, Void, FriendEnableWatch>{

		private int id;
		@Override
		protected FriendEnableWatch doInBackground(Long... params) {
			id = params[0].intValue();
			FriendEnableWatch friendsQuery = new FriendEnableWatch(appSettings, params[0]);
			friendsQuery.getResponse(WebQuery.GET);
			return friendsQuery;
		}
		@Override
		protected void onPostExecute(FriendEnableWatch query) 
		{
			super.onPostExecute(query);
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
		{/*
			if (checkResult(query))
			{
				FriendFollowing.Data data = (FriendFollowing.Data) query.getData();
				iFollow = data.friends;
				for(int i = 0; i<signers.size(); i++){
					FriendFollowed.Friend f = (FriendFollowed.Friend) signers.get(i);
					for(int j=0;j<iFollow.size();j++){
						if(f.getID() == iFollow.get(j).getID()){
							((FriendFollowed.Friend)signers.get(i)).setWatched(1);
							break;
						}
					}
				}
			}*/
			adapterSigner.notifyDataSetChanged();
			Scroller.setListViewHeightBasedOnChildren(lvNewSigners);
			Scroller.setListViewHeightBasedOnChildren(lvSigners);
			if(lvSigners.getCount()<=0 && lvNewSigners.getCount()<=0)	noUsersFound(true);
			else noUsersFound(false);
			super.onPostExecute(query);
		}
		
	}

}
