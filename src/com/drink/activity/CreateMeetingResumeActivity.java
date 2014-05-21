package com.drink.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.helpers.StatisticsHelper;
import com.drink.imageloader.WebImageView2;
import com.drink.query.AddMeetingQuery;
import com.drink.query.BarQuery;
import com.drink.query.WebQuery;
import com.drink.types.Friend;

public class CreateMeetingResumeActivity extends BasicActivity 
{
	TextView tvTitle;
	TextView tvWhen;
	WebImageView2 mInvited1;
	WebImageView2 mInvited2;
	WebImageView2 mInvited3;
	WebImageView2 mInvited4;
	LinearLayout llInvited;
	RelativeLayout rlInvitedCount;
	TextView mInvitedCount;
	LinearLayout llBar;
	WebImageView2 picture;
	TextView tvBarName;
	TextView tvBarOpened;
	TextView tvBarDistance;
	TextView tvBarRating;
	TextView tvAddress;
	TextView tvShowOnMap;
	TextView tvComment;
	ImageView ivFriends;
	Switch swFriends;
	ImageView ivFacebook;
	Switch swFacebook;
	ImageView ivTwitter;
	Switch swTwitter;
	
	double lat = 0.0;
	double lng = 0.0;

	String bar_id;
	Date date = new Date();
	ArrayList<Friend> invited;

	public CreateMeetingResumeActivity() 
	{
		super(R.layout.activity_create_meeting_resume);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		cancel = true;
	
		super.onCreate(savedInstanceState);

		mCaption.setText(R.string.meeting_resume);
		
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		tvTitle.setText(getIntent().getStringExtra("meeting_title"));

		bar_id = getIntent().getStringExtra("bar_id");
		date.setTime(getIntent().getLongExtra("meeting_date", 0));
		invited = getIntent().getParcelableArrayListExtra("selected");
		
		String _time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);

		String _date;
		if (CommonHelper.isToday(date))
		{
			_date = getString(R.string.today) + ",";
		}
		else
		{
			_date = android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(date);
		}
		
		tvWhen = (TextView) findViewById(R.id.tv_time);
		tvWhen.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		tvWhen.setText(_date + " " + _time);

		Button button = (Button) findViewById(R.id.btn_create_meeting);
		button.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		button.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					new CreateTask().execute();
				}
			});
			
		llBar = (LinearLayout) findViewById(R.id.ll_bar);
		llBar.setVisibility(View.GONE);
		
		picture = (WebImageView2) llBar.findViewById(R.id.picture);
		picture.setImagesStore(mImagesStore);
		picture.setCacheDir(appSettings.getCacheDirImage());
		picture.setHeight(123);
		
		tvBarName = (TextView) llBar.findViewById(R.id.title);
		tvBarName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		tvBarOpened = (TextView) llBar.findViewById(R.id.tv_opened);
		tvBarOpened.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));

		tvBarDistance = (TextView) llBar.findViewById(R.id.tv_distance);
		tvBarDistance.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));

		tvBarRating = (TextView) llBar.findViewById(R.id.tv_rating);
		tvBarRating.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));

		tvAddress = (TextView) llBar.findViewById(R.id.tv_address);
		tvAddress.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		llInvited = (LinearLayout) findViewById(R.id.ll_invited);
		llInvited.setVisibility(View.GONE);
		
		rlInvitedCount = (RelativeLayout) findViewById(R.id.rl_invited_count);
		rlInvitedCount.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(CreateMeetingResumeActivity.this, InvitedActivity.class);
				intent.putExtra("selected", invited);
				
				startActivity(intent);
			}
		});
		rlInvitedCount.setVisibility(View.INVISIBLE);
		
		TextView tvInvited = (TextView) findViewById(R.id.tv_invited);
		tvInvited.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));

		TextView tvInvitedAll = (TextView) rlInvitedCount.findViewById(R.id.tv_invited_all);
		tvInvitedAll.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		mInvitedCount = (TextView) rlInvitedCount.findViewById(R.id.tv_invited_count);
		mInvitedCount.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));

		mInvited1 = (WebImageView2) findViewById(R.id.invited_1);
		mInvited1.setCircle(true);
		mInvited1.setImagesStore(mImagesStore);
		mInvited1.setCacheDir(appSettings.getCacheDirImage());

		mInvited2 = (WebImageView2) findViewById(R.id.invited_2);
		mInvited2.setCircle(true);
		mInvited2.setImagesStore(mImagesStore);
		mInvited2.setCacheDir(appSettings.getCacheDirImage());

		mInvited3 = (WebImageView2) findViewById(R.id.invited_3);
		mInvited3.setCircle(true);
		mInvited3.setImagesStore(mImagesStore);
		mInvited3.setCacheDir(appSettings.getCacheDirImage());

		mInvited4 = (WebImageView2) findViewById(R.id.invited_4);
		mInvited4.setCircle(true);
		mInvited4.setImagesStore(mImagesStore);
		mInvited4.setCacheDir(appSettings.getCacheDirImage());

		TextView tvShowOnMap = (TextView) llBar.findViewById(R.id.tv_show_on_map);
		tvShowOnMap.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		tvShowOnMap.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					Intent intent = new Intent(CreateMeetingResumeActivity.this, BarOnMapActivity.class);
					intent.putExtra("name", mCaption.getText());
					intent.putExtra("lat", lat);
					intent.putExtra("lng", lng);
	
					startActivity(intent);
				}
			});
			
		LinearLayout llDescription = (LinearLayout) findViewById(R.id.include_meeting_description);
		
		TextView tvHeading = (TextView) llDescription.findViewById(R.id.tv_title);
		tvHeading.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		tvHeading.setText(R.string.meeting_description);
		
		tvComment = (TextView) llDescription.findViewById(R.id.tv_text);
		tvComment.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tvComment.setText(getIntent().getStringExtra("meeting_comment"));
		
		LinearLayout llReadMore = (LinearLayout) llDescription.findViewById(R.id.ll_read_more);
		llReadMore.setVisibility(View.GONE);

		boolean facebook = appSettings.getConnectFacebook() && appSettings.getPublishOnFacebook();
		boolean twitter = appSettings.getConnectTwitter() && appSettings.getPublishOnTwitter();

		// friends settings
		ivFriends = (ImageView) findViewById(R.id.img_settings_friends);
		
		TextView tvFriends = (TextView) findViewById(R.id.tv_settings_friends);
		tvFriends.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));

		swFriends = (Switch) findViewById(R.id.switch_settings_friends);
		swFriends.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				ivFriends.setSelected(isChecked);
			}
		});
		
		// facebook settings
		ivFacebook = (ImageView) findViewById(R.id.img_settings_facebook);
		ivFacebook.setSelected(facebook);
		
		TextView tvFacebook = (TextView) findViewById(R.id.tv_settings_facebook);
		tvFacebook.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		swFacebook = (Switch) findViewById(R.id.switch_settings_facebook);
		swFacebook.setChecked(facebook);
		swFacebook.setOnCheckedChangeListener(new OnCheckedChangeListener() 
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
				{
					if (isChecked)
					{
						ivFacebook.setSelected(true);
						checkConnectFacebook();
					}
					else
					{
						ivFacebook.setSelected(false);
					}
				}
			});

		// twitter settings
		ivTwitter = (ImageView) findViewById(R.id.img_settings_twitter);
		ivTwitter.setSelected(twitter);
		
		TextView tvTwitter = (TextView) findViewById(R.id.tv_settings_twitter);
		tvTwitter.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		swTwitter = (Switch) findViewById(R.id.switch_settings_twitter);
		swTwitter.setChecked(twitter);
		swTwitter.setOnCheckedChangeListener(new OnCheckedChangeListener() 
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
				{
					if (isChecked)
					{
						ivTwitter.setSelected(true);
						checkConnectTwitter();
					}
					else
					{
						ivTwitter.setSelected(false);
					}
				}
			});
		
		updateInvited();
		
		new LoadTask().execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		super.onActivityResult(requestCode, resultCode, intent);
		
		switch (requestCode) 
		{
		case CONNECT_FACEBOOK:
			if (!appSettings.getConnectFacebook())
			{
				ivFacebook.setSelected(false);
				swFacebook.setChecked(false);
			}
			break;
		case CONNECT_TWITTER:
			if (!appSettings.getConnectTwitter())
			{
				ivTwitter.setSelected(false);
				swTwitter.setChecked(false);
			}
			break;
		default:
			break;
		}
	}

	private void updateInvited()
	{
		if (invited.size() > 0)
		{
			llInvited.setVisibility(View.VISIBLE);
			mInvited1.setImageURL(invited.get(0).avatar, "123");
			
			if (invited.size() > 1)
			{
				mInvited2.setVisibility(View.VISIBLE);
				mInvited2.setImageURL(invited.get(1).avatar, "123");

				if (invited.size() > 2)
				{
					mInvited3.setVisibility(View.VISIBLE);
					mInvited3.setImageURL(invited.get(2).avatar, "123");

					if (invited.size() > 3)
					{
						mInvited4.setVisibility(View.VISIBLE);
						mInvited4.setImageURL(invited.get(3).avatar, "123");
						
						if (invited.size() > 4)
						{
							rlInvitedCount.setVisibility(View.VISIBLE);
							mInvitedCount.setText(Integer.toString(invited.size()));
						}
						else
						{
							rlInvitedCount.setVisibility(View.INVISIBLE);
						}
					}
					else
					{
						mInvited4.setVisibility(View.INVISIBLE);
					}
				}
				else
				{
					mInvited3.setVisibility(View.INVISIBLE);
					mInvited4.setVisibility(View.INVISIBLE);
				}
			}
			else
			{
				mInvited2.setVisibility(View.INVISIBLE);
				mInvited3.setVisibility(View.INVISIBLE);
				mInvited4.setVisibility(View.INVISIBLE);
			}
		}
		else
		{
			llInvited.setVisibility(View.GONE);
		}
	}
	
	private class LoadTask extends QueryTask<Void, Void, BarQuery> 
	{
		@Override
		protected BarQuery doInBackground(Void... params) 
		{
			BarQuery query = new BarQuery(appSettings, bar_id);
			query.getResponse(WebQuery.GET);

			return query;
		}

		protected void onPostExecute(BarQuery query) 
		{
			if (checkResult(query))
			{
				BarQuery.Data data = query.getData();
			
				// set bar title
				tvBarName.setText(data.name);
/*				// set bar distance
				String dist = getString(R.string.dist_c);
				double distance = Comm;
				if (distance > 0.001)
				{
					if (dist.equals("mi")) 
					{
						distance = distance / 1609.0;
					} 
					else 
					{
						distance = distance / 1000.0;
					}
					distance = (double) (int) (distance * 100.0) / 100.0;
					
					viewHolder.distance.setVisibility(View.VISIBLE);
					viewHolder.distance.setText(String.valueOf(distance) + " " + dist);
				}
				else
				{
					viewHolder.distance.setVisibility(View.GONE);
				}*/
				tvBarDistance.setVisibility(View.INVISIBLE);
				// set bar rating
				tvBarRating.setText(String.valueOf(data.rating) + "%");
				// set bar picture					
				if (data.pictures_count > 0)
				{
					picture.setImageURL(data.picList.get(0), "123");
				}

				tvAddress.setText(data.address);

				lat = data.lat;
				lng = data.lng;

				llBar.setVisibility(View.VISIBLE);
			}
			
			super.onPostExecute(query);
		}
	}

	private class CreateTask extends QueryTask<Void, Void, AddMeetingQuery> 
	{
		@Override
		protected AddMeetingQuery doInBackground(Void... params) 
		{
			AddMeetingQuery query = new AddMeetingQuery(appSettings, bar_id, date, tvTitle.getText().toString(), tvComment.getText().toString(), false, false);
			
			ArrayList<String> _invited = new ArrayList<String>();
			for (int i = 0; i < invited.size(); ++i)
			{
				_invited.add(invited.get(i).id);
			}
			
			query.setInvitedToMeetings(_invited);
			query.getResponse(WebQuery.POST);
			
			return query;
		}

		@Override
		protected void onPostExecute(AddMeetingQuery query) 
		{
			if (checkResult(query))
			{
				StatisticsHelper.sendMeetingEvent(CreateMeetingResumeActivity.this, bar_id);
				
				appSettings.setEventsMeetingsCount(appSettings.getEventsMeetingsCount() + 1);
				
				setResult(RESULT_OK);
				finish();
			}
			
			super.onPostExecute(query);
		}
	}
}
