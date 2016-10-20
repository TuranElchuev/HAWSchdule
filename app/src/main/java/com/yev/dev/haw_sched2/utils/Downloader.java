package com.yev.dev.haw_sched2.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.yev.dev.haw_sched2.R;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class Downloader {

	public interface OnFileDownloadedListener{
		public void onDownloadProgressUpdate(int progress);
		
		public void onFileDownloaded(String fileURL, String filePath, long lastModified);
		
		public void onFailedDownload(String fileURL);
		
		public void onCancelledDownload(String fileURL);
	}
	
	private OnFileDownloadedListener listener;
	
	private Context context;
	private ProgressDialog pDialog;
		
	private boolean SHOW_DIALOG = true;
	
	private DownloadFileFromURL downloadFileTask;
	
	public Downloader(Context context) {
		this.context = context;
	}
	
	//SET LISTENER
	public void setOnFileDownloadedListener(OnFileDownloadedListener listener){
		this.listener = listener;
	}
	
	//DOWNLOAD
	public void download(String url, String filePath, boolean SHOW_DIALOG){
		this.SHOW_DIALOG = SHOW_DIALOG;
		
		downloadFileTask = new DownloadFileFromURL(url, filePath);
		downloadFileTask.execute();
	}
	
	//CANCEL DOWNLOADING
	public void cancelDownloading(){
		if(downloadFileTask != null){
			downloadFileTask.stop();
		}
	}
	
	//SHOW DIALOG
	private void showDialog(){
		
		if(!SHOW_DIALOG)
			return;
		
		pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.downloading));
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);

        pDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
        					context.getString(R.string.cancel),
        					new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									pDialog.cancel();	
									cancelDownloading();
								}
							});
        
        pDialog.show();
	}
	
	//DISMISS DIALOG
	private void dismissDialog(){
		if(pDialog != null && pDialog.isShowing()){
			try{
				pDialog.dismiss();
			}catch(Exception e){}
		}
	}

	//SET PROGRESS
	private void setProgress(int progress){
		if(pDialog != null && pDialog.isShowing()){
			pDialog.setProgress(progress);
		}
	}
	
	//DOWNLOADING TASK
	private class DownloadFileFromURL extends AsyncTask<Void, Integer, Void> {
		
		private boolean SUCCESS = true;
		private boolean STOP = false;
		
		private String filePath;
		private String url_str;
		private long lastModified;
		
		public DownloadFileFromURL(String url_str, String filePath) {
			this.url_str = url_str;
			this.filePath = filePath;
		}
		
	    @Override
 	    protected void onPreExecute() {
	        super.onPreExecute();
	        showDialog();
	    }
	 	    
	    @Override
	    protected Void doInBackground(Void... args) {
	        int count;
	        try {
	        	
	            URL url = new URL(url_str);
	            URLConnection conection = url.openConnection();
	            conection.connect();
	            
	            lastModified = conection.getLastModified();
	            
	            // getting file length
	            int lenghtOfFile = conection.getContentLength();
	            
	            // input stream to read file - with 8k buffer
	            InputStream input = new BufferedInputStream(url.openStream(), 8192);
	 	            
	            // Output stream to write file
	            OutputStream output = new FileOutputStream(filePath);
	 
	            byte data[] = new byte[1024];
	 
	            long total = 0;
	 
	            while (!STOP && (count = input.read(data)) != -1) {
	                total += count;
	                
	                publishProgress((int)((total*100)/lenghtOfFile));
	 
	                // writing data to file
	                output.write(data, 0, count);
	            }
	 
	            // flushing output
	            output.flush();
	 
	            // closing streams
	            output.close();
	            input.close();
	 
	        } catch (Exception e) {
	            SUCCESS = false;
	        }
	 
	        return null;
	    }
	 
	    protected void onProgressUpdate(Integer... progress) {	        
	        setProgress(progress[0]);
	        if(listener != null){
	        	listener.onDownloadProgressUpdate(progress[0]);
	        }
	   }
	 
	    @Override
	    protected void onPostExecute(Void result) {
	 
	        dismissDialog();
	 
	        if(listener != null){
		        if(STOP){
		        	listener.onCancelledDownload(url_str); 
		        }else if(SUCCESS){
		        	listener.onFileDownloaded(url_str, filePath, lastModified);
		        }else{
		        	listener.onFailedDownload(url_str); 
		        }
	        }
	    }
	    
	    public void stop(){
	    	STOP = true;
	    }
	 
	}

}
