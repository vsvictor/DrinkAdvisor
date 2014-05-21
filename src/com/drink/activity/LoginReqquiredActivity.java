
package com.drink.activity;

import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.helpers.Defines;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class LoginReqquiredActivity extends BasicActivity implements OnClickListener
{
	static final int NEED_REGISTRATION = 0;

	public LoginReqquiredActivity() 
	{
		super(R.layout.activity_login_required);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mCaption.setText(getString(R.string.login_required));
		
		TextView txtOverSocialNetworks = (TextView) findViewById(R.id.txt_over_social_networks);
		txtOverSocialNetworks.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		ImageButton fb = (ImageButton) findViewById(R.id.btn_facebook);
		fb.setOnClickListener(this);

		ImageButton tw = (ImageButton) findViewById(R.id.btn_twitter);
		tw.setOnClickListener(this);

		ImageButton gp = (ImageButton) findViewById(R.id.btn_google);
		gp.setOnClickListener(this);
		
		Button btnEnter = (Button) findViewById(R.id.btn_enter);
		btnEnter.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		btnEnter.setOnClickListener(this);
		
		Button btnRegister = (Button) findViewById(R.id.btn_register);
		btnRegister.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		btnRegister.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) 
	{
		if (!checkInternetConnection()) return; 

		switch (v.getId())
		{
		case R.id.btn_facebook:
			{
				startActivityForResult(new Intent(this, FBLoginActivity.class).putExtra("login", true), Defines.TYPE_FACEBOOK);
			}
			break;
		case R.id.btn_twitter:
			{
				startActivityForResult(new Intent(this, TWLoginActivity.class).putExtra("login", true), Defines.TYPE_TWITTER);
			}
			break;
		case R.id.btn_google:
			{
				startActivityForResult(new Intent(this, GPLoginActivity.class).putExtra("login", true), Defines.TYPE_GOOGLE);
			}
			break;
		case R.id.btn_enter:
			{
				startActivityForResult(new Intent(this, EnterActivity.class), Defines.TYPE_USUAL);
			}
			break;
		case R.id.btn_register:
			{
				Intent intent = new Intent();
				intent.putExtra("type", Defines.TYPE_USUAL);
				intent.setClass(this, RegisterActivity.class);
				startActivityForResult(intent, Defines.TYPE_USUAL);
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		switch (resultCode)
		{
		case RESULT_OK:
			switch (requestCode)
			{
			case Defines.TYPE_FACEBOOK:
			case Defines.TYPE_TWITTER:
			case Defines.TYPE_GOOGLE:
			case Defines.TYPE_USUAL:
				CommonHelper.saveUserToken(appSettings);
				
				setResult(RESULT_OK);
				finish();

				break;
			case NEED_REGISTRATION:
				setResult(resultCode);
				finish();
				break;
			}
			break;
		case Defines.RESULT_NEED_REGISTRATION:
			data.setClass(LoginReqquiredActivity.this, RegisterActivity.class);
			startActivityForResult(data, NEED_REGISTRATION);
			break;
		default:
			setResult(RESULT_CANCELED);
			finish();
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
