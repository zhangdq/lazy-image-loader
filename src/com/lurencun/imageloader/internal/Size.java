package com.lurencun.imageloader.internal;

public class Size {

	public final int width;
	public final int height;

	public Size(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int size(){
		return width*height;
	}
	@Override
	public String toString() {
		return String.format("%sx%s", width, height);
	}
	
	
}