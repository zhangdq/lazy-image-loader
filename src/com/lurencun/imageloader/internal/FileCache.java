package com.lurencun.imageloader.internal;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.lurencun.imageloader.LoaderOptions;

import android.content.Context;
import android.os.Environment;

public class FileCache {
    
    private File cacheDir;
    
    private static final int RADIX = 10 + 26; // 10 digits + 26 letters
	
	public static String urlToName(String url){
		byte[] md5 = MD5(url.getBytes());
		BigInteger data = new BigInteger(md5).abs();
		String suffix = url.substring(url.lastIndexOf("."));
		return data.toString(RADIX) + (suffix.length() <= ".jpeg".length() ? suffix : "" );
	}

	static byte[] MD5(byte[] data) {
		byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(data);
			hash = digest.digest();
		} catch (NoSuchAlgorithmException exp) {
			exp.printStackTrace();
		}
		return hash;
	}
    
    public FileCache(Context context, LoaderOptions options){
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
    
    public File get(String url){
        File file = new File(cacheDir, urlToName(url));
        return file;
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null) return;
        for(File file : files){
        	file.delete();
        }
    }
    
    public void remove(String url){
    	File file = new File(cacheDir, urlToName(url));
    	if(file.exists()) file.delete();
    }

}