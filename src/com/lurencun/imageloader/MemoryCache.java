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
    
    private final Map<String, CacheWrapper> CACHE;
    private long allocatedSize;
    private long maxMemoryLimit;
    
    private final boolean enableLogging;

    public MemoryCache(LoaderOptions options){
    	CACHE = Collections.synchronizedMap(new LinkedHashMap<String, CacheWrapper>(10,1.5f,true));
    	enableLogging = options.logging;
    	maxMemoryLimit = options.maxMemoryInByte;
        if(enableLogging) Log.i(TAG, "MemoryCache MAX_SIZE = " + getSizeFormat(maxMemoryLimit));
    }
    
    public CacheWrapper get(String url){
		try {
			return CACHE.containsKey(url) ? CACHE.get(url) : null;
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			return null;
		}
    }
    
    public void put(String url, Bitmap bitmap, boolean isCompressed){
    	keepMemorySize();
    	try{
    		remove(url);
    		CacheWrapper cache = new CacheWrapper(url, bitmap, isCompressed);
    		allocatedSize += cache.size;
    		CACHE.put(url, cache);
    	}catch(Throwable exp){
    		exp.printStackTrace();
    	}
    }
    
    public void keepMemorySize(){
    	if(enableLogging) Log.i(TAG, "MemoryCache size: " + getSizeFormat(allocatedSize) + ", CacheObject count: " + CACHE.size());
        if(allocatedSize > maxMemoryLimit){
        	Iterator<Entry<String, CacheWrapper>> iter = CACHE.entrySet().iterator();
        	while(iter.hasNext()){
        		Entry<String, CacheWrapper> entry = iter.next();
        		CacheWrapper cache = entry.getValue();
        		if(cache.isUsing) continue;
        		allocatedSize -= cache.size;
        		cache.recycle();
        		iter.remove();
            	if(allocatedSize <= maxMemoryLimit) break;
        	}
        }
    }

    
    public void clear() {
        try{
        	Iterator<Entry<String, CacheWrapper>> iter = CACHE.entrySet().iterator();
        	while(iter.hasNext()){
        		Entry<String, CacheWrapper> entry = iter.next();
        		CacheWrapper cache = entry.getValue();
        		if(cache.isUsing) continue;
        		allocatedSize -= cache.size;
        		cache.recycle();
        		iter.remove();
        	}
        }catch(Throwable exp){
        	exp.printStackTrace();
        }
    }
    
    public void remove(String url){
    	if(url == null) return;
    	CacheWrapper cache = get(url);
    	if(cache != null){
    		allocatedSize -= cache.size;
    		cache.recycle();
    	}
    }
    
    String getSizeFormat(long size){
    	return String.format(Locale.getDefault(),"%.2f MB", (size/1024./1024.));
    }

}