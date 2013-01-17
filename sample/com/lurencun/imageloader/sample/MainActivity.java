package com.lurencun.imageloader.sample;

import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lurencun.android.adapter.HolderAdapter;
import com.lurencun.android.adapter.ViewBuilder;
import com.lurencun.imageloader.LazyImageLoader;
import com.lurencun.imageloader.LoaderOptions;

public class MainActivity extends Activity {
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoaderOptions.Builder builder = new LoaderOptions.Builder();
		builder
		.cacheDir("aaaa0000___lazyImageLoader")
		.enableLogging(true)
		.imageStubResId(R.drawable.avatar_stub);
		LazyImageLoader.init(getBaseContext(), builder.build());
		
		setContentView(R.layout.main);
		
		ListView list = (ListView) findViewById(R.id.listView);
		
		HolderAdapter<String> adapter = new HolderAdapter<String>(getLayoutInflater(), new ViewBuilder<String>(){

			@Override
			public View createView(LayoutInflater inflater, int position,String data) {
				View view = inflater.inflate(R.layout.layout_image, null);
				updateView(view, position, data);
				return view;
			}

			@Override
			public void updateView(View view, int position, String data) {
				ImageView image = (ImageView) view.findViewById(R.id.image);
				TextView title = (TextView) view.findViewById(R.id.title);
				
				title.setText(data);
				LazyImageLoader.getLoader().display(data, image);
			}
			
		});
		
		list.setAdapter(adapter);
		
		adapter.update(Arrays.asList(URLS.urls));
		
	}
	
	

	
	
}
