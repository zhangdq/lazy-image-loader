package com.lurencun.imageloader;

import java.io.File;

import android.graphics.Bitmap;


public class DisplayTask implements Runnable {

	private TaskRequest request;
	
	private LazyImageLoader loader;
	
	public DisplayTask(LazyImageLoader loader,TaskRequest request){
		this.request = request;
		this.loader = loader;
	}
	
	@Override
	public void run() {
		if(request.isViewReused()) {
			return;
		}
		
		//如果是本地图片，则直接显示
		if(request.isLocalFile){
			render(new File(request.target), request);
		}
		
		// 文件缓存
		File cache = loader.fileCache.get(request.target);
		// 一定会返回一个非Null的文件对象，因为网络下载需要文件对象（缓存路径）。
		if(!cache.exists()){
			Downloader downloader = new Downloader.SimpleDownloader();
			if(!downloader.load(request.target, cache)){
				cache = null;
			}else{
				if(request.isViewReused()){
					cache = null;;
				}
			}
		}
		
		if(cache != null){
			render(cache, request);
		}
	}
	
	void render(File file,TaskRequest request){
		Bitmap bitmap = ImageUtil.decode(file,request);
		//图片解码存在延时，View可能被重用。检查！
		if(!request.isViewReused()){
			loader.uiDrawableHandler.post(new DisplayRunner(request.receiver, bitmap,LazyImageLoader.options.displayAnimation));
			loader.memoryCache.put(request.target, bitmap);
		}else{
			bitmap.recycle();
			System.gc();
		}
	}

}
