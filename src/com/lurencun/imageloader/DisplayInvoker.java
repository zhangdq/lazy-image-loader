package com.lurencun.imageloader;

import java.io.File;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.lurencun.imageloader.internal.Downloader;
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
		
		File cache = loader.fileCache.get(params.diskCacheKey);
		// 一定会返回一个非Null的文件对象，因为网络下载需要文件对象（缓存路径）。
		if(!cache.exists()){
			Downloader downloader = new Downloader.SimpleDownloader();
			final boolean downloadStatus = downloader.load(params.targetUri, cache);
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
		render(cache);
		cache = null;
	}
	
	void render(File file){
		if(file == null) return;
		final Bitmap bitmap = ImageUtil.decode(file,params);
		loader.uiDrawableHandler.post(new Runnable() {
			@Override
			public void run() {
				//图片解码存在延时，View可能被重用。检查！
				if(!loader.isTargetDisplayerMappingBroken(params.targetUri, params.displayer)){
					drawToDisplayer();
				}else{
					bitmap.recycle();
					loader.clearWithStub(params.displayer);
					if(LazyImageLoader.DEBUG){
						final String message = "[DECODE] ~ Sended FILE to decode, but DISPLAY view seem to been reused. ";
						Log.e(TAG, String.format(message));
					}
				}
			}
			
			void drawToDisplayer(){
				if(bitmap != null && !bitmap.isRecycled()){
					params.displayer.setImageBitmap(bitmap);
				}else{
					if(LazyImageLoader.DEBUG){
						final String message = "[DRAWING] ~ Sended BITMAP to draw, but it was NULL or has been RECYCLED. ";
						Log.e(TAG, String.format(message));
					}
					loader.clearWithStub(params.displayer);
				}
				params.displayer.postInvalidate();
			}
		});
	}

}
