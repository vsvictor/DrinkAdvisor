
package com.drink.activity;

import java.io.File;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.drink.R;
import com.drink.constants.Constants;
import com.drink.constants.Constants.EventsHolder;
import com.drink.constants.Constants.UserProfileRequest;
import com.drink.imageloader.WebImageView;
import com.drink.imageloader.WebImageView2;
import com.drink.query.EventsQuery;
import com.drink.query.EventsQuery.EventData;
import com.drink.query.EventsQuery.EventQueryData;
import com.drink.query.FriendRequestQuery;
import com.drink.query.FriendRequestQuery.FriendRequestData;
import com.drink.query.ProfileQuery;
import com.drink.query.ProfileQuery.UserProfile;
import com.drink.query.WebQuery;

public class UserProfileActivity extends BasicActivity 
{
	final static int EVENT_LIMIT = 20;

	UserProfileRequest mActiveRequest = UserProfileRequest.NON;

	static final String TAG = UserProfileActivity.class.getSimpleName();

	static private final int EDIT_SETTINGS = 20;

	ListView mUserActivitiesList;
	WebImageView2 mProfileImage;
	TextView mUserName;
	TextView mFriends;
	TextView mCheckins;
	TextView mComments;
	
	LinearLayout llInfo;
	LinearLayout llActivity;

	Boolean mIsOwnProfile = false;
	UserProfile mUserData;
	private UserActionsAdapter mAdapter;

	private int mOffset = 0;
	ArrayList<EventsQuery.EventData> mUserEvents;
	private int mAllEventsCount = 0;

	public UserProfileActivity() 
	{
		super(R.layout.activity_user_profile);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mCaption.setText(getString(R.string.profile));
		
		llInfo = (LinearLayout) findViewById(R.id.ll_info);
		llInfo.setVisibility(View.GONE);

		mProfileImage = (WebImageView2) llInfo.findViewById(R.id.profile_avatar);
		mProfileImage.setCircle(true);
		mProfileImage.setImagesStore(mImagesStore);
		mProfileImage.setCacheDir(appSettings.getCacheDirImage());
		
		mUserName = (TextView) llInfo.findViewById(R.id.user_name);
		mUserName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		mFriends = (TextView) llInfo.findViewById(R.id.tv_friends);
		mFriends.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		
		mCheckins = (TextView) llInfo.findViewById(R.id.tv_checkins);
		mCheckins.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));

		mComments = (TextView) llInfo.findViewById(R.id.tv_comments);
		mComments.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		
		llActivity = (LinearLayout) findViewById(R.id.ll_activity);
		llActivity.setVisibility(View.GONE);

		mUserActivitiesList = (ListView) llActivity.findViewById(R.id.user_actions_list);

		mUserData = new UserProfile();
		if (getIntent().getExtras() != null) 
		{
			if (getIntent().getExtras().containsKey(Constants.KEY_USER_ID)) 
			{
				mUserData.mUserId = getIntent().getExtras().getString(Constants.KEY_USER_ID);
				if (mUserData.mUserId.equals(appSettings.getUserId())) 
				{
					mIsOwnProfile = true;
				}
			}
		} 
		else 
		{
			mIsOwnProfile = true;
			mUserData.mUserId = appSettings.getUserId();
		}
		
		if (mIsOwnProfile)
		{
			// add settings button
			ImageButton btnSettings = new ImageButton(this);
			btnSettings.setBackgroundResource(R.drawable.button_settings);
			btnSettings.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					startActivity(new Intent(UserProfileActivity.this, ProfileActivity.class));
				}
			});
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			btnSettings.setLayoutParams(params);
			
			RelativeLayout rlNavigationTop = (RelativeLayout) findViewById(R.id.rl_navigation_up);
			rlNavigationTop.addView(btnSettings);
		}

		mActiveRequest = UserProfileRequest.USER;
		mAdapter = new UserActionsAdapter();
		mUserActivitiesList.setAdapter(mAdapter);
/*
		mAddFriend.setOnClickListener(new View.OnClickListener() 
			{
				public void onClick(View v) 
				{
					mActiveRequest = UserProfileRequest.ADD_FRIEND;
					new LoadUserTask().execute();
				}
			});
		mOpenSettings.setOnClickListener(new View.OnClickListener() 
			{
				public void onClick(View v) 
				{
					startActivityForResult(new Intent(UserProfileActivity.this,	ProfileActivity.class), EDIT_SETTINGS);
				}
			});
		mDeleteFriend.setOnClickListener(new View.OnClickListener() 
			{
				public void onClick(View v) 
				{
					mActiveRequest = UserProfileRequest.DELETE_FRIEND;
					new LoadUserTask().execute();
				}
			});
	*/
		new LoadUserTask().execute();
	}

	class LoadUserTask extends QueryTask<Void, Void, WebQuery> 
	{
		@Override
		protected WebQuery doInBackground(Void... params) 
		{
			switch (mActiveRequest) 
			{
			case USER: 
			case USER_ONLY:
			{
				ProfileQuery profileQuery = new ProfileQuery(appSettings, mUserData.mUserId);
				profileQuery.getResponse(WebQuery.GET);
				
				return profileQuery;
			}
			case EVENTS: 
			{
				EventsQuery eventsQuery = new EventsQuery(getApplicationContext(), EventsHolder.PROFILE);
				eventsQuery.setUserId(mUserData.mUserId);
				eventsQuery.setOffset(mOffset);
				eventsQuery.setLimit(EVENT_LIMIT);
				eventsQuery.setDeviceToken(appSettings.getContentToken());
				eventsQuery.setUserToken(appSettings.getUserToken());
				eventsQuery.getResponse(WebQuery.GET);
				
				return eventsQuery;
			}
			case ADD_FRIEND: 
			{
				FriendRequestQuery requestQuery = new FriendRequestQuery(getBaseContext(), "createFriendshipOffer");
				requestQuery.setFriendId(mUserData.mUserId);
				String message = getResources().getString(R.string.friend_request_message);
				requestQuery.setMessageText(message);
				requestQuery.getResponse(WebQuery.GET);
				
				return requestQuery;
			}
			case DELETE_FRIEND: 
			{
				FriendRequestQuery requestQuery = new FriendRequestQuery(getBaseContext(), "rejectFriendshipOffer");
				requestQuery.setFriendId(mUserData.mUserId);
				requestQuery.getResponse(WebQuery.GET);
				
				return requestQuery;
			}
			default:
				break;
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(WebQuery query) 
		{
			super.onPostExecute(query);

			if (checkResult(query)) 
			{
				switch (mActiveRequest) 
				{
				case USER: 
				case USER_ONLY:
				{
					mUserData = (UserProfile) query.getData();
					drawHeaderView();

					mProfileImage.setImageURL(mUserData.mAvatar, "123");
					mProfileImage.update();
					
					mUserName.setText(mUserData.mName);
					
					mFriends.setText(Integer.toString(mUserData.mFollowers));
					mCheckins.setText(Integer.toString(mUserData.mCheckins));
					mComments.setText(Integer.toString(mUserData.mComments));
					
					if (mActiveRequest == UserProfileRequest.USER)
					{
						llInfo.setVisibility(View.VISIBLE);
						
						mActiveRequest = UserProfileRequest.EVENTS;
						new LoadUserTask().execute();
					}
					
					break;
				}
				case EVENTS: 
				{
					EventsQuery eventsQuery = (EventsQuery) query;
					EventQueryData data = eventsQuery.getData();
					mAllEventsCount = data.mAllEventsCount;
					if (mUserEvents != null) 
					{
						mUserEvents.addAll(data.mEventList);
					} 
					else 
					{
						mUserEvents = data.mEventList;
					}
					
					llActivity.setVisibility(View.VISIBLE);
					mAdapter.notifyDataSetChanged();
					
					break;
				}
				case ADD_FRIEND: 
				{
					FriendRequestQuery requestQuery = (FriendRequestQuery) query;
					FriendRequestData data = requestQuery.getData();
					if (data.mIsSuccess == true) 
					{
//						mAddFriend.setEnabled(false);
//						mAddFriend.setText(getResources().getString(R.string.profile_request_sent));
					}
					break;
				}
				case DELETE_FRIEND: 
				{
					FriendRequestQuery requestQuery = (FriendRequestQuery) query;
					FriendRequestData data = requestQuery.getData();
					if (data.mIsSuccess == true) 
					{
						setResult(RESULT_OK);
						finish();
					}
					break;
				}
				default:
					break;
				}
			}
		}
	}

	private void drawHeaderView() 
	{
/*		if (mIsOwnProfile == true) 
		{
			mOpenSettings.setVisibility(View.VISIBLE);
		} 
		else 
		{
			mAddFriend.setVisibility(View.VISIBLE);
			String text = "";
			if (mUserData.mFriendRequestSent == true) 
			{
				text = getResources().getString(R.string.profile_request_sent);
				mAddFriend.setEnabled(false);
			} 
			else if (mUserData.mIsFriend) 
			{
				mDeleteFriend.setVisibility(View.VISIBLE);
			} 
			else 
			{
				text = getResources().getString(R.string.profile_send_request);
			}
			mAddFriend.setText(text);
		}*/
	}

	class UserActionsAdapter extends BaseAdapter 
	{
		static final int ICON_LIKE = 0;
		static final int ICON_DISLIKE = 1;
		static final int ICON_RATE_LIKE = 2;
		static final int ICON_RATE_DISLIKE = 3;
		static final int ICON_COMMENT = 4;
		static final int ICON_CHECKIN = 5;
		static final int ICON_GO = 6;
		static final int ICON_MEETING = 7;
		static final int ICON_FRIEND = 8;
		static final int ICON_RATE = 9;

		LayoutInflater mInflater;

		public UserActionsAdapter() 
		{
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() 
		{
			if (mUserEvents == null) return 0;
			
			return mUserEvents.size();
		}

		@Override
		public Object getItem(int arg0) 
		{
			return null;
		}

		@Override
		public long getItemId(int arg0) 
		{
			return 0;
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) 
		{
			ViewHolder viewHolder;
			View row = view;
			if (row == null) 
			{
				row = mInflater.inflate(R.layout.listitem_feed, parent,	false);
				
				viewHolder = new ViewHolder();
				viewHolder.mImage = (WebImageView2) row.findViewById(R.id.image);
				viewHolder.mImage.setRoundedCorner(true);
				viewHolder.mImage.setImagesStore(mImagesStore);
				viewHolder.mImage.setCacheDir(appSettings.getCacheDirImage());
				
				viewHolder.mText = (TextView) row.findViewById(R.id.text);
				viewHolder.mText.setTypeface(Typeface.createFromAsset(UserProfileActivity.this.getAssets(),	"fonts/ProximaNova-Reg.otf"));

				viewHolder.mIcons.add((ImageView) row.findViewById(R.id.icon_like));
				viewHolder.mIcons.add((ImageView) row.findViewById(R.id.icon_dislike));
				viewHolder.mIcons.add((ImageView) row.findViewById(R.id.icon_rate_like));
				viewHolder.mIcons.add((ImageView) row.findViewById(R.id.icon_rate_dislike));
				viewHolder.mIcons.add((ImageView) row.findViewById(R.id.icon_comment));
				viewHolder.mIcons.add((ImageView) row.findViewById(R.id.icon_checkin));
				viewHolder.mIcons.add((ImageView) row.findViewById(R.id.icon_go));
				viewHolder.mIcons.add((ImageView) row.findViewById(R.id.icon_meeting));
				viewHolder.mIcons.add((ImageView) row.findViewById(R.id.icon_friend));
				viewHolder.mIcons.add((ImageView) row.findViewById(R.id.icon_rate));

				row.setTag(viewHolder);
			} 
			else 
			{
				viewHolder = (ViewHolder) row.getTag();
			}

			row.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					ViewHolder viewHolder = (ViewHolder) v.getTag();
					UserProfileActivity.this.openPage((Integer) viewHolder.mTag);
				}
			});			

			EventData currentEvent = mUserEvents.get(i);
			viewHolder.mImage.setTag(i);
			if (currentEvent.mTargetPicture != null) 
			{
				viewHolder.mImage.setImageURL(currentEvent.mTargetPicture, "123");
			}

			String strTime = "";
			if (currentEvent.mTimestamp > 7 * 24 * 60)
			{
				strTime = Integer.toString(currentEvent.mTimestamp / 7 / 24 / 60);
				strTime += " " + UserProfileActivity.this.getString(R.string.weeks_ago);
			}
			else if (currentEvent.mTimestamp > 24 * 60)
			{
				strTime = Integer.toString(currentEvent.mTimestamp / 24 / 60);
				strTime += " " + UserProfileActivity.this.getString(R.string.days_ago);
			}
			else if (currentEvent.mTimestamp > 60)
			{
				strTime = Integer.toString(currentEvent.mTimestamp / 60);
				strTime += " " + UserProfileActivity.this.getString(R.string.hours_ago);
			}
			else
			{
				strTime = Integer.toString(currentEvent.mTimestamp);
				strTime += " " + UserProfileActivity.this.getString(R.string.minutes_ago);
			}

			Spannable text = makeClickAbleText(currentEvent.mUserName, currentEvent.mEventStartText, currentEvent.mTargetName, strTime);
			viewHolder.mText.setText(text);
			viewHolder.mTag = i;

			// hide all icons
			for (int j = 0; j < viewHolder.mIcons.size(); ++j)
			{
				viewHolder.mIcons.get(j).setVisibility(View.INVISIBLE);
			}
			// show necessary icon
			switch (currentEvent.mEventType)
			{
			case FRIENDSHIP:
				viewHolder.mIcons.get(ICON_FRIEND).setVisibility(View.VISIBLE);
				break;
			case COMMENT:
				viewHolder.mIcons.get(ICON_COMMENT).setVisibility(View.VISIBLE);
				break;
			case LIKE:
				viewHolder.mIcons.get(ICON_LIKE).setVisibility(View.VISIBLE);
				break;
			case GO_TO_BAR:
			case GO_TO_MEETING:
				viewHolder.mIcons.get(ICON_GO).setVisibility(View.VISIBLE);
				break;
			case CHECKIN:
				viewHolder.mIcons.get(ICON_CHECKIN).setVisibility(View.VISIBLE);
				break;
			case RATE:
				{
					switch (currentEvent.mRate)
					{
					case EventData.RATE_STAR:
						viewHolder.mIcons.get(ICON_RATE).setVisibility(View.VISIBLE);
						break;
					case EventData.RATE_LIKE:
						viewHolder.mIcons.get(ICON_RATE_LIKE).setVisibility(View.VISIBLE);
						break;
					case EventData.RATE_DISLIKE:
						viewHolder.mIcons.get(ICON_RATE_DISLIKE).setVisibility(View.VISIBLE);
						break;
					}
				}
				break;
			case CREATE_MEETING:
				viewHolder.mIcons.get(ICON_MEETING).setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			
			synchronized (mActiveRequest) 
			{
				if (i == mUserEvents.size() - 1 && i < mAllEventsCount - 1) 
				{
					mOffset = mUserEvents.size();
					mActiveRequest = UserProfileRequest.EVENTS;
					new LoadUserTask().execute();
				}
			}

			return row;
		}

		class ViewHolder 
		{
			WebImageView2 mImage;
			TextView mText;
			ArrayList<ImageView> mIcons = new ArrayList<ImageView>();
			int	mTag = -1;
		}
	}

	private Spannable makeClickAbleText(String user, String txt, String target, String time) 
	{
		String finalText = user + " " + txt + " " + target + " " + time;
		Spannable span = Spannable.Factory.getInstance().newSpannable(finalText);

		int _target = user.length() + 1 + txt.length() + 1;
		int _time = _target + target.length() + 1;
		
		span.setSpan(new ForegroundColorSpan(0xFFCCCCCC), 0, user.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		span.setSpan(new ForegroundColorSpan(0xFF00E9FF), _target, _target + target.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), _target, _target + target.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		span.setSpan(new ForegroundColorSpan(0xFFCCCCCC), _time, _time + time.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		
		return span;
	}

	private void openPage(int index) 
	{
		EventData event = mUserEvents.get(index);

		switch (event.mEventType)
		{
		case FRIENDSHIP:
			startActivity(new Intent(UserProfileActivity.this, UserProfileActivity.class).putExtra(Constants.KEY_USER_ID, event.mTargetId));
			break;
		case COMMENT:
			switch (event.mTargetType) 
			{
			case BARS:
				startActivity(new Intent(UserProfileActivity.this, BarActivity.class).putExtra("ID", event.mTargetId));
				break;
			case BLOGPOSTS:
				startActivity(new Intent(UserProfileActivity.this, ReviewActivity.class).putExtra("ID", event.mTargetId));
				break;
			default:
				break;
			}
			break;
		case LIKE:
			switch (event.mTargetType) 
			{
			case BARS:
				startActivity(new Intent(UserProfileActivity.this, BarActivity.class).putExtra("ID", event.mTargetId));
				break;
			case COCKTAILS:
				Intent intent = new Intent();
				intent.putExtra("ID", Integer.parseInt(event.mTargetId));
				intent.putExtra("d_id", "92");
				intent.setClass(UserProfileActivity.this, CocktailActivity.class);
				
				startActivity(intent);
				break;
			case DRINKS:
				startActivity(new Intent(UserProfileActivity.this, DrinkActivity.class).putExtra("ID", event.mTargetId));
				break;
			case BRANDS:
				startActivity(new Intent(UserProfileActivity.this, BrandActivity.class).putExtra("ID", Integer.parseInt(event.mTargetId)));
				break;
			case BLOGPOSTS:
				startActivity(new Intent(UserProfileActivity.this, ReviewActivity.class).putExtra("ID", event.mTargetId));
				break;
			default:
				break;
			}
			break;
		case GO_TO_BAR:
		case CHECKIN:
		case RATE:
		case GO_TO_MEETING:
		case CREATE_MEETING:
			startActivity(new Intent(UserProfileActivity.this, BarActivity.class).putExtra("ID", event.mTargetId));
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{	
		if (resultCode == RESULT_OK) 
		{
			if (requestCode == EDIT_SETTINGS) 
			{
				if (data.getBooleanExtra("need_update", false))
				{
					File file = mImagesStore.getFileByURL(mProfileImage.getURL());
					file.delete();
					
					mImagesStore.removeImage(mProfileImage.getURL());
					
					mActiveRequest = UserProfileRequest.USER_ONLY;
					new LoadUserTask().execute();
				}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
