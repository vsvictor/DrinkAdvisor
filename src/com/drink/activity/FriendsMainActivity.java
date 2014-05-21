package com.drink.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.drink.R;
import com.drink.query.FriendFollowing;
import com.drink.query.FriendFollowing.Data;
import com.drink.query.FriendsQueryMain;
import com.drink.query.WebQuery;

public class FriendsMainActivity extends ActivityWithMenu implements OnClickListener {

	public static int width_separator = 0;
	public static int width_news = 0;
	private TextView followOnYou;
	private TextView followOnYouNew;
	private TextView followYou;
	private TextView fbActive;
	private TextView twActive;
	private LinearLayout ll_follow_onyou;
	private LinearLayout ll_follow_you;
	private LinearLayout ll_separator;
	private RelativeLayout ll_news;
	
	private LinearLayout ll_fr_fb;
	private LinearLayout ll_fr_tw;
	private LinearLayout ll_contacts;
	private ImageView iv_fr_fb;
	private ImageView iv_fr_tw;
	private Button btn_back;
	private TextView tv_caption;

	public FriendsMainActivity() {
		super(R.layout.activity_friends_new);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		TextView tv_you_follow = (TextView) findViewById(R.id.tv_you_follow);
		tv_you_follow.setTypeface(Typeface.createFromAsset(FriendsMainActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		TextView tv_follow= (TextView) findViewById(R.id.tv_follow);
		tv_follow.setTypeface(Typeface.createFromAsset(FriendsMainActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		TextView tv_fb= (TextView) findViewById(R.id.tv_fb);
		tv_fb.setTypeface(Typeface.createFromAsset(FriendsMainActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		TextView tv_tw= (TextView) findViewById(R.id.tv_tw);
		tv_tw.setTypeface(Typeface.createFromAsset(FriendsMainActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		TextView tv_invite= (TextView) findViewById(R.id.tv_invite);
		tv_invite.setTypeface(Typeface.createFromAsset(FriendsMainActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		
		followOnYou = (TextView) findViewById(R.id.tv_follow_onyou_count);
		followOnYou.setTypeface(Typeface.createFromAsset(FriendsMainActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));
		followOnYouNew = (TextView) findViewById(R.id.tv_follow_onyou_countnew);
		followOnYouNew.setTypeface(Typeface.createFromAsset(FriendsMainActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));
		
		followYou = (TextView) findViewById(R.id.tv_follow_you_count);
		followYou.setTypeface(Typeface.createFromAsset(FriendsMainActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));
		
		fbActive = (TextView) findViewById(R.id.tv_fb_act);
		fbActive.setTypeface(Typeface.createFromAsset(FriendsMainActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));

		twActive = (TextView) findViewById(R.id.tv_tw_act);
		twActive.setTypeface(Typeface.createFromAsset(FriendsMainActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		ll_separator = (LinearLayout) findViewById(R.id.ll_separator);
		ll_news = (RelativeLayout) findViewById(R.id.ll_news);
		this.width_separator = ll_separator.getLayoutParams().width;
		this.width_news = ll_news.getLayoutParams().width;
		ll_follow_onyou = (LinearLayout) findViewById(R.id.ll_follow_onyou);
		ll_follow_you = (LinearLayout) findViewById(R.id.ll_follow_you);
		ll_fr_fb = (LinearLayout) findViewById(R.id.ll_fr_fb);
		ll_fr_tw = (LinearLayout) findViewById(R.id.ll_fr_tw);
		ll_contacts = (LinearLayout) findViewById(R.id.ll_contacts);
		iv_fr_fb = (ImageView) findViewById(R.id.iv_fr_fb);
		iv_fr_tw = (ImageView) findViewById(R.id.iv_fr_tw);
		btn_back = (Button) findViewById(R.id.btn_back);
		tv_caption = (TextView) findViewById(R.id.tv_caption);
		//
		btn_back.setOnClickListener(this);
		ll_follow_you.setOnClickListener(this);
		ll_follow_onyou.setOnClickListener(this);
		ll_fr_fb.setOnClickListener(this);
		ll_fr_tw.setOnClickListener(this);
		ll_contacts.setOnClickListener(this);
		
		tv_caption.setText(getResources().getString(R.string.fr_caption));
		//new LoadFriendsMain().execute();
		//new LoadFriendsFollowing().execute();
	}
	@Override
	public void onResume(){
		super.onResume();
		ll_separator.getLayoutParams().width = this.width_separator;
		ll_news.getLayoutParams().width = this.width_news;
		new LoadFriendsMain().execute();
	}
	public class LoadFriendsMain extends QueryTask<Void, Void, FriendsQueryMain> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			followOnYou.setVisibility(View.INVISIBLE);
			followOnYouNew.setVisibility(View.INVISIBLE);
			followYou.setVisibility(View.INVISIBLE);
			ll_separator.setVisibility(View.INVISIBLE);
			ll_news.setVisibility(View.INVISIBLE);
		}
		@Override
		protected FriendsQueryMain doInBackground(Void... params) {
			FriendsQueryMain friendsQuery = new FriendsQueryMain(appSettings);
			//friendsQuery.setOnlyReq();
			friendsQuery.getResponse(WebQuery.GET);

			return friendsQuery;
		}
		@Override
		protected void onPostExecute(FriendsQueryMain query) {
			if (checkResult(query)) {
				FriendsQueryMain.Data data = query.getData();
				followOnYou.setText(String.valueOf(data.followers));
				followOnYouNew.setText(String.valueOf(data.waiting_app));
				followYou.setText(String.valueOf(data.i_follow));
				ll_separator.setVisibility(data.waiting_app<=0?View.INVISIBLE:View.VISIBLE);
				ll_news.setVisibility(data.waiting_app<=0?View.INVISIBLE:View.VISIBLE);
				ll_separator.getLayoutParams().width = data.waiting_app<=0?0:FriendsMainActivity.width_separator;
				ll_news.getLayoutParams().width = data.waiting_app<=0?0:FriendsMainActivity.width_news;
				fbActive.setText(data.facebook?getResources().getString(R.string.fr_isactive):getResources().getString(R.string.fr_facebook_act));
				fbActive.setTextColor(data.facebook?getResources().getColor(R.color.green_light):getResources().getColor(R.color.blue_panel));
				twActive.setText(data.twitter?getResources().getString(R.string.fr_isactive):getResources().getString(R.string.fr_twitter_act));
				twActive.setTextColor(data.twitter?getResources().getColor(R.color.green_light):getResources().getColor(R.color.blue_panel));
				followOnYou.setVisibility(View.VISIBLE);
				followOnYouNew.setVisibility(View.VISIBLE);
				followYou.setVisibility(View.VISIBLE);
				ll_separator.setVisibility(View.VISIBLE);
				ll_news.setVisibility(View.VISIBLE);
			}
			super.onPostExecute(query);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_follow_onyou:{
			Intent intent = new Intent(this, FriendFollowedActivity.class);
			startActivity(intent);
			break;}
		case R.id.ll_follow_you:{
				Intent intent = new Intent(this, FriendFollowingActivity.class);
				startActivity(intent);
			break;
		}
		case R.id.ll_fr_fb:{
			Intent intent = new Intent(this, FriendsFacebookActivity.class);
			//Intent intent = new Intent(this, FriendsSearchActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.ll_fr_tw:{
			
			Intent intent = new Intent(this, FriendsTwitterActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.ll_contacts:{
			Intent intent = new Intent(this, FriendsPhoneActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.btn_back:
			FriendsMainActivity.this.finish();
			break;
		}
	}
}
