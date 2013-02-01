package com.lurencun.imageloader.internal;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import com.lurencun.imageloader.LoaderOptions;

public class SimpleDiskCache {
    
    private File cacheDir;
    
    public SimpleDiskCache(Context context, LoaderOptions options){
    	String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
        	cacheDir = new File(Environment.getExternalStorageDirectory(),options.cacheDir);
        }else{
        	cacheDir = context.getCacheDir();
        }
        if(!cacheDir.exists()){
        	cacheDir.mkdirs();
        }
    }
    
    public File get(String fileName){
        File file = new File(cacheDir, fileName);
        return file;
    }
    
    public void clear(){
        File[] files = cacheDir.listFiles();
        if(files == null) return;
        for(File file : files){
        	file.delete();
        }
    }
    
    public void remove(String fileName){
    	File file = new File(cacheDir, fileName);
    	if(file.exists()) file.delete();
    }

}