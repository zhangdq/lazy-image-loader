package com.lurencun.imageloader;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;


public class LazyImageLoader {
	
	public static final String VERSION = "v1.0.0";
	
	private static LazyImageLoader instance;
	private final ExecutorService taskExecutor;
	MemoryCache memoryCache;

	// default , 包访问权限
	FileCache fileCache;
    Map<ImageView, String> displayViewsHolder;
    Handler uiDrawableHandler = new Handler();
    static LoaderOptions options;
    
    public static void init(Context context, LoaderOptions ops){
    	options = ops;
    	DisplayRunner.stubResid = options.imageStubResId;
    	if(instance == null){
    		instance = new LazyImageLoader(context);
    	}
    	System.out.println("Init --> LazyImageLoader "+ VERSION);
    }
    
    public static LazyImageLoader getLoader(){
    	return instance;
    }
    
    private LazyImageLoader(Context context){
        fileCache = new FileCache(context,options);
        memoryCache = new MemoryCache(options);
        taskExecutor = Executors.newFixedThreadPool(options.maxMemoryInByte);
        displayViewsHolder = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    }
    
    public void display(String url, ImageView imageView){
    	
    	if(imageView == null)  return;
    	if(url == null){
    		setViewStub(imageView);
    		return;
    	}
    	displayViewsHolder.put(imageView, url);
    	
    	Bitmap cacheBitmap = memoryCache.get(url);
    	if(cacheBitmap != null){
    		uiDrawableHandler.post(new DisplayRunner(imageView, cacheBitmap));
    		return;
    	}
    	
    	TaskRequest request = new TaskRequest(this, url, imageView);
    	taskExecutor.submit(new DisplayTask(this,request));
    	setViewStub(imageView);
    }
    
    
    void setViewStub(ImageView imageView){
    	imageView.setImageResource(options.imageStubResId);
    	imageView.postInvalidate();
    }

}
