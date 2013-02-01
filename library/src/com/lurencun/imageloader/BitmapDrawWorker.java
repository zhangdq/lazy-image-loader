package com.lurencun.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.lurencun.imageloader.internal.TaskParams;

public class BitmapDrawWorker implements Runnable{

	static final String TAG = "DRAW";
	
	private TaskParams params;
	private Bitmap bitmap;
	
	public BitmapDrawWorker(Bitmap bitmap, TaskParams params){
		this.bitmap = bitmap;
		this.params = params;
	}
	
	@Override
	public void run() {
		ImageView displayer = params.displayer();
		if(displayer == null){
			return;
		}
		if(bitmap != null && !bitmap.isRecycled()){
			displayer.setImageBitmap(bitmap);
		}else{
			displayer.setImageResource(LazyImageLoader.options.imageStubResId);
		}
		if(LazyImageLoader.options.enablePostInvalidate){
    		displayer.postInvalidate();
    	}
	}
}
