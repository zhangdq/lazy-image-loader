package com.lurencun.imageloader.internal;

import android.graphics.Bitmap;

public class CacheWrapper {

	public final Bitmap bitmap;
	public final boolean isCompressed;
	public final String key;
	public final long size;
	
	
	public CacheWrapper(String key, Bitmap bitmap,boolean isCompressed){
		this.key = key;
		this.bitmap = bitmap;
		this.isCompressed = isCompressed;
		this.size = getSizeInBytes(bitmap);
	}
	
	long getSizeInBytes(Bitmap bitmap) {
        if(bitmap==null) return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
	
	public void recycle(){
		if(!bitmap.isRecycled()){
			bitmap.recycle();
		}
		System.gc();
	}
}
