package com.lurencun.imageloader.internal;

import android.graphics.Bitmap;

public class DisplayRunner implements Runnable {

	private final TaskRequest request;
	private final Bitmap bitmap;
	
	public DisplayRunner(TaskRequest request, Bitmap bitmap){
		this.request = request;
		this.bitmap = bitmap;
	}
	
	@Override
	public void run() {
		request.view.setImageBitmap(bitmap);
	}

}
