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
import android.os.Handler;
import android.widget.ImageView;

import com.lurencun.imageloader.internal.CacheManager;
import com.lurencun.imageloader.internal.DrawWorker;
import com.lurencun.imageloader.internal.TaskParams;


public class LazyImageLoader {
	
	public static final String VERSION = "v1.3.0";
	
	public static boolean DEBUG = true;

	static final String TAG = "LazyImageLoader";
	
	private static LazyImageLoader instance;
	private final ExecutorService taskExecutor;
	private final ScheduledExecutorService taskSubmitExecutor;
	private final Handler uiDrawableHandler = new Handler();
	
    final Map<ImageView, String> targetToDisplayerMappingHolder;
    
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
    
    private Runnable taskExecutorDeamom = new Runnable(){
		@Override
		public void run() {
			TaskParams params = null;
			synchronized(taskStack){
				if( !taskStack.isEmpty() ){
					params = taskStack.peek();
				}
			}
			if(params != null && params.isReady && !isTargetDisplayerMappingBroken(params)){
				params.isReady = false;
				taskExecutor.submit(new DisplayInvoker(params, LazyImageLoader.this));
			}else{
				removeTask(params);
			}
		}
    };
    
    private LazyImageLoader(Context context){
    	DEBUG = options.enableLogging;
    	cacheManager = new CacheManager(context, options);
        taskExecutor = Executors.newFixedThreadPool(CORE_THREAD_SIZE * 2);
        taskSubmitExecutor = Executors.newScheduledThreadPool(CORE_THREAD_SIZE);
        targetToDisplayerMappingHolder = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
        taskSubmitExecutor.scheduleAtFixedRate(taskExecutorDeamom, 0, options.submitPeriod, TimeUnit.MILLISECONDS);
    }
    
    public void display(String targetUri, ImageView displayer,boolean allowCompress, boolean allowCacheToMemory, boolean isDiffSigntrue){
    	if(displayer == null) {
    		return;
    	}
		if( !isAvalidUri(targetUri)) {
			displayer.setImageResource(options.imageStubResId);
			return;
		}
		TaskParams params = new TaskParams(displayer, targetUri, allowCompress, allowCacheToMemory, isDiffSigntrue);
		if(isTargetDisplayerMappingBroken(params)){
			displayer.setImageResource(options.imageStubResId);
		}
		targetToDisplayerMappingHolder.put(displayer, targetUri);
		addTask(params);
    }
    
    void addTask(TaskParams task){
    	synchronized(taskStack){
			int size = taskStack.size();
			if( size > MAX_TASK_SIZE ){
				int maxIndex = MAX_TASK_SIZE - 1;
				int limit = MAX_TASK_SIZE / 2;
				for(int i=maxIndex; i<=limit; i--){
					taskStack.remove(i);
				}
			}
    		taskStack.push(task);
		}
    }
    
    private void removeTask(TaskParams task){
    	synchronized(taskStack){
    		taskStack.remove(task);
    	}
    }
    
    void submitDisplayTask(DrawWorker task){
    	uiDrawableHandler.post(task);
    	removeTask(task.params);
    }
    
    
    final static int MIN_URI_LENGTH = "http://".length(); // file:/// -> 8   http:// -> 7
    boolean isAvalidUri(String targetUri){
    	return targetUri != null && MIN_URI_LENGTH < targetUri.length();
    }
    
    boolean isTargetDisplayerMappingBroken(final TaskParams params){
    	String currentMappingValue = targetToDisplayerMappingHolder.get(params.displayer());
    	return (currentMappingValue != null && currentMappingValue != params.targetUri);
    }
    
    public CacheManager getCacheManager(){
    	return cacheManager;
    }
    
}
