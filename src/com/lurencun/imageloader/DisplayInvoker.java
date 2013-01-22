package com.lurencun.imageloader;

import java.io.File;

import android.graphics.Bitmap;
import android.util.Log;

import com.lurencun.imageloader.internal.Fetcher;
import com.lurencun.imageloader.internal.ImageUtil;
import com.lurencun.imageloader.internal.TaskParams;

public class DisplayInvoker implements Runnable {

	static final String TAG = "DisplayInvoker";
	
	private final TaskParams params;
	private final LazyImageLoader loader;
	
	public DisplayInvoker(TaskParams params, LazyImageLoader loader){
		this.params = params;
		this.loader = loader;
	}
	
	@Override
	public void run() {
		
		File cache = loader.cacheManager.getFromDiskCache(params.diskCacheKey);
		
		// 一定会返回一个非Null的文件对象，因为网络下载需要文件对象（缓存路径）。
		if(!cache.exists()){
			Fetcher fetcher = new Fetcher.SimpleFetcher();
			if( !fetcher.fetch(params.targetUri, cache) ){
				if(LazyImageLoader.DEBUG){
					final String message = "[DOWNLOAD] ~ Download **FAILURE**. INFO{ targetUrl:\"%s\" }";
					Log.e(TAG, String.format(message, params.targetUri));
				}
				cache.delete();
				cache = null;
			}else if(loader.isTargetDisplayerMappingBroken(params.targetUri, params.displayer())){
				if(LazyImageLoader.DEBUG){
					final String message = "[DOWNLOAD] ~ Download **SUCCESS**, but DISPLAY view seem to been reused. ";
					Log.i(TAG, String.format(message));
				}
				cache = null;
			}
		}
		if(cache != null){
			Bitmap bitmap = ImageUtil.decode(cache, params);
			if(loader.isTargetDisplayerMappingBroken(params.targetUri, params.displayer()) || 
					bitmap == null) {
				if(bitmap != null && !bitmap.isRecycled()){
					bitmap.recycle();
					loader.uiDrawableHandler.post(new StubDrawWorker(params));
					bitmap = null;
				}
				return;
			}else{
				loader.uiDrawableHandler.post(new BitmapDrawWorker(bitmap, params, loader));
				if(LazyImageLoader.options.enableMemoryCache && params.allowMemoryCache){
					loader.cacheManager.addToMemoryCache(params.memoryCacheKey, bitmap);
				}
			}
		}
	}

}
