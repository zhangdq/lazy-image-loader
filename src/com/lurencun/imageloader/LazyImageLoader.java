package com.lurencun.imageloader;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.lurencun.imageloader.internal.CacheManager;


public class LazyImageLoader {
	
	public static final String VERSION = "v1.1.0";
	
	public static boolean DEBUG = true;

	static final String TAG = "LazyImageLoader";
	
	private static LazyImageLoader instance;
	private final ExecutorService taskExecutor;
	private final ExecutorService taskSubmitExecutor;
	
    final Map<ImageView, String> targetToDisplayerMappingHolder;
    final Handler uiDrawableHandler = new Handler();
    static LoaderOptions options;
    final CacheManager cacheManager;
    
    public static void init(Context context, LoaderOptions ops){
    	options = ops;
    	if(instance == null){
    		instance = new LazyImageLoader(context);
    	}
    	System.out.println("Init --> LazyImageLoader "+ VERSION);
    }
    
    public static LazyImageLoader getLoader(){
    	return instance;
    }
    
    private LazyImageLoader(Context context){
    	DEBUG = options.logging;
    	cacheManager = new CacheManager(context, options);
        taskExecutor = Executors.newCachedThreadPool();
        taskSubmitExecutor = Executors.newCachedThreadPool();
        targetToDisplayerMappingHolder = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    }
    
    public void display(final String targetUri, final ImageView displayer){
    	if(displayer == null)  return;
    	if(targetUri == null){
    		if(LazyImageLoader.DEBUG){
				final String message = "[DISPLAY] ~ Given a NULL targetUri, set stub for the view. ";
				Log.e(TAG, String.format(message));
			}
    		clearWithStub(displayer);
    		return;
    	}
    	clearWithStub(displayer);
    	taskSubmitExecutor.submit(new Runnable(){
			@Override
			public void run() {
				taskExecutor.submit(new DisplayInvoker(displayer,targetUri, LazyImageLoader.this));
			}
    	});
    	targetToDisplayerMappingHolder.put(displayer, targetUri);
    }
    
    boolean isTargetDisplayerMappingBroken(String target, ImageView displayer){
    	String currentMappingValue = targetToDisplayerMappingHolder.get(displayer);
    	return (currentMappingValue != null && currentMappingValue != target);
    }
    
    void clearWithStub(ImageView imageView){
    	imageView.setImageBitmap(null);
    	imageView.setImageResource(options.imageStubResId);
    	imageView.postInvalidate();
    }
    
}
