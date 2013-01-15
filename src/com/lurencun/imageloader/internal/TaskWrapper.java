package com.lurencun.imageloader.internal;

import android.widget.ImageView;

/**
 * @author : 桥下一粒砂
 * @email  : chenyoca@gmail.com
 * @date   : 2013-1-4
 * @desc   : TODO
 */
public final class TaskWrapper {

	public final String url;
	public final ImageView view;
	public boolean fitToViewSize = true;
	
	public TaskWrapper(String url, ImageView view){
		this.url = url;
		this.view = view;
	}
}
