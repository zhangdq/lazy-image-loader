package com.lurencun.imageloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public interface Downloader {

	boolean load(String url, File save);
	
	public static class SimpleDownloader implements Downloader{

		@Override
		public boolean load(String url, File save) {
			try{
				URL imageUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
				conn.setConnectTimeout(30*1000);
				conn.setReadTimeout(30*1000);
				conn.setInstanceFollowRedirects(true);
				InputStream is = conn.getInputStream();
				OutputStream os = new FileOutputStream(save);
				save(is, os);
				os.close();
				is.close();
				return true;
			}catch(Throwable exp){
				return false;
			}
			
		}
		
		void save(InputStream is, OutputStream os){
	        final int bufferSize = 2 * 1024;
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
		
	}
}
