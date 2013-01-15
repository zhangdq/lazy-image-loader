package com.lurencun.imageloader;


/**
 * @author : 桥下一粒砂
 * @email  : chenyoca@gmail.com
 * @date   : 2013-1-3
 * @desc   : TODO
 */
public class LoaderOptions {

	public final int imageStubResId;
	public final int connectionTimeOut;
	public final int readTimeOut;
	public final long maxMemoryInByte;
	public final String cacheDir;
	public final boolean logging;
	
	private LoaderOptions(Builder builder){
		imageStubResId = builder.imageStubResId;
		connectionTimeOut = builder.connectionTimeOut;
		readTimeOut = builder.readTimeOut;
		maxMemoryInByte = builder.maxMemoryInByte;
		cacheDir = builder.cacheDir;
		logging = builder.enableLogging;
	}
	
	public static class Builder{
    	private int imageStubResId = -1;
    	private int connectionTimeOut = 30000;
    	private int readTimeOut = 30000;
    	private long maxMemoryInByte = Runtime.getRuntime().maxMemory()/4;//use 25% of available heap size
    	private String cacheDir = "_lrcImageLoaderCache";
    	private boolean enableLogging = false;
    	
    	public Builder imageStubResId(int stubImage){
    		imageStubResId = stubImage;
    		return this;
    	}
    	
    	public Builder connectionTimeOut(int millins){
    		imageStubResId = millins;
    		return this;
    	}
    	
    	public Builder readTimeOut(int millins){
    		imageStubResId = millins;
    		return this;
    	}
    	
    	public Builder maxMemoryInByte(int stubImage){
    		imageStubResId = stubImage;
    		return this;
    	}
    	
    	public Builder cacheDir(String dirName){
    		cacheDir = dirName;
    		return this;
    	}
    	
    	public Builder enableLogging(boolean logging){
    		enableLogging = logging;
    		return this;
    	}
    	
    	public LoaderOptions build(){
    		return new LoaderOptions(this);
    	}
    	
    }
}
