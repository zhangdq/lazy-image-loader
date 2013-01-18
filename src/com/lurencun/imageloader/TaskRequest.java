package com.lurencun.imageloader;



import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.widget.ImageView;

/**
 * @author : 桥下一粒砂
 * @email  : chenyoca@gmail.com
 * @date   : 2013-1-4
 * @desc   : TODO
 */
public final class TaskRequest {

	public final String targetUri;
	public final ImageView displayer;
	public final String fileName;
	public final boolean isLocalFile;
	public final LazyImageLoader loader;
	public boolean allowCompress = true;
	
	public TaskRequest(LazyImageLoader loader,String url, ImageView view){
		this.targetUri = new String(url);
		this.displayer = view;
		this.loader = loader;
		this.isLocalFile = !targetUri.startsWith("http");
		this.fileName = urlToName(targetUri);
	}
	
	public boolean isViewReused(){
        String url = loader.displayViewsHolder.get(displayer);
        //当ImageView对应的Url不存在，或者不是原来的Url，则说明View被重用了。
        return ( url == null || !url.equals(targetUri) );
	}
	
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
}
