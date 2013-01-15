package com.lurencun.imageloader.internal;

import java.io.File;

import android.graphics.Bitmap;

import com.lurencun.imageloader.LazyImageLoader;

public class DisplayTask implements Runnable {

	private TaskRequest request;
	
	private LazyImageLoader loader;
	
	public DisplayTask(LazyImageLoader loader,TaskRequest request){
		this.request = request;
		this.loader = loader;
	}
	
	@Override
	public void run() {
		if(loader.isViewReused(request)) return;
		
		//Not a cache file , just a image file with specified path
		if(request.isLocalFile){
			File localFile = new File(request.uri);
			render(localFile, request);
		}
		
		// File cache
		File cache = loader.getFileCache().get(request.uri);
		if(!cache.exists()){
			//Web
			Downloader downloader = new Downloader.SimpleDownloader();
			if(!downloader.load(request.uri, cache)){
				cache = null;
			}
		}
		
		if(cache != null){
			render(cache, request);
		}
	}
	
	void render(File file,TaskRequest request){
		Bitmap bitmap = ImageUtil.decode(file,request);
		if(request.cacheable){
			loader.getMemoryCache().put(request.uri, bitmap, request.allowCompress);
		}
		loader.postUIThreadRunner(new DisplayRunner(request, bitmap));
	}

}
