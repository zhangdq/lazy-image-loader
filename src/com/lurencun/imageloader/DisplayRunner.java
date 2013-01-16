package com.lurencun.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class DisplayRunner implements Runnable {

	private final ImageView view;
	private final Bitmap bitmap;
	
	public DisplayRunner(ImageView view, Bitmap bitmap){
		this.view = view;
		this.bitmap = bitmap;
	}
	
	@Override
	public void run() {
		if(!bitmap.isRecycled()){
			view.setImageBitmap(bitmap);
			view.postInvalidate();
		}
	}

}
