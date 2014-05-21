
package com.drink.activity;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import com.drink.R;
import com.drink.helpers.CommonHelper;

public class CheckAgeActivity extends BasicActivity 
{
	TextView	mTxtMessage;
	DatePicker	mDate;

	public CheckAgeActivity()
	{
		super(R.layout.activity_check_age);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mTxtMessage = (TextView) findViewById(R.id.txt_message);
		mTxtMessage.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Light.otf"));
		
		mDate = (DatePicker) findViewById(R.id.date);
		mDate.updateDate(Calendar.getInstance().get(Calendar.YEAR), 
						Calendar.getInstance().get(Calendar.MONTH), 
						Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		
		Button btnAprove = (Button) findViewById(R.id.btn_aprove);
		btnAprove.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnAprove.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					GregorianCalendar calendar = new GregorianCalendar(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth());
					
					if (CommonHelper.checkValidAge(calendar.getTime()))
					{
						startActivity(new Intent(CheckAgeActivity.this, HomeActivity.class));
					}
					else
					{
						mTxtMessage.setTextColor(Color.RED);
						mTxtMessage.setText(getString(R.string.check_age_denied));
					}
				}
			});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.activity_login, menu);
	
		return true;
	}
}
