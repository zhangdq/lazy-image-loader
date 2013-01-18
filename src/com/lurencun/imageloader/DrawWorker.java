package com.lurencun.imageloader;

import android.graphics.Bitmap;
import android.util.Log;

import com.lurencun.imageloader.internal.TaskParams;

public class DrawWorker implements Runnable{

	static final String TAG = "DRAW";
	
	private TaskParams params;
	private LazyImageLoader loader;
	private Bitmap bitmap;
	
	public DrawWorker(Bitmap bitmap, TaskParams params, LazyImageLoader loader){
		this.bitmap = bitmap;
		this.params = params;
		this.loader = loader;
	}
	
	@Override
	public void run() {
		//图片解码存在延时，View可能被重用。检查！
		if(!loader.isTargetDisplayerMappingBroken(params.targetUri, params.displayer)){
			drawToDisplayer();
		}else{
			if(bitmap != null && !bitmap.isRecycled()){
				bitmap.recycle();
			}
			loader.clearWithStub(params.displayer);
			if(LazyImageLoader.DEBUG){
				final String message = "[DECODE] ~ Sended FILE to decode, but DISPLAY view seem to been reused. ";
				Log.e(TAG, String.format(message));
			}
			bitmap = null;
		}
	}
	
	void drawToDisplayer(){
		if(bitmap != null && !bitmap.isRecycled()){
			params.displayer.setImageBitmap(bitmap);
		}else{
			if(LazyImageLoader.DEBUG){
				final String message = "[DRAWING] ~ Sended BITMAP to draw, but it was NULL or has been RECYCLED. ";
				Log.e(TAG, String.format(message));
			}
			loader.clearWithStub(params.displayer);
		}
		params.displayer.postInvalidate();
	}
	
}
