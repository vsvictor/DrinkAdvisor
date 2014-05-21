package com.drink.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.imageloader.WebImageView;
import com.drink.imageloader.WebImageView2;
import com.drink.query.UpdateUserDataQuery;
import com.drink.query.UserDataQuery;
import com.drink.query.UserDataQuery.Data;
import com.drink.query.WebQuery;

public class ProfileActivity extends BasicActivity 
{
	static final int DATE_DIALOG_ID = 999;
	
	private Uri mImageUri;
	private Uri mCropedImageUri;

	EditText mUserName;
	EditText mUserBirhday;
	EditText mUserMale;
	
	Date	mBirthday;
	
	TextView mFacebook;
	TextView mTwitter;

	LinearLayout mInfo;
	Switch mPrivate;
	Button	mExit;
	
	ImageButton mEmailLikes;
	ImageButton mPushLikes;
	ImageButton mEmailComments;
	ImageButton mPushComments;
	ImageButton mEmailPosts;
	ImageButton mPushPosts;
	ImageButton mEmailCheckins;
	ImageButton mPushCheckins;
	
	static final int REQUEST_FROM_CAMERA = 1;
	static final int REQUEST_FROM_GALLERY = 2;
	static final int REQUEST_IMAGE_CROP = 3;

	WebImageView2 mProfile;
	OnClickListener onUploadClickListener;
	
	boolean needUpdate = false;

	public ProfileActivity() 
	{
		super(R.layout.activity_profile);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mInfo = (LinearLayout) findViewById(R.id.ll_info);
		mInfo.setVisibility(View.GONE);
		
		mPrivate = (Switch) findViewById(R.id.sw_private);
		mPrivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() 
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
				{
					new UpdateTask().execute(UpdateUserDataQuery.PRIVATE_MODE);
				}
			});
		
		// LIKES notification
		LinearLayout llNotification = (LinearLayout) findViewById(R.id.notification_likes);
		
		TextView tvText = (TextView) llNotification.findViewById(R.id.tv_text);
		tvText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tvText.setText(R.string.notification_likes);
		
		mEmailLikes = (ImageButton) llNotification.findViewById(R.id.btn_email);
		mEmailLikes.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				mEmailLikes.setSelected(!mEmailLikes.isSelected());
				new UpdateTask().execute(UpdateUserDataQuery.LIKES_TO_EMAIL);
			}
		});
		mPushLikes = (ImageButton) llNotification.findViewById(R.id.btn_notification);
		mPushLikes.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				mPushLikes.setSelected(!mPushLikes.isSelected());
				new UpdateTask().execute(UpdateUserDataQuery.LIKES_TO_PUSH);
			}
		});
		
		// COMMENTS notification
		llNotification = (LinearLayout) findViewById(R.id.notification_comments);
		
		tvText = (TextView) llNotification.findViewById(R.id.tv_text);
		tvText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tvText.setText(R.string.notification_comments);
		
		mEmailComments = (ImageButton) llNotification.findViewById(R.id.btn_email);
		mEmailComments.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				mEmailComments.setSelected(!mEmailComments.isSelected());
				new UpdateTask().execute(UpdateUserDataQuery.COMMENTS_TO_EMAIL);
			}
		});
		mPushComments = (ImageButton) llNotification.findViewById(R.id.btn_notification);
		mPushComments.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				mPushComments.setSelected(!mPushComments.isSelected());
				new UpdateTask().execute(UpdateUserDataQuery.COMMENTS_TO_PUSH);
			}
		});
		
		// POSTS notification
		llNotification = (LinearLayout) findViewById(R.id.notification_posts);
		
		tvText = (TextView) llNotification.findViewById(R.id.tv_text);
		tvText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tvText.setText(R.string.notification_posts);
		
		mEmailPosts = (ImageButton) llNotification.findViewById(R.id.btn_email);
		mEmailPosts.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				mEmailPosts.setSelected(!mEmailPosts.isSelected());
				new UpdateTask().execute(UpdateUserDataQuery.POSTS_TO_EMAIL);
			}
		});
		mPushPosts = (ImageButton) llNotification.findViewById(R.id.btn_notification);
		mPushPosts.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				mPushPosts.setSelected(!mPushPosts.isSelected());
				new UpdateTask().execute(UpdateUserDataQuery.POSTS_TO_PUSH);
			}
		});
		
		// CHECK-INS notification
		llNotification = (LinearLayout) findViewById(R.id.notification_checkins);
		
		tvText = (TextView) llNotification.findViewById(R.id.tv_text);
		tvText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tvText.setText(R.string.notification_checkins);
		
		mEmailCheckins = (ImageButton) llNotification.findViewById(R.id.btn_email);
		mEmailCheckins.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				mEmailCheckins.setSelected(!mEmailCheckins.isSelected());
				new UpdateTask().execute(UpdateUserDataQuery.CHECKINS_TO_EMAIL);
			}
		});
		mPushCheckins = (ImageButton) llNotification.findViewById(R.id.btn_notification);
		mPushCheckins.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				mPushCheckins.setSelected(!mPushCheckins.isSelected());
				new UpdateTask().execute(UpdateUserDataQuery.CHECHINS_TO_PUSH);
			}
		});

		mUserName = (EditText) findViewById(R.id.et_user_name);
		mUserName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() 
		{
	        @Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
	        {
	            if (actionId == EditorInfo.IME_ACTION_DONE) 
	            {
	            	new UpdateTask().execute(UpdateUserDataQuery.USER_NAME);

	                return true;
	            }
	            
	            return false;
	        }
	    });
		
		mUserBirhday = (EditText) findViewById(R.id.et_user_birhday);
		mUserBirhday.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mUserBirhday.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					showDialog(DATE_DIALOG_ID);
				}
			});

		mUserMale = (EditText) findViewById(R.id.et_user_male);
		mUserMale.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mUserMale.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					showMaleDialog(ProfileActivity.this);
				}
			});
		
		mProfile = (WebImageView2) findViewById(R.id.photo_image);
		mProfile.setCircle(true);
		mProfile.setImagesStore(mImagesStore);
		mProfile.setCacheDir(appSettings.getCacheDirImage());
		mProfile.setImageResource(R.drawable.profile_camera);

		mFacebook = (TextView) findViewById(R.id.tv_facebook);
		mFacebook.setTypeface(Typeface.createFromAsset(ProfileActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		mFacebook.setSelected(appSettings.getConnectFacebook() && appSettings.getPublishOnFacebook());
		if (mFacebook.isSelected())
		{
			mFacebook.setText(getString(R.string.connected_to_facebook));
		}
		mFacebook.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					mFacebook.setSelected(!mFacebook.isSelected());
	
					if (mFacebook.isSelected())
					{
						mFacebook.setText(ProfileActivity.this.getString(R.string.connected_to_facebook));
						checkConnectFacebook();
					}
					else
					{
						mFacebook.setText(ProfileActivity.this.getString(R.string.connect_to_facebook));
					}
					
					new UpdateTask().execute(UpdateUserDataQuery.PUBLISH_ON_FACEBOOK);
				}
			});
		
		mTwitter = (TextView) findViewById(R.id.tv_twitter);
		mTwitter.setTypeface(Typeface.createFromAsset(ProfileActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		mTwitter.setSelected(appSettings.getConnectTwitter() && appSettings.getPublishOnTwitter());
		if (mTwitter.isSelected())
		{
			mTwitter.setText(getString(R.string.connected_to_twitter));
		}
		mTwitter.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					mTwitter.setSelected(!mTwitter.isSelected());
					
					if (mTwitter.isSelected())
					{
						mTwitter.setText(ProfileActivity.this.getString(R.string.connected_to_twitter));
						checkConnectTwitter();
					}
					else
					{
						mTwitter.setText(ProfileActivity.this.getString(R.string.connect_to_twitter));
					}
					
					new UpdateTask().execute(UpdateUserDataQuery.PUBLISH_ON_TWITTER);
				}
			});

		mExit = (Button) findViewById(R.id.btn_exit);
		mExit.setTypeface(Typeface.createFromAsset(ProfileActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		mExit.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				appSettings.clearAuthData();
				CommonHelper.clearUserToken(ProfileActivity.this);
				
				startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
				finish();
			}
		});
		
		findViewById(R.id.llContent).requestFocus();

		new LoadUserTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_profile, menu);
		return true;
	}

	private class LoadUserTask extends QueryTask<Void, Void, UserDataQuery> 
	{
		@Override
		protected UserDataQuery doInBackground(Void... params) 
		{
			UserDataQuery query = new UserDataQuery(appSettings);
			query.getResponse(WebQuery.GET);
			
			return query;
		}

		@Override
		protected void onPostExecute(UserDataQuery query) 
		{
			if (checkResult(query))
			{
				Data data = query.getData();
				if (data != null) 
				{
					mUserName.setText(data.name);
					mBirthday = data.date_birth;
					mUserBirhday.setText(DateFormat.getDateFormat(ProfileActivity.this).format(mBirthday));
					mProfile.setImageURL(data.avatar, "123");

					mUserMale.setText(R.string.sex_male);
					if (data.sex.equals("f"))
					{
						mUserMale.setText(R.string.sex_female);
					}
					
					mFacebook.setSelected(data.connectFacebook && data.publishFacebook);
					mTwitter.setSelected(data.connectTwitter && data.publishTwitter);
				
					mPrivate.setChecked(data.isPrivate);
					
					mEmailLikes.setSelected(data.notifyEmailLikes);
					mPushLikes.setSelected(data.notifyPushLikes);
					mEmailComments.setSelected(data.notifyEmailComments);
					mPushComments.setSelected(data.notifyPushComments);
					mEmailPosts.setSelected(data.notifyEmailPosts);
					mPushPosts.setSelected(data.notifyPushPosts);
					mEmailCheckins.setSelected(data.notifyEmailCheckins);
					mPushCheckins.setSelected(data.notifyPushCheckins);
					
					mInfo.setVisibility(View.VISIBLE);
				}
			}
			
			super.onPostExecute(query);
		}
	}

	private class UpdateTask extends QueryTask<Integer, Void, UpdateUserDataQuery> 
	{
		private String avatar;
		
		@Override
		protected UpdateUserDataQuery doInBackground(Integer... params) 
		{
			UpdateUserDataQuery query = null;
			
			switch (params[0])
			{
			case UpdateUserDataQuery.USER_NAME:
				query = new UpdateUserDataQuery(appSettings, params[0], mUserName.getText().toString());
				break;
			case UpdateUserDataQuery.USER_BIRTHDAY:
				query = new UpdateUserDataQuery(appSettings, params[0], new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mBirthday));
				break;
			case UpdateUserDataQuery.USER_SEX:
				query = new UpdateUserDataQuery(appSettings, params[0], (mUserMale.getText().toString().compareTo(ProfileActivity.this.getString(R.string.sex_male)) == 0) ? "m" : "f");
				break;
			case UpdateUserDataQuery.USER_AVATAR:
				mImagesStore.removeImage(mProfile.getURL());
				query = new UpdateUserDataQuery(appSettings, params[0], (mCropedImageUri != null ? CommonHelper.getEncodedImage(mCropedImageUri.getEncodedPath()) : null));
				break;
			case UpdateUserDataQuery.PUBLISH_ON_FACEBOOK:
				query = new UpdateUserDataQuery(appSettings, params[0], (mFacebook.isSelected() ? "1" : "0"));
				break;
			case UpdateUserDataQuery.PUBLISH_ON_TWITTER:
				query = new UpdateUserDataQuery(appSettings, params[0], (mTwitter.isSelected() ? "1" : "0"));
				break;
			case UpdateUserDataQuery.PRIVATE_MODE:
				query = new UpdateUserDataQuery(appSettings, params[0], (mPrivate.isChecked() ? "1" : "0"));
				break;
			case UpdateUserDataQuery.LIKES_TO_EMAIL:
				query = new UpdateUserDataQuery(appSettings, params[0], (mEmailLikes.isSelected() ? "1" : "0"));
				break;
			case UpdateUserDataQuery.LIKES_TO_PUSH:
				query = new UpdateUserDataQuery(appSettings, params[0], (mPushLikes.isSelected() ? "1" : "0"));
				break;
			case UpdateUserDataQuery.COMMENTS_TO_EMAIL:
				query = new UpdateUserDataQuery(appSettings, params[0], (mEmailComments.isSelected() ? "1" : "0"));
				break;
			case UpdateUserDataQuery.COMMENTS_TO_PUSH:
				query = new UpdateUserDataQuery(appSettings, params[0], (mPushComments.isSelected() ? "1" : "0"));
				break;
			case UpdateUserDataQuery.POSTS_TO_EMAIL:
				query = new UpdateUserDataQuery(appSettings, params[0], (mEmailPosts.isSelected() ? "1" : "0"));
				break;
			case UpdateUserDataQuery.POSTS_TO_PUSH:
				query = new UpdateUserDataQuery(appSettings, params[0], (mPushPosts.isSelected() ? "1" : "0"));
				break;
			case UpdateUserDataQuery.CHECKINS_TO_EMAIL:
				query = new UpdateUserDataQuery(appSettings, params[0], (mEmailCheckins.isSelected() ? "1" : "0"));
				break;
			case UpdateUserDataQuery.CHECHINS_TO_PUSH:
				query = new UpdateUserDataQuery(appSettings, params[0], (mPushCheckins.isSelected() ? "1" : "0"));
				break;
			}

			query.getResponse(WebQuery.POST);
			
			return query;
		}

		@Override
		protected void onPostExecute(UpdateUserDataQuery query) 
		{
			if (checkResult(query))
			{
				setResult(RESULT_OK, new Intent().putExtra("need_update", needUpdate));
			}
			
			super.onPostExecute(query);
		}
	}

	private void startCamera() 
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = getPath("tmp_cam.jpg");
		mImageUri = Uri.fromFile(f);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
		startActivityForResult(intent, REQUEST_FROM_CAMERA);
	}

	private void performCrop() 
	{
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		cropIntent.setDataAndType(mImageUri, "image/*");
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		cropIntent.putExtra("min-width", 160);
		cropIntent.putExtra("min-height", 160);
		cropIntent.putExtra("outputX", 160);
		cropIntent.putExtra("outputY", 160);
		cropIntent.putExtra("scale", true);
		cropIntent.putExtra("return-data", false);
		mCropedImageUri = Uri.fromFile(getPath("tmp_crop.jpg"));
		cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCropedImageUri);
		startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
	}

	private File getPath(String file) 
	{
		String SD_CARD_TEMP_DIR = Environment.getExternalStorageDirectory()	+ File.separator + "DrinkAdvisor" + File.separator + file;
		File f = new File(SD_CARD_TEMP_DIR);
		try 
		{
			if (f.exists() == false) 
			{
				f.getParentFile().mkdirs();
				f.createNewFile();
			}
		} 
		catch (Exception e) {}
		
		return f;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) 
		{
		case REQUEST_FROM_CAMERA: 
		{
			if (resultCode == RESULT_OK) 
			{
				performCrop();
			}
			break;
		}
		case REQUEST_FROM_GALLERY: 
		{
			if (resultCode == RESULT_OK) 
			{
				mImageUri = data.getData();
				performCrop();
			}
			break;
		}
		case REQUEST_IMAGE_CROP: 
		{
			if (resultCode == RESULT_OK)
			{
				Bitmap bitmap = BitmapFactory.decodeFile(mCropedImageUri.getEncodedPath());
				mProfile.setImageBitmap(bitmap);
				
				needUpdate = true;
			}
			break;
		}
		case CONNECT_FACEBOOK:
			if (!appSettings.getConnectFacebook())
			{
				mFacebook.setSelected(false);
				mFacebook.setText(ProfileActivity.this.getString(R.string.connect_to_facebook));
				
				new UpdateTask().execute(UpdateUserDataQuery.PUBLISH_ON_FACEBOOK);
			}
			break;
		case CONNECT_TWITTER:
			if (!appSettings.getConnectTwitter())
			{
				mTwitter.setSelected(false);
				mTwitter.setText(ProfileActivity.this.getString(R.string.connect_to_twitter));
				
				new UpdateTask().execute(UpdateUserDataQuery.PUBLISH_ON_TWITTER);
			}
			break;
		default:
			break;
		}
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() 
	{
		@Override
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) 
		{
			final Calendar c = Calendar.getInstance();
			c.set(selectedYear, selectedMonth, selectedDay);

			if (CommonHelper.checkValidAge(c.getTime()))
			{
				mBirthday = c.getTime();
				mUserBirhday.setText(android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(mBirthday));
				
				new UpdateTask().execute(UpdateUserDataQuery.USER_BIRTHDAY);
			}
			else
			{
				showErrorBox(getString(R.string.reg_birth_invalid), false);
			}
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		switch (id) 
		{
		case DATE_DIALOG_ID: 
			{
				Calendar c = Calendar.getInstance();
				c.setTime(mBirthday);
				
				return new DatePickerDialog(ProfileActivity.this, datePickerListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			}
		}
		
		return null;
	}

    public void showMaleDialog(final Context context) 
    {
        final Dialog dialog = new Dialog(context);
        dialog.setTitle(context.getString(R.string.select_male));

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        
        Button b1 = new Button(context);
        b1.setText(context.getString(R.string.sex_male));
        b1.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	mUserMale.setText(R.string.sex_male);
            	new UpdateTask().execute(UpdateUserDataQuery.USER_SEX);
            	
                dialog.dismiss();
            }
        });        
        ll.addView(b1);

        Button b2 = new Button(context);
        b2.setText(context.getString(R.string.sex_female));
        b2.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	mUserMale.setText(R.string.sex_female);
            	new UpdateTask().execute(UpdateUserDataQuery.USER_SEX);
            	
                dialog.dismiss();
            }
        });        
        ll.addView(b2);

        dialog.setContentView(ll);        
        dialog.show();        
    }
}
