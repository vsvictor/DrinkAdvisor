package com.drink.widgets;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.drink.R;
import com.drink.helpers.CommonHelper;
import com.drink.imageloader.ImagesStore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class ImagesViewer extends RelativeLayout implements Observer
{
	private ImageView imageFrom;
	private ImageView imageTo;
	private ImageView[] dots = new ImageView[5];
	private ProgressBar progress;
	private int ind = -1;
	private ArrayList<String> urls = new ArrayList<String>();
	
	private ImagesStore mImagesStore;
	private int	mHeight = 0;
	
	private float startX = 0.0f;
	private float startY = 0.0f;
	private boolean move = false;

	public ImagesViewer(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_images_viewer, this);
     
        imageFrom = (ImageView) findViewById(R.id.image_from);
        imageTo = (ImageView) findViewById(R.id.image_to);
        
        dots[0] = (ImageView) findViewById(R.id.dot_1);
        dots[1] = (ImageView) findViewById(R.id.dot_2);
        dots[2] = (ImageView) findViewById(R.id.dot_3);
        dots[3] = (ImageView) findViewById(R.id.dot_4);
        dots[4] = (ImageView) findViewById(R.id.dot_5);
        
        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);
	}

	private void setPicture(int ind)
	{
		int prev = this.ind;
		
		this.ind = ind;  

        final Bitmap bmp = mImagesStore.getImage(urls.get(ind));

        if (prev == -1)
		{
	        imageFrom.setVisibility(View.VISIBLE);
	        imageTo.setVisibility(View.INVISIBLE);
	        
	        if (bmp != null)
	        {
	        	imageFrom.setImageBitmap(bmp);
	        }
	        else
	        {
	        	progress.setVisibility(View.VISIBLE);
	        }
		}
        else
        {
	        imageFrom.setVisibility(View.VISIBLE);
	        imageTo.setVisibility(View.VISIBLE);
	
	        if (bmp != null)
	        {
	        	imageTo.setImageBitmap(bmp);
	        }

	        float finishFrom = (prev > ind ? CommonHelper.getScreenWidth() : -CommonHelper.getScreenWidth());
	        
	        TranslateAnimation animFrom = new TranslateAnimation(0.0f, finishFrom, 0.0f, 0.0f);
	        animFrom.setStartOffset(0);
	        animFrom.setDuration(300);
	        animFrom.setAnimationListener(new AnimationListener() 
	        {
				@Override
				public void onAnimationStart(Animation animation) 
				{
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) 
				{
				}
				
				@Override
				public void onAnimationEnd(Animation animation) 
				{
					imageFrom.setImageBitmap(bmp);

					if (bmp == null)
			        {
			        	progress.setVisibility(View.VISIBLE);
			        }
					
					imageTo.setVisibility(View.INVISIBLE);
				}
			});
			imageFrom.startAnimation(animFrom);
			
	        TranslateAnimation animTo = new TranslateAnimation(-finishFrom, 0.0f, 0.0f, 0.0f);
	        animTo.setStartOffset(0);
	        animTo.setDuration(300);
			imageTo.startAnimation(animTo);
        }
	}
	
	public void setURLs(ArrayList<String> urls)
	{
		this.urls = urls;

		setPicture(0);
		
		updateDots();
	}
	
	public void next()
	{
		if (ind >= urls.size() - 1) return;
		
		setPicture(ind + 1);
		
		updateDots();
	}
	
	public void previous()
	{
		if (ind <= 0) return;

		setPicture(ind - 1);
		
		updateDots();
	}
	   
	private void updateDots()
	{
		Drawable circleLight = getContext().getResources().getDrawable(R.drawable.images_viewer_circle_light);
		Drawable circleDark = getContext().getResources().getDrawable(R.drawable.images_viewer_circle_dark);
		
		for (int i = 0; i < 5; ++i)
		{
			if ((i < urls.size()) && (urls.size() > 1))
			{
				dots[i].setVisibility(View.VISIBLE);
				if (i == ind)
				{
					dots[i].setImageDrawable(circleDark);
				}
				else
				{
					dots[i].setImageDrawable(circleLight);
				}
			}
			else
			{
				dots[i].setVisibility(View.GONE);
			}
		}
	}

	public void setHeight(int height)
	{
		mHeight = height;

		if (mHeight > 0)
		{
			float k = getResources().getDisplayMetrics().scaledDensity;
			imageFrom.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) ((float) mHeight * k)));
			imageTo.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) ((float) mHeight * k)));
		}
		else
		{
			imageFrom.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			imageTo.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
	}

	public void setImagesStore(ImagesStore store) 
	{
		assert(store != null);
		assert(mImagesStore == null);
		
		mImagesStore = store;
		mImagesStore.addObserver(this);
	}

	@Override
	public void update(Observable observable, Object data) 
	{
		int ind = -1;
		for (int i = 0; i < urls.size(); ++i)
		{
			if (urls.get(i).equalsIgnoreCase((String) data))
			{
				ind = i;
				break;
			}
		}
		
		if (ind == -1) return;

		if (this.ind == ind)
		{
			final Bitmap bmp = mImagesStore.getImage((String) data);
			imageFrom.post(new Runnable() 
			{
				@Override
				public void run() 
				{
		  			progress.setVisibility(View.INVISIBLE);
			  		
		  			imageFrom.setImageBitmap(bmp);
				}
			});
		}
	}

    @Override 
    public boolean onTouchEvent(MotionEvent event)
    {
    	if (event.getAction() == MotionEvent.ACTION_DOWN)
    	{
    		startX = event.getX();
    		startY = event.getY();
    		
    		move = true;
    		
    		return true;
    	}
    	else if ((event.getAction() == MotionEvent.ACTION_UP) && move)
    	{
    		move = false;
    		
    		return true;
    	}
    	else if ((event.getAction() == MotionEvent.ACTION_MOVE) && move)
    	{
    		if (event.getX() < startX)
    		{
    			if (startX - event.getX() > 30.0f * getContext().getResources().getDisplayMetrics().scaledDensity)
    			{
    				next();
    				
    				move = false;
    				
    				return true;
    			}
    		}
    		else
    		{
    			if (event.getX() - startX > 50.0f * getContext().getResources().getDisplayMetrics().scaledDensity)
    			{
    				previous();
    				
    				move = false;
    				
    				return true;
    			}
    		}
    		
    		if (Math.abs(event.getY() - startY) > 20.0f * getContext().getResources().getDisplayMetrics().density)
    		{
    			move = false;
    		}
    	}

        return super.onTouchEvent(event);
    }
}
