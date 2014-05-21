package com.drink.helpers;

import com.drink.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppRater 
{
    private final static String APP_TITLE = "Drink Advisor";
    private final static String APP_PNAME = "com.drink";
    
    private static Context context;
    
    public static void app_launched(Context mContext) 
    {
    	context = mContext;
    	
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        SharedPreferences.Editor editor = prefs.edit();
        
        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);
        editor.commit();

        if (launch_count < 2) return;
        
        boolean rate = false;
        if (launch_count % 3 == 2)
        {
            if (!prefs.getBoolean("dont_show_again_rate", false))
            {
                showRateDialog(mContext, editor);
                rate = true;
            }
        }
        
        if (!rate && (launch_count % 4 == 3))
        {
            if (!prefs.getBoolean("dont_show_again_like", false))
            {
                showLikeDialog(mContext, editor);
            }
        }
    }   
    
    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) 
    {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Rate " + APP_TITLE);

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        
        TextView tv = new TextView(mContext);
        tv.setText(context.getString(R.string.apprater_text));
        tv.setWidth(240);
        tv.setPadding(4, 0, 4, 10);
        ll.addView(tv);
        
        Button b1 = new Button(mContext);
        b1.setText(context.getString(R.string.apprater_rate_it));
        b1.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                editor.putBoolean("dont_show_again_rate", true);
                editor.commit();
                dialog.dismiss();
            }
        });        
        ll.addView(b1);
/*
        Button b2 = new Button(mContext);
        b2.setText(context.getString(R.string.apprater_dont_like));
        b2.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
    		   	Intent intent = new Intent();
				intent.putExtra("ID", "-1");
				intent.setClass(context, SendWrongInfoActivity.class);
				
				context.startActivity(intent);

				editor.putBoolean("dont_show_again_rate", true);
                editor.commit();
                dialog.dismiss();
            }
        });        
        ll.addView(b2);
*/
        Button b3 = new Button(mContext);
        b3.setText(context.getString(R.string.apprater_remind_later));
        b3.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
                dialog.dismiss();
            }
        });
        ll.addView(b3);

        Button b4 = new Button(mContext);
        b4.setText(context.getString(R.string.apprater_dont_show));
        b4.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
                editor.putBoolean("dont_show_again_rate", true);
                editor.commit();
                dialog.dismiss();
            }
        });
        ll.addView(b4);

        dialog.setContentView(ll);        
        dialog.show();        
    }

    public static void showLikeDialog(final Context mContext, final SharedPreferences.Editor editor) 
    {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Like " + APP_TITLE);

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        
        TextView tv = new TextView(mContext);
        tv.setText(context.getString(R.string.apprater_text_like));
        tv.setWidth(240);
        tv.setPadding(4, 0, 4, 10);
        ll.addView(tv);
        
        Button b1 = new Button(mContext);
        b1.setText(context.getString(R.string.apprater_follow));
        b1.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/DrinkAdvisor")));
                editor.putBoolean("dont_show_again_like", true);
                editor.commit();
                dialog.dismiss();
            }
        });        
        ll.addView(b1);

        Button b2 = new Button(mContext);
        b2.setText(context.getString(R.string.apprater_remind_later));
        b2.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
                dialog.dismiss();
            }
        });
        ll.addView(b2);

        Button b3 = new Button(mContext);
        b3.setText(context.getString(R.string.apprater_dont_show));
        b3.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
                editor.putBoolean("dont_show_again_like", true);
                editor.commit();
                dialog.dismiss();
            }
        });
        ll.addView(b3);

        dialog.setContentView(ll);        
        dialog.show();        
    }
}
