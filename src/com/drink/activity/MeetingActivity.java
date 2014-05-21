
package com.drink.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import com.drink.R;
import com.drink.constants.Constants;
import com.drink.imageloader.WebImageView2;
import com.drink.query.AddInvitedQuery;
import com.drink.query.CancelMeetingQuery;
import com.drink.query.CommentsQuery;
import com.drink.query.MeetingQuery;
import com.drink.query.UpdateGoQuery;
import com.drink.query.WebQuery;
import com.drink.settings.AppSettings;
import com.drink.types.Comment;
import com.drink.types.Friend;
import com.drink.types.Invited;
import com.drink.types.Meeting;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class MeetingActivity extends BasicActivity 
{
	static private final int POST_COMMENT = 0;
	static private final int ADD_FRIENDS = 1;
	
	static public final int NEED_REMOVE = 10;
	static public final int NEED_UPDATE = 11;
	
	private ListView mCommentsList;

	WebImageView2 mAvatar;
	WebImageView2 mBarPic;

	WebImageView2 mInvited1;
	WebImageView2 mInvited2;
	WebImageView2 mInvited3;
	WebImageView2 mInvited4;

	WebImageView2 mWillGo1;
	WebImageView2 mWillGo2;
	WebImageView2 mWillGo3;
	WebImageView2 mWillGo4;

	TextView barName;
	TextView mInvite;
	TextView mTime;
	TextView mAddress;
	TextView mDescription;
	TextView mInvitedCount;
	TextView mWillGoCount;
	
	Button btnGo;
	Button btnDontGo;
	Button btnDontKnow;
	Button btnRemove;
	Button btnInviteFriends;
	
	LinearLayout header;
	LinearLayout llInvited;
	RelativeLayout rlInvitedCount;
	LinearLayout llWillGo;
	RelativeLayout rlWillGoCount;

	private Meeting mMeeting;
	private String mMeetingId;
	private CommentsAdapter mAdapter;
	
	boolean loading = false;
	boolean empty = false;
	
	boolean needUpdate = false;

	private OnClickListener onClickAvatar = new OnClickListener() 
	{
		@Override
		public void onClick(View v) 
		{
			Intent intent = new Intent();
			intent.putExtra(Constants.KEY_USER_ID, (String) v.getTag());
			intent.setClass(MeetingActivity.this, UserProfileActivity.class);

			startActivity(intent);
		}
	};
	
	public MeetingActivity() 
	{
		super(R.layout.activity_meeting);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mCaption.setTextColor(Color.WHITE);
		mCaption.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		if (getIntent().hasExtra("meeting"))
		{
			mMeeting = getIntent().getParcelableExtra("meeting");
			mMeetingId = mMeeting.id;
		}
		else
		{
			mMeetingId = getIntent().getStringExtra("meeting_id");
		}

		mCommentsList = (ListView) findViewById(R.id.lv_comments);
		header = (LinearLayout) getLayoutInflater().inflate(R.layout.header_meeting, (ViewGroup) findViewById(R.id.ll_content));
		header.setVisibility(View.GONE);
		mCommentsList.addHeaderView(header, null, false);

		mAvatar = (WebImageView2) findViewById(R.id.wiv_avatar);
		mAvatar.setCircle(true);
		mAvatar.setImagesStore(mImagesStore);
		mAvatar.setCacheDir(appSettings.getCacheDirImage());
		mAvatar.setOnClickListener(onClickAvatar);
		
		mInvited1 = (WebImageView2) findViewById(R.id.invited_1);
		mInvited1.setCircle(true);
		mInvited1.setImagesStore(mImagesStore);
		mInvited1.setCacheDir(appSettings.getCacheDirImage());
		mInvited1.setOnClickListener(onClickAvatar);

		mInvited2 = (WebImageView2) findViewById(R.id.invited_2);
		mInvited2.setCircle(true);
		mInvited2.setImagesStore(mImagesStore);
		mInvited2.setCacheDir(appSettings.getCacheDirImage());
		mInvited2.setOnClickListener(onClickAvatar);

		mInvited3 = (WebImageView2) findViewById(R.id.invited_3);
		mInvited3.setCircle(true);
		mInvited3.setImagesStore(mImagesStore);
		mInvited3.setCacheDir(appSettings.getCacheDirImage());
		mInvited3.setOnClickListener(onClickAvatar);

		mInvited4 = (WebImageView2) findViewById(R.id.invited_4);
		mInvited4.setCircle(true);
		mInvited4.setImagesStore(mImagesStore);
		mInvited4.setCacheDir(appSettings.getCacheDirImage());
		mInvited4.setOnClickListener(onClickAvatar);

		mWillGo1 = (WebImageView2) findViewById(R.id.will_go_1);
		mWillGo1.setCircle(true);
		mWillGo1.setImagesStore(mImagesStore);
		mWillGo1.setCacheDir(appSettings.getCacheDirImage());
		mWillGo1.setOnClickListener(onClickAvatar);

		mWillGo2 = (WebImageView2) findViewById(R.id.will_go_2);
		mWillGo2.setCircle(true);
		mWillGo2.setImagesStore(mImagesStore);
		mWillGo2.setCacheDir(appSettings.getCacheDirImage());
		mWillGo2.setOnClickListener(onClickAvatar);

		mWillGo3 = (WebImageView2) findViewById(R.id.will_go_3);
		mWillGo3.setCircle(true);
		mWillGo3.setImagesStore(mImagesStore);
		mWillGo3.setCacheDir(appSettings.getCacheDirImage());
		mWillGo3.setOnClickListener(onClickAvatar);

		mWillGo4 = (WebImageView2) findViewById(R.id.will_go_4);
		mWillGo4.setCircle(true);
		mWillGo4.setImagesStore(mImagesStore);
		mWillGo4.setCacheDir(appSettings.getCacheDirImage());
		mWillGo4.setOnClickListener(onClickAvatar);

		mInvite = (TextView) findViewById(R.id.tv_invite);
		mInvite.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mTime = (TextView) findViewById(R.id.tv_time);
		mTime.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		mAddress = (TextView) findViewById(R.id.tv_address);
		mAddress.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		RelativeLayout rlPicture = (RelativeLayout) findViewById(R.id.include_picture);
		
		mBarPic = (WebImageView2) rlPicture.findViewById(R.id.picture);
		mBarPic.setHeight(160);
		mBarPic.setImagesStore(mImagesStore);
		mBarPic.setCacheDir(appSettings.getCacheDirImage());
		
		barName = (TextView) rlPicture.findViewById(R.id.title);
		barName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		btnGo = (Button) findViewById(R.id.btn_will_go);
		btnGo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnGo.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					new GoTask("yes").execute();
				}
			});
		btnDontGo = (Button) findViewById(R.id.btn_dont_go);
		btnDontGo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnDontGo.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					new GoTask("not").execute();
				}
			});
		btnDontKnow = (Button) findViewById(R.id.btn_dont_know);
		btnDontKnow.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnDontKnow.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					new GoTask("maybe").execute();
				}
			});
		btnRemove = (Button) findViewById(R.id.btn_remove);
		btnRemove.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		llInvited = (LinearLayout) findViewById(R.id.ll_invited);
		llInvited.setVisibility(View.GONE);
		
		rlInvitedCount = (RelativeLayout) findViewById(R.id.rl_invited_count);
		rlInvitedCount.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(MeetingActivity.this, InvitedActivity.class);
				intent.putExtra("meeting_id", mMeetingId);
				
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
		
		llWillGo = (LinearLayout) findViewById(R.id.ll_will_go);
		llWillGo.setVisibility(View.GONE);
		
		rlWillGoCount = (RelativeLayout) findViewById(R.id.rl_will_go_count);
		rlWillGoCount.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(MeetingActivity.this, InvitedActivity.class);
				intent.putExtra("meeting_id", mMeetingId);
				
				startActivity(intent);
			}
		});
		rlWillGoCount.setVisibility(View.INVISIBLE);
		
		TextView tvWillGo = (TextView) findViewById(R.id.tv_will_go);
		tvWillGo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));

		TextView tvWillGoAll = (TextView) rlWillGoCount.findViewById(R.id.tv_will_go_all);
		tvWillGoAll.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));

		mWillGoCount = (TextView) rlWillGoCount.findViewById(R.id.tv_will_go_count);
		mWillGoCount.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));

		Button btnAddComment = (Button) findViewById(R.id.btn_add_comment);
		btnAddComment.setTextSize(20.0f);
		btnAddComment.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnAddComment.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent(MeetingActivity.this, AddTextCommentActivity.class);
					intent.putExtra("ID", mMeetingId);
					intent.putExtra("type", CommentsQuery.MEETING);
					
					startActivityForResult(intent, POST_COMMENT);
				}
			});
		
		LinearLayout llDescription = (LinearLayout) findViewById(R.id.include_meeting_description);
		
		TextView tvTitle = (TextView) llDescription.findViewById(R.id.tv_title);
		tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		tvTitle.setText(R.string.meeting_description);
		
		TextView tvShowOnMap = (TextView) findViewById(R.id.tv_show_on_map);
		tvShowOnMap.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tvShowOnMap.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(MeetingActivity.this, BarOnMapActivity.class);
				intent.putExtra("name", mMeeting.bar_name);
				intent.putExtra("lat", mMeeting.lat);
				intent.putExtra("lng", mMeeting.lng);

				startActivity(intent);
			}
		});
		
		mDescription = (TextView) llDescription.findViewById(R.id.tv_text);
		mDescription.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		LinearLayout llReadMore = (LinearLayout) llDescription.findViewById(R.id.ll_read_more);
		llReadMore.setVisibility(View.GONE);
		
		TextView tvComments = (TextView) findViewById(R.id.tv_comments);
		tvComments.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));

		btnInviteFriends = (Button) findViewById(R.id.btn_call_friends);
		btnInviteFriends.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnInviteFriends.setTextSize(20.0f);
		btnInviteFriends.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(MeetingActivity.this, InviteFriendsActivity.class);
				intent.putExtra("mode", InviteFriendsActivity.MODE_AddFriends);
				intent.putExtra("invited", mMeeting.invited);
				startActivityForResult(intent, ADD_FRIENDS);
			}
		});

		mAdapter = new CommentsAdapter(getApplicationContext());
		mCommentsList.setAdapter(mAdapter);
		mCommentsList.setOnScrollListener(new OnScrollListener() 
		{
			private boolean needLoading = false;
		
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
			{
				if ((totalItemCount > 0) && (!loading))
				{
					needLoading = (firstVisibleItem + visibleItemCount + 3 >= totalItemCount);
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) 
			{
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) 
				{
					if (!empty && needLoading && !loading)
					{
						loading = true;
						needLoading = false;
						
						new LoadCommentsTask().execute();
					}
				}
			}
		});
		
		if (mMeeting == null)
		{
			new LoadMeetingTask().execute();
		}
		else
		{
			fillHeaderData();
		}
		
		new LoadCommentsTask().execute();
	}

	private void fillHeaderData() 
	{
		mCaption.setText(mMeeting.title);
		
		Date date = new Date(Long.valueOf(mMeeting.date) * 1000);
		String _time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
		String _date = android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(date);

		mAvatar.setImageURL(mMeeting.user_picture, "123");
		mAvatar.setTag(mMeeting.user_id);
		mInvite.setText(mMeeting.user_name + " " + getString(R.string.invite));
		mTime.setText(_date + " " + _time);
		barName.setText(mMeeting.bar_name);
		mAddress.setText(mMeeting.address);
		
		ArrayList<Invited> will_go = new ArrayList<Invited>();
		for (int i = 0; i < mMeeting.invited_count; ++i)
		{
			if (mMeeting.invited.get(i).state == Meeting.GO)
			{
				will_go.add(mMeeting.invited.get(i));
			}
		}
		
		if (mMeeting.invited_count > 0)
		{
			llInvited.setVisibility(View.VISIBLE);
		
			mInvited1.setImageURL(mMeeting.invited.get(0).avatar, "123");
			mInvited1.setTag(mMeeting.invited.get(0).id);
			
			if (mMeeting.invited_count > 1)
			{
				mInvited2.setImageURL(mMeeting.invited.get(1).avatar, "123");
				mInvited2.setTag(mMeeting.invited.get(1).id);
				
				if (mMeeting.invited_count > 2)
				{
					mInvited3.setImageURL(mMeeting.invited.get(2).avatar, "123");
					mInvited3.setTag(mMeeting.invited.get(2).id);
					
					if (mMeeting.invited_count > 3)
					{
						mInvited4.setImageURL(mMeeting.invited.get(3).avatar, "123");
						mInvited4.setTag(mMeeting.invited.get(3).id);
						
						if (mMeeting.invited_count > 4)
						{
							mInvitedCount.setText(String.valueOf(mMeeting.invited_count));
							rlInvitedCount.setVisibility(View.VISIBLE);
						}
					}
				}
			}
		}

		if (will_go.size() > 0)
		{
			llWillGo.setVisibility(View.VISIBLE);
		
			mWillGo1.setImageURL(will_go.get(0).avatar, "123");
			mWillGo1.setTag(will_go.get(0).id);
			
			if (will_go.size() > 1)
			{
				mWillGo2.setImageURL(will_go.get(1).avatar, "123");
				mWillGo2.setTag(will_go.get(1).id);
				
				if (will_go.size() > 2)
				{
					mWillGo3.setImageURL(will_go.get(2).avatar, "123");
					mWillGo3.setTag(will_go.get(2).id);
					
					if (will_go.size() > 3)
					{
						mWillGo4.setImageURL(will_go.get(3).avatar, "123");
						mWillGo4.setTag(will_go.get(3).id);
						
						if (will_go.size() > 4)
						{
							mWillGoCount.setText(String.valueOf(will_go.size()));
							rlWillGoCount.setVisibility(View.VISIBLE);
						}
					}
				}
			}
		}

		mDescription.setText(mMeeting.description);

		if (mMeeting.bar_picture != null)
		{
			mBarPic.setImageURL(mMeeting.bar_picture, "123");
		}
		
		if (mMeeting.owner)
		{
			btnRemove.setText(R.string._cancel);
			btnRemove.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MeetingActivity.this);
					alertDialogBuilder.setTitle("");
					alertDialogBuilder.setMessage(R.string.ask_cancel_meeting);
					alertDialogBuilder.setCancelable(false);
					alertDialogBuilder.setPositiveButton(getString(R.string.profile_text_tb_on), new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int id) 
							{
								new CancelMeetingTask().execute();
							}
						});
					alertDialogBuilder.setNegativeButton(getString(R.string.profile_text_tb_off), new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog, int which) 
							{
								dialog.cancel();
							}
						});

					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}
			});
			
			btnGo.setEnabled(false);
			btnDontGo.setEnabled(false);
			btnDontKnow.setEnabled(false);
		}
		else
		{
			btnRemove.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
				}
			});

			switch (mMeeting.state)
			{
			case Meeting.GO:
				btnGo.setSelected(true);
				break;
			case Meeting.DONT_GO:
				btnDontGo.setSelected(true);
				break;
			case Meeting.DONT_KNOW:
				btnDontKnow.setSelected(true);
				break;
			default:
				break;
			}
		}
	}

	private class CommentsAdapter extends BaseAdapter 
	{
		private LayoutInflater layoutInflater;
		private ArrayList<Comment> mComments;

		public CommentsAdapter(Context context) 
		{
			layoutInflater = LayoutInflater.from(context);
		}

		public void addData(ArrayList<Comment> comments)
		{
			if (mComments == null)
			{
				mComments = comments;
			}
			else
			{
				mComments.addAll(comments);
			}
			
			notifyDataSetChanged();
		}
		
		public void resetData()
		{
			mComments = null;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() 
		{
			if (mComments == null) return 0;
			
			return mComments.size();
		}

		@Override
		public Object getItem(int arg0) 
		{
			return arg0;
		}

		@Override
		public long getItemId(int arg0) 
		{
			return arg0;
		}

		@Override
		public View getView(int i, View view, ViewGroup parent) 
		{
			ViewHolder viewHolder;
			View row = view;
			if (row == null) 
			{
				row = this.layoutInflater.inflate(R.layout.listitem_comment, parent, false);

				viewHolder = new ViewHolder();
				viewHolder.logo = (WebImageView2) row.findViewById(R.id.iv_avatar);
				viewHolder.logo.setCircle(true);
				viewHolder.logo.setImagesStore(mImagesStore);
				viewHolder.logo.setCacheDir(appSettings.getCacheDirImage());
				viewHolder.logo.setOnClickListener(onClickAvatar); 
				
				viewHolder.name = (TextView) row.findViewById(R.id.tv_user_name);
				viewHolder.name.setTypeface(Typeface.createFromAsset(MeetingActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				
				viewHolder.time = (TextView) row.findViewById(R.id.tv_time);
				viewHolder.time.setTypeface(Typeface.createFromAsset(MeetingActivity.this.getAssets(), "fonts/HelveticaNeue.otf"));
				
				viewHolder.text = (TextView) row.findViewById(R.id.tv_comment_text);
				viewHolder.text.setTypeface(Typeface.createFromAsset(MeetingActivity.this.getAssets(), "fonts/HelveticaNeue.otf"));

				row.setTag(viewHolder);
				
				row.findViewById(R.id.iv_like).setVisibility(View.GONE);
				row.findViewById(R.id.tv_translate).setVisibility(View.GONE);
			} 
			else 
			{
				viewHolder = (ViewHolder) row.getTag();
			}

			Comment comment = mComments.get(i);

			viewHolder.name.setText(comment.user_name);
			viewHolder.time.setText(comment.created_at);
			viewHolder.text.setText(comment.text);

			if (!comment.user_picture.equals("")) 
			{
				viewHolder.logo.setImageURL(comment.user_picture, "123");
				viewHolder.logo.setTag(comment.user_id);
			}

			return row;
		}

		private class ViewHolder 
		{
			public WebImageView2 logo;
			public TextView name;
			public TextView time;
			public TextView text;
		}
	}

	class LoadMeetingTask extends QueryTask<Void, Void, MeetingQuery> 
	{
		@Override
		protected MeetingQuery doInBackground(Void... params) 
		{
			MeetingQuery meetingQuery = new MeetingQuery(appSettings, mMeetingId);
			meetingQuery.getResponse(WebQuery.GET);
			
			return meetingQuery;
		}

		@Override
		protected void onPostExecute(MeetingQuery query) 
		{
			if (checkResult(query))
			{
				mMeeting = query.getData().meeting;
				fillHeaderData();
			}
			
			header.setVisibility(View.VISIBLE);
			
			super.onPostExecute(query);
		}
	}

	class LoadCommentsTask extends QueryTask<Void, Void, CommentsQuery> 
	{
		static private final int LIMIT = 25;
		
		@Override
		protected CommentsQuery doInBackground(Void... params) 
		{
			CommentsQuery query = new CommentsQuery(appSettings, mMeetingId, CommentsQuery.MEETING, mAdapter.getCount(), LIMIT);
			query.getResponse(WebQuery.GET);
			
			return query;
		}
		
		@Override
		protected void onPostExecute(CommentsQuery query) 
		{
			if (checkResult(query))
			{
				CommentsQuery.Data data = query.getData();
				
				mAdapter.addData(data.comments);
				
				empty = (data.comments.size() < LIMIT);
			}
			
			loading = false;
		    
			super.onPostExecute(query);
		}
	}

	private class GoTask extends QueryTask<Void, Void, UpdateGoQuery> 
	{
		private String go;

		public GoTask(String go) 
		{
			super();
			
			this.go = go;
		}

		@Override
		protected UpdateGoQuery doInBackground(Void... params) 
		{
			UpdateGoQuery query = new UpdateGoQuery(appSettings, mMeetingId, go);
			query.getResponse(WebQuery.GET);

			return query;
		}
		
		@Override
		protected void onPostExecute(UpdateGoQuery query) 
		{
			if (checkResult(query))
			{
				UpdateGoQuery.Data data = query.getData();
				
				if (data.success)
				{
					mMeeting.will_go_count = data.willGo;
					mWillGoCount.setText(Integer.toString(data.willGo));
					
					if (go.equals("yes"))
					{
						btnGo.setSelected(true);
						
						mMeeting.state = Meeting.GO;
					}
					else
					{
						btnGo.setSelected(false);
					}

					if (go.equals("not"))
					{
						btnDontGo.setSelected(true);
						
						mMeeting.state = Meeting.DONT_GO;
					}
					else
					{
						btnDontGo.setSelected(false);
					}
					
					if (go.equals("maybe"))
					{
						btnDontKnow.setSelected(true);
						
						mMeeting.state = Meeting.DONT_KNOW;
					}
					else
					{
						btnDontKnow.setSelected(false);
					}
					
					needUpdate = true;
				}
			}
			
			super.onPostExecute(query);
		}
	}
	
	private class CancelMeetingTask extends QueryTask<Void, Void, CancelMeetingQuery> 
	{
		@Override
		protected CancelMeetingQuery doInBackground(Void... params) 
		{
			CancelMeetingQuery query = new CancelMeetingQuery(appSettings, mMeetingId);
			query.getResponse(WebQuery.GET);

			return query;
		}
		
		@Override
		protected void onPostExecute(CancelMeetingQuery query) 
		{
			if (checkResult(query))
			{
				CancelMeetingQuery.Data data = query.getData();
				
				if (data.success)
				{
					AppSettings appSettings = new AppSettings(getApplicationContext());
					appSettings.setEventsMeetingsCount(appSettings.getEventsMeetingsCount() - 1);

					setResult(NEED_REMOVE, new Intent().putExtra("meeting_id", mMeetingId));
					finish();
				}
			}
			
			super.onPostExecute(query);
		}
	}

	private class AddInvitedTask extends QueryTask<ArrayList<Friend>, Void, AddInvitedQuery> 
	{
		@Override
		protected AddInvitedQuery doInBackground(ArrayList<Friend>... params) 
		{
			AddInvitedQuery query = new AddInvitedQuery(appSettings, mMeetingId, params[0]);
			query.getResponse(WebQuery.GET);

			return query;
		}
		
		@Override
		protected void onPostExecute(AddInvitedQuery query) 
		{
			if (checkResult(query))
			{
			}
			
			super.onPostExecute(query);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) 
		{
			if (requestCode == POST_COMMENT)
			{
				mAdapter.resetData();
				empty = false;
				
				new LoadCommentsTask().execute();
			}
			else if (requestCode == ADD_FRIENDS)
			{
				ArrayList<Friend> friends = data.getParcelableArrayListExtra("selected");
			
				new AddInvitedTask().execute(friends);
			}
		}
	}

	@Override
	public void onBackPressed()  
	{
		if (needUpdate)
		{
			Intent intent = new Intent();
			intent.putExtra("meeting_id", mMeetingId);
			intent.putExtra("state", mMeeting.state);
			intent.putExtra("will_go", mMeeting.will_go_count);
			
			setResult(NEED_UPDATE, intent);
			finish();
		}
		else
		{
			super.onBackPressed();
		}
	}
}
