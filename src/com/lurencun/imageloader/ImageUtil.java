package com.lurencun.imageloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ImageUtil {

	public static Bitmap decode(File file,TaskRequest request){
		if(file == null || !file.exists()) return null;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			if (request.allowCompress) {
				FileInputStream mersureStream = new FileInputStream(file);
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(mersureStream, null, opts);
				mersureStream.close();
				Size fixedSize = getImageSizeScaleTo(request.view);
				opts.inSampleSize = computeSampleSize(opts,-1, fixedSize.size());
			}
			opts.inJustDecodeBounds = false;
			FileInputStream outputStream = new FileInputStream(file);
			Bitmap bitmap = BitmapFactory.decodeStream(outputStream, null, opts);
			outputStream.close();
			return bitmap;
		} catch (IOException exp) {
			exp.printStackTrace();
		}
		return null;
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
    
    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
    
    static class Size {

    	public final int width;
    	public final int height;

    	public Size(int width, int height) {
    		this.width = width;
    		this.height = height;
    	}

    	public int size(){
    		return width*height;
    	}
    	
    }
}
