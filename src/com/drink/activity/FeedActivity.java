
package com.drink.activity;

import java.util.ArrayList;
import com.drink.R;
import com.drink.constants.Constants;
import com.drink.constants.Constants.EventsHolder;
import com.drink.imageloader.WebImageView2;
import com.drink.query.EventsQuery;
import com.drink.query.WebQuery;
import com.drink.query.EventsQuery.EventData;
import com.drink.query.EventsQuery.EventQueryData;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FeedActivity extends BasicActivity 
{
	final static int EVENT_LIMIT = 20;
	
	ListView mList;
	TextView tvNoActivities;
	private int mOffset = 0;
	ArrayList<EventsQuery.EventData> mUserEvents;
	private int mAllEventsCount = 0;
	private UserActionsAdapter mAdapter;
	OnClickListener onAvatarClickListener;

	public FeedActivity() 
	{
		super(R.layout.activity_feed);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		setContentView(R.layout.activity_feed);

		super.onCreate(savedInstanceState);
		
		mCaption.setText(getString(R.string.activity));
		
		tvNoActivities = (TextView) findViewById(R.id.no_activities);
		tvNoActivities.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		tvNoActivities.setVisibility(View.GONE);
		
		mList = (ListView) findViewById(R.id.list);
		mList.setVisibility(View.GONE);
		
		mAdapter = new UserActionsAdapter();
		mList.setAdapter(mAdapter);

		onAvatarClickListener = new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					EventData clickedData = mUserEvents.get((Integer) v.getTag());
					
					Intent i = new Intent();
					i.putExtra(Constants.KEY_USER_ID, clickedData.mUserId);
					i.setClass(FeedActivity.this, UserProfileActivity.class);
	
					startActivity(i);
				}
			};

		new LoadEventTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_feed, menu);
		
		return true;
	}

	private class LoadEventTask extends QueryTask<Void, Void, WebQuery> 
	{
		@Override
		protected EventsQuery doInBackground(Void... params) 
		{
			EventsQuery eventsQuery = new EventsQuery(getApplicationContext(), EventsHolder.MAIN);
			eventsQuery.setOffset(mOffset);
			eventsQuery.setLimit(EVENT_LIMIT);
			eventsQuery.setDeviceToken(appSettings.getContentToken());
			eventsQuery.setUserToken(appSettings.getUserToken());
			eventsQuery.setUserId(appSettings.getUserId());
			eventsQuery.getResponse(WebQuery.GET);
			
			return eventsQuery;
		}

		@Override
		protected void onPostExecute(WebQuery query) 
		{
			if (checkResult(query))
			{
				EventQueryData data = (EventQueryData) query.getData();
				mAllEventsCount = data.mAllEventsCount;
				if (mUserEvents != null) 
				{
					mUserEvents.addAll(data.mEventList);
				} 
				else 
				{
					mUserEvents = data.mEventList;
				}
				drawEvents();
				
				if (mUserEvents.size() > 0)
				{
					mList.setVisibility(View.VISIBLE);
				}
				else
				{
					tvNoActivities.setVisibility(View.VISIBLE);
				}
			}
			
			super.onPostExecute(query);
		}
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
				row = mInflater.inflate(R.layout.listitem_feed, parent, false);
				
				viewHolder = new ViewHolder();
				viewHolder.mImage = (WebImageView2) row.findViewById(R.id.image);
				viewHolder.mImage.setCircle(true);
				viewHolder.mImage.setImagesStore(mImagesStore);
				viewHolder.mImage.setCacheDir(appSettings.getCacheDirImage());
				viewHolder.mImage.setOnClickListener(onAvatarClickListener);
				
				viewHolder.mText = (TextView) row.findViewById(R.id.text);
				viewHolder.mText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
				
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
					FeedActivity.this.openPage((Integer) viewHolder.mTag);
				}
			});			
			
			EventData currentEvent = mUserEvents.get(i);
			viewHolder.mImage.setTag(i);
			if (currentEvent.mUserAvatar != null) 
			{
				viewHolder.mImage.setImageURL(currentEvent.mUserAvatar, "123");
			}

			String strTime = "";
			if (currentEvent.mTimestamp > 7 * 24 * 60)
			{
				strTime = Integer.toString(currentEvent.mTimestamp / 7 / 24 / 60);
				strTime += " " + FeedActivity.this.getString(R.string.weeks_ago);
			}
			else if (currentEvent.mTimestamp > 24 * 60)
			{
				strTime = Integer.toString(currentEvent.mTimestamp / 24 / 60);
				strTime += " " + FeedActivity.this.getString(R.string.days_ago);
			}
			else if (currentEvent.mTimestamp > 60)
			{
				strTime = Integer.toString(currentEvent.mTimestamp / 60);
				strTime += " " + FeedActivity.this.getString(R.string.hours_ago);
			}
			else
			{
				strTime = Integer.toString(currentEvent.mTimestamp);
				strTime += " " + FeedActivity.this.getString(R.string.minutes_ago);
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

			if ((i == mUserEvents.size() - 1) && (i < mAllEventsCount - 1)) 
			{
				mOffset = mUserEvents.size();
				
				new LoadEventTask().execute();
			}
			
			return row;
		}

		class ViewHolder 
		{
			WebImageView2 mImage;
			TextView mText;
			ArrayList<ImageView> mIcons = new ArrayList<ImageView>();
			int mTag = -1;
		}
	}

	private void drawEvents() 
	{
		mAdapter.notifyDataSetChanged();
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
			startActivity(new Intent(FeedActivity.this, UserProfileActivity.class).putExtra(Constants.KEY_USER_ID, event.mTargetId));
			break;
		case COMMENT:
			switch (event.mTargetType) 
			{
			case BARS:
				startActivity(new Intent(FeedActivity.this, BarActivity.class).putExtra("ID", event.mTargetId));
				break;
			case BLOGPOSTS:
				startActivity(new Intent(FeedActivity.this, ReviewActivity.class).putExtra("ID", event.mTargetId));
				break;
			default:
				break;
			}
			break;
		case LIKE:
			switch (event.mTargetType) 
			{
			case BARS:
				startActivity(new Intent(FeedActivity.this, BarActivity.class).putExtra("ID", event.mTargetId));
				break;
			case COCKTAILS:
				Intent intent = new Intent();
				intent.putExtra("ID", Integer.parseInt(event.mTargetId));
				intent.putExtra("d_id", "92");
				intent.setClass(FeedActivity.this, CocktailActivity.class);
				
				startActivity(intent);
				break;
			case DRINKS:
				startActivity(new Intent(FeedActivity.this, DrinkActivity.class).putExtra("ID", event.mTargetId));
				break;
			case BRANDS:
				startActivity(new Intent(FeedActivity.this, BrandActivity.class).putExtra("ID", Integer.parseInt(event.mTargetId)));
				break;
			case BLOGPOSTS:
				startActivity(new Intent(FeedActivity.this, ReviewActivity.class).putExtra("ID", event.mTargetId));
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
			startActivity(new Intent(FeedActivity.this, BarActivity.class).putExtra("ID", event.mTargetId));
			break;
		default:
			break;
		}
	}
}
