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
		//显示图片任务已经提交并运行，由于线程池调度顺序不确定，此时 View -> URL 的对应关系
		//可能会由于View的重用而破坏。
		//如果View被重用，则退出显示图片任务
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
			//文件不存在，则从网络下载
			Downloader downloader = new Downloader.SimpleDownloader();
			if(!downloader.load(request.target, cache)){
				cache = null;
			}else{
				//网络下载存在非常大的延时，先做View重用的检查，再render,因为render需要读取文件。
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
			loader.uiDrawableHandler.post(new DisplayRunner(request.receiver, bitmap));
			if(request.cacheable){
				loader.getMemoryCache().put(request.target, bitmap, request.allowCompress);
			}
		}
	}

}
