package com.lurencun.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.lurencun.imageloader.internal.DrawWorker;
import com.lurencun.imageloader.internal.TaskParams;

public class BitmapDrawWorker extends DrawWorker {

	private Bitmap bitmap;
	
	public BitmapDrawWorker(Bitmap bitmap, TaskParams params){
		super(params);
		this.bitmap = bitmap;
	}
	
	@Override
	public void run() {
		ImageView displayer = params.displayer();
		if(displayer == null) return;
		if(bitmap != null && !bitmap.isRecycled()){
			displayer.setImageBitmap(bitmap);
		}else{
			displayer.setImageResource(LazyImageLoader.options.imageStubResId);
		}
    	displayer.postInvalidate();
	}
}
