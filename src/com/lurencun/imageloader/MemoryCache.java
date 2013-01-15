package com.lurencun.imageloader;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import android.graphics.Bitmap;
import android.util.Log;

public class MemoryCache {

    private static final String TAG = "MemoryCache";
    private final Map<String, Bitmap> cache;
    private long allocatedSize;
    private long maxMemoryLimit;
    
    private final boolean enableLogging;

    public MemoryCache(LoaderOptions options){
    	setLimit(options.maxMemoryInByte);
    	cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10,1.5f,true));
    	enableLogging = options.logging;
    }
    
    public void setLimit(long new_limit){
        maxMemoryLimit = new_limit;
        if(enableLogging) Log.i(TAG, "MemoryCache size will use up to " + getSizeString(maxMemoryLimit));
    }

    public Bitmap get(String url){
        try{
            if(!cache.containsKey(url))  return null;
            return cache.get(url);
        }catch(NullPointerException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public void put(String url, Bitmap bitmap){
        try{
            if(cache.containsKey(url)){
            	allocatedSize -= getSizeInBytes(cache.get(url));
            }
            cache.put(url, bitmap);
            allocatedSize += getSizeInBytes(bitmap);
            keepInMemorySize();
        }catch(Throwable th){
            th.printStackTrace();
        }
    }
    
    private void keepInMemorySize() {
    	if(enableLogging) Log.i(TAG, "MemoryCache TotalSize: " + getSizeString(allocatedSize) + ", CacheObject count: " + cache.size());
        if(allocatedSize > maxMemoryLimit){
            Iterator<Entry<String, Bitmap>> iter=cache.entrySet().iterator(); 
            while(iter.hasNext()){
                Entry<String, Bitmap> entry=iter.next();
                Bitmap bitmap = entry.getValue();
                allocatedSize -= getSizeInBytes(bitmap);
                if(bitmap != null && bitmap.isRecycled()){
                	bitmap.recycle();
                }
                iter.remove();
                if(allocatedSize <=maxMemoryLimit) break;
            }
            System.gc();
            if(enableLogging) Log.i(TAG, "Cleaned memory cache, CacheObject count: " + cache.size());
        }
    }
    
    private String getSizeString(long size){
    	return String.format(Locale.getDefault(),"%.2f MB", (size/1024./1024.));
    }

    public void clear() {
        try{
            cache.clear();
            allocatedSize =0;
        }catch(NullPointerException ex){
            ex.printStackTrace();
        }
    }
    
    public void remove(String uri){
    	if(uri == null) return;
    	if(cache.containsKey(uri)){
    		Bitmap bitmap = cache.get(uri);
    		if(bitmap != null && bitmap.isRecycled()){
    			allocatedSize -= getSizeInBytes(bitmap);
            	bitmap.recycle();
            }
    		cache.remove(uri);
    	}
    }

    long getSizeInBytes(Bitmap bitmap) {
        if(bitmap==null) return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}