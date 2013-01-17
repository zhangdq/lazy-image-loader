package com.lurencun.imageloader;

import android.graphics.Bitmap;

public class CacheWrapper {

	public final Bitmap bitmap;
	public final boolean isCompressed;
	public final String key;
	public final long size;
	
	private boolean isUsing;
	
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
	
	public synchronized boolean isRecycled(){
		return bitmap == null || bitmap.isRecycled();
	}
	
	public synchronized boolean isUsing(){
		return isUsing;
	}
	
	public synchronized void setIsUsing(){
		isUsing = true;
	}
	
	public synchronized void setUnUsing(){
		isUsing = false;
	}
	
	/**
	 * 当缓存正在被使用，缓存将拒绝回收
	 * @return
	 */
	public void recycle(){
		if(isUsing) return;
		if(!isRecycled()){
			bitmap.recycle();
		}
		System.gc();
	}
	
	public void forceRecycle(){
		if(!isRecycled()){
			bitmap.recycle();
		}
		System.gc();
	}
}
