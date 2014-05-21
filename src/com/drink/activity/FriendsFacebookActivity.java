package com.drink.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.drink.R;
import com.drink.activity.BasicActivity.QueryTask;
import com.drink.activity.FriendsTwitterActivity.TwitterAdapter;
import com.drink.constants.Constants.FriendType;
import com.drink.imageloader.WebImageView;
import com.drink.imageloader.WebImageView2;
import com.drink.query.FriendFollowed;
import com.drink.query.FriendSubscribe;
import com.drink.query.FriendUnsubsrcibe;
import com.drink.query.FriendsFacebook;
import com.drink.query.FriendsFacebook.Friend;
import com.drink.query.FriendsSearchQuery;
import com.drink.query.WebQuery;
import com.drink.types.BaseObject;
import com.drink.types.BaseObjectList;
import com.drink.types.FilteredAdapter;
import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.facebook.widget.WebDialog.RequestsDialogBuilder;

public class FriendsFacebookActivity extends  BasicActivity {
	
	static private final int LOGIN_FACEBOOK = 0;
	
	private FriendsFacebookActivity ac;
	
	private BaseObjectList friends;
	private BaseObjectList signers;
	private BaseObjectList allFriends;
	
	private FriendsAdapter friendsAdapter;
	private SignersAdapter signersAdapter;
	private ListView lvSigners;
	private ListView lvFriends;
	
	private RelativeLayout rlRoot;
	private LinearLayout llFBFooter;
	private ImageView bInvateAll;
	private ImageView bInvateSelected;
	private TextView tvInviteAll;
	private TextView tvInviteSelected;
	private int heightFooter;
	private EditText edSearch;
	private LinearLayout llHint;
	private TextView tvNoUsers;
	private ScrollView svLists;
	private ImageView ivPanel;

	public FriendsFacebookActivity() {
		super(R.layout.activity_friends_facebook);
	}
	public void progressOn(){
		super.showProgressBar();
	}
	public void progressOff(){
		super.hideProgressBar();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		ac = this;
		TextView tv = (TextView) findViewById(R.id.tv_caption);
		tv.setTypeface(Typeface.createFromAsset(FriendsFacebookActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		tv.setText(R.string.your_friends_in_facebook);
		
		llHint = (LinearLayout) this.findViewById(R.id.llFBHint);
		edSearch = (EditText) this.findViewById(R.id.edFBSearch);
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
		edSearch.setOnKeyListener(new View.OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER){
					signersAdapter.resetFilter();
					signersAdapter.getFilter().filter(edSearch.getText().toString());
					signersAdapter.notifyDataSetChanged();
					Scroller.setListViewHeightBasedOnChildren(lvFriends);
					friendsAdapter.resetFilter();
					friendsAdapter.getFilter().filter(edSearch.getText().toString());
					friendsAdapter.notifyDataSetChanged();
					Scroller.setListViewHeightBasedOnChildren(lvSigners);
					if(lvSigners.getCount()<=0 && lvFriends.getCount()<=0)	noUsersFound(true);
					else noUsersFound(false);
				}
				return false;
			}});
		edSearch.addTextChangedListener(new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		  });
		heightFooter = (int)(50*this.getResources().getDisplayMetrics().density);
		rlRoot = (RelativeLayout) this.findViewById(R.id.rlMainFacebook);
		signers = new BaseObjectList();
		friends = new BaseObjectList();
		allFriends = new BaseObjectList();
		lvSigners = (ListView) this.findViewById(R.id.lvFBSigners);
		signersAdapter = new SignersAdapter(signers);
		lvFriends = (ListView) this.findViewById(R.id.lvFBNewSigners);
		friendsAdapter = new FriendsAdapter(friends);
		lvFriends.setAdapter(signersAdapter);
		lvSigners.setAdapter(friendsAdapter);
		llFBFooter = (LinearLayout) this.findViewById(R.id.llFBFooter);
		//llFBFooter.getLayoutParams().height = 0;
		llFBFooter.setVisibility(View.INVISIBLE);
		tvInviteAll = (TextView) this.findViewById(R.id.tvInvateAll);
		tvInviteAll.setTypeface(Typeface.createFromAsset(FriendsFacebookActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		tvInviteSelected = (TextView) this.findViewById(R.id.tvInviteSelected);
		tvInviteSelected.setTypeface(Typeface.createFromAsset(FriendsFacebookActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		bInvateAll = (ImageView) this.findViewById(R.id.bFBInvateAll);
		bInvateAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				ArrayList<String> list = new ArrayList<String>();
				for(int i = 0; i<friends.size();i++){
					FriendsFacebook.Friend f = (FriendsFacebook.Friend) friends.get(i);
					list.add(String.valueOf(f.getID()));
				}
				onInvate(list, 0, 50);
			}
		});
		bInvateSelected = (ImageView) this.findViewById(R.id.bFBInvateSelected);
		bInvateSelected.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				ArrayList<String> list = new ArrayList<String>();
				for(int i = 0; i<friends.size();i++){
					FriendsFacebook.Friend f = (FriendsFacebook.Friend) friends.get(i);
					if(f.getWatched() == 1){
						list.add(String.valueOf(f.getID()));
					}
				}
				onInvate(list, 0, 50);
			}
		});
		tvNoUsers = (TextView) this.findViewById(R.id.tvNoUserFacebook);
		svLists = (ScrollView) this.findViewById(R.id.scrollFBList);
		ivPanel = (ImageView) this.findViewById(R.id.ivFBSigners);

		getFBFriends();
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
	private void getFBFriends(){
		Session session = Session.getActiveSession();

		if (session == null){
			startActivityForResult(new Intent(this, FBLoginActivity.class).putExtra("login", false), LOGIN_FACEBOOK);
		}   
		else if (session.getState().isOpened())	{
			Request fRequest = Request.newMyFriendsRequest(session, new GraphUserListCallback(){
					@Override
					public void onCompleted(List<GraphUser> users, Response response){
						ArrayList<String> ids = new ArrayList<String>();
						for (int i = 0; i < users.size(); ++i){
							GraphUser g = users.get(i);
							String sAvatar = null;
							try {
								JSONObject obj = g.getInnerJSONObject().getJSONObject("picture").getJSONObject("data");
								sAvatar = obj.getString("url");
								sAvatar = sAvatar.replace("p50x50", "p100x100");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							FriendsFacebook.Friend f = new FriendsFacebook.Friend(Long.parseLong(g.getId()), g.getName(), sAvatar);
							allFriends.add(f);
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
	private void onInvate(final ArrayList<String> arr, int begin, final int count){
		Bundle params = new Bundle();
		final int i = begin;
		int j = 1;
		String ids = new String(arr.get(begin));
		while((j<count)&&((i+j))<arr.size()){
			ids = ids + ", ";
			ids = ids + arr.get(i+j);
			j++;
		}
		final int pack_size = j;
	    params.putString("message", "Learn how to make your Android apps social");
	    params.putString("to", ids);
	    WebDialog requestsDialog = (
	        new WebDialog.RequestsDialogBuilder(ac,Session.getActiveSession(),params))
	            .setOnCompleteListener(new OnCompleteListener() {
	                @Override
	                public void onComplete(Bundle values,FacebookException error) {
	                	if(error != null){
	                		return;
	                	}
	                	final String requestId = values.getString("request");
	                	if(((i+pack_size)<arr.size()) && (requestId !=null)){onInvate(arr, i+pack_size, count);}
	                	else if(((i+pack_size)<arr.size()) && (requestId == null)){}
	                	else ac.finish();
	                }
	            }).build();
	    requestsDialog.show();
	}
	private class FindFriendsTask extends QueryTask<Void, Void, FriendsSearchQuery>{
		public FriendType type;
		public ArrayList<String> ids;

		public FindFriendsTask(FriendType type, ArrayList<String> ids){
			super();
			this.type = type;
			this.ids = ids;
		}
		@Override
		protected FriendsSearchQuery doInBackground(Void... params){
			FriendsSearchQuery friendsSearchQuery = new FriendsSearchQuery(FriendsFacebookActivity.this);
			friendsSearchQuery.setNetworkType("facebook");
			friendsSearchQuery.setSocIds(ids);
			friendsSearchQuery.getResponse(WebQuery.GET);
			return friendsSearchQuery;
		}
		@Override
		protected void onPostExecute(FriendsSearchQuery query){
			if (checkResult(query))
			{
				FriendsSearchQuery.Data data = query.getData();
				if (data != null){
					for(int i = 0;i<data.friends.size();i++){
						long id = Long.parseLong(data.friends.get(i).mAppId);
						FriendsFacebook.Friend f = new FriendsFacebook.Friend(id, data.friends.get(i).name, data.friends.get(i).mPicture);
						f.setWatched(data.friends.get(i).status);
						signers.add(f);
					}
					for(int i = 0;i<allFriends.size();i++){
						FriendsFacebook.Friend g = (FriendsFacebook.Friend) allFriends.get(i);
						String sName = g.getName();
						long id = g.getID(); 
						BaseObject b = signers.find(sName);
						if(b==null){
							FriendsFacebook.Friend f = new FriendsFacebook.Friend(id, g.getName(), g.getPicture());
							f.setWatched(0);
							friends.add(f);
						}
					}
				}
				signersAdapter.notifyDataSetChanged();
				friendsAdapter.notifyDataSetChanged();
				Scroller.setListViewHeightBasedOnChildren(lvFriends);
				Scroller.setListViewHeightOwerflow(lvSigners,0);
				if(lvSigners.getCount()<=0 && lvFriends.getCount()<=0)	noUsersFound(true);
				else noUsersFound(false);

			}
			super.onPostExecute(query);
		}
	}
	private class SignersAdapter extends FilteredAdapter{
		private LayoutInflater inflater;
		private SignersAdapter ad;

		public SignersAdapter(BaseObjectList list) {
			super(list);
			inflater = (LayoutInflater) ac.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ad = this;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = inflater.inflate(R.layout.twitter_item, parent, false);
			final FriendsFacebook.Friend f = (FriendsFacebook.Friend) getData().get(position);
			WebImageView2 picture = (WebImageView2) v.findViewById(R.id.ivTWAvatarSigner);
			picture.setCircle(true);
			//picture.setMaxWidth(80);
			//picture.setMaxHeight(80);
			picture.setImagesStore(mImagesStore);
			picture.setCacheDir(appSettings.getCacheDirImage());
			picture.setImageURL(f.getPicture(), "123");
			TextView tvName = (TextView) v.findViewById(R.id.tvTWNameSigner);
			tvName.setTypeface(Typeface.createFromAsset(FriendsFacebookActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
			tvName.setText(f.getName());
			RelativeLayout rlDisabled = (RelativeLayout) v.findViewById(R.id.rlTWDisableSigner);
			RelativeLayout rlWatch = (RelativeLayout) v.findViewById(R.id.rlTWWatchSigner);
			rlDisabled.setVisibility(View.INVISIBLE);
			rlWatch.setVisibility(View.INVISIBLE);
			rlWatch.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					long l = f.getID();
					new LoadUnsubscribe().execute(l);
				}
			});
			rlDisabled.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new LoadSubscribe().execute(new Long(f.getID()));					
				}
			});
			rlDisabled.setVisibility((f.getWatched()==0)||(f.getWatched()==2)?View.VISIBLE:View.INVISIBLE);
			rlWatch.setVisibility(f.getWatched()==1?View.VISIBLE:View.INVISIBLE);
			return v;
		}
		
	}
	private class FriendsAdapter extends FilteredAdapter{
		private LayoutInflater inflater;
		private FriendsAdapter ad;

		public FriendsAdapter(BaseObjectList list) {
			super(list);
			inflater = (LayoutInflater) ac.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ad = this;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = inflater.inflate(R.layout.friends_facebook_item, parent, false);
			final FriendsFacebook.Friend f = (FriendsFacebook.Friend) getData().get(position);
			WebImageView2 picture = (WebImageView2) v.findViewById(R.id.ivFBAvatarSigner);
			picture.setCircle(true);
			picture.setMaxWidth(80);
			picture.setMaxHeight(80);
			picture.setImagesStore(mImagesStore);
			picture.setCacheDir(appSettings.getCacheDirImage());
			picture.setImageURL(f.getPicture(), "123");
			TextView tvName = (TextView) v.findViewById(R.id.tvFBNameSigner);
			tvName.setTypeface(Typeface.createFromAsset(FriendsFacebookActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
			tvName.setText(f.getName());
			final RelativeLayout rlDisabled = (RelativeLayout) v.findViewById(R.id.rlFBDisableSigner);
			RelativeLayout rlWatch = (RelativeLayout) v.findViewById(R.id.rlFBWatchSigner);
//			rlDisabled.setVisibility(View.VISIBLE);
//			rlWatch.setVisibility(View.INVISIBLE);
			rlWatch.setTag(v);
			rlWatch.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					f.setWatched(0);
					//friendsAdapter.notifyDataSetChanged();
					View vs = (View) rlDisabled.getTag();
					RelativeLayout dis = (RelativeLayout) vs.findViewById(R.id.rlFBDisableSigner);
					RelativeLayout en = (RelativeLayout) vs.findViewById(R.id.rlFBWatchSigner);
					dis.setVisibility(View.VISIBLE);
					en.setVisibility(View.INVISIBLE);
					boolean b = false;
					for(int i=0; i<getData().size();i++){
						FriendsFacebook.Friend it = (FriendsFacebook.Friend) getData().get(i);
						b = (b || (it.getWatched()==1));
					}
					if(!b){
						//llFBFooter.getLayoutParams().height = 0;
						llFBFooter.setVisibility(View.INVISIBLE);
						//Scroller.setListViewHeightOwerflow(lvSigners, 0);
					}
				}
			});
			rlDisabled.setTag(v);
			rlDisabled.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//llFBFooter.getLayoutParams().height = heightFooter;
					llFBFooter.setVisibility(View.VISIBLE);
					f.setWatched(1);
					View vs = (View) rlDisabled.getTag();
					RelativeLayout dis = (RelativeLayout) vs.findViewById(R.id.rlFBDisableSigner);
					RelativeLayout en = (RelativeLayout) vs.findViewById(R.id.rlFBWatchSigner);
					dis.setVisibility(View.INVISIBLE);
					en.setVisibility(View.VISIBLE);
					//Scroller.setListViewHeightOwerflow(lvSigners, heightFooter);
					//new InviteSelectedTask().execute(ad);
				}
			});
			rlDisabled.setVisibility((f.getWatched()==0)||(f.getWatched()==2)?View.VISIBLE:View.INVISIBLE);
			rlWatch.setVisibility(f.getWatched()==1?View.VISIBLE:View.INVISIBLE);
			return v;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (resultCode == RESULT_OK){
			switch (requestCode){
			case LOGIN_FACEBOOK:
				Session session = Session.getActiveSession();
				if (session.getState().isOpened()){
					Request fRequest = Request.newMyFriendsRequest(session,	new GraphUserListCallback(){
							@Override
							public void onCompleted(List<GraphUser> users, Response response){
								ArrayList<String> ids = new ArrayList<String>();
								
								for (int i = 0; i < users.size(); ++i){
									GraphUser g = users.get(i);
									String sAvatar = null;
									try {
										JSONObject obj = g.getInnerJSONObject().getJSONObject("picture").getJSONObject("data");
										sAvatar = obj.getString("url");
										sAvatar = sAvatar.replace("p50x50", "p100x100");
										
									} catch (JSONException e) {
										e.printStackTrace();
									}
									FriendsFacebook.Friend f = new FriendsFacebook.Friend(Long.parseLong(g.getId()), g.getName(), sAvatar);
									allFriends.add(f);
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
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void onResume(){
		super.onResume();
		edSearch.clearFocus();
		rlRoot.requestFocus();
		Scroller.setListViewHeightBasedOnChildren(lvSigners);
		Scroller.setListViewHeightBasedOnChildren(lvFriends);
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
			FriendsFacebook.Friend f = (FriendsFacebook.Friend) signers.find(id);
			f.setWatched(0);
			signersAdapter.notifyDataSetChanged();
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
			final FriendsFacebook.Friend f = (FriendsFacebook.Friend) signers.find(id);
			FriendSubscribe.Data d = (FriendSubscribe.Data) query.getData();			
			if(d.getAllow()) {f.setWatched(1); signersAdapter.notifyDataSetChanged();}
			else{
				AlertDialog.Builder b = new AlertDialog.Builder(ac);
				b.setTitle(ac.getResources().getString(R.string.alert))
				.setMessage(ac.getResources().getString(R.string.this_prifile))
				.setNegativeButton(ac.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						f.setWatched(2);
						signersAdapter.notifyDataSetChanged();
					}
				});
				AlertDialog dialog = b.create();
				dialog.show();
			}
		}
	}	
}
