package com.lurencun.imageloader.internal;

public abstract class DrawWorker implements Runnable {

	static final String TAG = "DRAW";
	
	public final TaskParams params;
	
	public DrawWorker(TaskParams params){
		this.params = params;
	}

}
