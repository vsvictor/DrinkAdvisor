package com.drink.activity;

import com.drink.R;
import com.drink.imageloader.WebImageView2;
import com.drink.query.AboutCocktailQuery;
import com.drink.query.LikeQuery;
import com.drink.query.WebQuery;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannedString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CocktailActivity extends BasicActivity 
{
	private static final int LOGIN_REQUIED_FOR_LIKE = 0;
	
	String ID;
	String DRINK_ID;

	ImageButton btnLike;

	AboutCocktailQuery.Data coctail;

	private ScrollView scrollView;
	
	private TextView tvCocktailName;
	private WebImageView2 wivPicture;
	private TextView tvLikeCount;
	private TextView tvHistoryText;
	private TextView tvFactsText;
	private TextView tvHowMake;
	private TextView tvHowDrink;
	
	private RelativeLayout rlIngredients;
	private LinearLayout llHistory;
	private LinearLayout llFacts;
	private LinearLayout llHowMake;
	private LinearLayout llHowDrink;

	public CocktailActivity() 
	{
		super(R.layout.activity_cocktail);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		mCaption.setText(getString(R.string.cocktails));
		
		Bundle bundle = getIntent().getExtras();

		ID = String.valueOf(bundle.getInt("ID"));
		DRINK_ID = bundle.getString("d_id");

		scrollView = (ScrollView) findViewById(R.id.scrollView1);
		scrollView.setVisibility(View.GONE);

		tvCocktailName = (TextView) findViewById(R.id.title);
		tvCocktailName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		wivPicture = (WebImageView2) findViewById(R.id.picture);
		wivPicture.setImagesStore(mImagesStore);
		wivPicture.setCacheDir(appSettings.getCacheDirImage());

		// like button
		RelativeLayout relativeLayout = (RelativeLayout) scrollView.findViewById(R.id.btn_like);
		
		btnLike = (ImageButton) relativeLayout.findViewById(R.id.button);
		btnLike.setOnClickListener(new OnClickListener() 
			{
			
			
				@Override
				public void onClick(View v) 
				{
					if (v.isSelected()) return;
					
					if(userToken.length() > 0)
					{
						new LikeTask().execute();
					}
					else 
					{
						Intent i = new Intent(CocktailActivity.this, LoginReqquiredActivity.class);
						startActivityForResult(i, LOGIN_REQUIED_FOR_LIKE);						
					}
				}
			});
		
		TextView buttontext = (TextView) relativeLayout.findViewById(R.id.text);
		buttontext.setTypeface(Typeface.createFromAsset(CocktailActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

		tvLikeCount = (TextView) relativeLayout.findViewById(R.id.counter);
		tvLikeCount.setTypeface(Typeface.createFromAsset(CocktailActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

		// ingredients
		rlIngredients = (RelativeLayout) scrollView.findViewById(R.id.rl_cocktail_ingredients);
		
		TextView ingridientsTitle = (TextView) rlIngredients.findViewById(R.id.tv_coctail_ingridients_title);
		ingridientsTitle.setTypeface(Typeface.createFromAsset(CocktailActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));

		// history
		llHistory = (LinearLayout) findViewById(R.id.include_history);
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
	            	if (coctail == null) return;
	            	
	                ViewTreeObserver obs = tvHistoryText.getViewTreeObserver();
	                obs.removeGlobalOnLayoutListener(this);
	                
	            	if (coctail.history.length() <= 10) return;
	            	
	            	LinearLayout llReadMore = (LinearLayout) llHistory.findViewById(R.id.ll_read_more);

	            	SpannedString text = (SpannedString) tvHistoryText.getText().subSequence(0, tvHistoryText.getLayout().getLineEnd(3) - 1);
					if (tvHistoryText.getText().length() > text.length()) 
					{
						tvHistoryText.setText(text);
						llHistory.setOnClickListener(new OnClickListener() 
							{
								@Override
								public void onClick(View arg0) 
								{
									Intent intent = new Intent();
									intent.putExtra("caption", CocktailActivity.this.getString(R.string.cocktails));
									intent.putExtra("subject", coctail.name);
									intent.putExtra("text", coctail.history);
									intent.putExtra("title", CocktailActivity.this.getString(R.string.drink_text_history));
									intent.putExtra("image", coctail.pictures.get(0));
									intent.setClass(CocktailActivity.this, ExtraTextActivity.class);
									
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
        
        // facts
		llFacts = (LinearLayout) findViewById(R.id.include_facts);
		llFacts.setVisibility(View.GONE);

		TextView tvFactsTitle = (TextView) llFacts.findViewById(R.id.tv_title);
		tvFactsTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		tvFactsTitle.setText(R.string.drink_text_interesting_facts);

		tvFactsText = (TextView) llFacts.findViewById(R.id.tv_text);
		tvFactsText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		tvReadMore = (TextView) llFacts.findViewById(R.id.tv_read_more);
		tvReadMore.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
        
		vto = tvFactsText.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
        {
            @Override
            public void onGlobalLayout() 
            {
            	if (coctail == null) return;
            	
                ViewTreeObserver obs = tvFactsText.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                
            	if (coctail.interesting_facts.size() == 0) return;
            	
				LinearLayout llReadMore = (LinearLayout) llFacts.findViewById(R.id.ll_read_more); 

				StringBuilder facts = new StringBuilder();
				for (String fact : coctail.interesting_facts) 
				{
					facts.append("- ");
					facts.append(fact);
					if (coctail.interesting_facts.size() > 1)
					{
						facts.append("\r\n");
					}
				}

				boolean extra = (coctail.interesting_facts.size() > 1);
				if (!extra)
				{
					String text = (String) tvFactsText.getText().subSequence(0, tvFactsText.getLayout().getLineEnd(3) - 1);
					if (text.length() < tvFactsText.getText().length())
					{
						tvFactsText.setText(text);
						extra = true;
					}
				}

				if (extra)
				{
					final String extraText = facts.toString();
					llFacts.setOnClickListener(new OnClickListener() 
						{
							@Override
							public void onClick(View arg0) 
							{
								Intent intent = new Intent();
								intent.setClass(CocktailActivity.this, ExtraTextActivity.class);
								intent.putExtra("caption", CocktailActivity.this.getString(R.string.cocktails));
								intent.putExtra("subject", coctail.name);
								intent.putExtra("text", extraText);
								intent.putExtra("title", CocktailActivity.this.getString(R.string.drink_text_interesting_facts));
								intent.putExtra("image", coctail.pictures.get(0));
								
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
		llHowMake = (LinearLayout) findViewById(R.id.include_how_do);
		llHowMake.setVisibility(View.GONE);

		TextView tvHowMakeTitle = (TextView) llHowMake.findViewById(R.id.tv_title);
		tvHowMakeTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		tvHowMakeTitle.setText(R.string.drink_text_how_made);

		tvHowMake = (TextView) llHowMake.findViewById(R.id.tv_text);
		tvHowMake.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		tvReadMore = (TextView) llHowMake.findViewById(R.id.tv_read_more);
		tvReadMore.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		vto = tvHowMake.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
	        {
	            @Override
	            public void onGlobalLayout() 
	            {
	            	if (coctail == null) return;

	            	ViewTreeObserver obs = tvHowMake.getViewTreeObserver();
	                obs.removeGlobalOnLayoutListener(this);
	                
	            	if (coctail.how_do.length() <= 10) return;
	            	
	            	LinearLayout llReadMore = (LinearLayout) llHowMake.findViewById(R.id.ll_read_more);
	            	
	                SpannedString text = (SpannedString) tvHowMake.getText().subSequence(0, tvHowMake.getLayout().getLineEnd(3) - 1);
					if (tvHowMake.getText().length() > text.length()) 
					{
						tvHowMake.setText(text);
						llHowMake.setOnClickListener(new OnClickListener() 
							{
								@Override
								public void onClick(View arg0) 
								{
									Intent intent = new Intent();
									intent.putExtra("caption", CocktailActivity.this.getString(R.string.cocktails));
									intent.putExtra("subject", coctail.name);
									intent.putExtra("text", coctail.how_do);
									intent.putExtra("title", CocktailActivity.this.getString(R.string.drink_text_how_made));
									intent.putExtra("image", coctail.pictures.get(0));
									intent.setClass(CocktailActivity.this, ExtraTextActivity.class);
									
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

		// with drink
		llHowDrink = (LinearLayout) findViewById(R.id.include_with_drink);
		llHowDrink.setVisibility(View.GONE);

		TextView tvHowDrinkTitle = (TextView) llHowDrink.findViewById(R.id.tv_title);
		tvHowDrinkTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		tvHowDrinkTitle.setText(R.string.drink_text_how_drink);

		tvHowDrink = (TextView) llHowDrink.findViewById(R.id.tv_text);
		tvHowDrink.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		tvReadMore = (TextView) llHowDrink.findViewById(R.id.tv_read_more);
		tvReadMore.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		
		vto = tvHowDrink.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
	        {
	            @Override
	            public void onGlobalLayout() 
	            {
	            	if (coctail == null) return;
	            	
	                ViewTreeObserver obs = tvHowDrink.getViewTreeObserver();
	                obs.removeGlobalOnLayoutListener(this);
	                
	            	if (coctail.with_what_drink.length() <= 10) return;
	            	
	            	LinearLayout llReadMore = (LinearLayout) llHowMake.findViewById(R.id.ll_read_more);

	            	int lineEnd = tvHowDrink.getLayout().getLineEnd(3);
	            	if (lineEnd > 0)
	            	{
		            	SpannedString text = (SpannedString) tvHowDrink.getText().subSequence(0, lineEnd - 1);
						if (tvHowDrink.getText().length() > text.length()) 
						{
							tvHowDrink.setText(text);
							tvHowDrink.setOnClickListener(new OnClickListener() 
								{
									@Override
									public void onClick(View arg0) 
									{
										Intent intent = new Intent();
										intent.putExtra("caption", CocktailActivity.this.getString(R.string.cocktails));
										intent.putExtra("subject", coctail.name);
										intent.putExtra("text", coctail.with_what_drink);
										intent.putExtra("title", CocktailActivity.this.getString(R.string.drink_text_how_drink));
										intent.putExtra("image", coctail.pictures.get(0));
										intent.setClass(CocktailActivity.this, ExtraTextActivity.class);
										
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
	            	else
	            	{
	            		llReadMore.setVisibility(View.GONE);
	            	}
	            }
	        });

        new LoadTask().execute();
	}

	private void update() 
	{
		// update cocktail name
		tvCocktailName.setText(coctail.name);
		// update cocktail picture
		wivPicture.setImageURL(coctail.pictures.get(0), "1");
		// update like button
		btnLike.setSelected(coctail.like);
		tvLikeCount.setText(Integer.toString(coctail.likes_count));

		// update ingredients
		if (coctail.ingridients.size() > 0) 
		{
			LayoutInflater inflater = getLayoutInflater();

			TableLayout tlIngr = (TableLayout) rlIngredients.findViewById(R.id.tl_coctail_ingridients);
			tlIngr.removeAllViews();
			
			for (int i = 0; i < coctail.ingridients.size(); ++i) 
			{				
				TableRow tr = (TableRow) inflater.inflate(R.layout.row_ingridients, tlIngr, false);

				TextView tvIngridientsQuantity = (TextView) tr.findViewById(R.id.tv_coctail_ingridients_quantity); 
				tvIngridientsQuantity.setTypeface(Typeface.createFromAsset(CocktailActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				if (coctail.ingridients.get(i).quantity != "null")
				{
					tvIngridientsQuantity.setText(coctail.ingridients.get(i).quantity);
				}
				
				TextView tvIngridientsName = (TextView) tr.findViewById(R.id.tv_coctail_ingridients_name); 
				tvIngridientsName.setTypeface(Typeface.createFromAsset(CocktailActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				tvIngridientsName.setText(coctail.ingridients.get(i).name);
				
				tlIngr.addView(tr);
			}
			
			rlIngredients.setVisibility(View.VISIBLE);
		}
		else 
		{
			rlIngredients.setVisibility(View.GONE);
		}

		// history
		if (coctail.history.length() > 10)
		{
			tvHistoryText.setText(Html.fromHtml(coctail.history));
			
			llHistory.setVisibility(View.VISIBLE);
		}
		else
		{
			llHistory.setVisibility(View.GONE);
		}
		
		// facts
		if (coctail.interesting_facts.size() > 0)
		{
			tvFactsText.setText(coctail.interesting_facts.get(0));
			
			llFacts.setVisibility(View.VISIBLE);
		}
		else
		{
			llFacts.setVisibility(View.GONE);
		}
		
		// how do
		if (coctail.how_do.length() > 10)
		{
			tvHowMake.setText(Html.fromHtml(coctail.how_do));
			
			llHowMake.setVisibility(View.VISIBLE);
		}
		else
		{
			llHowMake.setVisibility(View.GONE);
		}
		
		// with drink
		if (coctail.with_what_drink.length() > 10)
		{
			tvHowDrink.setText(Html.fromHtml(coctail.with_what_drink));
			
			llHowDrink.setVisibility(View.VISIBLE);
		}
		else
		{
			llHowDrink.setVisibility(View.GONE);
		}
	}

	private class LoadTask extends QueryTask<Void, Void, AboutCocktailQuery> 
	{
		@Override
		protected AboutCocktailQuery doInBackground(Void... params) 
		{
			AboutCocktailQuery cocktailQuery = new AboutCocktailQuery(appSettings, ID, DRINK_ID);
			cocktailQuery.getResponse(WebQuery.GET);
			
			return cocktailQuery;
		}

		@Override
		protected void onPostExecute(AboutCocktailQuery query) 
		{
			if (checkResult(query))
			{
				coctail = query.getData();
				
				update();
				
				scrollView.setVisibility(View.VISIBLE);
			}

			super.onPostExecute(query);
		}
	}

	private class LikeTask extends QueryTask<Void, Void, LikeQuery> 
	{
		@Override
		protected LikeQuery doInBackground(Void... params) 
		{
			LikeQuery likeQuery = new LikeQuery(appSettings, LikeQuery.COCKTAILS, ID);
			likeQuery.getResponse(WebQuery.GET);

			return likeQuery;
		}

		@Override
		protected void onPostExecute(LikeQuery query) 
		{
			if	(checkResult(query))
			{
				btnLike.setSelected(true);
				tvLikeCount.setText(Integer.toString(query.getData().likes_count));
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
