package com.drink.activity;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import com.drink.R;
import com.drink.controls.SwipeDetector;
import com.drink.helpers.CommonHelper;
import com.drink.imageloader.WebImageView2;
import com.drink.query.BlogPostQueryAdapted;
import com.drink.query.CanCommentQuery;
import com.drink.query.CommentsQuery;
import com.drink.query.LikeQuery;
import com.drink.query.WebQuery;
import com.drink.twitter.TwitterApp;
import com.drink.twitter.TwitterDialog;
import com.drink.twitter.TwitterSession;
import com.drink.twitter.TwitterApp.TwDialogListener;
import com.drink.types.Block;
import com.drink.types.Comment;
import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Session.StatusCallback;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

public class ReviewOneActivity extends BasicActivity implements OnClickListener {
	private static final int NEED_LOGIN = 0;
	private static final int POST_COMMENT = 1;
	private static final int REQUIED_FOR_LIKE = 2;
	private static final int FB_SHARED = 3;
	private static final int TW_SHARED = 4;

	private String ID;
	private BlogPostQueryAdapted.Data data;
	private BlogPostQueryAdapted.Data data1;
	private ListView listView;
	private BlocksAdapter adapter;
	private RelativeLayout header;
	private boolean empty = false;
	private boolean loading = false;
	private Button btn_back;
	private ImageView btn_like;
	private ImageView btn_fb;
	private ImageView btn_tw;
	private TextView tv_caption;
	private int itemHeight;
	private RequestToken requestToken;

	private static final int MSG_UPDATE_ADAPTER = 0;
	private static final int MSG_CHANGE_ITEM = 1;
	private static final int MSG_ANIMATION_CHANGE = 2;

	private static final int NONE = 0;
	private static final int TARGET_TYPE_BAR = 1;
	private static final int TARGET_TYPE_DRINK = 2;
	private static final int TARGET_TYPE_COCKT = 3;
	private static final int TARGET_TYPE_REW = 4;
	private static final int TARGET_TYPE_BRAND = 5;
	private static final int TARGET_TYPE_MEET = 6;

	private static final int BLOCK_ONE = 1;
	private static final int BLOCK_TWO = 2;
	private static final int BLOCK_THREE = 3;
	private static final int BLOCK_FOUR = 4;
	private static final int BLOCK_FIVE = 5;
	private static final int BLOCK_SIX = 6;
	private static final int BLOCK_SEVEN = 7;
	private static final int BLOCK_EIGHT = 8;
	private static final int BLOCK_NINE = 9;
	private static final int BLOCK_FOOTER = 110;
	private static final int BLOCK_COMMENT = 100;

	private TwitterApp mTwitter;

	private enum FROM {
		TWITTER_POST, TWITTER_LOGIN
	};

	private enum MESSAGE {
		SUCCESS, DUPLICATE, FAILED, CANCELLED
	};

	public ReviewOneActivity() {
		super(R.layout.activity_review_one, true);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_UPDATE_ADAPTER:
				adapter.notifyDataSetChanged();

				break;
			case MSG_CHANGE_ITEM:

				if (Integer.valueOf(data.blocks.get(msg.arg1).type) == 8) {
					ArrayList<String> arrUrlPictures = new ArrayList<String>();
					int pic_arr_size = data.blocks.get(msg.arg1).child_blocks
							.size();
					for (int i = 0; i < pic_arr_size; i++) {
						arrUrlPictures
								.add(data.blocks.get(msg.arg1).child_blocks
										.get(i).picture);

					}
					int pic_pos = data.blocks.get(msg.arg1).arr_item;

					if (arrUrlPictures.size() > 0) {
						Intent intent = new Intent();
						intent.setClass(ReviewOneActivity.this,
								PicturesActivity.class);
						intent.putExtra("index", pic_pos);
						intent.putStringArrayListExtra("urls",
								(ArrayList<String>) arrUrlPictures);

						startActivity(intent);
					}
				} else {
					if (data.blocks.get(msg.arg1).goal_type != null) {
						int goal_id = 0;
						if (data.blocks.get(msg.arg1).goal_id != null) {

							goal_id = Integer
									.valueOf(data.blocks.get(msg.arg1).goal_id);
						}

						switch (Integer
								.valueOf(data.blocks.get(msg.arg1).goal_type)) {
						case NONE:

							break;
						case TARGET_TYPE_BAR:
							Intent intent = new Intent();
							intent.setClass(ReviewOneActivity.this,
									BarActivity.class);
							intent.putExtra("ID", goal_id);
							startActivity(intent);
							break;
						case TARGET_TYPE_DRINK:
							Intent intent2 = new Intent();
							intent2.setClass(ReviewOneActivity.this,
									DrinkActivity.class);
							intent2.putExtra("ID", goal_id);
							startActivity(intent2);
							break;
						case TARGET_TYPE_COCKT:
							Intent intent3 = new Intent();
							intent3.setClass(ReviewOneActivity.this,
									CocktailActivity.class);
							intent3.putExtra("ID", goal_id);
							startActivity(intent3);
							break;
						case TARGET_TYPE_REW:
							Intent intent4 = new Intent();
							intent4.setClass(ReviewOneActivity.this,
									ReviewOneActivity.class);
							intent4.putExtra("ID", goal_id);
							startActivity(intent4);
							break;
						case TARGET_TYPE_BRAND:
							Intent intent5 = new Intent();
							intent5.setClass(ReviewOneActivity.this,
									BrandActivity.class);
							intent5.putExtra("ID", goal_id);
							startActivity(intent5);
							break;
						case TARGET_TYPE_MEET:
							Intent intent6 = new Intent();
							intent6.setClass(ReviewOneActivity.this,
									MeetingActivity.class);
							intent6.putExtra("ID", goal_id);
							startActivity(intent6);
							break;

						}
					}
				}

				break;
			case MSG_ANIMATION_CHANGE:

				View view = (View) msg.obj;

				if (Integer.valueOf(data.blocks.get(msg.arg1).type) == 8) {
					int arr_val = data.blocks.get(msg.arg1).arr_item;
					if (arr_val == 0 && msg.arg2 == 1) {
					} else if (arr_val == data.blocks.get(msg.arg1).child_blocks
							.size() - 1 && msg.arg2 == 0) {
					} else {
						view.startAnimation(getChangeAnim(
								0,
								(msg.arg2 == 0) ? -view.getWidth() : 2 * view
										.getWidth(), msg.arg1, msg.arg2));
					}

				}

				break;
			}
		}
	};

	public Handler getHandler() {
		return handler;
	}

	private Animation getChangeAnim(float fromX, float toX, int position,
			int direct) {

		Animation animation = new TranslateAnimation(fromX, toX, 0, 0);
		animation.setStartOffset(200);
		animation.setDuration(1000);
		animation.setAnimationListener(new ChangeAnimationListenter(position,
				direct));
		// animation.setInterpolator(AnimationUtils.loadInterpolator(this,
		// android.R.anim.anticipate_overshoot_interpolator));
		animation.setInterpolator(AnimationUtils.loadInterpolator(this,
				android.R.anim.accelerate_decelerate_interpolator));

		return animation;
	}

	public class ChangeAnimationListenter implements
			Animation.AnimationListener {
		private int position;
		private int direct;

		public ChangeAnimationListenter(int position, int direct) {
			this.position = position;
			this.direct = direct;
		}

		@Override
		public void onAnimationEnd(Animation arg0) {

			int s = data.blocks.get(position).child_blocks.size();
			int this_pos = data.blocks.get(position).arr_item;

			if (direct == 1) {
				if (data.blocks.get(position).arr_item > 0) {
					data.blocks.get(position).setArrPos(
							data.blocks.get(position).arr_item - 1);
					adapter.notifyDataSetChanged();

				}

			}

			if (direct == 0) {
				if (data.blocks.get(position).arr_item < (data.blocks
						.get(position).child_blocks.size() - 1)) {
					data.blocks.get(position).setArrPos(
							data.blocks.get(position).arr_item + 1);
					adapter.notifyDataSetChanged();
				}

			}

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationStart(Animation animation) {

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ID = getIntent().getStringExtra("ID");
		btn_back = (Button) findViewById(R.id.btn_back);
		tv_caption = (TextView) findViewById(R.id.tv_caption);
		btn_back.setOnClickListener(this);
		itemHeight = getWidth(ReviewOneActivity.this);
		tv_caption.setText(getResources().getString(R.string.rew_caption));
		listView = (ListView) findViewById(R.id.listView);
		adapter = new BlocksAdapter(getApplicationContext());
		header = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.listitem_review,
				(ViewGroup) findViewById(R.id.ll_content));
		header.setVisibility(View.GONE);
		listView.addHeaderView(header, null, false);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new OnScrollListener() {
			private boolean needLoading = false;

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if ((totalItemCount > 0) && (!loading)) {
					needLoading = (firstVisibleItem + visibleItemCount >= totalItemCount);
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (!empty && needLoading && !loading) {
						loading = true;
						needLoading = false;
						new LoadTaskComm().execute();
						// new LoadCommentsTask().execute();
					}
				}
			}
		});
		listView.setVisibility(View.GONE);
		final SwipeDetector swipeDetector = new SwipeDetector();
		listView.setOnTouchListener(swipeDetector);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position == 0 || position == data.blocks.size() + 1)
					return;

				Message msg = new Message();
				msg.arg1 = position - 1;

				if (swipeDetector.swipeDetected()) {
					if (swipeDetector.getAction() == SwipeDetector.Action.LR
							|| swipeDetector.getAction() == SwipeDetector.Action.RL) {
						msg.what = MSG_ANIMATION_CHANGE;

						msg.arg2 = swipeDetector.getAction() == SwipeDetector.Action.LR ? 1
								: 0;
						msg.obj = view;
					}
				}

				else
					msg.what = MSG_CHANGE_ITEM;

				handler.sendMessage(msg);
			}
		});

		new LoadTask().execute();

		// new CheckAddCommentTask().execute();
	}

	private void updateView() {
		listView.setVisibility(View.VISIBLE);
		TextView isnew = (TextView) findViewById(R.id.tv_review_isnew);
		isnew.setVisibility(View.GONE);

		WebImageView2 usrPicture = (WebImageView2) findViewById(R.id.user_picture);
		usrPicture.setImagesStore(mImagesStore);
		usrPicture.setCacheDir(appSettings.getCacheDirImage());
		usrPicture.setCircle(true);
		usrPicture.setImageURL(data.authPic, "123");

		WebImageView2 picture = (WebImageView2) findViewById(R.id.picture);
		picture.setImagesStore(mImagesStore);
		picture.setCacheDir(appSettings.getCacheDirImage());
		picture.setHeight(itemHeight);
		picture.setImageURL(data.picture, "123");
		int h = itemHeight;
		TextView dateTv = (TextView) findViewById(R.id.tv_review_date);
		dateTv.setTypeface(Typeface.createFromAsset(
				ReviewOneActivity.this.getAssets(), "fonts/HelveticaNeue.otf"));
		TextView name = (TextView) findViewById(R.id.tv_review_username);
		name.setTypeface(Typeface.createFromAsset(
				ReviewOneActivity.this.getAssets(), "fonts/HelveticaNeue.otf"));
		TextView title = (TextView) findViewById(R.id.tv_review_theme);
		title.setTypeface(Typeface.createFromAsset(
				ReviewOneActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

		long timestamp = Integer.valueOf(data.date);
		timestamp *= 1000;

		if (!data.date.equals("")) {
			Date dateVal = new Date();
			dateVal.setTime((long) timestamp);

			if (dateVal != null) {

				dateTv.setText(new SimpleDateFormat("MMMM", Locale.getDefault())
						.format(dateVal)
						+ " "
						+ new SimpleDateFormat("dd", Locale.getDefault())
								.format(dateVal));
				dateTv.setTypeface(Typeface.createFromAsset(
						ReviewOneActivity.this.getAssets(),
						"fonts/ProximaNova-Bold.otf"));

			}
		}

		name.setText(data.authName);
		title.setText(Html.fromHtml(data.title));
		header.setVisibility(View.VISIBLE);
		adapter.notifyDataSetChanged();

	}

	private class LoadTask extends QueryTask<Void, Void, BlogPostQueryAdapted> {
		@Override
		protected BlogPostQueryAdapted doInBackground(Void... params) {
			BlogPostQueryAdapted query = new BlogPostQueryAdapted(
					ReviewOneActivity.this, ID, 1);

			return query;
		}

		@Override
		protected void onPostExecute(BlogPostQueryAdapted query) {
			if (checkResult(query)) {
				data = query.getData();

				if (data.blocks.size() > 0) {

					empty = false;
				} else {
					empty = true;
				}
				updateView();

			}

			super.onPostExecute(query);
		}
	}

	private class CheckAddCommentTask extends
			QueryTask<Void, Void, CanCommentQuery> {
		@Override
		protected CanCommentQuery doInBackground(Void... params) {
			CanCommentQuery query = new CanCommentQuery(appSettings, ID);
			query.getResponse(WebQuery.GET);

			return query;
		}

		@Override
		protected void onPostExecute(CanCommentQuery query) {
			if (checkResult(query)) {
				CanCommentQuery.Data data = query.getData();
				if (data.mCanComment) {
					Intent intent = new Intent();
					intent.setClass(ReviewOneActivity.this,
							AddTextCommentActivity.class);
					intent.putExtra("ID", ID);
					intent.putExtra("type", CommentsQuery.BLOG_POSTS);

					startActivityForResult(intent, POST_COMMENT);
				} else {
					showErrorBox(
							CommonHelper.getRemainingTimePhrase(data.mMinutes),
							false);
				}
			}

			super.onPostExecute(query);
		}
	}

	private class BlocksAdapter extends BaseAdapter {
		LayoutInflater layoutInflater;

		public BlocksAdapter(Context context) {
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (data == null || data.blocks == null)
				return 0;

			return data.blocks.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public int getItemViewType(int position) {

			return Integer.valueOf(data.blocks.get(position).type);
		}

		@SuppressLint("DefaultLocale")
		@Override
		public View getView(int i, View view, ViewGroup parent) {

			ViewHolder viewHolder;
			View row = view;

			Block block = data.blocks.get(i);
			int type = Integer.valueOf(block.type);
			viewHolder = new ViewHolder();
			// if (row == null) {
			viewHolder = new ViewHolder();
			switch (type) {
			case BLOCK_ONE:
				if (block.number != null) {
					if (Integer.valueOf(block.number) > 0) {
						row = this.layoutInflater.inflate(
								R.layout.listitem_reviewone_block1_1, parent,
								false);
						viewHolder.item_number = (TextView) row
								.findViewById(R.id.tv_reviewone_blocknum1);
						viewHolder.item_number.setText(block.number);
						viewHolder.item_number.setTypeface(Typeface
								.createFromAsset(
										ReviewOneActivity.this.getAssets(),
										"fonts/ProximaNova-Reg.otf"));
					}

				} else {
					row = this.layoutInflater.inflate(
							R.layout.listitem_reviewone_block1, parent, false);
				}

				viewHolder.text = (TextView) row
						.findViewById(R.id.tv_reviewone_block1);
				viewHolder.text.setText(block.text.toUpperCase());
				viewHolder.text.setTypeface(Typeface.createFromAsset(
						ReviewOneActivity.this.getAssets(),
						"fonts/ProximaNova-Reg.otf"));

				break;

			case BLOCK_TWO:
				if (block.number != null) {
					if (Integer.valueOf(block.number) > 0) {
						row = this.layoutInflater.inflate(
								R.layout.listitem_reviewone_block2_1, parent,
								false);
						viewHolder.item_number = (TextView) row
								.findViewById(R.id.tv_reviewone_blocknum21);
						viewHolder.item_number.setText(block.number);
						viewHolder.item_number.setTypeface(Typeface
								.createFromAsset(
										ReviewOneActivity.this.getAssets(),
										"fonts/ProximaNova-Reg.otf"));
					}

				} else {
					row = this.layoutInflater.inflate(
							R.layout.listitem_reviewone_block2, parent, false);
				}

				viewHolder.text = (TextView) row
						.findViewById(R.id.tv_reviewone_block2);
				viewHolder.text.setText(block.text.toUpperCase());
				viewHolder.text.setTypeface(Typeface.createFromAsset(
						ReviewOneActivity.this.getAssets(),
						"fonts/ProximaNova-Reg.otf"));

				break;

			case BLOCK_THREE:
				row = this.layoutInflater.inflate(
						R.layout.listitem_reviewone_block3, parent, false);

				break;
			case BLOCK_FOUR:
				row = this.layoutInflater.inflate(
						R.layout.listitem_reviewone_block4, parent, false);
				viewHolder.text = (TextView) row
						.findViewById(R.id.tv_reviewone_block4);
				viewHolder.text.setText(block.text);

				viewHolder.text.setTypeface(Typeface.createFromAsset(
						ReviewOneActivity.this.getAssets(),
						"fonts/ProximaNova-Reg.otf"));

				break;
			case BLOCK_FIVE:

				if (block.child_blocks.size() > 0) {
					for (int k = 0; k < block.child_blocks.size(); k++) {
						row = this.layoutInflater.inflate(
								R.layout.listitem_reviewone_block5, parent,
								false);

						viewHolder.text = (TextView) row
								.findViewById(R.id.tv_reviewone_block5);
						viewHolder.text.setText(block.child_blocks.get(k).text);
						viewHolder.text.setTypeface(Typeface.createFromAsset(
								ReviewOneActivity.this.getAssets(),
								"fonts/ProximaNova-Reg.otf"));
					}
				}

				break;

			case BLOCK_SIX:
				if (block.child_blocks.size() > 0) {
					for (int l = 0; l < block.child_blocks.size(); l++) {

						row = this.layoutInflater.inflate(
								R.layout.listitem_reviewone_block6, parent,
								false);
						viewHolder.text = (TextView) row
								.findViewById(R.id.tv_reviewone_block6);
						viewHolder.text.setText(block.child_blocks.get(l).text);
						viewHolder.text.setTypeface(Typeface.createFromAsset(
								ReviewOneActivity.this.getAssets(),
								"fonts/ProximaNova-Reg.otf"));
						viewHolder.item_number = (TextView) row
								.findViewById(R.id.tv_reviewone_blocknum6);
						viewHolder.item_number.setText(block.child_blocks
								.get(l).number);
						viewHolder.item_number.setTypeface(Typeface
								.createFromAsset(
										ReviewOneActivity.this.getAssets(),
										"fonts/ProximaNova-Reg.otf"));
					}
				}

				break;
			case BLOCK_SEVEN:
				if (block.child_blocks.size() > 0) {
					for (int m = 0; m < block.child_blocks.size(); m++) {

						row = this.layoutInflater.inflate(
								R.layout.listitem_reviewone_block6, parent,
								false);
						viewHolder.text = (TextView) row
								.findViewById(R.id.tv_reviewone_block6);
						viewHolder.text.setText(block.child_blocks.get(m).text);
						viewHolder.text.setTypeface(Typeface.createFromAsset(
								ReviewOneActivity.this.getAssets(),
								"fonts/ProximaNova-Reg.otf"));
						viewHolder.item_number = (TextView) row
								.findViewById(R.id.tv_reviewone_blocknum6);
						viewHolder.item_number.setText(block.child_blocks
								.get(m).number);
						viewHolder.item_number.setTypeface(Typeface
								.createFromAsset(
										ReviewOneActivity.this.getAssets(),
										"fonts/ProximaNova-Reg.otf"));
					}

				}

				break;
			case BLOCK_EIGHT:
				row = this.layoutInflater.inflate(
						R.layout.listitem_reviewone_block8, parent, false);
				viewHolder.ll_img = (LinearLayout) row
						.findViewById(R.id.ll_block8_points);
				viewHolder.logo = (WebImageView2) row
						.findViewById(R.id.iv_revone_block8);
				viewHolder.logo.setImagesStore(mImagesStore);
				viewHolder.logo.setCacheDir(appSettings.getCacheDirImage());

				viewHolder.logo.setImageURL(
						block.child_blocks.get(block.arr_item).picture, "123");
				if (block.child_blocks.size() > 1) {
					for (int n = 0; n < block.child_blocks.size(); n++) {

						ImageView imageView = new ImageView(
								ReviewOneActivity.this);

						if (n == block.arr_item) {
							imageView.setImageResource(R.drawable.avatar);
						} else {
							imageView.setImageResource(R.drawable.avatar2);
						}

						imageView.setLayoutParams(new LayoutParams(15, 15));
						imageView.setPadding(2, 0, 2, 0);
						viewHolder.ll_img.addView(imageView);

					}
				}
				break;
			case BLOCK_NINE:
				row = this.layoutInflater.inflate(
						R.layout.listitem_reviewone_block9, parent, false);
				viewHolder.text = (TextView) row
						.findViewById(R.id.tv_reviewone_block9);
				viewHolder.text.setText(block.text);
				viewHolder.text.setTypeface(Typeface.createFromAsset(
						ReviewOneActivity.this.getAssets(),
						"fonts/ProximaNova-Reg.otf"));

				break;

			case BLOCK_FOOTER:

				row = this.layoutInflater.inflate(R.layout.header_review,
						parent, false);

				viewHolder.button_like = (ImageView) row
						.findViewById(R.id.im_review_ok);
				viewHolder.button_like
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (userToken.length() > 0) {
									new LikeQueryTask().execute();
								} else {
									Intent i = new Intent(
											ReviewOneActivity.this,
											LoginReqquiredActivity.class);
									startActivityForResult(i, REQUIED_FOR_LIKE);
								}
							}
						});
				viewHolder.button_fb = (ImageView) row
						.findViewById(R.id.im_review_fb);
				viewHolder.button_fb.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						Session session = Session.getActiveSession();

						if (session != null) {

							if (session.getState().isOpened()) {
								FbSharedDialog(session);
							} else
								FbLogin();

						} else {
							FbLogin();

						}
					}
				});
				viewHolder.button_tw = (ImageView) row
						.findViewById(R.id.im_review_tw);
				viewHolder.button_tw.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						loginToTwitter();
						/*
						 * if (userToken.length() > 0) { if
						 * (appSettings.getConnectTwitter()) {
						 * //TwSharedDialog();
						 * 
						 * } else { startActivityForResult(new Intent(
						 * ReviewOneActivity.this,
						 * TWLoginActivity.class).putExtra( "login", true),
						 * TW_SHARED);
						 * 
						 * }
						 * 
						 * } else { Intent i = new
						 * Intent(ReviewOneActivity.this,
						 * LoginReqquiredActivity.class);
						 * startActivityForResult(i, TW_SHARED); }
						 */
					}
				});

				viewHolder.text = (TextView) row
						.findViewById(R.id.tv_footerreviewone);
				viewHolder.text.setText(getResources()
						.getString(R.string.share));
				viewHolder.button = (Button) row.findViewById(R.id.add_comment);

				viewHolder.button.setTypeface(Typeface.createFromAsset(
						ReviewOneActivity.this.getAssets(),
						"fonts/ProximaNova-Bold.otf"));
				viewHolder.button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (checkLogin(NEED_LOGIN)) {
							new CheckAddCommentTask().execute();
						}
					}
				});

				break;

			case BLOCK_COMMENT:

				row = this.layoutInflater.inflate(
						R.layout.listitem_comment_one, parent, false);

				viewHolder.logo2 = (WebImageView2) row
						.findViewById(R.id.iv_avatar);

				viewHolder.name = (TextView) row
						.findViewById(R.id.tv_user_name);
				viewHolder.time = (TextView) row.findViewById(R.id.tv_time);
				viewHolder.text = (TextView) row
						.findViewById(R.id.tv_comment_text);

				row.findViewById(R.id.iv_like).setVisibility(View.GONE);
				row.findViewById(R.id.tv_translate).setVisibility(View.GONE);

				row.setTag(viewHolder);

				Comment comment = data.blocks.get(i).comments.get(0);

				viewHolder.name.setTypeface(Typeface.createFromAsset(
						ReviewOneActivity.this.getAssets(),
						"fonts/ProximaNova-Bold.otf"));
				viewHolder.name.setText(comment.user_name);

				viewHolder.time.setTypeface(Typeface.createFromAsset(
						ReviewOneActivity.this.getAssets(),
						"fonts/HelveticaNeue.otf"));
				viewHolder.time.setText(comment.created_at);

				viewHolder.text.setTypeface(Typeface.createFromAsset(
						ReviewOneActivity.this.getAssets(),
						"fonts/HelveticaNeue.otf"));
				viewHolder.text.setText(comment.text);

				if (!comment.user_picture.equals("")) {
					viewHolder.logo2.setImagesStore(mImagesStore);
					viewHolder.logo2
							.setCacheDir(appSettings.getCacheDirImage());
					viewHolder.logo2.setCircle(true);
					viewHolder.logo2.setImageURL(comment.user_picture, "123");
				}

				break;

			}

			return row;
		}

		private class ViewHolder {
			public Button button;
			public ImageView button_like;
			public ImageView button_fb;
			public ImageView button_tw;

			public TextView text;
			public TextView item_number;
			public WebImageView2 logo;
			public WebImageView2 logo2;
			public TextView name;
			public TextView time;
			public TextView item_id;
			public LinearLayout ll_img;

		}
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
				MeasureSpec.AT_MOST);
		int totalHeight = 0;
		View view = null;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			view = listAdapter.getView(i, view, listView);
			if (i == 0) {
				view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
						LayoutParams.WRAP_CONTENT));
			}
			view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += view.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == NEED_LOGIN) {
				// new CheckAddCommentTask().execute();
			} else if (requestCode == POST_COMMENT) {
				new LoadTask().execute();
			}
			if (requestCode == REQUIED_FOR_LIKE) {
				new LikeQueryTask().execute();
			}
			if (requestCode == FB_SHARED) {
				// FbSharedDialog();
			}
			if (requestCode == TW_SHARED) {
				loginToTwitter();
			}

			mCurrentSession.onActivityResult(this, requestCode, resultCode,
					data);

		}
	}

	private class LoadTaskComm extends
			QueryTask<Void, Void, BlogPostQueryAdapted> {
		@Override
		protected BlogPostQueryAdapted doInBackground(Void... params) {
			BlogPostQueryAdapted query = new BlogPostQueryAdapted(
					ReviewOneActivity.this, ID, 2);

			return query;
		}

		@Override
		protected void onPostExecute(BlogPostQueryAdapted query) {
			if (checkResult(query)) {
				data1 = query.getData();
				if (data1.blocks.size() > 0) {
					data.blocks.addAll(data1.blocks);
					empty = false;
				} else {
					empty = true;
				}

				adapter.notifyDataSetChanged();
				listView.invalidate();
				listView.smoothScrollToPosition(adapter.getCount());

			}

			super.onPostExecute(query);
		}
	}

	public class LikeQueryTask extends QueryTask<Void, Void, LikeQuery> {
		@Override
		protected LikeQuery doInBackground(Void... params) {
			LikeQuery likeQuery = new LikeQuery(appSettings, LikeQuery.REVIEW,
					ID);
			likeQuery.getResponse(WebQuery.GET);

			return likeQuery;
		}

		@Override
		protected void onPostExecute(LikeQuery query) {
			if (checkResult(query)) {
				LikeQuery.Data data = query.getData();
				if (data != null) {
					Toast.makeText(ReviewOneActivity.this, "Like was added",
							Toast.LENGTH_SHORT).show();
				}
			}

			super.onPostExecute(query);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_back:
			ReviewOneActivity.this.finish();

			break;
		/*
		 * case R.id.im_review_ok: if (userToken.length() > 0) { new
		 * LikeQueryTask().execute(); } else { Intent i = new
		 * Intent(ReviewOneActivity.this, LoginReqquiredActivity.class);
		 * startActivityForResult(i, REQUIED_FOR_LIKE); } break; case
		 * R.id.im_review_fb: if (userToken.length() > 0) { Session session =
		 * Session.getActiveSession(); if (session.getState().isOpened()) {
		 * FbSharedDialog(); }
		 * 
		 * else { startActivityForResult(new Intent(ReviewOneActivity.this,
		 * FBLoginActivity.class).putExtra("login", true), FB_SHARED); }
		 * 
		 * } else { Intent i = new Intent(ReviewOneActivity.this,
		 * LoginReqquiredActivity.class); startActivityForResult(i, FB_SHARED);
		 * }
		 * 
		 * break; case R.id.im_review_tw: if (userToken.length() > 0) { if
		 * (appSettings.getConnectTwitter()) { TwSharedDialog(); } else {
		 * startActivityForResult(new Intent(ReviewOneActivity.this,
		 * TWLoginActivity.class).putExtra("login", true), TW_SHARED);
		 * 
		 * }
		 * 
		 * } else { Intent i = new Intent(ReviewOneActivity.this,
		 * LoginReqquiredActivity.class); startActivityForResult(i, TW_SHARED);
		 * } break;
		 */

		}

	}

	private void FbSharedDialog(Session sess) {
		final Session session = sess;
		ReviewOneActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Bundle params = new Bundle();
				params.putString("name", data.title);
				String url = "http://www.drinkadvisor.com/en/reviews/showOgp/"
						+ ID + ".html";
				params.putString("description", data.shText);
				params.putString("caption", "World\'s Best Bars & Drinks Guide");
				params.putString("link", url);
				params.putString("picture", data.picture);
				// params.putString("message", "Please see ");
				WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(
						ReviewOneActivity.this, session, params))
						.setOnCompleteListener(new OnCompleteListener() {

							@Override
							public void onComplete(Bundle values,
									FacebookException error) {

							}
						}).build();
				feedDialog.show();
			}
		});

	}

	private static void storeAccessToken(long id, AccessToken accessToken) {
		// store accessToken.getToken()
		// store accessToken.getTokenSecret()
	}

	public static final String OAUTH_CALLBACK_SCHEME = "drinkadvisor";
	public static final String OAUTH_CALLBACK_HOST = "callback";
	public static final String CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://"
			+ OAUTH_CALLBACK_HOST;
	private static final String TWITTER_REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	private static final String TWITTER_AUTHORZE_URL = "https://api.twitter.com/oauth/authorize";
	private static final String TWITTER_ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
	private OAuthProvider mHttpOauthprovider;
	private CommonsHttpOAuthConsumer mHttpOauthConsumer;
	private TwitterSession mSession;
	private AccessToken mAccessToken;
	private ProgressDialog mProgressDlg;

	// private TwDialogListener mListener;

	private void postAsToast(FROM twitterPost, MESSAGE success) {
		switch (twitterPost) {
		case TWITTER_LOGIN:
			switch (success) {
			case SUCCESS:
				Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG)
						.show();
				break;
			case FAILED:
				Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
			break;
		case TWITTER_POST:
			switch (success) {
			case SUCCESS:
				Toast.makeText(this, "Posted Successfully", Toast.LENGTH_LONG)
						.show();
				break;
			case FAILED:
				Toast.makeText(this, "Posting Failed", Toast.LENGTH_LONG)
						.show();
				break;
			case DUPLICATE:
				Toast.makeText(this,
						"Posting Failed because of duplicate message...",
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
			break;
		}
	}

	private Handler mHandler = new Handler()

	{
		@Override
		public void handleMessage(Message msg) {
			mProgressDlg.dismiss();

			if (msg.what == 1) {
				if (msg.arg1 == 1) {
					postAsToast(FROM.TWITTER_LOGIN, MESSAGE.FAILED);

				} else {
					postAsToast(FROM.TWITTER_LOGIN, MESSAGE.FAILED);

				}
			} else {
				if (msg.arg1 == 1) {
					showLoginDialog((String) msg.obj);
				} else {
					try {
						// add to preferences?
					} catch (Exception e) {
						if (e.getMessage().toString().contains("duplicate")) {
							postAsToast(FROM.TWITTER_POST, MESSAGE.DUPLICATE);
						}
						e.printStackTrace();

						setResult(RESULT_CANCELED);
						finish();
					}
				}
			}
		}
	};

	private String getVerifier(String callbackUrl) {
		String verifier = "";

		try {
			callbackUrl = callbackUrl.replace("drinkadvisor", "http");

			URL url = new URL(callbackUrl);
			String query = url.getQuery();

			String array[] = query.split("&");

			for (String parameter : array) {
				String v[] = parameter.split("=");

				if (URLDecoder.decode(v[0]).equals(
						oauth.signpost.OAuth.OAUTH_VERIFIER)) {
					verifier = URLDecoder.decode(v[1]);
					break;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return verifier;
	}

	public void processToken(String callbackUrl) {
		if (callbackUrl == null) {
			setResult(RESULT_CANCELED);
			finish();
		} else {
			mProgressDlg.setMessage("Finalizing ...");
			mProgressDlg.show();

			final String verifier = getVerifier(callbackUrl);

			new Thread() {
				@Override
				public void run() {
					int what = 1;

					try {
						mHttpOauthprovider.retrieveAccessToken(
								mHttpOauthConsumer, verifier);
						mAccessToken = new AccessToken(
								mHttpOauthConsumer.getToken(),
								mHttpOauthConsumer.getTokenSecret());
						String tweet = "World\'s Best Bars & Drinks Guide. "
								+ "http://www.drinkadvisor.com/en/reviews/showOgp/"
								+ ID + ".html";

						Twitter twitter = new TwitterFactory().getInstance();
						twitter.setOAuthConsumer(
								getString(R.string.twitter_consumer_key),
								getString(R.string.twitter_consumer_secret));

						AccessToken accessToken = null;
						accessToken = new AccessToken(mAccessToken.getToken(),
								mAccessToken.getTokenSecret());
						twitter.setOAuthAccessToken(accessToken);

						try {

							Status status = twitter.updateStatus(tweet);
							System.out
									.println("Successfully updated the status to ["
											+ status.getText() + "].");
						} catch (TwitterException e) {

							e.printStackTrace();
							Log.e("error twitter", e.toString());

						}

						what = 0;
					} catch (Exception e) {
						e.printStackTrace();
					}

					mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
				}
			}.start();
		}
	}

	private void showLoginDialog(String url) {
		final TwDialogListener listener = new TwDialogListener() {
			public void onComplete(String value) {
				processToken(value);
			}

			public void onError(String value) {

				postAsToast(FROM.TWITTER_LOGIN, MESSAGE.FAILED);
			}
		};

		new TwitterDialog(this, url, listener).show();
	}

	private void loginToTwitter() {
		mProgressDlg = new ProgressDialog(this);
		mHttpOauthprovider = new DefaultOAuthProvider(TWITTER_REQUEST_URL,
				TWITTER_ACCESS_TOKEN_URL, TWITTER_AUTHORZE_URL);
		mHttpOauthConsumer = new CommonsHttpOAuthConsumer(
				getString(R.string.twitter_consumer_key),
				getString(R.string.twitter_consumer_secret));
		mProgressDlg.setMessage("Initializing ...");
		mProgressDlg.show();

		new Thread() {
			@Override
			public void run() {
				String authUrl = "";
				int what = 1;

				try {
					authUrl = mHttpOauthprovider.retrieveRequestToken(
							mHttpOauthConsumer, CALLBACK_URL);
					what = 0;
				} catch (Exception e) {
					try {
						authUrl = mHttpOauthprovider.retrieveRequestToken(
								mHttpOauthConsumer, CALLBACK_URL);
						what = 0;
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}

				mHandler.sendMessage(mHandler
						.obtainMessage(what, 1, 0, authUrl));
			}
		}.start();

	}

	private void TwSharedDialog(String token, String secret) {
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(getString(R.string.twitter_consumer_key),
				getString(R.string.twitter_consumer_secret));

		AccessToken accessToken = null;
		accessToken = new AccessToken(token, secret);
		twitter.setOAuthAccessToken(accessToken);

		try {
			String tweet = data.title + ". World\'s Best Bars & Drinks Guide. "
					+ "http://www.drinkadvisor.com/en/reviews/showOgp/" + ID
					+ ".html";
			String tweet1 = "test2";
			Status status = twitter.updateStatus(tweet);
			System.out.println("Successfully updated the status to ["
					+ status.getText() + "].");
		} catch (TwitterException e) {

			e.printStackTrace();
			Log.e("error twitter", e.toString());

		}

	}

	private GraphUser userData;
	String access_token;
	boolean login = false;
	Session mCurrentSession = null;

	@SuppressWarnings("deprecation")
	private void FbLogin() {

		List<String> permissions = new ArrayList<String>();
		permissions.add("email");
		permissions.add("user_birthday");
		permissions.add("user_about_me");
		permissions.add("user_hometown");
		permissions.add("user_website");

		mCurrentSession = new Session.Builder(getBaseContext())
				.setApplicationId(getString(R.string.app_id)).build();
		Session.setActiveSession(mCurrentSession);

		if (!mCurrentSession.isOpened()) {
			Session.OpenRequest openRequest = new Session.OpenRequest(
					ReviewOneActivity.this);
			openRequest.setDefaultAudience(SessionDefaultAudience.FRIENDS);
			openRequest.setPermissions(permissions);
			openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
			openRequest.setCallback(statusCallback);
			mCurrentSession.openForRead(openRequest);
		} else {
			Request.executeMeRequestAsync(mCurrentSession,
					new Request.GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user,
								Response response) {

							onCompleteRequest(user);
						}
					});
		}
	}

	private StatusCallback statusCallback = new StatusCallback() {
		@SuppressWarnings("deprecation")
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state == SessionState.CLOSED_LOGIN_FAILED) {

				setResult(RESULT_CANCELED);
				finish();
			} else if (session.isOpened()) {
				Request.executeMeRequestAsync(mCurrentSession,
						new Request.GraphUserCallback() {
							@Override
							public void onCompleted(GraphUser user,
									Response response) {

								onCompleteRequest(user);
							}
						});
			}
		}
	};

	private void onCompleteRequest(GraphUser user) {
		access_token = mCurrentSession.getAccessToken();
		userData = user;

		if (access_token != null) {

			FbSharedDialog(mCurrentSession);

		} else {
			setResult(RESULT_CANCELED);
			// finish();
		}
	}

}
