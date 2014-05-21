
package com.drink.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.query.LoginQuery;
import com.drink.query.WebQuery;

public class EnterActivity extends BasicActivity 
{
	static final int NEED_REGISTRATION = 50;

	EditText	email;
	EditText	password;

	public EnterActivity() 
	{
		super(R.layout.activity_enter);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		setContentView(R.layout.activity_enter);

		super.onCreate(savedInstanceState);
		mCaption.setText(getString(R.string.usual_login_header));
		
		email = (EditText) findViewById(R.id.user_email);
		email.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		email.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) 
			{
				if (email.getText().length() > 0)
				{
					email.setTypeface(Typeface.createFromAsset(EnterActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				}
				else
				{
					email.setTypeface(Typeface.createFromAsset(EnterActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});

		password = (EditText) findViewById(R.id.user_pass);
		password.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		password.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) 
			{
				if (password.getText().length() > 0)
				{
					password.setTypeface(Typeface.createFromAsset(EnterActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				}
				else
				{
					password.setTypeface(Typeface.createFromAsset(EnterActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});

		RelativeLayout regButton = (RelativeLayout) findViewById(R.id.enter_button);
		
		ImageButton button = (ImageButton) regButton.findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					new EnterTask().execute();
				}
			});
		
		TextView text = (TextView) regButton.findViewById(R.id.text);
		text.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_login, menu);
	
		return true;
	}

	private class EnterTask extends QueryTask<Void, Void, LoginQuery> 
	{
		@Override
		protected LoginQuery doInBackground(Void... params) 
		{
			LoginQuery loginQuery = new LoginQuery(appSettings, email.getText().toString(), password.getText().toString());
			loginQuery.getResponse(WebQuery.POST);

			return loginQuery;
		}

		@Override
		protected void onPostExecute(LoginQuery query)
		{
			if (checkResult2(query))
			{
				LoginQuery.Data data = query.getData();
				
				if (data.error == -1) 
				{
					appSettings.setUserToken(data.user_token);
					appSettings.setUserId(String.valueOf(data.user_id));
					appSettings.setUserName(data.user_name);
					appSettings.setUserAvatar(data.user_picture);
					
					appSettings.setEventsFeedCount(data.events_feed);
					appSettings.setEventsFriendsCount(data.events_friends);
					appSettings.setEventsMeetingsCount(data.events_meetings);
	
					appSettings.setConnectFacebook(data.connect_facebook);
					appSettings.setConnectTwitter(data.connect_twitter);
					
					appSettings.setPublishOnFacebook(data.publish_on_facebook);
					appSettings.setPublishOnTwitter(data.publish_on_twitter);
					
					CommonHelper.saveUserToken(appSettings);

					setResult(RESULT_OK);
					
					finish();
				} 
			}
			
			super.onPostExecute(query);
		}
	}
}
