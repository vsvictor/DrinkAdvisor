package com.drink.activity;

import com.drink.R;
import com.drink.imageloader.WebImageView2;
import com.drink.query.AboutDrinkQuery;
import com.drink.query.LikeQuery;
import com.drink.query.WebQuery;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannedString;
import android.view.Menu;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class DrinkActivity extends BasicActivity 
{
	private static final int LOGIN_REQUIED_FOR_LIKE = 0;
	
	private String ID;

	AboutDrinkQuery.Data info = null;
	
	private ScrollView sw;
	
	private TextView tvTitle;
	private WebImageView2 wivPicture;
	private TextView tvLikesCount;
	private TextView tvFact1Text;
	private TextView tvFact2Text;
	private TextView tvFact3Text;
	private TextView tvHistoryText;
	private TextView tvWithDrinkText;
	private TextView tvHowDoText;
	
	private LinearLayout llFactsHeading;
	private LinearLayout llFact1Text;
	private LinearLayout llFact2Text;
	private LinearLayout llFact3Text;
	private LinearLayout llHistory;
	private LinearLayout llHowDrink;
	private LinearLayout llHowMake;
	
	private RelativeLayout rlBrands;
	
	private ImageButton btnLike;
	private ImageButton btnBrands;

	public DrinkActivity() 
	{
		super(R.layout.activity_drink);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mCaption.setText(getString(R.string.drinks));
		
		sw = (ScrollView) findViewById(R.id.sv_drink);
		sw.setVisibility(View.GONE);

		ID = getIntent().getStringExtra("ID");
		
		tvTitle = (TextView) findViewById(R.id.title);
		tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		wivPicture = (WebImageView2) findViewById(R.id.picture);
		wivPicture.setImagesStore(mImagesStore);
		wivPicture.setCacheDir(appSettings.getCacheDirImage());

		// like button
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
					new LikeQueryTask().execute();
				}
				else 
				{
					Intent i = new Intent(DrinkActivity.this, LoginReqquiredActivity.class);
					startActivityForResult(i, LOGIN_REQUIED_FOR_LIKE);				
				}
			}
		});

		TextView textView = (TextView) relativeLayout.findViewById(R.id.text);
		textView.setTypeface(Typeface.createFromAsset(DrinkActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		tvLikesCount = (TextView) relativeLayout.findViewById(R.id.counter);
		tvLikesCount.setTypeface(Typeface.createFromAsset(DrinkActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

		// facts heading
		llFactsHeading = (LinearLayout) findViewById(R.id.facts_heading);
		llFactsHeading.setVisibility(View.GONE);
		
		TextView tvFactsTitle = (TextView) llFactsHeading.findViewById(R.id.tv_title);
		tvFactsTitle.setTypeface(Typeface.createFromAsset(DrinkActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));
		tvFactsTitle.setText(R.string.drink_text_interesting_facts);

		// fact #1
		llFact1Text = (LinearLayout) findViewById(R.id.fact_1);
		llFact1Text.setVisibility(View.GONE);

		TextView tvFact1Number = (TextView) llFact1Text.findViewById(R.id.tv_number);
		tvFact1Number.setTypeface(Typeface.createFromAsset(DrinkActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));
		tvFact1Number.setText("1");
		
		tvFact1Text = (TextView) llFact1Text.findViewById(R.id.tv_text);
		tvFact1Text.setTypeface(Typeface.createFromAsset(DrinkActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

		// fact #2
		llFact2Text = (LinearLayout) findViewById(R.id.fact_2);
		llFact2Text.setVisibility(View.GONE);

		TextView tvFact2Number = (TextView) llFact2Text.findViewById(R.id.tv_number);
		tvFact2Number.setTypeface(Typeface.createFromAsset(DrinkActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));
		tvFact2Number.setText("2");
		
		tvFact2Text = (TextView) llFact2Text.findViewById(R.id.tv_text);
		tvFact2Text.setTypeface(Typeface.createFromAsset(DrinkActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

		// fact #3
		llFact3Text = (LinearLayout) findViewById(R.id.fact_3);
		llFact3Text.setVisibility(View.GONE);

		TextView tvFact3Number = (TextView) llFact3Text.findViewById(R.id.tv_number);
		tvFact3Number.setTypeface(Typeface.createFromAsset(DrinkActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));
		tvFact3Number.setText("3");
		
		tvFact3Text = (TextView) llFact3Text.findViewById(R.id.tv_text);
		tvFact3Text.setTypeface(Typeface.createFromAsset(DrinkActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

        // history
		llHistory = (LinearLayout) findViewById(R.id.history);
		llHistory.setVisibility(View.GONE);
		
		TextView tvHistoryTitle = (TextView) llHistory.findViewById(R.id.tv_title);
		tvHistoryTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		tvHistoryTitle.setText(R.string.drink_text_history);
		
		tvHistoryText = (TextView) llHistory.findViewById(R.id.tv_text);
		tvHistoryText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		TextView tvReadMore = (TextView) llHistory.findViewById(R.id.tv_read_more);
		tvReadMore.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));

		ViewTreeObserver vto = tvHistoryText.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
        {
            @Override
            public void onGlobalLayout() 
            {
            	if (info == null) return;
            	
				ViewTreeObserver obs = tvHistoryText.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);

            	if (info.history.length() <= 10) return;
            	
            	LinearLayout llReadMore = (LinearLayout) llHistory.findViewById(R.id.ll_read_more);

            	SpannedString str = (SpannedString) tvHistoryText.getText().subSequence(0, tvHistoryText.getLayout().getLineEnd(3) - 1);
				if (tvHistoryText.getText().length() > str.length()) 
				{
					tvHistoryText.setText(str);
					llHistory.setOnClickListener(new OnClickListener() 
						{
							@Override
							public void onClick(View arg0) 
							{
								Intent intent = new Intent();
								intent.putExtra("caption", DrinkActivity.this.getString(R.string.drinks));
								intent.putExtra("subject", info.name);
								intent.putExtra("text", info.history);
								intent.putExtra("title", DrinkActivity.this.getString(R.string.drink_text_history));
								intent.putExtra("image", info.picture.get(0));
								intent.setClass(DrinkActivity.this, ExtraTextActivity.class);

								startActivity(intent);
							}
						});
					
					llReadMore.setVisibility(View.VISIBLE);
				}
				else
				{
					llReadMore.setVisibility(View.GONE);
				}
            }
        });				

        // how drink
		llHowDrink = (LinearLayout) findViewById(R.id.how_drink);
		llHowDrink.setVisibility(View.GONE);

		TextView tvWithDrinkTitle = (TextView) llHowDrink.findViewById(R.id.tv_title);
		tvWithDrinkTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		tvWithDrinkTitle.setText(R.string.drink_text_how_drink);

		tvWithDrinkText = (TextView) llHowDrink.findViewById(R.id.tv_text);
		tvWithDrinkText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		tvReadMore = (TextView) llHowDrink.findViewById(R.id.tv_read_more);
		tvReadMore.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));

		vto = tvWithDrinkText.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
	        {
	            @Override
	            public void onGlobalLayout() 
	            {
	            	if (info == null) return;
	            	
					ViewTreeObserver obs = tvWithDrinkText.getViewTreeObserver();
	                obs.removeGlobalOnLayoutListener(this);
	                
	            	if (info.with_drink.length() <= 10) return;
	            	
	            	LinearLayout llReadMore = (LinearLayout) llHowDrink.findViewById(R.id.ll_read_more);

	            	SpannedString str = (SpannedString) tvWithDrinkText.getText().subSequence(0, tvWithDrinkText.getLayout().getLineEnd(3) - 1);
					if (tvWithDrinkText.getText().length() > str.length()) 
					{
						tvWithDrinkText.setText(str);
						llHowDrink.setOnClickListener(new OnClickListener() 
							{
								@Override
								public void onClick(View arg0) 
								{
									Intent intent = new Intent();
									intent.putExtra("caption", DrinkActivity.this.getString(R.string.drinks));
									intent.putExtra("subject", info.name);
									intent.putExtra("text", info.with_drink);
									intent.putExtra("title", DrinkActivity.this.getString(R.string.drink_text_how_drink));
									intent.putExtra("image", info.picture.get(0));
									intent.setClass(DrinkActivity.this, ExtraTextActivity.class);

									startActivity(intent);
								}
							});
						
						llReadMore.setVisibility(View.VISIBLE);
					}
					else
					{
						llReadMore.setVisibility(View.GONE);
					}
	            }
	        });

        // how make
		llHowMake = (LinearLayout) findViewById(R.id.how_made);
		llHowMake.setVisibility(View.GONE);

		TextView tvHowDoTitle = (TextView) llHowMake.findViewById(R.id.tv_title);
		tvHowDoTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		tvHowDoTitle.setText(R.string.drink_text_how_made);

		tvHowDoText = (TextView) llHowMake.findViewById(R.id.tv_text);
		tvHowDoText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		tvReadMore = (TextView) llHowMake.findViewById(R.id.tv_read_more);
		tvReadMore.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		vto = tvHowDoText.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
        {
            @Override
            public void onGlobalLayout() 
            {
            	if (info == null) return;
            	
				ViewTreeObserver obs = tvHowDoText.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                
            	if (info.how_do.length() <= 10) return;
            	
            	LinearLayout llReadMore = (LinearLayout) llHowMake.findViewById(R.id.ll_read_more);

            	SpannedString str = (SpannedString) tvHowDoText.getText().subSequence(0, tvHowDoText.getLayout().getLineEnd(3) - 1);
				if (tvHowDoText.getText().length() > str.length()) 
				{
					tvHowDoText.setText(str);
					llHowMake.setOnClickListener(new OnClickListener() 
						{
							@Override
							public void onClick(View arg0) 
							{
								Intent intent = new Intent();
								intent.putExtra("caption", DrinkActivity.this.getString(R.string.drinks));
								intent.putExtra("subject", info.name);
								intent.putExtra("text", info.how_do);
								intent.putExtra("title", DrinkActivity.this.getString(R.string.drink_text_how_made));
								intent.putExtra("image", info.picture.get(0));
								intent.setClass(DrinkActivity.this, ExtraTextActivity.class);

								startActivity(intent);
							}
						});
					
					llReadMore.setVisibility(View.VISIBLE);
				}
				else
				{
					llReadMore.setVisibility(View.GONE);
				}
            }
        });

        // brands button
		rlBrands = (RelativeLayout) findViewById(R.id.btn_brands);
		
		btnBrands = (ImageButton) rlBrands.findViewById(R.id.button);
		btnBrands.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent();
					intent.putExtra("ID", ID);
					intent.putExtra("drink_name", info.name);
					intent.setClass(DrinkActivity.this, BrandsActivity.class);
					
					startActivity(intent);
				}
			});
		
		textView = (TextView) rlBrands.findViewById(R.id.text);
		textView.setTypeface(Typeface.createFromAsset(DrinkActivity.this.getAssets(), "fonts/ProximaNova-Light.otf"));
		textView.setText(getString(R.string.drink_btn_brands));
		
		new LoadTask().execute();
	}

	private void updateData()
	{
		tvTitle.setText(info.name);
		wivPicture.setImageURL(info.picture.get(0), "120");

		btnLike.setSelected(info.like);
		tvLikesCount.setText(String.valueOf(info.like_count));

		// facts
		if (info.facts.size() > 0)
		{
			llFactsHeading.setVisibility(View.VISIBLE);
			
			llFact1Text.setVisibility(View.VISIBLE);
			tvFact1Text.setText(info.facts.get(0));
			
			if (info.facts.size() > 1)
			{
				llFact2Text.setVisibility(View.VISIBLE);
				tvFact2Text.setText(info.facts.get(1));
				
				if (info.facts.size() > 2)
				{
					llFact3Text.setVisibility(View.VISIBLE);
					tvFact3Text.setText(info.facts.get(2));
				}
				else
				{
					llFact3Text.setVisibility(View.GONE);
				}
			}
			else
			{
				llFact2Text.setVisibility(View.GONE);
				llFact3Text.setVisibility(View.GONE);
			}
		}
		else
		{
			llFactsHeading.setVisibility(View.GONE);
			llFact1Text.setVisibility(View.GONE);
			llFact2Text.setVisibility(View.GONE);
			llFact3Text.setVisibility(View.GONE);
		}

		// history
		if (info.history.length() > 10)
		{
			tvHistoryText.setText(Html.fromHtml(info.history));
			
			llHistory.setVisibility(View.VISIBLE);
		}
		else
		{
			llHistory.setVisibility(View.GONE);
		}

		// with drink
		if (info.with_drink.length() > 10)
		{
			tvWithDrinkText.setText(Html.fromHtml(info.with_drink));
			
			llHowDrink.setVisibility(View.VISIBLE);
		}
		else
		{
			llHowDrink.setVisibility(View.GONE);
		}

		// how do
		if (info.how_do.length() > 10)
		{
			tvHowDoText.setText(Html.fromHtml(info.how_do));
			
			llHowMake.setVisibility(View.VISIBLE);
		}
		else
		{
			llHowMake.setVisibility(View.GONE);
		}
		
		rlBrands.setVisibility(info.brands ? View.VISIBLE : View.GONE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_about_drink, menu);
		
		return true;
	}

	private class LoadTask extends QueryTask<Void, Void, AboutDrinkQuery> 
	{
		@Override
		protected AboutDrinkQuery doInBackground(Void... params) 
		{
			AboutDrinkQuery drinksQuery = new AboutDrinkQuery(appSettings, ID, 25, 0);
			drinksQuery.getResponse(WebQuery.GET);

			return drinksQuery;
		}

		@Override
		protected void onPostExecute(AboutDrinkQuery query) 
		{
			if (checkResult(query))
			{
				info = query.getData();
				
				updateData();

				sw.setVisibility(View.VISIBLE);
			}
			
			super.onPostExecute(query);
		}
	}

	public class LikeQueryTask extends QueryTask<Void, Void, LikeQuery> 
	{
		@Override
		protected LikeQuery doInBackground(Void... params) 
		{
			LikeQuery likeQuery = new LikeQuery(appSettings, LikeQuery.DRINKS, ID);
			likeQuery.getResponse(WebQuery.GET);

			return likeQuery;
		}

		@Override
		protected void onPostExecute(LikeQuery query) 
		{
			if (checkResult(query))
			{
				LikeQuery.Data data = query.getData();
				if (data != null)
				{
					btnLike.setSelected(true);
					tvLikesCount.setText(String.valueOf(data.likes_count));
				}
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
				new LikeQueryTask().execute();
			}
		}
	}
}
