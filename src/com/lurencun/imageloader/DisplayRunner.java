package com.lurencun.imageloader;

import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.widget.ImageView;

public class DisplayRunner implements Runnable {

	public static int stubResid = -1;
	
	private final ImageView view;
	private final Bitmap bitmap;
	private final Animation displayAnimation;
	
	public DisplayRunner(ImageView view, Bitmap bitmap,Animation displayAnimation){
		this.view = view;
		this.bitmap = bitmap;
		this.displayAnimation = displayAnimation;
	}
	
	@Override
	public void run() {
		if(bitmap != null && !bitmap.isRecycled()){
			view.setImageBitmap(bitmap);
			if(displayAnimation != null){
				view.startAnimation(displayAnimation);
			}
		}else{
			if(stubResid>0) view.setImageResource(stubResid);
			view.postInvalidate();
		}
	}

}
