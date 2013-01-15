package com.lurencun.imageloader;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;

import com.lurencun.imageloader.internal.CacheWrapper;
import com.lurencun.imageloader.internal.DisplayTask;
import com.lurencun.imageloader.internal.FileCache;
import com.lurencun.imageloader.internal.MemoryCache;
import com.lurencun.imageloader.internal.TaskRequest;
import com.lurencun.imageloader.internal.ThreadPoolManager;

public class LazyImageLoader {
    
	private MemoryCache memoryCache;
	private FileCache fileCache;
	
    private Map<ImageView, String> targetViewsHolder;
    
    private Handler uiDrawableHandler = new Handler();
    
    private final ThreadPoolManager threadPool;
    private static LoaderOptions loaderOptions;
    private static LazyImageLoader instance;
    
    public static void init(Context context, LoaderOptions options){
    	loaderOptions = options;
    	if(instance == null){
    		instance = new LazyImageLoader(context);
    	}
    }
    
    public static LazyImageLoader getLoader(){
    	return instance;
    }
    
    private LazyImageLoader(Context context){
        fileCache = new FileCache(context,loaderOptions);
        memoryCache = new MemoryCache(loaderOptions);
        threadPool = new ThreadPoolManager();
        targetViewsHolder = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    }
    
    public boolean isViewReused(TaskRequest task){
        String tag = targetViewsHolder.get(task.view);
        return (tag==null || !tag.equals(task.uri));
    }
    
    public void display(String url, ImageView imageView){
    	if(imageView == null)  return;
    	if(url == null){
    		imageView.setImageResource(loaderOptions.imageStubResId);
    		return;
    	}
    	CacheWrapper cache = memoryCache.get(url);
    	if(cache != null){
    		imageView.setImageBitmap(cache.bitmap);
    		return;
    	}

    	imageView.setImageResource(loaderOptions.imageStubResId);
    	submitDisplayTask(url,imageView, true, true);
    }
    
    public void displayWithoutCache(String url, ImageView imageView){
    	submitDisplayTask(url,imageView, true, false);
    }
    
    public void postUIThreadRunner(Runnable r){
    	uiDrawableHandler.post(r);
    }
    
    public void submitDisplayTask(String url, ImageView imageView,boolean allowCompress, boolean cacheable){
    	TaskRequest request = new TaskRequest(url, imageView);
    	request.allowCompress = allowCompress;
    	request.cacheable = cacheable;
        targetViewsHolder.put(imageView, url);
        threadPool.submit(new DisplayTask(this,request));
    }
    
    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
    
    public MemoryCache getMemoryCache(){
    	return memoryCache;
    }
    
    public FileCache getFileCache(){
    	return fileCache;
    }

}
