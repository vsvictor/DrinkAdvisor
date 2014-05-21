
package com.drink.imageloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import com.drink.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;

public class WebImageView extends ImageSwitcher implements ViewFactory, Observer 
{
	private String mImageURL;
	private String mCacheDir;
	private LoadInBackground mLoadingTask;
	private ImagesStore mImagesStore = null;
	private String mExpTime;
	private int boundBoxInDp = -1;
	private View view;
	
	public WebImageView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		setFactory(this);
		setAnimateFirstView(false);
	}
	
	public String getURL()
	{
		return mImageURL;
	}
	
	public boolean update()
	{
		if (mImageURL == null) return false;
		
		String url = mImageURL;
		
		mImageURL = null;
		setImageURL(url, "123");
		
		return true;
	}
	
	public void setImagesStore(ImagesStore store) 
	{
		assert(store != null);
		assert(mImagesStore == null);
		
		mImagesStore = store;
		mImagesStore.addObserver(this);
	}
	
	public void setImageBitmap(Bitmap bmp) 
	{
		if (view != null)
		{
			view.setVisibility(View.VISIBLE);
		}
		else
		{
			view = getNextView();
			showNext();
		}
		view.setBackgroundDrawable(new BitmapDrawable(bmp));

		if (boundBoxInDp > 0)
		{
		    int width = bmp.getWidth();
		    int height = bmp.getHeight();
		    
		    float k = (float) width / (float) height;
		    
		    view.setLayoutParams(new FrameLayout.LayoutParams(boundBoxInDp, (int) ((float) boundBoxInDp / k)));
		}
	}
	
	public void setBoundBox (int width)
	{	
		boundBoxInDp = width;
	}

	public void setImageURL(String url, String expTime) 
	{
		if (mImageURL == url) return;
		
		mImageURL = url;
		mExpTime = expTime;
		Bitmap bmp = null;
		if (mImagesStore != null) 
		{
			bmp = mImagesStore.getImage(url);
			if (bmp != null)
			{
				setImageBitmap(bmp);
			}
			else
			{
//				setImageResource(R.drawable.i_image_loading);
				
				if (view != null)
				{
					view.setVisibility(View.GONE);
				}
			}
			
			return;
		}
		bmp = getFromCache(url);
		if (bmp == null)
		{
			if (mLoadingTask != null)
			{
				mLoadingTask.cancel(true);
			}
			mLoadingTask = (LoadInBackground) new LoadInBackground().execute(url);
		}
		else
		{
			setImageBitmap(bmp);
		}
	}
	
	@Override
	public View makeView() 
	{
		ImageView imageView = new ImageView(getContext());
		ImageSwitcher.LayoutParams prms = new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//		prms.gravity = Gravity.CENTER;
		imageView.setLayoutParams(prms);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		
		return imageView;
	}
	
	@Override
	public void update(Observable observable, Object data) 
	{
		if (mImageURL == null) return;
		
		Bitmap bmp = mImagesStore.getImage(mImageURL);
		if (bmp != null)
		{
			setImageBitmap(bmp);
		}
	}
	
	public void setCacheDir(String dir) 
	{
		if (dir == null)
		{
			throw new NullPointerException("WebImageView: Recieved null cache directory.");
		}
		if (dir.equals(mCacheDir)) return;
		
		mCacheDir = dir.trim();
		File file = new File(mCacheDir);
		if (!file.exists())
		{
			file.mkdirs();
		}
	}
	
	private Bitmap getFromCache(String url) 
	{
		if (mCacheDir == null) return null;
		
		File file = getFileByURL(url);
		if (file.exists())
		{
			return BitmapFactory.decodeFile(file.getAbsolutePath());
		}
		
		return null;
	}
	
	private void putToCache(String url, Bitmap bmp) 
	{
		if (mCacheDir == null || bmp == null) return;
		
		File file = getFileByURL(url);
		FileOutputStream outputStream;
		try 
		{
			file.createNewFile();
			outputStream = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
			outputStream.close();
		} 
		catch (IOException Ex) {}
	}
	
	private File getFileByURL(String url) 
	{
		if (url == null)
		{
			throw new NullPointerException("WebImageView: URL is null.");
		}
		
		StringBuilder sb = new StringBuilder(mCacheDir).append("/").append(mExpTime).append("_").append(Integer.toHexString(url.trim().hashCode()));
		return new File(sb.toString());
	}
	
	private class LoadInBackground extends AsyncTask<String, Void, Bitmap> 
	{
		private String imageURL;
		
		@Override
		protected void onPreExecute() 
		{
			setImageResource(R.drawable.i_image_loading);
		}
		
		@Override
		protected Bitmap doInBackground(String... urls) 
		{
			imageURL = urls[0];
			try 
			{
				Bitmap bmp = BitmapFactory.decodeStream(new URL(imageURL).openStream());
				if (bmp != null)
				{
					putToCache(imageURL, bmp);
				}
				
				return bmp;
			} 
			catch (Exception Ex) 
			{
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap bmp) 
		{
			if (bmp != null)
			{
				setImageBitmap(bmp);
			}
		}
	}
}