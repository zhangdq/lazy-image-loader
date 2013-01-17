package com.lurencun.imageloader;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public interface Downloader {

	boolean load(String target, File save);
	
	public static class SimpleDownloader implements Downloader{

		private static final int CONNECT_TIMEOUT = 30 * 1000;
		private static final int READ_TIMEOUT = 30 * 1000;
		private static final int SC_TEMP_REDIRECT = 307;
		private static final int REDIRECT_RETRY_CONT = 3;
		
		private int manualRedirects = 0;
		
		@Override
		public boolean load(String url, File save) {
			InputStream is = null;
	        OutputStream os = null;
	        HttpURLConnection conn = null;
	        boolean status = true;
	        try {
	            conn = openConnection(url);
	            conn.setConnectTimeout(CONNECT_TIMEOUT);
	            conn.setReadTimeout(READ_TIMEOUT);
	            if (conn.getResponseCode() == SC_TEMP_REDIRECT) {
	                redirect(save, conn);
	            } else {
	            	final int contentLength = conn.getContentLength();
	                is = conn.getInputStream();
	                os = new FileOutputStream(save);
	                copyStream(contentLength, is, os);
	            }
	        } catch (Throwable ex) {
	            ex.printStackTrace();
	            status = false;
	        } finally {
	            if (conn != null) {
	                conn.disconnect();
	            }
	            closeStream(is);
	            closeStream(os);
	        }
	        return status;
		}
		
		void redirect(File save, HttpURLConnection conn) {
	        if (manualRedirects++ < REDIRECT_RETRY_CONT) {
	        	load(conn.getHeaderField("Location"), save);
	        } else {
	            manualRedirects = 0;
	        }
	    }
		
		HttpURLConnection openConnection(String url) throws IOException, MalformedURLException {
	        return (HttpURLConnection) new URL(url).openConnection();
	    }
		
		void copyStream(int totalSize,InputStream is, OutputStream os){
	        final int bufferSize = 2 * 1024;
	        try{
	            byte[] bytes = new byte[bufferSize];
	            int length = 0;
	            while( (length = is.read(bytes, 0, bufferSize)) != -1){
	            	os.write(bytes, 0, length);
	            }
	        } catch(Exception exp){
	        	exp.printStackTrace();
	        }
	    }
		
		void closeStream(Closeable stream) {
	        try {
	            if (stream != null) {
	                stream.close();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
		
	}
}
