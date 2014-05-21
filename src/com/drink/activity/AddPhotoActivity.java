package com.drink.activity;

import java.io.File;
import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.query.CheckInQuery;
import com.drink.query.WebQuery;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AddPhotoActivity extends BasicActivity 
{
	static final int REQUEST_FROM_CAMERA = 0;
	static final int REQUEST_IMAGE_CROP = 1;

	String mBarId;
	private Uri mImageUri;
	private Uri mCropedImageUri;
	ImageView mPhoto;
	Button takePhoto;
	TextView comment;
	
	public AddPhotoActivity() 
	{
		super(R.layout.activity_add_photo);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mCaption.setText(R.string.add_photo);
		
		comment = (TextView) findViewById(R.id.comment);
		comment.setTypeface(Typeface.createFromAsset(AddPhotoActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		mPhoto = (ImageView)findViewById(R.id.photo);

		mBarId = getIntent().getExtras().getString("bar_id");

		takePhoto = (Button) findViewById(R.id.btn_take_photo);
		takePhoto.setTypeface(Typeface.createFromAsset(AddPhotoActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		takePhoto.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					takePhoto();
				}
			});
		
		Button sendButton = (Button) findViewById(R.id.btn_send);
		sendButton.setTypeface(Typeface.createFromAsset(AddPhotoActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
		sendButton.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					new LoadTask().execute();
				}
			});
		
		takePhoto();
	}

	private void takePhoto()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mImageUri = Uri.fromFile(getPath("tmp_cam.jpg"));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
	
		startActivityForResult(intent, REQUEST_FROM_CAMERA);			
	}
	
	private class LoadTask extends QueryTask<Void, Void, CheckInQuery> 
	{
		@Override
		protected CheckInQuery doInBackground(Void... params) 
		{
			CheckInQuery query = new CheckInQuery(appSettings, mBarId, false, false);
			query.setComment(comment.getText().toString());
			if (mCropedImageUri != null)
			{
				query.setPicture(CommonHelper.getEncodedImage(mCropedImageUri.getEncodedPath()));
			}
			query.getResponse(WebQuery.POST);
			
			return query;
		}

		@Override
		protected void onPostExecute(CheckInQuery query) 
		{
			if (checkResult(query))
			{
				Intent intent = new Intent();
				intent.putExtra("hasPhoto", mCropedImageUri != null);
				
				setResult(RESULT_OK, intent);

				finish();
			}
			
			super.onPostExecute(query);
		}
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
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
				mPhoto.setVisibility(View.VISIBLE);
				mPhoto.setImageBitmap(BitmapFactory.decodeFile(mCropedImageUri.getEncodedPath()));
				
				takePhoto.setText(R.string._change_photo);
			}
			break;
		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
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
}
