package com.lurencun.imageloader;


import android.widget.ImageView;

/**
 * @author : 桥下一粒砂
 * @email  : chenyoca@gmail.com
 * @date   : 2013-1-4
 * @desc   : TODO
 */
public final class TaskRequest {

	public final String uri;
	public final ImageView view;
	
	public final boolean isLocalFile;
	public final boolean alreadySubmit = true;
	public final LazyImageLoader loader;
	
	public boolean allowCompress = true;
	public boolean cacheable = true;
	
	public TaskRequest(LazyImageLoader loader,String url, ImageView view){
		this.uri = new String(url);
		this.view = view;
		this.loader = loader;
		isLocalFile = !uri.startsWith("http"); 
	}
	
	public boolean verifyViewReused(){
        String url = loader.displayViewsHolder.get(view);
        //当ImageView对应的Url不存在，或者不是原来的Url，则说明View被重用了。
        return ( url == null || !url.equals(uri) );
	}
}
