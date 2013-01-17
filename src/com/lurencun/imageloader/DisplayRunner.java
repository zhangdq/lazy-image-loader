package com.lurencun.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class DisplayRunner implements Runnable {

	public static int stubResid = -1;
	
	private final ImageView view;
	private final Bitmap bitmap;
	
	public DisplayRunner(ImageView view, Bitmap bitmap){
		this.view = view;
		this.bitmap = bitmap;
	}
	
	@Override
	public void run() {
		if(bitmap != null && !bitmap.isRecycled()){
			view.setImageBitmap(bitmap);
		}else{
			if(stubResid>0) view.setImageResource(stubResid);
		}
		view.postInvalidate();
	}

}
