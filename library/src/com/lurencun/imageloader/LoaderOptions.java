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
	public final boolean enableMemoryCache;
	public final int maxMemoryInByte;
	public final String cacheDir;
	public final boolean enableLogging;
	public final int submitPeriod;
	public final float scaleForFixSize;
	
	private LoaderOptions(Builder builder){
		this.imageStubResId = builder.imageStubResId;
		this.connectionTimeOut = builder.connectionTimeOut;
		this.readTimeOut = builder.readTimeOut;
		this.maxMemoryInByte = builder.maxMemoryInByte;
		this.cacheDir = builder.cacheDir;
		this.enableLogging = builder.enableLogging;
		this.enableMemoryCache = builder.enableMemoryCache;
		this.submitPeriod = builder.submitPeriod;
		this.scaleForFixSize = builder.scaleForFixedSize;
	}
	
	public static class Builder{
    	private int imageStubResId = -1;
    	private int connectionTimeOut = 30 * 1000;
    	private int readTimeOut = 30 * 1000;
    	private int maxMemoryInByte = (int) (Runtime.getRuntime().maxMemory()/4);
    	private String cacheDir = "lrc_cache";
    	private boolean enableLogging = false;
    	private boolean enableMemoryCache = true;
    	private int submitPeriod = 38;
    	float scaleForFixedSize = 1.5f;
    	
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
    	 * 设置提交显示请求的周期
    	 * @param period
    	 * @return
    	 */
    	public Builder setSubmitPeriod(int period){
    		submitPeriod = period;
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
