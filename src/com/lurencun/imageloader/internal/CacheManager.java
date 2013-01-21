package com.lurencun.imageloader.internal;

import java.io.File;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.lurencun.imageloader.LazyImageLoader;
import com.lurencun.imageloader.LoaderOptions;

public class CacheManager {

	static final String TAG = "CacheManager";
	
	private LruCache<String, Bitmap> memoryCache;
	private SimpleDiskCache diskCache;
	
	public CacheManager(Context context,LoaderOptions options){
		diskCache = new SimpleDiskCache(context, options);
		if(options.enableMemoryCache){
			if(LazyImageLoader.DEBUG){
				final String message = "[MEMORY CACHE] ~ Memory cache set up to %s";
				Log.i(TAG, String.format(message, makeSizeFormat(options.maxMemoryInByte)));
			}
			memoryCache = new LruCache<String, Bitmap>(options.maxMemoryInByte) {
	            @Override
	            protected int sizeOf(String key, Bitmap bitmap) {
	            	return bitmap.getRowBytes() * bitmap.getHeight();
	            }
	        };
		}
	}
	
	public File getFromDiskCache(String fileName){
		return diskCache.get(fileName);
	}
	
	public void addToMemoryCache(String key,Bitmap bitmap){
        if (memoryCache != null && memoryCache.get(key) == null) {
        	memoryCache.put(key, bitmap);
        }
	}
	
	public Bitmap getFromMemoryCache(String key) {
        if (memoryCache != null) {
            final Bitmap bitmap = memoryCache.get(key);
            if (bitmap != null) {
            	if(!bitmap.isRecycled()){
            		return bitmap;
            	}else{
            		if(LazyImageLoader.DEBUG){
                		final String message = "[MEMORY] ~ Current item had RECYCLED, remove it. INFO{ key:\"%s\" }";
    					Log.d(TAG, String.format(message, key));
    				}
            		memoryCache.remove(key);
            	}
            }
        }
        return null;
    }
	
	void clearMemoryCache(){
		if (memoryCache != null) {
			memoryCache.evictAll();
        }
	}
	
	public static String makeSizeFormat(long size){
    	return String.format(Locale.getDefault(),"%.2f MB", (size/1024./1024.));
    }

}
