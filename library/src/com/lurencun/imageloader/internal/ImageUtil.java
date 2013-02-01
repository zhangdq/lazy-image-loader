package com.lurencun.imageloader.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.lurencun.imageloader.LazyImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;


public class ImageUtil {
	
	final static String TAG = "ImageUtil";

	public static Bitmap decode(File file,TaskParams params){
		if(file == null || !file.exists()) return null;
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			if (params.allowCompress) {
				FileInputStream mersureStream = new FileInputStream(file);
				opts.inJustDecodeBounds = true;
				Bitmap bitmap = BitmapFactory.decodeStream(mersureStream, null, opts);
				mersureStream.close();
				Size fixedSize = getImageSizeScaleTo(params.displayer());
				opts.inSampleSize = computeSampleSize(opts,-1, fixedSize.size());
				if(bitmap != null && !bitmap.isRecycled()){
					bitmap.recycle();
				}
				bitmap = null;
			}
			opts.inJustDecodeBounds = false;
			FlushedInputStream stream = new FlushedInputStream(new FileInputStream(file));
			Bitmap bitmap = BitmapFactory.decodeStream(stream, null, opts);
			stream.close();
			if(bitmap == null){
				if(LazyImageLoader.DEBUG){
					final String message = "[DECODE] ~ Found a file cannot be decode, DELETE it for redownload.";
					Log.e(TAG, String.format(message));
				}
				file.delete();
			}
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

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
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
    
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}
