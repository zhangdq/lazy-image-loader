package com.lurencun.imageloader;
import android.widget.ImageView;

import com.lurencun.imageloader.internal.DrawWorker;
import com.lurencun.imageloader.internal.TaskParams;


public class StubDrawWorker extends DrawWorker {
	
	public StubDrawWorker(TaskParams params) {
		super(params);
	}

	@Override
	public void run() {
		ImageView displayer = params.displayer();
		if(displayer == null) return;
		displayer.setImageResource(LazyImageLoader.options.imageStubResId);
		displayer.postInvalidate();
	}

}
