package com.lurencun.imageloader;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.lurencun.imageloader.internal.TaskParams;

public class BitmapDrawWorker implements Runnable{

	static final String TAG = "DRAW";
	
	private TaskParams params;
	private LazyImageLoader loader;
	private Bitmap bitmap;
	
	public BitmapDrawWorker(Bitmap bitmap, TaskParams params, LazyImageLoader loader){
		this.bitmap = bitmap;
		this.params = params;
		this.loader = loader;
	}
	
	@Override
	public void run() {
		ImageView displayer = params.displayer();
		if(displayer == null){
			if(LazyImageLoader.DEBUG){
				final String message = "[DRAWING] ~ The display view had been recycle, abort to display. ";
				Log.e(TAG, String.format(message));
			}
			return;
		}
		synchronized(displayer){
			if(bitmap != null && !bitmap.isRecycled()){
				displayer.setImageBitmap(bitmap);
			}else{
				if(LazyImageLoader.DEBUG){
					final String message = "[DRAWING] ~ Sended BITMAP to draw, but it was NULL or has been RECYCLED. ";
					Log.e(TAG, String.format(message));
				}
				loader.uiDrawableHandler.post(new StubDrawWorker(params));
			}
			if(LazyImageLoader.options.enablePostInvalidate){
	    		displayer.postInvalidate();
	    	}
		}
	}
}
