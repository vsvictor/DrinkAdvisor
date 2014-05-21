package com.drink.imageloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ImagesStore extends Observable {
	
	private static final int PROCESS_STATE_LOADING = 0;
	private static final int PROCESS_STATE_DONE = 1;
	private static final int PROCESS_STATE_ERROR = 2;
	private static final int MAX_ERRORS_COUNT = 3;

	private HashMap<String, Image> mImages = new HashMap<String, Image>();
	private ArrayList<AsyncTask<String, Void, Bitmap>> mTasksList = new ArrayList<AsyncTask<String,Void,Bitmap>>();
	private String mCacheDir = null;

	public ImagesStore(String cacheDir) 
	{
		super();
		setCacheDir(cacheDir);
	}
	
	private void setCacheDir(String dir) 
	{
		if (dir == null) return;
		if (dir.equals(mCacheDir)) return;
		
		mCacheDir = dir.trim();
		File file = new File(mCacheDir);
		if (!file.exists())
		{
			file.mkdirs();
		}
	}
	
	public synchronized Bitmap getImage(String url) 
	{
		Image image = mImages.get(url);
		if (image == null) 
		{
			Bitmap bmp = getFromCache(url);
			if (bmp == null) 
			{
				createImage(url);
				new LoadInBackground().execute(url);
				
				return null;
			} 
			else 
			{
				createImage(url);
				updateImage(url, bmp);
				
				return bmp;
			}
		} 
		else 
		{
			switch (image.state) 
			{
			case PROCESS_STATE_DONE:
				return image.bmp;
			case PROCESS_STATE_LOADING:
				return null;
			case PROCESS_STATE_ERROR:
				if (image.errors < MAX_ERRORS_COUNT) 
				{
					image.state = PROCESS_STATE_LOADING;
					new LoadInBackground().execute(url);
				}
				return null;
			default:
				return null;
			}
		}
	}
	
	public synchronized boolean checkError(String url) 
	{
		Image image = mImages.get(url);
		if (image == null) return true;
		
		return image.errors > 0;
	}
	
	private void createImage(String url) 
	{
		Image image = new Image();
		image.state = PROCESS_STATE_LOADING;
		mImages.put(url, image);
	}
	
	public void updateImage(String url, Bitmap bmp) 
	{
		Image image = mImages.get(url);
		if (image == null) 
		{
			createImage(url);
			image = mImages.get(url);
		}
		image.bmp = bmp;
		if (bmp == null) 
		{
			image.state = PROCESS_STATE_ERROR;
			image.errors++;
		} 
		else 
		{
			image.state = PROCESS_STATE_DONE;
		}
	}
	
	public void removeImage(String url)
	{
		mImages.remove(url);
		removeFromCache(url);
	}
	
	public synchronized void recycle() 
	{
		// Remove all obervers.
		deleteObservers();
		// Destroy all current tasks.
		for (AsyncTask<String, Void, Bitmap> task : mTasksList)
			task.cancel(true);
		mTasksList.clear();
		// Recycle all images.
		for (Entry<String, Image> item : mImages.entrySet()) 
		{
			Image image = item.getValue();
			if (image.state == PROCESS_STATE_DONE)
			{
				image.bmp.recycle();
			}
		}
		mImages.clear();
	}
	
	public File getFileByURL(String url) 
	{
		if (url == null)
		{
			throw new NullPointerException("ImagesStore: URL is null.");
		}
		
		StringBuilder sb = new StringBuilder(mCacheDir).append("/").append(Integer.toHexString(url.trim().toLowerCase().hashCode()));
		return new File(sb.toString());
	}
	
	private Bitmap getFromCache(String url) 
	{
		if (mCacheDir == null) return null;
		
		File file = getFileByURL(url);
		long fSize = file.length();
		Bitmap bitmap = null;
		if (file.exists())
		{
			try 
			{
			    BitmapFactory.Options options = new BitmapFactory.Options();
			    options.inSampleSize = 1;
				if (fSize > 400 * 1024 && fSize < 600 * 1024)
				{
				    options.inSampleSize = 2;
				}
				else if (fSize >= 600 * 1024 && fSize <= 1024 * 1024)
				{
				    options.inSampleSize = 2;
				}
				else if (fSize > 1024 * 1024)
				{
				    options.inSampleSize = 4;
				}
			    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			 } 
			catch (OutOfMemoryError e) 
			{
			    e.printStackTrace();
			    try 
			    {
				    BitmapFactory.Options options = new BitmapFactory.Options();
				    options.inSampleSize = 4;
			        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			    } 
			    catch (OutOfMemoryError e2) 
			    {
			    	e2.printStackTrace();
			    }
			}
		}
		
		return bitmap;
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
	
	private void removeFromCache(String url)
	{
		if (mCacheDir == null) return;
		
		File file = getFileByURL(url);
		file.delete();
	}
	
	private class Image 
	{
		public Bitmap bmp;
		public int state;
		public int errors;
	}
	
	private class LoadInBackground extends AsyncTask<String, Void, Bitmap> 
	{
		private String imageURL;
		
		@Override
		protected void onPreExecute() 
		{
			mTasksList.add(this);
		}
		
		@Override
		protected Bitmap doInBackground(String... urls) 
		{
			imageURL = urls[0];
			try 
			{
				String url = imageURL;
				url = url.replace(" ", "%20");
				return BitmapFactory.decodeStream(new URL(url).openStream());
			} 
			catch (Exception Ex) 
			{
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap bmp) 
		{
			mTasksList.remove(this);
			synchronized (this) { updateImage(imageURL, bmp); }
			putToCache(imageURL, bmp);
			setChanged();
			notifyObservers(imageURL);
		}
	}
}