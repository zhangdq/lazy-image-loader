package com.lurencun.imageloader;

import java.io.File;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.lurencun.imageloader.internal.WebFetcher;
import com.lurencun.imageloader.internal.ImageUtil;
import com.lurencun.imageloader.internal.TaskParams;

public class DisplayInvoker implements Runnable {

	static final String TAG = "DisplayInvoker";
	
	private final TaskParams params;
	
	private final LazyImageLoader loader;
	
	public DisplayInvoker(ImageView displayer,String targetUri, LazyImageLoader loader){
		params = new TaskParams(displayer,targetUri);
		this.loader = loader;
	}
	
	@Override
	public void run() {
		
		if(LazyImageLoader.options.enableMemoryCache){
			Bitmap bitmap = loader.cacheManager.getFromMemoryCache(params.memoryCacheKey);
			if(bitmap != null){
				loader.uiDrawableHandler.post(new DrawWorker(bitmap, params, loader));
				return;
			}
		}
		
		File cache = loader.cacheManager.getFromDiskCache(params.diskCacheKey);
		// 一定会返回一个非Null的文件对象，因为网络下载需要文件对象（缓存路径）。
		if(!cache.exists()){
			WebFetcher downloader = new WebFetcher.SimpleFetcher();
			final boolean downloadStatus = downloader.fetch(params.targetUri, cache);
			if(!downloadStatus){
				if(LazyImageLoader.DEBUG){
					final String message = "[DOWNLOAD] ~ Download **FAILURE**. INFO{ targetUrl:\"%s\" }";
					Log.e(TAG, String.format(message, params.targetUri));
				}
				cache = null;
			}else if(loader.isTargetDisplayerMappingBroken(params.targetUri, params.displayer)){
				if(LazyImageLoader.DEBUG){
					final String message = "[DOWNLOAD] ~ Download **SUCCESS**, but DISPLAY view seem to been reused. ";
					Log.e(TAG, String.format(message));
				}
				cache = null;
			}
		}
		if(cache != null){
			Bitmap bitmap = ImageUtil.decode(cache, params);
			loader.uiDrawableHandler.post(new DrawWorker(bitmap, params, loader));
			if(params.allowCacheToMemory){
				loader.cacheManager.addToMemoryCache(params.memoryCacheKey, bitmap);
			}
		}
	}

}
