package com.drink.activity;

import java.io.InputStream;
import java.util.ArrayList;
import com.drink.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class PicturesActivity extends BasicActivity 
{
	private PicAdapter imgAdapt;
	private Gallery picGallery;
	
	ArrayList<String> arrUrl;

	public PicturesActivity() 
	{
		super(R.layout.activity_pictures);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		arrUrl = bundle.getStringArrayList("urls");		
		
		picGallery = (Gallery) findViewById(R.id.gallery);
		imgAdapt = new PicAdapter(this);
		
		for (String url : arrUrl) 
		{
			Bitmap btm = mImagesStore.getImage(url);
			if (btm == null)
			{
				new DownloadImageTask().execute(url);
			}
			else
			{
				imgAdapt.addPic(btm);
			}
		}

		picGallery.setAdapter(imgAdapt);

		int index = bundle.getInt("index");
		if (index < imgAdapt.getCount())
		{
			picGallery.setSelection(index);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_pictures, menu);
		return true;
	}
	
    public class PicAdapter extends BaseAdapter 
    {
        int defaultItemBackground;
        private Context galleryContext;
        private ArrayList<Bitmap> imageBitmaps;
        Bitmap placeholder;

        public PicAdapter(Context c) 
        {
        	galleryContext = c;        	
            imageBitmaps  = new ArrayList<Bitmap>();
            TypedArray styleAttrs = galleryContext.obtainStyledAttributes(R.styleable.PicGallery);
            defaultItemBackground = styleAttrs.getResourceId(R.styleable.PicGallery_android_galleryItemBackground, 0);
            styleAttrs.recycle();
        }

        public int getCount() 
        {
        	return imageBitmaps.size();
        }

        public Object getItem(int position) 
        {
            return position;
        }

        public long getItemId(int position) 
        {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView imageView = new ImageView(galleryContext);
            imageView.setImageBitmap(imageBitmaps.get(position));

		    int width = imageBitmaps.get(position).getWidth();
		    float xScale = ((float) (getWindowManager().getDefaultDisplay().getWidth())) / width;

            imageView.setLayoutParams(new Gallery.LayoutParams((int)(xScale * width), (int)((xScale * width) / 2)));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setBackgroundResource(defaultItemBackground);
            imageView.setOnClickListener(new OnClickListener() 
            {
				@Override
				public void onClick(View arg0) 
				{
					onBackPressed();
				}
			});
            
            return imageView;
        }
        
        public void addPic(Bitmap newPic)
        {
        	imageBitmaps.add(newPic);
        }
        
        public Bitmap getPic(int posn)
        {
        	return imageBitmaps.get(posn);
        }
    }
    
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> 
    {
    	@Override
		protected Bitmap doInBackground(String... params) 
    	{
			String url = params[0];
            Bitmap mIcon11 = null;
            try 
            {	
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
            
            return mIcon11;
		}
    	
    	@Override
        protected void onPostExecute(Bitmap result) 
    	{
        	if (result != null)
        	{
            	imgAdapt.addPic(result);
        	}
			picGallery.setAdapter(imgAdapt);
			
			super.onPostExecute(result);
        }
    }
}
