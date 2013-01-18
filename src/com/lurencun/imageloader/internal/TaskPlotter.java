package com.lurencun.imageloader.internal;

import com.lurencun.imageloader.LazyImageLoader;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

public class TaskPlotter implements Runnable {

	static final String TAG = "TaskPlotter";
	
	public static int stubResID;
	
	private final ImageView displayer;
	private final Bitmap bitmap;
	
	public TaskPlotter(ImageView displayer, Bitmap bitmap){
		this.displayer = displayer;
		this.bitmap = bitmap;
	}
	
	@Override
	public void run() {
		if(bitmap != null && !bitmap.isRecycled()){
			displayer.setImageBitmap(bitmap);
		}else{
			if(LazyImageLoader.DEBUG){
				final String message = "[DRAWING] ~ Sended BITMAP to draw, but it was NULL or has been RECYCLED. ";
				Log.e(TAG, String.format(message));
			}
			displayer.setImageBitmap(null);
			displayer.setImageResource(stubResID);
		}
		displayer.postInvalidate();
	}

}
