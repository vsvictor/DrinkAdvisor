
package com.drink.activity;

import com.drink.R;
import com.drink.imageloader.WebImageView2;

import android.os.Bundle;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ExtraTextActivity extends BasicActivity 
{
	public ExtraTextActivity() 
	{
		super(R.layout.activity_extra_text);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();

		mCaption.setText(bundle.getString("caption"));

		RelativeLayout rlPicture= (RelativeLayout) findViewById(R.id.include_picture);
		
		TextView tvName = (TextView) rlPicture.findViewById(R.id.title);
		tvName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tvName.setText(bundle.getString("subject"));
		
		WebImageView2 wivPicture = (WebImageView2) rlPicture.findViewById(R.id.picture);
		wivPicture.setImagesStore(mImagesStore);
		wivPicture.setCacheDir(appSettings.getCacheDirImage());
		wivPicture.setImageURL(bundle.getString("image"), "1");
		
		LinearLayout llInfo = (LinearLayout) findViewById(R.id.include_info);
		
		TextView tvTitle = (TextView) llInfo.findViewById(R.id.tv_title);
		tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		tvTitle.setText(bundle.getString("title"));
		
		TextView tvText = (TextView) llInfo.findViewById(R.id.tv_text);
		tvText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tvText.setMaxLines(999);
		tvText.setText(Html.fromHtml(bundle.getString("text")));
		
		LinearLayout llReadMore = (LinearLayout) llInfo.findViewById(R.id.ll_read_more);
		llReadMore.setVisibility(View.GONE);
	}
}
