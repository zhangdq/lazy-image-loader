package com.lurencun.imageloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import com.lurencun.imageloader.internal.BitmapSampleUtil;
import com.lurencun.imageloader.internal.TaskWrapper;
import com.lurencun.imageloader.internal.Size;
import com.lurencun.imageloader.internal.Utils;

public class ImageLoader {
    
	private MemoryCache memoryCache;
	private FileCache fileCache;
    private Map<ImageView, String> viewsHolder;
    private ExecutorService executorService;
    private Handler uiActionHandler = new Handler();
    
    private static LoaderOptions loaderOptions;
    
    static class InstanceProvider{
    	static ImageLoader loader;
    }
    
    public static void init(Context context, LoaderOptions options){
    	loaderOptions = options;
    	if(InstanceProvider.loader == null){
    		InstanceProvider.loader = new ImageLoader(context);
    	}
    }
    
    public static ImageLoader getLoader(){
    	return InstanceProvider.loader;
    }
    
    private ImageLoader(Context context){
        fileCache = new FileCache(context,loaderOptions);
        memoryCache = new MemoryCache(loaderOptions);
        executorService = Executors.newFixedThreadPool(loaderOptions.threadPoolSize);
        viewsHolder = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    }
    
    public void addLazyDisplayTask(String url,ImageView view){
    	
    }
    
    public void commitLazyTask(){
    	
    }
    
    public void removeFormCache(String uri){
    	memoryCache.remove(uri);
    	fileCache.remove(uri);
    }
    
    public void displayImage(String url, ImageView imageView){
    	displayImage(url, imageView, true);
    }
    
    public void displayImage(String url, ImageView imageView, boolean fitToViewSize){
    	if(loaderOptions == null){
    		throw new IllegalStateException("Method init() NOT CALL! ");
    	}
        viewsHolder.put(imageView, url);
        
        if(!fitToViewSize){
        	submitAsLoadTask(url, imageView, fitToViewSize);
        }else{
        	// From memory cache
            Bitmap bitmap = memoryCache.get(url);
            if(bitmap != null && !bitmap.isRecycled())
                imageView.setImageBitmap(bitmap);
            else{
            	imageView.setImageResource(loaderOptions.imageStubResId);
                submitAsLoadTask(url, imageView, fitToViewSize);
            }
        }
    }
    
    public MemoryCache getMemoryCache(){
    	return memoryCache;
    }
    
    public FileCache getFileCache(){
    	return fileCache;
    }
        
    private void submitAsLoadTask(String url, ImageView imageView, boolean fitToView) {
        TaskWrapper task = new TaskWrapper(url, imageView);
        task.fitToViewSize = fitToView;
        executorService.submit(new LoadImageTask(task));
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File file, TaskWrapper task){
    	if(!file.exists()) return null;
        try {
        	BitmapFactory.Options opts = new BitmapFactory.Options();
        	if(task.fitToViewSize){
        		FileInputStream mersureStream = new FileInputStream(file);
            	opts.inJustDecodeBounds = true;
            	BitmapFactory.decodeStream(mersureStream, null, opts);
            	mersureStream.close();
            	Size fixedSize = Utils.getImageSizeScaleTo(task.view);
            	opts.inSampleSize = BitmapSampleUtil.computeSampleSize(opts, -1, fixedSize.size());
        	}
        	opts.inJustDecodeBounds = false;
        	FileInputStream outputStream = new FileInputStream(file);
        	Bitmap bitmap = BitmapFactory.decodeStream(outputStream, null, opts);
        	outputStream.close();
            return bitmap;
        } catch (IOException exp) {
            exp.printStackTrace();
        }
        return null;
    }
    
    private Bitmap getBitmapFromFileOrWeb(TaskWrapper task){
    	Bitmap bitmap=null;
    	//from SDCard
    	File file = null;
        if(!task.url.startsWith("http://")){
        	file = new File(task.url);
        }
        //form SDCard cache
        else{
        	file = fileCache.getFile(task.url);
        }
        bitmap = decodeFile(file, task);
        if( bitmap != null){
        	return bitmap;
        }
        
        //from web url
        try {
            URL imageUrl = new URL(task.url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(loaderOptions.connectionTimeOut);
            conn.setReadTimeout(loaderOptions.readTimeOut);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(file);
            Utils.copy(is, os);
            os.close();
            bitmap = decodeFile(file,task);
            return bitmap;
        } catch (Throwable exp){
           exp.printStackTrace();
           if(exp instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }
    
    class LoadImageTask implements Runnable {
        
    	TaskWrapper task;
        
        LoadImageTask(TaskWrapper task){
            this.task = task;
        }
        
        @Override
        public void run() {
            try{
                if(imageViewReused(task)) return;
                Bitmap bmp = getBitmapFromFileOrWeb(task);
                memoryCache.put(task.url, bmp);
                if(imageViewReused(task)) return;
                UIThreadDisplayer displayer = new UIThreadDisplayer(bmp, task);
                uiActionHandler.post(displayer);
            }catch(Throwable exp){
                exp.printStackTrace();
            }
        }
    }
    
    boolean imageViewReused(TaskWrapper task){
        String tag = viewsHolder.get(task.view);
        return (tag==null || !tag.equals(task.url));
    }
    
    //Used to display bitmap in the UI thread
    class UIThreadDisplayer implements Runnable{
    	
        Bitmap bitmap;
        TaskWrapper task;
        
        public UIThreadDisplayer(Bitmap bitmap, TaskWrapper task){
        	this.bitmap = bitmap;
        	this.task = task;
        }
        
        @Override
        public void run() {
            if(imageViewReused(task)) return;
            if(bitmap!=null){
                task.view.setImageBitmap(bitmap);
            }
            else
                task.view.setImageResource(loaderOptions.imageStubResId);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}
