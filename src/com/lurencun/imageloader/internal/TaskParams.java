package com.lurencun.imageloader.internal;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.widget.ImageView;

public class TaskParams {

	public final String targetUri;
	public final ImageView displayer;
	public final boolean allowCompress;
	public final boolean allowCacheToMemory;
	public final String diskCacheKey;
	public final String memoryCacheKey;

	public TaskParams(ImageView displayer,String targetUri){
		this.allowCompress = true;
		this.allowCacheToMemory = true;
		this.targetUri = targetUri;
		this.displayer = displayer;
		this.diskCacheKey = urlToName(targetUri);
		this.memoryCacheKey = diskCacheKey;
	}
	
	private static final int RADIX = 10 + 26;
	
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
}
