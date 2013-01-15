package com.lurencun.imageloader.internal;

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
	
	public boolean allowCompress = true;
	public boolean cacheable = true;
	
	public TaskRequest(String url, ImageView view){
		this.uri = url;
		this.view = view;
		isLocalFile = url != null && url.startsWith("http"); 
	}
}
