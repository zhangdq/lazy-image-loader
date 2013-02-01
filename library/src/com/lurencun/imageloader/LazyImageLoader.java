package com.lurencun.imageloader;

import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

import com.lurencun.imageloader.internal.CacheManager;
import com.lurencun.imageloader.internal.TaskParams;


public class LazyImageLoader {
	
	public static final String VERSION = "v1.2.0";
	
	public static boolean DEBUG = true;

	static final String TAG = "LazyImageLoader";
	
	private static LazyImageLoader instance;
	private final ExecutorService taskExecutor;
	private final ScheduledExecutorService taskSubmitExecutor;
	
    final Map<ImageView, String> targetToDisplayerMappingHolder;
    final Handler uiDrawableHandler = new Handler();
    final Handler delaySubmitHandler = new Handler();
    static LoaderOptions options;
    final CacheManager cacheManager;
    final Stack<TaskParams> taskStack = new Stack<TaskParams>();
    static final int CORE_THREAD_SIZE = 2;
    static final int MAX_TASK_SIZE = 10;
    
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
    
    private Runnable taskDeamom = new Runnable(){
		@Override
		public void run() {
			TaskParams params = null;
			synchronized(taskStack){
				if( !taskStack.isEmpty() ){
					params = taskStack.pop();
				}
			}
			if(params == null){
				return;
			}
			if(!isTargetDisplayerMappingBroken(params.targetUri, params.displayer())){
				taskExecutor.submit(new DisplayInvoker(params, LazyImageLoader.this));
			}
		}
    };
    
    private LazyImageLoader(Context context){
    	DEBUG = options.enableLogging;
    	cacheManager = new CacheManager(context, options);
        taskExecutor = Executors.newFixedThreadPool(CORE_THREAD_SIZE * 2);
        taskSubmitExecutor = Executors.newScheduledThreadPool(CORE_THREAD_SIZE);
        targetToDisplayerMappingHolder = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
        taskSubmitExecutor.scheduleAtFixedRate(taskDeamom, 0, options.submitPeriod, TimeUnit.MILLISECONDS);
    }
    
    public void display(String targetUri, ImageView displayer,boolean allowCompress, boolean allowCacheToMemory, boolean isDiffSigntrue){
    	if(displayer == null) {
    		return;
    	}
		displayer.setImageResource(options.imageStubResId);
		if(targetUri == null || "http://".length() > targetUri.length()) {
			return;
		}
		TaskParams params = new TaskParams(displayer, targetUri, allowCompress, allowCacheToMemory, isDiffSigntrue);
		if(LazyImageLoader.options.enableMemoryCache){
			Bitmap bitmap = cacheManager.getFromMemoryCache(params.memoryCacheKey);
			if(bitmap != null){
				uiDrawableHandler.post(new BitmapDrawWorker(bitmap, params));
				return;
			}
		}
		targetToDisplayerMappingHolder.put(displayer, targetUri);
		synchronized(taskStack){
			int size = taskStack.size();
			if( size > MAX_TASK_SIZE ){
				int maxIndex = MAX_TASK_SIZE - 1;
				int limit = MAX_TASK_SIZE / 2;
				for(int i=maxIndex; i<=limit; i--){
					taskStack.remove(i);
				}
			}
			taskStack.push(params);
		}
		
    }
    
    boolean isTargetDisplayerMappingBroken(String target, ImageView displayer){
    	String currentMappingValue = targetToDisplayerMappingHolder.get(displayer);
    	return (currentMappingValue != null && currentMappingValue != target);
    }
    
    public CacheManager getCacheManager(){
    	return cacheManager;
    }
    
}
