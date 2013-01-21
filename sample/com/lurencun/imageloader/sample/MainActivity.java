package com.lurencun.imageloader.sample;

import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.lurencun.android.adapter.AbstractAdapter;
import com.lurencun.android.adapter.ViewBuilder;
import com.lurencun.imageloader.LazyImageLoader;
import com.lurencun.imageloader.LoaderOptions;
import com.lurencun.imageloader.internal.CacheManager;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoaderOptions.Builder builder = new LoaderOptions.Builder()
			.cacheDir("aaaa0000___lazyImageLoader")
			.enableLogging(true)
			.enableMemoryCache(true)
			.maxMemoryInByte(4 * 1024 *1024)
			.imageStubResId(R.drawable.avatar_stub);
		LazyImageLoader.init(getBaseContext(), builder.build());
		
		setContentView(R.layout.main);
		
		ListView list = (ListView) findViewById(R.id.listView);
		
		final ViewBuilder<String> vb = new ViewBuilder<String>(){

			@Override
			public View createView(LayoutInflater inflater, int position,String data) {
				View view = inflater.inflate(R.layout.layout_image, null);
				updateView(view, position, data);
				return view;
			}

			@Override
			public void updateView(View view, int position, String data) {
				ImageView image = (ImageView) view;
				image.setImageResource(R.drawable.avatar_stub);
				LazyImageLoader.getLoader().display(data, image, true, true, false);
			}

			@Override
			public void releaseView(View view, String data) {
				ImageView image = (ImageView) view;
				image.setImageBitmap(null);
			}
			
		};
		
		AbstractAdapter<String> adapter = new AbstractAdapter<String>(null, null) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				String data = getItem(position);
				if(convertView == null){
					convertView = vb.createView(getLayoutInflater(), position, data);
				}else{
					vb.updateView(convertView, position, data);
				}
				return convertView;
			}
		};
		
		list.setAdapter(adapter);
		
		adapter.update(Arrays.asList(URLS.urls));
		
//		new Handler().postDelayed(new Runnable(){
//
//			@Override
//			public void run() {
//				Runtime runtime = Runtime.getRuntime();
//				System.out.println(String.format(">>>> FreeMemory:%s", CacheManager.makeSizeFormat(runtime.freeMemory())));
//			}
//		}, 3000);
		
	}
	
	

	
	
}
