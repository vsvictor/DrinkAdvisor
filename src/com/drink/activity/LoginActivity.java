
package com.drink.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.helpers.Defines;

public class LoginActivity extends BasicActivity 
{
	static final int NEED_REGISTRATION = 50;
	
	ImageButton	mBtnFacebook;
	ImageButton	mBtnTwitter;
	ImageButton	mBtnGoogle;
	
	Button		mBtnEnter;
	Button		mBtnRegistration;
	TextView	mTxtSkipRegistration;
	
	public LoginActivity() 
	{
		super(R.layout.activity_login);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mBtnFacebook = (ImageButton) findViewById(R.id.btn_facebook);
		mBtnFacebook.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				if (!checkInternetConnection()) return;

				startActivityForResult(new Intent(LoginActivity.this, FBLoginActivity.class).putExtra("login", true), Defines.TYPE_FACEBOOK);
			}
		});
		
		mBtnTwitter = (ImageButton) findViewById(R.id.btn_twitter);
		mBtnTwitter.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				if (!checkInternetConnection()) return;

				startActivityForResult(new Intent(LoginActivity.this, TWLoginActivity.class).putExtra("login", true), Defines.TYPE_TWITTER);
			}
		});
		
		mBtnGoogle = (ImageButton) findViewById(R.id.btn_google);
		mBtnGoogle.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				if (!checkInternetConnection()) return;

				startActivityForResult(new Intent(LoginActivity.this, GPLoginActivity.class).putExtra("login", true), Defines.TYPE_GOOGLE);
			}
		});
		
		mBtnEnter = (Button)  findViewById(R.id.btn_enter);
		mBtnEnter.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mBtnEnter.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (!checkInternetConnection()) return;

				startActivityForResult(new Intent(LoginActivity.this, EnterActivity.class), Defines.TYPE_USUAL);
			}
		});
		
		mBtnRegistration = (Button)  findViewById(R.id.btn_registration);
		mBtnRegistration.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mBtnRegistration.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (!checkInternetConnection()) return;

				Intent intent = new Intent();
				intent.putExtra("type", Defines.TYPE_USUAL);
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				startActivityForResult(intent, Defines.TYPE_USUAL);
			}
		});
	
		mTxtSkipRegistration = (TextView) findViewById(R.id.txt_skip_registration);
		mTxtSkipRegistration.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		mTxtSkipRegistration.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					appSettings.setUserToken("");
					appSettings.setUserId(String.valueOf("0"));
					
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, CheckAgeActivity.class);
					startActivity(intent);
				}
			});

		SpannableString content = new SpannableString(mTxtSkipRegistration.getText());
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		mTxtSkipRegistration.setText(content);	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_login, menu);
	
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
			case Defines.TYPE_FACEBOOK:
			case Defines.TYPE_TWITTER:
			case Defines.TYPE_GOOGLE:
			case Defines.TYPE_USUAL:
			case NEED_REGISTRATION:
				{
					CommonHelper.saveUserToken(appSettings);
					
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_MAIN);
					intent.setClass(LoginActivity.this, HomeActivity.class);
					startActivity(intent);
					
					finish();
				}
				break;
			}
		}
		else if (resultCode == Defines.RESULT_NEED_REGISTRATION)
		{
			data.setClass(LoginActivity.this, RegisterActivity.class);
			startActivityForResult(data, NEED_REGISTRATION);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
