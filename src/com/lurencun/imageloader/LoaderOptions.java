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
	public final int submitDelay;
	public final boolean enableMemoryCache;
	public final int maxMemoryInByte;
	public final String cacheDir;
	public final boolean logging;
	
	private LoaderOptions(Builder builder){
		imageStubResId = builder.imageStubResId;
		connectionTimeOut = builder.connectionTimeOut;
		readTimeOut = builder.readTimeOut;
		maxMemoryInByte = builder.maxMemoryInByte;
		cacheDir = builder.cacheDir;
		logging = builder.enableLogging;
		enableMemoryCache = builder.enableMemoryCache;
		submitDelay = builder.submitDelay;
	}
	
	public static class Builder{
    	private int imageStubResId = -1;
    	private int connectionTimeOut = 30 * 1000;
    	private int readTimeOut = 30 * 1000;
    	private int maxMemoryInByte = (int) (Runtime.getRuntime().maxMemory()/4);
    	private String cacheDir = "_lrcImageLoaderCache";
    	private int submitDelay = 101;
    	private boolean enableLogging = false;
    	private boolean enableMemoryCache = true;
    	
    	/**
    	 * 设置默认资源图片
    	 * @param resid
    	 * @return
    	 */
    	public Builder setImageStub(int resid){
    		imageStubResId = resid;
    		return this;
    	}
    	
    	/**
    	 * 设置下载图片的连接超时
    	 * @param millins
    	 * @return
    	 */
    	public Builder setConnectionTimeOut(int millins){
    		imageStubResId = millins;
    		return this;
    	}
    	
    	/**
    	 * 设置下载图片读取数据超时
    	 * @param millins
    	 * @return
    	 */
    	public Builder setReadTimeOut(int millins){
    		imageStubResId = millins;
    		return this;
    	}
    	
    	/**
    	 * 设置提交显示图片的请求间隔(必须大于50ms)
    	 * @param millins
    	 * @return
    	 */
    	public Builder setDisplayRequestDelay(int millins){
    		submitDelay = millins;
    		if(submitDelay < 50) submitDelay = 50;
    		return this;
    	}
    	
    	/**
    	 * 设置最大内存缓存(单位Byte)
    	 * @param size
    	 * @return
    	 */
    	public Builder setMaxMemoryInByte(int size){
    		maxMemoryInByte = size;
    		return this;
    	}
    	
    	/**
    	 * 设置缓存目录
    	 * @param dirName
    	 * @return
    	 */
    	public Builder setCacheDir(String dirName){
    		cacheDir = dirName;
    		return this;
    	}
    	
    	/**
    	 * 设置是否输出调试信息
    	 * @param enable
    	 * @return
    	 */
    	public Builder setEnableLogging(boolean enable){
    		enableLogging = enable;
    		return this;
    	}
    	
    	/**
    	 * 设置是否开启内存缓存
    	 * @param enable
    	 * @return
    	 */
    	public Builder setEnableMemoryCache(boolean enable){
    		enableMemoryCache = enable;
    		return this;
    	}
    	
    	public LoaderOptions build(){
    		return new LoaderOptions(this);
    	}
    	
    }
}
