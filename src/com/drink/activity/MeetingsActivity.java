
package com.drink.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.imageloader.WebImageView2;
import com.drink.query.CancelMeetingQuery;
import com.drink.query.RemoveMeetingQuery;
import com.drink.query.UpdateGoQuery;
import com.drink.query.MeetingsQuery;
import com.drink.query.WebQuery;
import com.drink.settings.AppSettings;
import com.drink.swipelistview.BaseSwipeListViewListener;
import com.drink.swipelistview.SwipeListView;
import com.drink.types.Meeting;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MeetingsActivity extends BasicActivity 
{
	static private final int CREATE_MEETING = 1;
	static private final int SHOW_MEETING = 2;
	
	TextView tvNoMeetings;

//	ListView 	listView;
	SwipeListView listView;
	MeetingListAdapter 	adapter;

	OnClickListener mListItemClickListener;

	int pastIndex = -1;
	
	boolean empty = false;
	boolean loading = false;
	
	public MeetingsActivity()
	{
		super(R.layout.activity_meetings, false);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mCaption.setText(R.string.meetings);
		
		// add create meeting button
		ImageButton btnCreateMeeting = new ImageButton(this);
		btnCreateMeeting.setBackgroundResource(R.drawable.button_create_meeting);
		btnCreateMeeting.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				startActivityForResult(new Intent(MeetingsActivity.this, CreateMeetingInfoActivity.class), CREATE_MEETING);
			}
		});
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btnCreateMeeting.setLayoutParams(params);
		
		RelativeLayout rlNavigationTop = (RelativeLayout) findViewById(R.id.rl_navigation_up);
		rlNavigationTop.addView(btnCreateMeeting);
		
		tvNoMeetings = (TextView) findViewById(R.id.no_meetings);
		tvNoMeetings.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		tvNoMeetings.setVisibility(View.GONE);
		
		listView = (SwipeListView) findViewById(R.id.listView);
		
		adapter = new MeetingListAdapter(getApplicationContext());
		
		listView.setAdapter(adapter);

		
		listView.setSwipeListViewListener(new BaseSwipeListViewListener(){
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
/*                View v = (View) listView.getChildAt(position - listView.getFirstVisiblePosition());
				LinearLayout llBack = (LinearLayout) v.findViewById(R.id.ll_buttons);
                llBack.setVisibility(View.INVISIBLE); */
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            	//listView.closeOpenedItems();
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
/*            	if(listView.getAdapter().getItemViewType(position)<0) return;
            	listView.closeOpenedItems();
                View v = (View) listView.getChildAt(position - listView.getFirstVisiblePosition());
				LinearLayout llBack = (LinearLayout) v.findViewById(R.id.ll_buttons);
                llBack.setVisibility(View.VISIBLE); */
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
/*            	final Meeting meeting = (Meeting) listView.getAdapter().getItem(position);
				Intent intent = new Intent();
				intent.putExtra("meeting_id", meeting.id);
				intent.setClass(MeetingsActivity.this, MeetingActivity.class);
				startActivityForResult(intent, SHOW_MEETING); */
            }

            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
            }
		});

		listView.setOnScrollListener(new OnScrollListener() 
		{
			private boolean needLoading = false;
		
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) 
			{
				listView.closeOpenedItems();
				if ((totalItemCount > 0) && (!loading))
				{
					needLoading = (firstVisibleItem + visibleItemCount >= totalItemCount);
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
						
						new LoadTask().execute();
					}
				}
			}
		});
/*
		listView.setOnTouchListener(new View.OnTouchListener() 
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				if (event.getAction() == MotionEvent.ACTION_MOVE)
				{
					adapter.processMoveEvent(event);
				}
				
				return false;
			}
		});
*/
		new LoadTask().execute();
	}

	private class MeetingListAdapter extends BaseAdapter 
	{
		private LayoutInflater layoutInflater;
		MeetingsQuery.Data data;
		
		private View swipingView;
		private View showingView;
		private View shownView;
		private View hidingView;
		
		private float swipeStartX = 0.0f;
		private float swipeStartY = 0.0f;
		private MeetingListAdapter ad;

		public MeetingListAdapter(Context context) 
		{
			this.layoutInflater = LayoutInflater.from(context);
			ad = this;
		}

		public void clear()
		{
			data = null;
			notifyDataSetChanged();
		}
		
		public void addData(MeetingsQuery.Data data) 
		{
			if (this.data == null)
			{
				this.data = data;
			}
			else
			{
				this.data.meetings.addAll(data.meetings);
			}
			
			notifyDataSetChanged();
		}

		@Override
		public int getCount() 
		{
			if (this.data == null) return 0;
			
			if (pastIndex == -1)
			{
				return this.data.meetings.size();
			}
			else
			{
				return (this.data.meetings.size() + 1);
			}
		}

		public void showView()
		{
			if (showingView != null) return;
			if (swipingView == null) return;
			
			if (shownView != null)
			{
				hideView();
			}
			

//			swipingView.llButtons.setVisibility(View.VISIBLE);
		
			float offset = swipingView.getTranslationX();
			swipingView.setTranslationX(0.0f);
			
	        TranslateAnimation tAnim = new TranslateAnimation(offset, -250.0f * getResources().getDisplayMetrics().density, 0.0f, 0.0f);
	        tAnim.setStartOffset(0);
	        tAnim.setDuration(300);
//	        tAnim.setFillAfter(true);
//	        tAnim.setFillEnabled(true);
	        tAnim.setAnimationListener(new Animation.AnimationListener()
	        {
				@Override
				public void onAnimationStart(Animation animation) 
				{
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) 
				{
				}
				
				@Override
				public void onAnimationEnd(Animation animation) 
				{
					shownView = showingView;
					showingView = null;

					shownView.setTranslationX(-250.0f * getResources().getDisplayMetrics().density);
				}
			});
	        swipingView.startAnimation(tAnim);
	        
	        showingView = swipingView;
	        swipingView = null;
		}

		private void hideView()
		{
			if (hidingView != null) return;
			if (shownView == null) return;
			
			shownView.setTranslationX(0.0f);
			
	        TranslateAnimation tAnim = new TranslateAnimation(-250.0f * getResources().getDisplayMetrics().density, 0.0f, 0.0f, 0.0f);
	        tAnim.setStartOffset(0);
	        tAnim.setDuration(300);
	        tAnim.setFillAfter(true);
	        tAnim.setFillEnabled(true);
	        tAnim.setAnimationListener(new Animation.AnimationListener() 
	        {
				@Override
				public void onAnimationStart(Animation animation) 
				{
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) 
				{
				}
				
				@Override
				public void onAnimationEnd(Animation animation) 
				{
					hidingView = null;

					
//					hidingView.llButtons.setVisibility(View.GONE);
				}
			});
	        shownView.startAnimation(tAnim);
	        
	        hidingView = shownView;
	        shownView = null;
		}

		private void returnView()
		{
			if (swipingView == null) return;
			
			float offset = swipingView.getTranslationX();
			float k = Math.abs(offset / 150.0f);
			swipingView.setTranslationX(0.0f);
			
	        TranslateAnimation tAnim = new TranslateAnimation(offset, 0.0f, 0.0f, 0.0f);
	        tAnim.setStartOffset(0);
	        tAnim.setDuration((int) (300.0f * k));
	        tAnim.setFillAfter(true);
	        tAnim.setFillEnabled(true);
	        swipingView.startAnimation(tAnim);
	        
	        swipingView = null;
		}

		private void processMoveEvent(MotionEvent event)
		{
/*			if (swipingView == null) return;
			if (event.getAction() != MotionEvent.ACTION_MOVE) return;
			
			float delta = event.getX() - swipeStartX;
			if (delta < -150.0f * getResources().getDisplayMetrics().density)
			{
				showView();
			}
			else if (delta < 0.0f)
			{
				swipingView.setTranslationX(delta / 2.0f);
			}

			if (Math.abs(event.getY() - swipeStartY) >= 150.0f * getResources().getDisplayMetrics().density)
			{
				returnView();
			}*/
		}
		
		@Override
		public int getItemViewType(int position){
			if(position >= pastIndex && pastIndex != -1 && (this.getCount()!=this.data.meetings.size())) return -1;
			else return super.getItemViewType(position);
		}
		
		@Override
		public View getView(int i, View view, ViewGroup parent)
		{
			if (i == pastIndex)
			{
				return layoutInflater.inflate(R.layout.listitem_past_meetings, null);
			}
			ViewHolder viewHolder = null;
			View row = view;
			if ((row == null) || (row.getTag() == null)) 
			{
				row = layoutInflater.inflate(R.layout.listitem_meetings, null);

				viewHolder = new ViewHolder();
				// bar picture
				viewHolder.wivPicture = (WebImageView2) row.findViewById(R.id.picture);
				viewHolder.wivPicture.setHeight(165);
				viewHolder.wivPicture.setImagesStore(mImagesStore);
				viewHolder.wivPicture.setCacheDir(appSettings.getCacheDirImage());
				// blur
				viewHolder.llBlur = (LinearLayout) row.findViewById(R.id.ll_blur);
				// NEW!!! caption
				viewHolder.tvNew = (TextView) row.findViewById(R.id.tv_new);
				viewHolder.tvNew.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
				// meeting title
				viewHolder.tvTitle = (TextView) row.findViewById(R.id.tv_title);
				viewHolder.tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
				// meeting time
				viewHolder.tvTime = (TextView) row.findViewById(R.id.tv_when);
				viewHolder.tvTime.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
				// user avatar
				viewHolder.wivAvatar = (WebImageView2) row.findViewById(R.id.avatar);
				viewHolder.wivAvatar.setCircle(true);
				viewHolder.wivAvatar.setImagesStore(mImagesStore);
				viewHolder.wivAvatar.setCacheDir(appSettings.getCacheDirImage());
				// user name
				viewHolder.tvFriendName = (TextView) row.findViewById(R.id.tv_user_name);
				viewHolder.tvFriendName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
				// invited caption
				viewHolder.tvInvited = (TextView) row.findViewById(R.id.tv_invited);
				viewHolder.tvInvited.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
				// invited count
				viewHolder.tvInvitedCount = (TextView) row.findViewById(R.id.tv_invited_count);
				viewHolder.tvInvitedCount.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
				// separator
				viewHolder.tvSeparator = (TextView) row.findViewById(R.id.tv_separator);
				viewHolder.tvSeparator.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
				// will go caption
				viewHolder.tvWillGo = (TextView) row.findViewById(R.id.tv_will_go);
				viewHolder.tvWillGo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
				// will go count
				viewHolder.tvWillGoCount = (TextView) row.findViewById(R.id.tv_will_go_count);
				viewHolder.tvWillGoCount.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
				// button layout
				viewHolder.llButtons = (LinearLayout) row.findViewById(R.id.ll_buttons);
				// don't know button
				viewHolder.tvBtnDontKnow = (TextView) viewHolder.llButtons.findViewById(R.id.tv_btn_dont_know);
				viewHolder.tvBtnDontKnow.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
				// don't go button
				viewHolder.tvBtnDontGo = (TextView) viewHolder.llButtons.findViewById(R.id.tv_btn_dont_go);
				viewHolder.tvBtnDontGo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
				// will go button
				viewHolder.tvBtnWillGo = (TextView) viewHolder.llButtons.findViewById(R.id.tv_btn_will_go);
				viewHolder.tvBtnWillGo.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
				// don't know button
				viewHolder.tvBtnCancel = (TextView) viewHolder.llButtons.findViewById(R.id.tv_btn_cancel);
				viewHolder.tvBtnCancel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
				
				viewHolder.rlMainInfo = (RelativeLayout) row.findViewById(R.id.rl_main_info);

				row.setTag(viewHolder);
				viewHolder.rlMainInfo.setOnTouchListener(new View.OnTouchListener() 
				{
					@Override
					public boolean onTouch(View v, MotionEvent event) 
					{
//						if(ad.getItemViewType(pos)<0) return true;
						LinearLayout llButtons = (LinearLayout) v.getTag();
						int ind = (Integer) llButtons.getTag();
						if (event.getAction() == MotionEvent.ACTION_DOWN)
						{
							if (shownView != v)
							{
								hideView();

								if ((pastIndex == -1) || (ind < pastIndex))
								{
									swipingView = v;
									swipeStartX = event.getX();
									swipeStartY = event.getY();
									
									llButtons.setVisibility(View.VISIBLE);
								}
							}
						}
						else if (event.getAction() == MotionEvent.ACTION_UP)
						{
							if (swipingView != null)
							{
								returnView();
							}

							if (shownView == v)
							{
								if (event.getX() < CommonHelper.getScreenWidth() - 250.0f * getResources().getDisplayMetrics().density)
								{
									hideView();
								}
							}
							else if (shownView == null)
							{
								Intent intent = new Intent();
								intent.putExtra("meeting_id", getItem(ind).id);
								intent.setClass(MeetingsActivity.this, MeetingActivity.class);
								
								startActivityForResult(intent, SHOW_MEETING);
							}
						}
						else if (event.getAction() == MotionEvent.ACTION_MOVE)
						{
							//processMoveEvent(event);
						}
						
						return true;
					}
				});

			} 
			else 
			{
				viewHolder = (ViewHolder) row.getTag();
			}

			final Meeting meeting = getItem(i);
			
			if (meeting.owner)
			{
				viewHolder.tvFriendName.setText(meeting.user_name);
			}
			else
			{
				viewHolder.tvFriendName.setText(meeting.user_name + " " + MeetingsActivity.this.getString(R.string.invites_you) + ":");
			}
			
			viewHolder.tvTitle.setText(meeting.title);
			viewHolder.tvInvitedCount.setText(" " + String.valueOf(meeting.invited_count));
			viewHolder.tvWillGoCount.setText(" " + String.valueOf(meeting.will_go_count));

			if (meeting.user_picture != null)
			{
				viewHolder.wivAvatar.setImageURL(meeting.user_picture, "123");
			}

			if (meeting.bar_picture != null)
			{
				viewHolder.wivPicture.setImageURL(meeting.bar_picture, "123");
			}
			
			viewHolder.llButtons.setTag(i);
			viewHolder.rlMainInfo.setTag(viewHolder.llButtons);

			if ((pastIndex != -1) && (i > pastIndex))
			{
				viewHolder.tvNew.setVisibility(View.INVISIBLE);
				viewHolder.llBlur.setVisibility(View.VISIBLE);
				viewHolder.tvTitle.setTextColor(Color.parseColor("#4CFFFFFF"));
				viewHolder.tvTime.setTextColor(Color.parseColor("#4CFFFFFF"));
				viewHolder.tvTime.setText(R.string.meeting_past);
				if (meeting.owner)
				{
					viewHolder.tvInvited.setVisibility(View.VISIBLE);
					viewHolder.tvInvitedCount.setVisibility(View.VISIBLE);
					viewHolder.tvSeparator.setVisibility(View.VISIBLE);
					viewHolder.tvWillGo.setVisibility(View.VISIBLE);
					viewHolder.tvWillGo.setTextColor(Color.WHITE);
					viewHolder.tvWillGo.setText(R.string.went);
					viewHolder.tvWillGoCount.setTextColor(Color.WHITE);
				}
				else
				{
					viewHolder.tvInvited.setVisibility(View.GONE);
					viewHolder.tvInvitedCount.setVisibility(View.GONE);
					viewHolder.tvSeparator.setVisibility(View.GONE);
					viewHolder.tvWillGo.setVisibility(View.GONE);
					viewHolder.tvWillGoCount.setTextColor(Color.WHITE);
					viewHolder.tvWillGoCount.setText(meeting.bar_name);
				}
			}
			else 
			{
				Date date = new Date(Long.valueOf(meeting.date) * 1000);
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
				
				viewHolder.tvNew.setVisibility(meeting.view ? View.INVISIBLE : View.VISIBLE);
				viewHolder.llBlur.setVisibility(View.GONE);
				viewHolder.tvTitle.setTextColor(Color.WHITE);
				viewHolder.tvTime.setTextColor(Color.WHITE);
				viewHolder.tvTime.setText(_date + " " + _time);
				viewHolder.tvWillGo.setTextColor(getResources().getColor(R.color.blue_light));
				viewHolder.tvWillGo.setText(R.string._will_go_);

				if (meeting.owner)
				{
					viewHolder.tvBtnCancel.setText(R.string._cancel);
					viewHolder.tvBtnCancel.setOnClickListener(new OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MeetingsActivity.this);
							alertDialogBuilder.setTitle("");
							alertDialogBuilder.setMessage(R.string.ask_cancel_meeting);
							alertDialogBuilder.setCancelable(false);
							alertDialogBuilder.setPositiveButton(getString(R.string.profile_text_tb_on), new DialogInterface.OnClickListener() 
								{
									@Override
									public void onClick(DialogInterface dialog, int _id) 
									{
										new CancelMeetingTask(meeting.id).execute();
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
					viewHolder.tvBtnWillGo.setEnabled(false);
					viewHolder.tvBtnWillGo.setSelected(false);
					viewHolder.tvBtnWillGo.setOnClickListener(new OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
						}
					});
					viewHolder.tvBtnDontGo.setEnabled(false);
					viewHolder.tvBtnDontGo.setSelected(false);
					viewHolder.tvBtnDontGo.setOnClickListener(new OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
						}
					});
					viewHolder.tvBtnDontKnow.setEnabled(false);
					viewHolder.tvBtnDontKnow.setSelected(false);
					viewHolder.tvBtnDontKnow.setOnClickListener(new OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
						}
					});

					viewHolder.tvInvited.setVisibility(View.VISIBLE);
					viewHolder.tvInvitedCount.setVisibility(View.VISIBLE);
					viewHolder.tvSeparator.setVisibility(View.VISIBLE);
					viewHolder.tvWillGo.setVisibility(View.VISIBLE);
					viewHolder.tvWillGoCount.setTextColor(getResources().getColor(R.color.blue_light));
				}
				else
				{
					viewHolder.tvBtnCancel.setText(R.string._remove);
					viewHolder.tvBtnCancel.setOnClickListener(new OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MeetingsActivity.this);
							alertDialogBuilder.setTitle("");
							alertDialogBuilder.setMessage(R.string.ask_remove_meeting);
							alertDialogBuilder.setCancelable(false);
							alertDialogBuilder.setPositiveButton(getString(R.string.profile_text_tb_on), new DialogInterface.OnClickListener() 
								{
									@Override
									public void onClick(DialogInterface dialog, int _id) 
									{
										new RemoveMeetingTask(meeting.id).execute();
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

					viewHolder.tvBtnWillGo.setEnabled(true);
					if (meeting.state == Meeting.GO)
					{
						viewHolder.tvBtnWillGo.setSelected(true);
					}
					else
					{
						viewHolder.tvBtnWillGo.setSelected(false);
					}
					viewHolder.tvBtnWillGo.setOnClickListener(new OnClickListener() 
						{
							@Override
							public void onClick(View v) 
							{
								new GoTask(meeting.id, "yes").execute();
							}
						});
					
					viewHolder.tvBtnDontGo.setEnabled(true);
					if (meeting.state == Meeting.DONT_GO)
					{
						viewHolder.tvBtnDontGo.setSelected(true);
					}
					else
					{
						viewHolder.tvBtnDontGo.setSelected(false);
					}
					viewHolder.tvBtnDontGo.setOnClickListener(new OnClickListener() 
						{
							@Override
							public void onClick(View v) 
							{
								new GoTask(meeting.id, "not").execute();
							}
						});

					viewHolder.tvBtnDontKnow.setEnabled(true);
					if (meeting.state == Meeting.DONT_KNOW)
					{
						viewHolder.tvBtnDontKnow.setSelected(true);
					}
					else
					{
						viewHolder.tvBtnDontKnow.setSelected(false);
					}
					viewHolder.tvBtnDontKnow.setOnClickListener(new OnClickListener() 
						{
							@Override
							public void onClick(View v) 
							{
								new GoTask(meeting.id, "maybe").execute();
							}
						});
					
					viewHolder.tvInvited.setVisibility(View.GONE);
					viewHolder.tvInvitedCount.setVisibility(View.GONE);
					viewHolder.tvSeparator.setVisibility(View.GONE);
					viewHolder.tvWillGo.setVisibility(View.GONE);
					viewHolder.tvWillGoCount.setTextColor(Color.WHITE);
					viewHolder.tvWillGoCount.setText(meeting.bar_name);
				}
			}
			viewHolder.llButtons.setVisibility(View.INVISIBLE);
			return row;
		}

		@Override
		public Meeting getItem(int position) 
		{
			if (position == pastIndex) return null;
			
			if ((pastIndex != -1) && (position > pastIndex))
			{
				return data.meetings.get(position - 1);
			}
			else
			{
				return data.meetings.get(position);
			}
		}

		public Meeting findMeetingByID(String id) 
		{
			for (int i = 0; i < data.meetings.size(); ++i)
			{
				if (data.meetings.get(i).id.equals(id))
				{
					return data.meetings.get(i);
				}
			}
			
			return null;
		}

		public void updateMeeting(String id, int state, int willGo)
		{
			for (int i = 0; i < data.meetings.size(); ++i)
			{
				if (i == pastIndex) break;
				
				if (data.meetings.get(i).id.equals(id))
				{
					data.meetings.get(i).will_go_count = willGo;
					data.meetings.get(i).state = state;
					
					notifyDataSetChanged();
					
					break;
				}
			}
		}
		
		public void removeMeeting(String id)
		{
			for (int i = 0; i < data.meetings.size(); ++i)
			{
				if (i == pastIndex) break;
				
				if (data.meetings.get(i).id.equals(id))
				{
					data.meetings.remove(i);
					notifyDataSetChanged();
					
					if (pastIndex != -1)
					{
						--pastIndex;
					}
					
					break;
				}
			}
		}
		
		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		class ViewHolder 
		{
			WebImageView2 wivPicture;
			LinearLayout llBlur;
			WebImageView2 wivAvatar;
			TextView tvNew;
			TextView tvTitle;
			TextView tvTime;
			TextView tvFriendName;
			TextView tvInvited;
			TextView tvInvitedCount;
			TextView tvSeparator;
			TextView tvWillGo;
			TextView tvWillGoCount;
			RelativeLayout rlMainInfo;
			LinearLayout llButtons;
			TextView tvBtnWillGo;
			TextView tvBtnDontGo;
			TextView tvBtnDontKnow;
			TextView tvBtnCancel;
		}
	}

	private class LoadTask extends QueryTask<Void, Void, MeetingsQuery> 
	{
		private final int LIMIT = 12;
		
		@Override
		protected MeetingsQuery doInBackground(Void... params) 
		{
			MeetingsQuery meetingsQuery = new MeetingsQuery(appSettings, adapter.getCount(), LIMIT);
			meetingsQuery.getResponse(WebQuery.GET);
			
			return meetingsQuery;
		}

		@Override
		protected void onPostExecute(MeetingsQuery query) 
		{
			if (checkResult(query))
			{
				MeetingsQuery.Data data = query.getData();

				if (data.meetings.size() > 0)
				{
					if (pastIndex == -1)
					{
						for (int i = 0; i < data.meetings.size(); ++i)
						{
							Long time = Long.parseLong(data.meetings.get(i).date);
							Long curTime = Calendar.getInstance().getTimeInMillis() / 1000;
							
							if (curTime - time >= 12 * 60 * 60)
							{
								if (adapter.getCount() == 0)
								{
									pastIndex = i + 1;
								}
								else
								{
									pastIndex = adapter.getCount() + i;
								}
								break;
							}
						}
					}

					adapter.addData(query.getData());
				}
				
				if (listView.getCount() == 0)
				{
					tvNoMeetings.setVisibility(View.VISIBLE);
				}
				else
				{
					tvNoMeetings.setVisibility(View.GONE);
				}
				
				empty = (data.meetings.size() < LIMIT);
			}
			
			loading = false;
			
			super.onPostExecute(query);
		}
	}

	private class GoTask extends QueryTask<Void, Void, UpdateGoQuery> 
	{
		private String meeting_id;
		private String go;

		public GoTask(String meeting_id, String go) 
		{
			super();
			
			this.meeting_id = meeting_id;
			this.go = go;
		}

		@Override
		protected UpdateGoQuery doInBackground(Void... params) 
		{
			UpdateGoQuery query = new UpdateGoQuery(appSettings, meeting_id, go);

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
					Meeting meeting = adapter.findMeetingByID(meeting_id);
					
					meeting.invited_count = data.invited;
					meeting.will_go_count = data.willGo;
					
					if (go.equals("yes"))
					{
						meeting.state = Meeting.GO;
					}
					else if (go.equals("not"))
					{
						meeting.state = Meeting.DONT_GO;
					}
					else 
					{
						meeting.state = Meeting.DONT_KNOW;
					}
					
					adapter.notifyDataSetChanged();
				}
			}
			
			super.onPostExecute(query);
		}
	}
	
	private class CancelMeetingTask extends QueryTask<Void, Void, CancelMeetingQuery> 
	{
		private String meeting_id;

		public CancelMeetingTask(String meeting_id) 
		{
			super();
			
			this.meeting_id = meeting_id;
		}

		@Override
		protected CancelMeetingQuery doInBackground(Void... params) 
		{
			CancelMeetingQuery query = new CancelMeetingQuery(appSettings, meeting_id);

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

					adapter.removeMeeting(meeting_id);
				}
			}
			
			super.onPostExecute(query);
		}
	}

	private class RemoveMeetingTask extends QueryTask<Void, Void, RemoveMeetingQuery> 
	{
		private String meeting_id;

		public RemoveMeetingTask(String meeting_id) 
		{
			super();
			
			this.meeting_id = meeting_id;
		}

		@Override
		protected RemoveMeetingQuery doInBackground(Void... params) 
		{
			RemoveMeetingQuery query = new RemoveMeetingQuery(appSettings, meeting_id);

			return query;
		}
		
		@Override
		protected void onPostExecute(RemoveMeetingQuery query) 
		{
			if (checkResult(query))
			{
				RemoveMeetingQuery.Data data = query.getData();
				
				if (data.success)
				{
					AppSettings appSettings = new AppSettings(getApplicationContext());
					appSettings.setEventsMeetingsCount(appSettings.getEventsMeetingsCount() - 1);

					adapter.removeMeeting(meeting_id);
				}
			}
			
			super.onPostExecute(query);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		switch (resultCode)
		{
		case RESULT_OK:
			if (requestCode == CREATE_MEETING)
			{
				adapter.clear();
				empty = false;
				new LoadTask().execute();
			}
			break;
		case MeetingActivity.NEED_UPDATE:
			adapter.updateMeeting(intent.getStringExtra("meeting_id"), intent.getIntExtra("state", Meeting.DONT_KNOW), intent.getIntExtra("will_go", 0));
			break;
		case MeetingActivity.NEED_REMOVE:
			adapter.removeMeeting(intent.getStringExtra("meeting_id"));
			break;
		}

		super.onActivityResult(requestCode, resultCode, intent);
	}
}
