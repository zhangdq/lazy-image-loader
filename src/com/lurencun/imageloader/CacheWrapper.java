package com.lurencun.imageloader;

import android.graphics.Bitmap;

public class CacheWrapper {

	public final Bitmap bitmap;
	public final boolean isCompressed;
	public final String key;
	public final long size;
	
	public boolean isUsing;
	
	
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
	
	/**
	 * 当缓存正在被使用，缓存将拒绝回收
	 * @return
	 */
	public void recycle(){
		if(isUsing) return;
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
		}
		System.gc();
	}
}
