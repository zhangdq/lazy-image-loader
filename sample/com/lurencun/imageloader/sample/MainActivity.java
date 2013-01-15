package com.lurencun.imageloader.sample;

import com.lurencun.imageloader.LazyImageLoader;
import com.lurencun.imageloader.LoaderOptions;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoaderOptions.Builder builder = new LoaderOptions.Builder();
		builder.cacheDir("lazyImageLoader")
		.imageStubResId(R.drawable.icon);
		LazyImageLoader.init(getBaseContext(), builder.build());
		
		setContentView(R.layout.main);
		
		ImageView imageView = (ImageView) findViewById(R.id.image);
		
		LazyImageLoader.getLoader().submitDisplayTask("http://cdn.kpbz.net/uploads/allimg/2009-09/11164P6-1-21309.jpg", imageView,true,false);
	}

	
}
