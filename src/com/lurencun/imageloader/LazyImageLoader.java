package com.lurencun.imageloader;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.lurencun.imageloader.internal.CacheManager;
import com.lurencun.imageloader.internal.TaskParams;


public class LazyImageLoader {
	
	public static final String VERSION = "v1.1.0";
	
	public static boolean DEBUG = true;

	static final String TAG = "LazyImageLoader";
	
	private static LazyImageLoader instance;
	private final ExecutorService taskExecutor;
	private final ExecutorService taskSubmitExecutor;
	
    final Map<ImageView, String> targetToDisplayerMappingHolder;
    final Handler uiDrawableHandler = new Handler();
    final Handler delaySubmitHandler = new Handler();
    static LoaderOptions options;
    final CacheManager cacheManager;
    
    static final int CORE_THREAD_SIZE = 2;
    
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
        taskExecutor = Executors.newFixedThreadPool(CORE_THREAD_SIZE * 2);
        taskSubmitExecutor = Executors.newFixedThreadPool(CORE_THREAD_SIZE);
        targetToDisplayerMappingHolder = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    }
    
    public void display(final String targetUri, final ImageView displayer,final boolean allowCompress, final boolean allowCacheToMemory, final boolean isDiffSigntrue){
    	if(displayer == null)  return;
    	clearWithStub(displayer);
    	if(targetUri == null) return;
    	taskSubmitExecutor.submit(new Runnable(){
			@Override
			public void run() {
				WeakReference<ImageView> displayerRef = new WeakReference<ImageView>(displayer);
				TaskParams params = new TaskParams(displayerRef, targetUri, allowCompress, allowCacheToMemory, isDiffSigntrue);
				if(LazyImageLoader.options.enableMemoryCache){
					Bitmap bitmap = cacheManager.getFromMemoryCache(params.memoryCacheKey);
					if(bitmap != null){
						uiDrawableHandler.post(new DrawWorker(bitmap, params, LazyImageLoader.this));
						if(LazyImageLoader.DEBUG){
							final String message = "[CACHE] ~ Cache hint { targetUrl:\"%s\" }";
							Log.i(TAG, String.format(message, params.targetUri));
						}
						return;
					}
				}
				sleep(options.submitDelay);
				if(!isTargetDisplayerMappingBroken(targetUri, displayer)) {
					taskExecutor.submit(new DisplayInvoker(params, LazyImageLoader.this));
				}else{
					clearWithStub(displayer);
					return;
				}
			}
    	});
    	targetToDisplayerMappingHolder.put(displayer, targetUri);
    }
    
    void sleep(int sleep){
    	try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    boolean isTargetDisplayerMappingBroken(String target, ImageView displayer){
    	String currentMappingValue = targetToDisplayerMappingHolder.get(displayer);
    	return (currentMappingValue != null && currentMappingValue != target);
    }
    
    void clearWithStub(ImageView imageView){
    	if(imageView == null) return;
    	imageView.setImageBitmap(null);
    	imageView.setImageResource(options.imageStubResId);
    	imageView.postInvalidate();
    }
    
    public CacheManager getCacheManager(){
    	return cacheManager;
    }
    
}
