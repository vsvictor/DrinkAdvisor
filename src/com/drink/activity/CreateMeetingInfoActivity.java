package com.drink.activity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.drink.R;

import android.os.Bundle;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class CreateMeetingInfoActivity extends BasicActivity 
{
	private static final int SELECT_CITY = 1;
	private static final int SELECT_BAR = 2;
	
	EditText tvTitle;
	EditText tvDate;
	EditText tvTime;
	EditText tvComment;

	private Date date = new Date();

	public CreateMeetingInfoActivity() 
	{
		super(R.layout.activity_create_meeting_info, true, false);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		mCaption.setText(R.string.create_meeting_info);

		tvTitle = (EditText) findViewById(R.id.tv_title);
		tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));

		tvDate = (EditText) findViewById(R.id.tv_date);
		tvDate.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tvDate.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showDialog(1);
			}
		});

		tvTime = (EditText) findViewById(R.id.tv_time);
		tvTime.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		tvTime.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showDialog(2);
			}
		});

		tvComment = (EditText) findViewById(R.id.tv_comment);
		tvComment.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Reg.otf"));
		
		Button btnNext = (Button) findViewById(R.id.btn_next);
		btnNext.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Bold.otf"));
		btnNext.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent();
				intent.putExtra("meeting_title", tvTitle.getText().toString());
				intent.putExtra("meeting_date", date.getTime());
				intent.putExtra("meeting_comment", tvComment.getText().toString());
				
				if (appSettings.getUserCityId() == -1)
				{
					intent.setClass(CreateMeetingInfoActivity.this, FilterCitiesActivity.class);
					intent.putExtra("mode", FilterCitiesActivity.MODE_SelectMeetingCity);
					
					startActivityForResult(intent, SELECT_CITY);
				}
				else
				{
					intent.setClass(CreateMeetingInfoActivity.this, BarsActivity.class);
					intent.putExtra("city_id", appSettings.cityId);
					intent.putExtra("city_name", appSettings.cityName);
					intent.putExtra("city_lat", appSettings.lat);
					intent.putExtra("city_lng", appSettings.lng);
					intent.putExtra("mode", BarsActivity.MODE_SelectMeetingBar);
					
					startActivityForResult(intent, SELECT_BAR);
				}
			}
		});
	}

	DatePickerDialog.OnDateSetListener dateSetListener = new OnDateSetListener() 
		{
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
			{
				Calendar calendar = GregorianCalendar.getInstance();
				int _year = calendar.get(Calendar.YEAR);
				int _month = calendar.get(Calendar.MONTH);
				int _day = calendar.get(Calendar.DAY_OF_MONTH);
				
				// check year
				if (_year > year)
				{
					showErrorBox(CreateMeetingInfoActivity.this.getString(R.string.wrong_meeting_date), false);
					return;
				}
				else if (_year == year)
				{
					// for this year check month
					if (_month > monthOfYear)
					{
						showErrorBox(CreateMeetingInfoActivity.this.getString(R.string.wrong_meeting_date), false);
						return;
					}
					else if (_month == monthOfYear)
					{
						// for this month check day
						if (_day > dayOfMonth)
						{
							showErrorBox(CreateMeetingInfoActivity.this.getString(R.string.wrong_meeting_date), false);
							return;
						}
						else if (_day == dayOfMonth)
						{
							// for this day check time if set
							if (tvTime.getText().toString().length() > 0)
							{
								int hour = calendar.get(Calendar.HOUR_OF_DAY);
								int minute = calendar.get(Calendar.MINUTE);
								
								// check hour
								if (hour > date.getHours())
								{
									showErrorBox(CreateMeetingInfoActivity.this.getString(R.string.wrong_meeting_date), false);
									return;
								}
								else if (hour == date.getHours())
								{
									// for this hour check minute
									if (minute > date.getMinutes())
									{
										showErrorBox(CreateMeetingInfoActivity.this.getString(R.string.wrong_meeting_date), false);
										return;
									}
								}
							}
						}
					} 
				}
				
				date.setYear(year - 1900);
				date.setMonth(monthOfYear);
				date.setDate(dayOfMonth);
				
				tvDate.setText(DateFormat.getDateFormat(getApplicationContext()).format(date));
			}
		};
	TimePickerDialog.OnTimeSetListener timeSetListener = new OnTimeSetListener() 
		{
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
			{
				// if date is already set check time
				if (tvDate.getText().toString().length() > 0)
				{
					Calendar calendar = GregorianCalendar.getInstance();
					int _year = calendar.get(Calendar.YEAR);
					int _month = calendar.get(Calendar.MONTH);
					int _day = calendar.get(Calendar.DAY_OF_MONTH);
					
					// if date is today check time
					if ((_year == date.getYear() + 1900) && (_month == date.getMonth()) && (_day == date.getDate()))
					{
						int _hour = calendar.get(Calendar.HOUR_OF_DAY);
						int _minute = calendar.get(Calendar.MINUTE);
						
						// check hour
						if (_hour > hourOfDay)
						{
							showErrorBox(CreateMeetingInfoActivity.this.getString(R.string.wrong_meeting_date), false);
							return;
						}
						else if (_hour == hourOfDay)
						{
							// for this hour check minute
							if (_minute > minute)
							{
								showErrorBox(CreateMeetingInfoActivity.this.getString(R.string.wrong_meeting_date), false);
								return;
							}
						}
					}
				}
				
				date.setHours(hourOfDay);
				date.setMinutes(minute);
				
				tvTime.setText(getString(R.string.meeting_time) + " - " + DateFormat.getTimeFormat(getApplicationContext()).format(date));
			}
		};
	
	private void selectBar()
	{
		Intent intent = new Intent(CreateMeetingInfoActivity.this, BarsActivity.class);
		intent.putExtra("mode", BarsActivity.MODE_SelectMeetingBar);
		
		startActivityForResult(intent, SELECT_CITY);
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		super.onActivityResult(requestCode, resultCode, intent);

		switch (resultCode)
		{
			case RESULT_OK:
			{
				switch (requestCode)
				{
				case SELECT_CITY:
				case SELECT_BAR:
					setResult(RESULT_OK);
					finish();
					break;
				}
			}
			break;
			case RESULT_INTERNAL_CANCEL:
			{
				setResult(RESULT_INTERNAL_CANCEL);
				finish();
			}
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) 
	{
		switch (id)
		{
		case 1:
			Calendar rightNow = Calendar.getInstance();
			return new DatePickerDialog(this, dateSetListener, rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DAY_OF_MONTH) + 1);
		case 2:
			return new TimePickerDialog(this, timeSetListener, 18, 0, false);
		}
		
		return null;
	}
}
