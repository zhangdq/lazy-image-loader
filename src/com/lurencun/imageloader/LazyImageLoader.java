package com.lurencun.imageloader;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;


public class LazyImageLoader {
	
	public static final String VERSION = "v1.0.0";
	
	private static LazyImageLoader instance;
	private final ThreadPoolManager threadPool;

	// default , 包访问权限
	MemoryCache memoryCache;
	FileCache fileCache;
    Map<ImageView, String> displayViewsHolder;
    Handler uiDrawableHandler = new Handler();
    static LoaderOptions loaderOptions;
    
    public static void init(Context context, LoaderOptions options){
    	loaderOptions = options;
    	DisplayRunner.stubResid = loaderOptions.imageStubResId;
    	if(instance == null){
    		instance = new LazyImageLoader(context);
    	}
    	System.out.println("Init --> LazyImageLoader "+ VERSION);
    }
    
    public static LazyImageLoader getLoader(){
    	return instance;
    }
    
    private LazyImageLoader(Context context){
        fileCache = new FileCache(context,loaderOptions);
        memoryCache = new MemoryCache(loaderOptions);
        threadPool = new ThreadPoolManager();
        displayViewsHolder = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    }
    
    static boolean compressVerify(boolean allowCompress,boolean isCompressed){
		if(allowCompress) {
			return true;
		}else{
			return !(allowCompress | isCompressed);
		}
	}
    
    private void display(String url, ImageView imageView, boolean allowCompress, boolean cacheable, boolean findInCache){
    	
    	if(imageView == null)  return;
    	if(url == null){
    		imageView.setImageResource(loaderOptions.imageStubResId);
    		return;
    	}
    	displayViewsHolder.put(imageView, url);
    	if(findInCache){
    		CacheWrapper cache = memoryCache.get(url);
        	if(cache != null && compressVerify(allowCompress, cache.isCompressed)){
        		cache.setIsUsing();
        		uiDrawableHandler.post(new DisplayRunner(imageView, cache.bitmap));
        		return;
        	}
    	}
    	submitDisplayTask(url,imageView, allowCompress, cacheable);
    }
    
    public void display(String url, ImageView imageView){
    	display(url, imageView, true, true, true);
    }
    
    public void displayWithoutCache(String url, ImageView imageView){
    	display(url, imageView, true, false, false);
    }
    
    public void displayWithoutCompressAndCache(String url, ImageView imageView){
    	display(url, imageView, false, false, false);
    }
    
    private void submitDisplayTask(String url, ImageView imageView,boolean allowCompress, boolean cacheable){
    	TaskRequest request = new TaskRequest(this, url, imageView);
    	request.allowCompress = allowCompress;
    	request.cacheable = cacheable;
    	//调用到此方法，说明图片不在内存缓存中。先显示一张Stub图片。
    	imageView.setImageResource(loaderOptions.imageStubResId);
    	imageView.postInvalidate();
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
