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
	public final int maxMemoryInByte;
	public final int maxWeakItems;
	public final int threadPoolSize;
	public final String cacheDir;
	public final boolean logging;
	
	public final int cacheDeamonDelay;
	
	private LoaderOptions(Builder builder){
		imageStubResId = builder.imageStubResId;
		connectionTimeOut = builder.connectionTimeOut;
		readTimeOut = builder.readTimeOut;
		maxMemoryInByte = builder.maxMemoryInByte;
		cacheDir = builder.cacheDir;
		logging = builder.enableLogging;
		cacheDeamonDelay = builder.cacheDeamonDelay;
		threadPoolSize = builder.threadPoolSize;
		maxWeakItems = builder.maxWeakItems;
	}
	
	public static class Builder{
    	private int imageStubResId = -1;
    	private int connectionTimeOut = 30 * 1000;
    	private int readTimeOut = 30 * 1000;
    	private int maxMemoryInByte = (int) (Runtime.getRuntime().maxMemory()/4);//use 25% of available heap size
    	private String cacheDir = "_lrcImageLoaderCache";
    	private int threadPoolSize = 5;
    	int maxWeakItems = 10;
    	private boolean enableLogging = false;
    	
    	private int cacheDeamonDelay = 3 * 1000;
    	
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
    	
    	public Builder threadPoolSize(int size){
    		threadPoolSize = size;
    		return this;
    	}
    	
    	public Builder maxWeakItems(int size){
    		maxWeakItems = size;
    		return this;
    	}
    	
    	public LoaderOptions build(){
    		return new LoaderOptions(this);
    	}
    	
    }
}
