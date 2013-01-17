package com.lurencun.imageloader;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;

public class MemoryCache {

    private final Map<String, CacheWrapper> CACHE;
    private long allocatedSize;
    private long maxMemoryLimit;
    
    private final boolean enableLogging;
    
    public MemoryCache(LoaderOptions options){
    	CACHE = Collections.synchronizedMap(new LinkedHashMap<String, CacheWrapper>(10,1.5f,true));
    	enableLogging = options.logging;
    	maxMemoryLimit = options.maxMemoryInByte;
        if(enableLogging) {
        	System.out.println("【INFO】~ [Memory Cache INIT] => { maxSize:" + getSizeFormat(maxMemoryLimit)+" }");
        }
        
    }
    
    public CacheWrapper get(String url){
		try {
			synchronized(CACHE){
				CacheWrapper cache = CACHE.containsKey(url) ? CACHE.get(url) : null;
				if(cache != null && !cache.isRecycled()){
					cache.setIsUsing();
					return cache;
				}else{
					return null;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
    }
    
    public void put(String url, Bitmap bitmap, boolean isCompressed){
    	keepCacheSize();
    	try{
    		remove(url);
    		CacheWrapper cache = new CacheWrapper(url, bitmap, isCompressed);
    		allocatedSize += cache.size;
    		CACHE.put(url, cache);
    	}catch(Throwable exp){
    		exp.printStackTrace();
    	}
    }
    
    public void clear() {
        try{
        	Iterator<Entry<String, CacheWrapper>> iter = CACHE.entrySet().iterator();
        	while(iter.hasNext()){
        		Entry<String, CacheWrapper> entry = iter.next();
        		CacheWrapper cache = entry.getValue();
        		allocatedSize -= cache.size;
        		cache.forceRecycle();
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
    		CACHE.remove(url);
    	}
    }
    
    void keepCacheSize(){
    	if(enableLogging) {
			System.out.println("【INFO】~ [Memory KeepSize] => " +
					"{ "+"CacheSize: " + getSizeFormat(allocatedSize) + ", ObjectCount: " + CACHE.size()+" }");
		}
    	if(allocatedSize > maxMemoryLimit){
        	Iterator<Entry<String, CacheWrapper>> iter = CACHE.entrySet().iterator();
        	while(iter.hasNext()){
        		Entry<String, CacheWrapper> entry = iter.next();
        		CacheWrapper cache = entry.getValue();
        		if(cache.isUsing()) continue;
        		allocatedSize -= cache.size;
        		cache.recycle();
        		iter.remove();
            	if(allocatedSize <= maxMemoryLimit) break;
        	}
        }
    }
    
    String getSizeFormat(long size){
    	return String.format(Locale.getDefault(),"%.1f MB", (size/1024./1024.));
    }

}