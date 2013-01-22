package com.lurencun.imageloader;

import android.widget.ImageView;

import com.lurencun.imageloader.internal.TaskParams;

public class StubDrawWorker implements Runnable{

	static final String TAG = "DRAW";
	
	private TaskParams params;
	
	public StubDrawWorker(TaskParams params){
		this.params = params;
	}
	
	@Override
	public void run() {
		ImageView displayer = params.displayer();
		if(displayer == null) return;
		synchronized(displayer){
			displayer.setImageBitmap(null);
			displayer.setImageResource(LazyImageLoader.options.imageStubResId);
	    	if(LazyImageLoader.options.enablePostInvalidate){
	    		displayer.postInvalidate();
	    	}
		}
	}
}
