
package com.drink.activity;

import com.drink.R;
import com.drink.imageloader.WebImageView2;
import com.drink.query.BrandQuery;
import com.drink.query.LikeQuery;
import com.drink.query.WebQuery;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class BrandActivity extends BasicActivity 
{
	private static final int LOGIN_REQUIED_FOR_LIKE = 0;

	private String ID;
	private ScrollView sw;
	
	ImageButton btnLike;
	TextView tvLikesCount;

	public BrandActivity() 
	{
		super(R.layout.activity_brand);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mCaption.setText(R.string.brands);
		
		sw = (ScrollView)findViewById(R.id.sv_main);
		sw.setVisibility(View.GONE);

		Bundle bundle = getIntent().getExtras();
		ID = String.valueOf(bundle.getInt("ID"));
		
		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.btn_like);
		btnLike = (ImageButton) relativeLayout.findViewById(R.id.button);
		btnLike.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					if (v.isSelected()) return;
					
					if (userToken.length() > 0)
					{
						new LikeTask().execute();
					}
					else 
					{
						Intent i = new Intent(BrandActivity.this, LoginReqquiredActivity.class);
						startActivityForResult(i, LOGIN_REQUIED_FOR_LIKE);					
					}
				}
			});

		new LoadTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_about_brand, menu);
		return true;
	}

	private class LoadTask extends QueryTask<Void, Void, BrandQuery> 
	{
		@Override
		protected BrandQuery doInBackground(Void... params) 
		{
			BrandQuery brandQuery = new BrandQuery(BrandActivity.this, ID);
			
			return brandQuery;
		}

		@Override
		protected void onPostExecute(BrandQuery query) 
		{
			if (checkResult(query))
			{
				BrandQuery.Data data = query.getData();
				
				WebImageView2 picture = (WebImageView2) findViewById(R.id.picture);
				picture.setImagesStore(mImagesStore);
				picture.setCacheDir(appSettings.getCacheDirImage());
				picture.setImageURL(data.picture, "123");

				TextView name = (TextView) findViewById(R.id.title);
				name.setTypeface(Typeface.createFromAsset(BrandActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				name.setText(data.name);
				
				TextView tvText = (TextView) findViewById(R.id.tv_text);
				tvText.setTypeface(Typeface.createFromAsset(BrandActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				tvText.setText(Html.fromHtml(data.description));
				
				LinearLayout llReadMore = (LinearLayout) findViewById(R.id.ll_read_more);
				llReadMore.setVisibility(View.GONE);
				
				btnLike.setSelected(data.state_like);
				
				TextView buttontext = (TextView) findViewById(R.id.text);
				buttontext.setTypeface(Typeface.createFromAsset(BrandActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
	
				RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.btn_like);
				tvLikesCount = (TextView) relativeLayout.findViewById(R.id.counter);
				tvLikesCount.setTypeface(Typeface.createFromAsset(BrandActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				tvLikesCount.setText(Integer.toString(data.like));
				
				sw.setVisibility(View.VISIBLE);
			}

			super.onPostExecute(query);
		}
	}

	private class LikeTask extends QueryTask<Void, Void, LikeQuery> 
	{
		@Override
		protected LikeQuery doInBackground(Void... params) 
		{
			LikeQuery likeQuery = new LikeQuery(appSettings, LikeQuery.BRANDS, ID);
			likeQuery.getResponse(WebQuery.GET);

			return likeQuery;
		}

		@Override
		protected void onPostExecute(LikeQuery query) 
		{
			if (checkResult(query))
			{
				tvLikesCount.setText(String.valueOf(query.getData().likes_count));

				btnLike.setSelected(true);
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
			if (requestCode == LOGIN_REQUIED_FOR_LIKE) 
			{
				new LikeTask().execute();
			}
		}
	}
}
