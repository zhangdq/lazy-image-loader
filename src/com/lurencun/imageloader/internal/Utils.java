package com.lurencun.imageloader.internal;

import java.io.InputStream;
import java.io.OutputStream;

import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class Utils {
	
    public static void copy(InputStream is, OutputStream os){
        final int bufferSize = 1024;
        try{
            byte[] bytes=new byte[bufferSize];
            int length = 0;
            while( (length = is.read(bytes, 0, bufferSize)) != -1){
            	os.write(bytes, 0, length);
            }
        } catch(Exception exp){
        	exp.printStackTrace();
        }
    }
    
    public static Size getImageSizeScaleTo(ImageView imageView) {
		DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
		LayoutParams params = imageView.getLayoutParams();
		int width = params.width; // Get layout width parameter
		if (width <= 0) width = displayMetrics.widthPixels;
		int height = params.height; // Get layout height parameter
		if (height <= 0) height = displayMetrics.heightPixels;
		return new Size(width, height);
	}

}