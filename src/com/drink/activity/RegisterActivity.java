
package com.drink.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.helpers.Defines;
import com.drink.helpers.StatisticsHelper;
import com.drink.query.CitiesListQuery;
import com.drink.query.RegQuery;
import com.drink.query.WebQuery;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RegisterActivity extends BasicActivity
{
	private EditText email;
	private EditText name;
	private EditText password;
	private EditText date;
	private RegQuery regQuery = new RegQuery();
	private Date birthday;
	ArrayAdapter<String> adapter;
	ArrayList<String> strings = new ArrayList<String>();
	CitiesListQuery.Data citiesList;
	static final int DATE_DIALOG_ID = 999;
	DatePickerDialog datePickerDialog;
	
	private int type = Defines.TYPE_USUAL;
	public RegisterActivity() 
	{
		super(R.layout.activity_register);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		type = getIntent().getIntExtra("type", Defines.TYPE_USUAL);
		mCaption.setText(getString(R.string.usual_reg_header));
		
		TextView txtMessage = (TextView) findViewById(R.id.txt_message);
		txtMessage.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		
		email = (EditText) findViewById(R.id.user_email);
		email.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		email.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) 
			{
				if (email.getText().length() > 0)
				{
					email.setTypeface(Typeface.createFromAsset(RegisterActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				}
				else
				{
					email.setTypeface(Typeface.createFromAsset(RegisterActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});

		name = (EditText) findViewById(R.id.user_name);
		name.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		name.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) 
			{
				if (name.getText().length() > 0)
				{
					name.setTypeface(Typeface.createFromAsset(RegisterActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				}
				else
				{
					name.setTypeface(Typeface.createFromAsset(RegisterActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
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
					password.setTypeface(Typeface.createFromAsset(RegisterActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				}
				else
				{
					password.setTypeface(Typeface.createFromAsset(RegisterActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});

		date = (EditText) findViewById(R.id.date_birth);
		date.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		date.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					showDialog(DATE_DIALOG_ID);
				}
			});
		date.addTextChangedListener(new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) 
			{
				if (date.getText().length() > 0)
				{
					date.setTypeface(Typeface.createFromAsset(RegisterActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));
				}
				else
				{
					date.setTypeface(Typeface.createFromAsset(RegisterActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			
			@Override
			public void afterTextChanged(Editable arg0) {}
		});

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);

		final CheckBox man = (CheckBox) findViewById(R.id.man);
		man.setTypeface(Typeface.createFromAsset(RegisterActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));
		final CheckBox woman = (CheckBox) findViewById(R.id.woman);
		woman.setTypeface(Typeface.createFromAsset(RegisterActivity.this.getAssets(), "fonts/ProximaNova-Reg.otf"));

		man.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				man.setChecked(true);
				woman.setChecked(false);
			}
		});
		woman.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				man.setChecked(false);
				woman.setChecked(true);
			}
		});

		RelativeLayout regButton = (RelativeLayout) findViewById(R.id.signup_button);
		
		ImageButton button = (ImageButton) regButton.findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					if (name.getText().toString().equals("")) 
					{
						showErrorBox(getString(R.string.reg_name_misssing), false);
						return;
					}
	
					if (email.getText().toString().equals("")) 
					{
						showErrorBox(getString(R.string.reg_email_missing), false);
						return;
					}
					
					if (!CommonHelper.checkEmail(email.getText().toString()))
					{
						showErrorBox(getString(R.string.invalid_email), false);
						return;
					}
	
					if (date.getText().toString().equals("")) 
					{
						showErrorBox(getString(R.string.reg_birth_missing), false);
						return;
					}
	
					if (!CommonHelper.checkValidAge(birthday)) 
					{
						showErrorBox(getString(R.string.reg_birth_invalid), false);
						return;
					}
	
					if (!man.isChecked() && !woman.isChecked())
					{
						showErrorBox(getString(R.string.reg_gender_missing), false);
						return;
					}

					if (type == Defines.TYPE_USUAL) 
					{
						EditText pass = (EditText) findViewById(R.id.user_pass);
						if (pass.getText().toString().length() < 6)
						{
							showErrorBox(getString(R.string.reg_password_short), false);
							return;
						}
					}
					
					register();
				}
			});
		
		TextView text = (TextView) regButton.findViewById(R.id.text);
		text.setTypeface(Typeface.createFromAsset(RegisterActivity.this.getAssets(), "fonts/ProximaNova-Bold.otf"));

		EditText pass = (EditText) findViewById(R.id.user_pass);

		switch (type)
		{
		case Defines.TYPE_FACEBOOK:
			TextView textView = (TextView) findViewById(R.id.user_name);
			textView.setText(getIntent().getStringExtra("user_name"));

			textView = (TextView) findViewById(R.id.user_email);
			textView.setText(getIntent().getStringExtra("email").toString());

			birthday = new Date(getIntent().getLongExtra("birthday", 0));

			textView = (TextView) findViewById(R.id.date_birth);
			textView.setText(android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(birthday));

			if (getIntent().getStringExtra("gender").equals("female"))
			{
				woman.setChecked(true);
			}
			else if (getIntent().getStringExtra("gender").equals("male"))
			{
				man.setChecked(true);
			}

			pass.setVisibility(View.GONE);
			break;
		case Defines.TYPE_TWITTER:
			textView = (TextView) findViewById(R.id.user_name);
			textView.setText(getIntent().getStringExtra("user_name"));

			pass.setVisibility(View.GONE);
			break;
		case Defines.TYPE_GOOGLE:
			textView = (TextView) findViewById(R.id.user_name);
			textView.setText(getIntent().getStringExtra("user_name"));

			textView = (TextView) findViewById(R.id.user_email);
			textView.setText(getIntent().getStringExtra("email").toString());

			pass.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) 
	{
		switch (id) 
		{
		case DATE_DIALOG_ID: 
			{
				Calendar c = Calendar.getInstance();
				datePickerDialog = new DatePickerDialog(RegisterActivity.this, datePickerListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				return datePickerDialog;
			}
		}
		
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() 
	{
		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) 
		{
			final Calendar c = Calendar.getInstance();
			c.set(selectedYear, selectedMonth, selectedDay);
			birthday = c.getTime();

			date.setText(android.text.format.DateFormat.getDateFormat(getApplicationContext()).format(birthday));
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_register, menu);

		return true;
	}

	private void register() 
	{
		// user name
		EditText name = (EditText) findViewById(R.id.user_name);
		regQuery.setName(name.getText().toString());
		// user birthday
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		regQuery.setDateBirth(sdf.format(birthday));
		// user email
		EditText email = (EditText) findViewById(R.id.user_email);
		regQuery.setMail(email.getText().toString());
		// gender
		CheckBox man = (CheckBox) findViewById(R.id.man);
		CheckBox woman = (CheckBox) findViewById(R.id.woman);
		if (man.isChecked())
		{
			regQuery.setSex("m");
		}
		else if (woman.isChecked())
		{
			regQuery.setSex("w");
		}
		// device token
		regQuery.setDeviceToken(appSettings.getContentToken());

		switch (type)
		{
		case Defines.TYPE_USUAL:
			{
				// set password
				EditText pass = (EditText) findViewById(R.id.user_pass);
				regQuery.setPassword(pass.getText().toString());
				// reset social identifier and access token
				regQuery.setSocialIdentifier("");
				regQuery.setAccessToken("");
				// registration type
				regQuery.setTypeReg(RegQuery.USUAL);
			}
			break;
		case Defines.TYPE_FACEBOOK:
			{
				// reset password
				regQuery.setPassword("");
				// set social identifier and access token
				regQuery.setSocialIdentifier(getIntent().getStringExtra("user_id"));
				regQuery.setAccessToken(getIntent().getStringExtra("access_token"));
				// registration type
				regQuery.setTypeReg(RegQuery.FACEBOOK);
			}
			break;
		case Defines.TYPE_TWITTER:
			{
				// reset password
				regQuery.setPassword("");
				// set social identifier and access token
				regQuery.setSocialIdentifier(getIntent().getStringExtra("user_id"));
				regQuery.setAccessToken(getIntent().getStringExtra("access_token"));
				//need to add one more data
				regQuery.setAccessSecret(getIntent().getStringExtra("access_secret"));
				
				// registration type
				regQuery.setTypeReg(RegQuery.TWITTER);
			}
			break;
		case Defines.TYPE_GOOGLE:
			{
				// reset password
				regQuery.setPassword("");
				// set social identifier and access token
				regQuery.setSocialIdentifier(getIntent().getStringExtra("user_id"));
				regQuery.setAccessToken(getIntent().getStringExtra("access_token"));
				// registration type
				regQuery.setTypeReg(RegQuery.GOOGLE);
			}
			break;
		}
		
		new RegTask(type).execute();
	}

	private class RegTask extends QueryTask<Void, Void, RegQuery> 
	{
		private int m_type;
		
		public RegTask(int type)
		{
			this.m_type = type;
		}
		
		@Override
		protected RegQuery doInBackground(Void... params) 
		{
			regQuery.getResponse(WebQuery.POST);

			return regQuery;
		}

		@Override
		protected void onPostExecute(RegQuery query) 
		{
			if (checkResult(query))
			{
				if (query.getData().user_token != null) 
				{
					StatisticsHelper.sendRegisterEvent(RegisterActivity.this, m_type);
					
					EditText email = (EditText) findViewById(R.id.user_email);
					
					switch (m_type)
					{
					case Defines.TYPE_USUAL:
						EditText pass = (EditText) findViewById(R.id.user_pass);
						appSettings.setUserAccount(email.getText().toString(), pass.getText().toString(), RegQuery.USUAL);
						break;
					case Defines.TYPE_FACEBOOK:
						appSettings.setUserAccount(email.getText().toString(), null, RegQuery.FACEBOOK);
						appSettings.setConnectFacebook(true);
						appSettings.setPublishOnFacebook(true);
						break;
					case Defines.TYPE_TWITTER:
						appSettings.setUserAccount(email.getText().toString(), null, RegQuery.TWITTER);
						
						appSettings.setConnectTwitter(true);
						appSettings.setPublishOnTwitter(true);
						break;
					case Defines.TYPE_GOOGLE:
						appSettings.setUserAccount(email.getText().toString(), null, RegQuery.GOOGLE);
						break;
					}
					
					appSettings.setUserToken(query.getData().user_token);
					appSettings.setUserSecret(query.getData().user_secret);
					appSettings.setUserId(query.getData().user_id);
					
					EditText name = (EditText) findViewById(R.id.user_name);
					appSettings.setUserName(name.getText().toString());

					appSettings.setEventsFeedCount(0);
					appSettings.setEventsFriendsCount(0);
					appSettings.setEventsMeetingsCount(0);

					setResult(RESULT_OK);
				}
				else
				{
					setResult(RESULT_CANCELED);
				}
				
				finish();
			}
			
			super.onPostExecute(query);
		}
	}
}
