package com.drink.activity;

import java.io.File;
import java.util.ArrayList;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.helpers.StatisticsHelper;
import com.drink.query.CheckInQuery;
import com.drink.query.WebQuery;
import com.drink.types.Friend;

public class CheckinActivity extends BasicActivity 
{
	static final int REQUEST_FROM_CAMERA = 0;
	static final int REQUEST_IMAGE_CROP = 1;
	static final int REQUEST_FRIENDS = 2;
	
	private String ID;
	private Uri mImageUri;
	private Uri mCropedImageUri;
	ImageView mSelectedImage;
	
	EditText etComment;
	Switch swFacebook;
	Switch swTwitter;

	ArrayList<Friend> invited = new ArrayList<Friend>();
	
	public CheckinActivity() 
	{
		super(R.layout.activity_checkin);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		ID = bundle.getString("ID");
		
		etComment = (EditText) findViewById(R.id.comment);
		etComment.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		mSelectedImage = (ImageView)findViewById(R.id.checkin_image);
		
		Button btnFriends =  (Button) findViewById(R.id.btn_choose_friends);
		btnFriends.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnFriends.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					Intent intent = new Intent(CheckinActivity.this, InviteFriendsActivity.class);
					intent.putExtra("mode", InviteFriendsActivity.MODE_Checkin);
					intent.putExtra("selected", invited);
					startActivityForResult(intent, REQUEST_FRIENDS);
				}
			});
		
		TextView tvSocialTitle =  (TextView) findViewById(R.id.tvTitle);
		tvSocialTitle.setTypeface(Typeface.createFromAsset(CheckinActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		Button checkinButton = (Button) findViewById(R.id.btn_checkin);
		checkinButton.setTypeface(Typeface.createFromAsset(CheckinActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		checkinButton.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					new CheckInTask().execute();
				}
			});

		Button takePhoto = (Button) findViewById(R.id.btn_take_photo);
		takePhoto.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		takePhoto.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = getPath("tmp_cam.jpg");
					mImageUri = Uri.fromFile(f);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
					
					startActivityForResult(intent, REQUEST_FROM_CAMERA);			
				}
			});
		
		swFacebook = (Switch) findViewById(R.id.switch_facebook);
		swFacebook.setChecked(appSettings.getConnectFacebook() && appSettings.getPublishOnFacebook());
		swFacebook.setOnCheckedChangeListener(new OnCheckedChangeListener() 
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
				{
					if (!isChecked) return;
					
					checkConnectFacebook();
				}
			});

		swTwitter = (Switch) findViewById(R.id.switch_twitter);
		swTwitter.setChecked(appSettings.getConnectTwitter() && appSettings.getPublishOnTwitter());
		swTwitter.setOnCheckedChangeListener(new OnCheckedChangeListener() 
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
				{
					if (!isChecked) return;
					
					checkConnectTwitter();
				}
			});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		switch (requestCode) 
		{
		case REQUEST_FROM_CAMERA:
			if (resultCode == RESULT_OK) 
			{
				performCrop();
			}
			break;
		case REQUEST_IMAGE_CROP: 
			if (resultCode == RESULT_OK)
			{
				mSelectedImage.setVisibility(View.VISIBLE);
				mSelectedImage.setImageBitmap(BitmapFactory.decodeFile(mCropedImageUri.getEncodedPath()));
			}
			break;
		case REQUEST_FRIENDS:
			if (resultCode == RESULT_OK)
			{
				invited = intent.getParcelableArrayListExtra("selected");
			}
			break;
		case CONNECT_FACEBOOK:
			if (!appSettings.getConnectFacebook())
			{
				swFacebook.setChecked(false);
			}
			break;
		case CONNECT_TWITTER:
			if (!appSettings.getConnectTwitter())
			{
				swTwitter.setChecked(false);
			}
			break;
		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, intent);
	}

	private void performCrop() 
	{
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		cropIntent.setDataAndType(mImageUri, "image/*");
		cropIntent.putExtra("crop", "true");
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		cropIntent.putExtra("min-width", 160);
		cropIntent.putExtra("min-height", 160);
		cropIntent.putExtra("outputX", 320);
		cropIntent.putExtra("outputY", 320);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_checkin, menu);
		return true;
	}

	private class CheckInTask extends QueryTask<Void, Void, CheckInQuery> 
	{
		@Override
		protected CheckInQuery doInBackground(Void... params) 
		{
			CheckInQuery query = new CheckInQuery(appSettings, ID, swFacebook.isChecked(), swTwitter.isChecked());
			if (etComment.getText().length() > 0)
			{
				query.setComment(etComment.getText().toString());
			}
			if (mCropedImageUri != null)
			{
				query.setPicture(CommonHelper.getEncodedImage(mCropedImageUri.getEncodedPath()));
			}
			if (invited.size() > 0)
			{
				ArrayList<String> ids = new ArrayList<String>();
				
				for (int i = 0; i < invited.size(); ++i)
				{
					ids.add(invited.get(i).id);
				}
				
				query.setFriends(ids);
			}
			
			query.getResponse(WebQuery.POST);
			
			return query;
		}

		@Override
		protected void onPostExecute(CheckInQuery query) 
		{
			if (checkResult(query))
			{
				StatisticsHelper.sendCheckinEvent(CheckinActivity.this, ID);
				
				Intent intent = new Intent();
				intent.putExtra("hasPhoto", mCropedImageUri != null);
				
				setResult(RESULT_OK, intent);
				
				finish();
			}
			
			super.onPostExecute(query);
		}
	}
}
