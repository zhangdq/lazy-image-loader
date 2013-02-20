package com.lurencun.imageloader.internal;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.widget.ImageView;

public class TaskParams {
	
	private final ImageView displayer;
	public final String targetUri;
	public final boolean allowCompress;
	public final boolean allowMemoryCache;
	public final String diskCacheKey;
	public final String memoryCacheKey;
	
	public boolean isReady = true;

	public TaskParams(ImageView displayer,String targetUri, boolean allowCompress, boolean allowCacheToMemory, boolean isDiffSigntrue){
		this.allowCompress = allowCompress;
		this.allowMemoryCache = allowCacheToMemory;
		this.targetUri = targetUri;
		this.displayer = displayer;
		this.diskCacheKey = urlToName(targetUri);
		this.memoryCacheKey = diskCacheKey + (isDiffSigntrue ? "#sign" : "");
	}
	
	public ImageView displayer(){
		return displayer;
	}
	
	@Override
	public boolean equals(Object obj){
		TaskParams params = (TaskParams) obj;
		return params.displayer == this.displayer 
				&& params.targetUri.equals(this.targetUri);
	}
	
	private static final int RADIX = 10 + 26;
	
	public static String urlToName(String url){
		if(url == null) return null;
		byte[] md5 = MD5(url.getBytes());
		BigInteger data = new BigInteger(md5).abs();
		int index = url.lastIndexOf(".");
		if( index > 0 ){
			String suffix = url.substring(index);
			return data.toString(RADIX) + (suffix.length() <= ".jpeg".length() ? suffix : "" );
		}else{
			return data.toString(RADIX);
		}
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
}
