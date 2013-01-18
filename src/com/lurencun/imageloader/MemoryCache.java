package com.lurencun.imageloader;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Locale;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

public class MemoryCache {

	static boolean DEBUG = true;
	
	static final String TAG = "MemoryCache";
	
	final LruCache<String, Bitmap> strongLRUCache;
	static final int SOFT_CACHE_CAPACITY = 10;
	final LinkedHashMap<String, SoftReference<Bitmap>> weakSoftRefCache;
	
    public MemoryCache(LoaderOptions options){
    	if(DEBUG){
			final String message = "[MEMORY CACHE] ~ LRUCACHE set to %s , WEAKCACHE will set to %d items(MAX).";
			Log.d(TAG, String.format(message, makeSizeFormat(options.maxMemoryInByte),SOFT_CACHE_CAPACITY));
		}
    	strongLRUCache = new LruCache<String, Bitmap>(options.maxMemoryInByte){
			@Override
			public int sizeOf(String key, Bitmap value){
				return value.getRowBytes() * value.getHeight();
			}
			@Override
			protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue){
				if(DEBUG){
					final String message = "[LRU CACHE] ~ LRUCACHE full, cache to WEAKCACHE. INFO{ size:%s, target:\"%s\" }";
					final String size = makeSizeFormat(strongLRUCache.size());
					Log.d(TAG, String.format(message, size, key));
				}
				weakSoftRefCache.put(key, new SoftReference<Bitmap>(oldValue));
			}
    	};
    	weakSoftRefCache = new LinkedHashMap<String, SoftReference<Bitmap>>(SOFT_CACHE_CAPACITY, 0.75f,true){
			private static final long serialVersionUID = 4011842900981762651L;
			@Override
			protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
				return size() > SOFT_CACHE_CAPACITY;
			}
    	};
    }
    
    public void put(String key, Bitmap bitmap){
        synchronized(strongLRUCache){  
        	strongLRUCache.put(key, bitmap);
        }
    }
    
    public Bitmap get(String key){  
        synchronized(strongLRUCache){  
            final Bitmap bitmap = strongLRUCache.get(key);  
            if(bitmap != null)  
                return bitmap;  
        }
        
        synchronized(weakSoftRefCache){  
            SoftReference<Bitmap> reference = weakSoftRefCache.get(key);  
            if(reference != null){  
                final Bitmap bitmap = reference.get();  
                if(bitmap != null)  
                    return bitmap;  
                else{
                	if(DEBUG){
                		final String message = "[WEAK CACHE] ~ Current item has been recycle, remove from WEAKCACHE. INFO{ key:\"%s\" }";
    					Log.d(TAG, String.format(message, key));
    				}
                    weakSoftRefCache.remove(key);  
                }  
            }  
        }  
        return null;  
    }

    static String makeSizeFormat(int size){
    	return String.format(Locale.getDefault(),"%.2f MB", (size/1024./1024.));
    }
    
}