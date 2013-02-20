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
		//loader.uiDrawableHandler.post(new StubDrawWorker(params));
		//缓存
		if(LazyImageLoader.options.enableMemoryCache){
			Bitmap bitmap = loader.cacheManager.getFromMemoryCache(params.memoryCacheKey);
			if(bitmap != null && !isMappingBroken()){
				loader.submitDisplayTask(new BitmapDrawWorker(bitmap, params));
				return;
			}
		}
		// 文件或者网络
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
			}else if(isMappingBroken()){
				if(LazyImageLoader.DEBUG){
					final String message = "[DOWNLOAD] ~ Download **SUCCESS**, but DISPLAY view seem to been reused. ";
					Log.i(TAG, String.format(message));
				}
				cache = null;
			}
		}
		if(cache != null){
			Bitmap bitmap = ImageUtil.decode(cache, params,LazyImageLoader.options.scaleForFixSize);
			if(isMappingBroken() || bitmap == null) {
				if(bitmap != null && !bitmap.isRecycled()){
					bitmap.recycle();
					loader.submitDisplayTask(new StubDrawWorker(params));
					bitmap = null;
				}
			}else{
				loader.submitDisplayTask(new BitmapDrawWorker(bitmap, params));
				if(LazyImageLoader.options.enableMemoryCache && params.allowMemoryCache){
					loader.cacheManager.addToMemoryCache(params.memoryCacheKey, bitmap);
				}
			}
		}
	}
	
	boolean isMappingBroken(){
		return loader.isTargetDisplayerMappingBroken(params);
	}

}
