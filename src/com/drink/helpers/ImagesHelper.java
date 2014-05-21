package com.drink.helpers;

import com.drink.ControlApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

public class ImagesHelper 
{
	public static Bitmap getScaledBitmap(Context context, Bitmap bitmap, int width, int height)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		float scaleWidth = (float) width / (float) w;
		float scaleHeight = (float) height / (float) h;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);

		// recreate the new Bitmap
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}
	
	public static Bitmap getCircleBitmap(Bitmap bitmap)
	{
		int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
		
		Bitmap circleBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

	    BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
	    Paint paint = new Paint();
	    paint.setShader(shader);

	    Canvas c = new Canvas(circleBitmap);
	    c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, size / 2, paint);
	    
	    return circleBitmap;
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int color, int cornerDips, int borderDips) 
	{
	    int size = Math.min(bitmap.getWidth(), bitmap.getHeight());

	    Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) borderDips,
	            ControlApplication.getContext().getResources().getDisplayMetrics());
	    final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips,
	    		ControlApplication.getContext().getResources().getDisplayMetrics());
	    final Paint paint = new Paint();
	    final Rect rectFrom = new Rect((bitmap.getWidth() - size) / 2, (bitmap.getHeight() - size) / 2, (bitmap.getWidth() - size) / 2 + size, (bitmap.getHeight() - size) / 2 + size);
	    final Rect rectTo = new Rect(0, 0, size, size);
	    final RectF rectF = new RectF(rectTo);

	    // prepare canvas for transfer
	    paint.setAntiAlias(true);
	    paint.setColor(0xFFFFFFFF);
	    paint.setStyle(Paint.Style.FILL);
	    canvas.drawARGB(0, 0, 0, 0);
	    canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

	    // draw bitmap
	    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rectFrom, rectTo, paint);

	    // draw border
	    paint.setColor(color);
	    paint.setStyle(Paint.Style.STROKE);
	    paint.setStrokeWidth((float) borderSizePx);
	    canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

	    return output;
	}
}
