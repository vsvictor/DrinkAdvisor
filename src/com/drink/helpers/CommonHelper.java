package com.drink.helpers;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.drink.ControlApplication;
import com.drink.R;
import com.drink.settings.AppSettings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.view.WindowManager;

public class CommonHelper 
{
	static public boolean checkInternetConnection() 
	{
		ConnectivityManager cm = (ConnectivityManager) ControlApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		return ((netInfo != null) && netInfo.isAvailable() && netInfo.isConnected());
	}

	static public boolean saveUserToken(AppSettings appSettings)
	{
		String userToken = appSettings.getUserToken();
		
		if ((userToken == null) || (userToken.length() == 0)) return false;
		
		try 
		{
			FileOutputStream fos = ControlApplication.getContext().openFileOutput("user_token", Context.MODE_PRIVATE);
			fos.write(userToken.getBytes());
			fos.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return false;
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	static public boolean loadUserToken()
	{
		String userToken = null;
		
		try 
		{
			FileInputStream fis = ControlApplication.getContext().openFileInput("user_token");
			
			byte[] buffer = new byte[fis.available()];
			
			fis.read(buffer);
			fis.close();
			
			userToken = new String(buffer);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return false;
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}		
		
		if ((userToken == null) || (userToken.length() == 0)) return false;
		
		new AppSettings(ControlApplication.getContext()).setUserToken(userToken);
		
		return true;
	}

	public static boolean saveContentToken(String token)
	{
		if ((token == null) || (token.length() == 0)) return false;
		
		try 
		{
			FileOutputStream fos = ControlApplication.getContext().openFileOutput("content_token", Context.MODE_PRIVATE);
			fos.write(token.getBytes());
			fos.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return false;
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static String readContentToken()
	{
		String contentToken = null;
		
		try 
		{
			FileInputStream fis = ControlApplication.getContext().openFileInput("content_token");
			
			byte[] buffer = new byte[fis.available()];
			
			fis.read(buffer);
			fis.close();
			
			contentToken = new String(buffer);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return null;
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}		
		
		if ((contentToken == null) || (contentToken.length() == 0)) return null;
		
		return contentToken;
	}

	static public void clearUserToken(Context context)
	{
		context.deleteFile("user_token");
	}
	
	static public void showMessageBox(final Activity activity, String title, String message, final boolean finish) 
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int id) 
				{
					dialog.cancel();
					if (finish)
					{
						activity.finish();
					}
				}
			});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	static public boolean checkEmail(String email) 
	{
	    if (email == null) return false; 

	    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	static public String getRemainingTimePhrase(int time)
	{
		Context context = ControlApplication.getContext();
		
		int minutes = time % 60;
		time /= 60;
		int hours = time % 24;
		time /= 24;
		int days = time;
		
		String daysLabel = "";
		String hoursLabel = "";
		String minutesLabel = "";
		
		String daysStr = " " + Integer.toString(days);
		String hoursStr = " " + Integer.toString(hours);
		String minutesStr = " " + Integer.toString(minutes);
		
		if (days < 21)
		{
	        if (days == 0)
	        {
	            daysLabel = "";
	            daysStr = "";
	        }	       
	        else if (days == 1)
	        {
	            daysLabel = context.getString(R.string.remaining_days_1);
	        }
	        if (days < 5)
	        {
	            daysLabel = context.getString(R.string.remaining_days_2_4);
	        }
	        else
	        {
	            daysLabel = context.getString(R.string.remaining_days_5);
	        }
	    }
		
		if (hours < 21)
		{
	        if (hours == 0)
	        {
	            hoursLabel = "";
	            hoursStr = "";
	        }
	        else if (hours == 1)
	        {
	            hoursLabel = context.getString(R.string.remaining_hours_1);
	        }
	        else if (hours < 5)
	        {
	            hoursLabel = context.getString(R.string.remaining_hours_2_4);
	        }
	        else
	        {
	            hoursLabel = context.getString(R.string.remaining_hours_5);
	        }
	    }
		else
	    {
	        
	        if (hours % 10 == 0)
	        {
	            hoursLabel = context.getString(R.string.remaining_hours_5);
	        }
	        else if (hours % 10 == 1)
	        {
	            hoursLabel = context.getString(R.string.remaining_hours_1);
	        }
	        else if (hours % 10 < 5)
	        {
	            hoursLabel = context.getString(R.string.remaining_hours_2_4);
	        }
	        else
	        {
	            hoursLabel = context.getString(R.string.remaining_hours_5);
	        }
	    }
		
		if (minutes < 21)
		{
	        if (minutes == 0)
	        {
	            minutesLabel = "";
	            minutesStr = "";
	        }
	        else if (minutes == 1)
	        {
	            minutesLabel = context.getString(R.string.remaining_minutes_1);
	        }
	        else if (minutes < 5)
	        {
	            minutesLabel = context.getString(R.string.remaining_minutes_2_4);
	        }
	        else
	        {
	            minutesLabel = context.getString(R.string.remaining_minutes_5);
	        }
	    }
		else
		{
	        if (minutes % 10 == 0)
	        {
	            minutesLabel = context.getString(R.string.remaining_minutes_5);
	        }
	        else if (minutes % 10 == 1)
	        {
	            minutesLabel = context.getString(R.string.remaining_minutes_1);
	        }
	        else if (minutes % 10 < 5)
	        {
	            minutesLabel = context.getString(R.string.remaining_minutes_2_4);
	        }
	        else
	        {
	            minutesLabel = context.getString(R.string.remaining_minutes_5);
	        }
	    }
		
		StringBuilder builder = new StringBuilder(context.getString(R.string.bar_remaining_time));
		if (days > 0)
		{
			builder.append(" ").append(daysStr).append(" ").append(daysLabel);
		}
		if (hours > 0)
		{
			builder.append(" ").append(hoursStr).append(" ").append(hoursLabel);
		}
		if (minutes > 0)
		{
			builder.append(" ").append(minutesStr).append(" ").append(minutesLabel);
		}
		
		return builder.toString();
	}
	
	static public double getDistanceFromLatLonInKm(Location loc1, Location loc2) 
	{
		  double R = 6371.0; // Radius of the earth in km
		  double dLat = deg2rad(loc2.getLatitude() - loc1.getLatitude());
		  double dLon = deg2rad(loc2.getLongitude() - loc1.getLongitude()); 
		  double a =  Math.sin(dLat / 2.0) * Math.sin(dLat / 2.0) + 
				  	  Math.cos(deg2rad(loc1.getLatitude())) * Math.cos(deg2rad(loc2.getLatitude())) * Math.sin(dLon / 2.0f) * Math.sin(dLon / 2.0); 
		  double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a)); 
		  double d = R * c; // Distance in km
		  
		  return d;
	}

	static double deg2rad(double deg) 
	{
		return deg * (Math.PI / 180.0);
	}
	
	static public String getEncodedImage(String path) 
	{
		Bitmap bm = BitmapFactory.decodeFile(path);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); 
													
		byte[] byteArrayImage = baos.toByteArray();
		return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
	}
	
	static public boolean checkValidAge(Date date)
	{
		// get birthday date
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// get age in milliseconds
		long diff = Calendar.getInstance().getTimeInMillis() - cal.getTimeInMillis(); 
		// calculate full years number
		int years = (int) ((double) diff / 1000.0 / 60.0 / 60.0 / 24.0 / 365.242199);
		
		return (years >= 21); 
	}
	
	static public Date parseDate(String str)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());  
		try 
		{  
		    return format.parse(str);  
		} 
		catch (ParseException e) 
		{
			format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		    try 
		    {
				return format.parse(str);
			} 
		    catch (ParseException e1) 
		    {
		    	return null;
			}  
		}
	}
	
	static public boolean isToday(Date date)
	{
		Calendar calCurrent = GregorianCalendar.getInstance();
		
		Calendar calDate = GregorianCalendar.getInstance();
		calDate.setTime(date);
		
		if ((calCurrent.get(Calendar.YEAR) == calDate.get(Calendar.YEAR)) &&
			(calCurrent.get(Calendar.DAY_OF_YEAR) == calDate.get(Calendar.DAY_OF_YEAR)))
		{
			return true;
		}
		
		return false;
	}
	
	static public boolean isAuthorized(AppSettings appSettings)
	{
		return ((appSettings.getUserToken() != null) && (appSettings.getUserToken().length() > 0));
	}
	
	static public int getScreenWidth()
	{
    	WindowManager wm = (WindowManager) ControlApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
    	
    	Point size = new Point();
    	wm.getDefaultDisplay().getSize(size);
    	
    	return size.x;
	}
	
	static public int getScreenHeight()
	{
    	WindowManager wm = (WindowManager) ControlApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
    	
    	Point size = new Point();
    	wm.getDefaultDisplay().getSize(size);
    	
    	return size.y;
	}
}
