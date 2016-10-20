package com.yev.dev.haw_sched2.utils;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;

import com.yev.dev.haw_sched2.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ScheduleImporter {
	
	public interface OnFileImportedListener{
		public void onImportProgressUpdate(int progress);
		
		public void onFileImported(String fileUrl);
		
		public void onFailedImport(String fileUrl);
		
	}
	
	private OnFileImportedListener listener;
	
	private Context context;
	private ProgressDialog pDialog;
	
	private String fileUrl;
	private String filePath;
	private int priority;
	private long fileLastModified = 0;
	private int state;
	
	private boolean SHOW_DIALOG = true;
		
	public ScheduleImporter(Context context) {
		this.context = context;
	}
	
	//SET LISTENER
	public void setOnFileImportedListener(OnFileImportedListener listener){
		this.listener = listener;
	}
	
	//DOWNLOAD
	public void importSchedule(String fileUrl,
			String filePath,
			int priority,
			int state,
			long lastModified,
			boolean SHOW_DIALOG){
		
		this.fileUrl = fileUrl;
		this.filePath = filePath;
		this.priority = priority;
		this.state = state;
		this.SHOW_DIALOG = SHOW_DIALOG;
		this.fileLastModified = lastModified;
		
		new LoadFileToDataBaseTask().execute();
	}
	
	
	//SHOW DIALOG
	private void showDialog(){
		
		if(!SHOW_DIALOG)
			return;
		
		pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.importing));
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);
        
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
	

	//GET VALUE FROM ICS LINE
	private void getValueFromLine(String line, ContentValues cv){
		
		String value = null;
		MyCalendar calendar;
			
		if(line.startsWith(Const.CAL_KEY_UID)){
			
			value = line.substring(Const.CAL_KEY_UID.length(), line.length());
			
			cv.put(DBHelper.COL_UID, value);
						
		}else if(line.startsWith(Const.CAL_KEY_SUMMARY)){
			
			value = line.substring(Const.CAL_KEY_SUMMARY.length(), line.length());
			
			cv.put(DBHelper.COL_SUMMARY, value);
						
		}else if(line.startsWith(Const.CAL_KEY_LOCATION)){

			value = line.substring(Const.CAL_KEY_LOCATION.length(), line.length() - 17);
			
			cv.put(DBHelper.COL_LOCATION, value);
						
		}else if(line.startsWith(Const.CAL_KEY_DATE_START)){
			
			line = line.substring(line.length() - 15, line.length());
			
			calendar = new MyCalendar(Const.DEF_LOCALE, line, Const.DATE_FORMAT_ICS);
			
			cv.put(DBHelper.COL_START, line);
			cv.put(DBHelper.COL_START_DAYTIME, line.substring(line.length() - 6, line.length()));
			cv.put(DBHelper.COL_START_DAYBEGINNING, calendar.get_Beginning_Of_Day_Date_String(Const.DATE_FORMAT_ICS));
			cv.put(DBHelper.COL_START_DAYOFWEEK, calendar.get_Day_Of_Week());
			cv.put(DBHelper.COL_START_WEEKBEGINNING, calendar.get_Beginning_Of_Week_Date_String(Const.DATE_FORMAT_ICS));
			cv.put(DBHelper.COL_START_WEEKENDING, calendar.get_End_Of_Week_Date_String(Const.DATE_FORMAT_ICS));
			cv.put(DBHelper.COL_START_WEEKNUMBER, calendar.get_Number_Of_Week());
						
		}else if(line.startsWith(Const.CAL_KEY_DATE_END)){
			
			line = line.substring(line.length() - 15, line.length());
						
			cv.put(DBHelper.COL_END, line);
			cv.put(DBHelper.COL_END_DAYTIME, line.substring(line.length() - 6, line.length()));
							
		}
							
	}
	
	
	//DOWNLOADING TASK
	private class LoadFileToDataBaseTask extends AsyncTask<String, Integer, Void> {
		
		private boolean SUCCESS = true;
		
	    @Override
 	    protected void onPreExecute() {
	        super.onPreExecute();
	        showDialog();
	    }
	 	    
	    @Override
	    protected Void doInBackground(String... f_url) {
	        
	    		    	
	    	File file = new File(filePath);
			
			if(!file.exists()
					|| !Uri.fromFile(file).getLastPathSegment().endsWith("ics")){
				
				SUCCESS = false;
				
				return null;
			}
			
			long length = file.length();
			long count = 0;

			DBHelper dbh = new DBHelper(context);
			
			SQLiteDatabase db = dbh.getWritableDatabase();
			
			try {
							
				//DELETE PREVIOUS VERSION OF FILE
				db.delete(DBHelper.TABLE_NAME_SCHEDULE,
						DBHelper.COL_FILE_URL + " LIKE ?",
						new String[] {fileUrl});
				
				long current_time = System.currentTimeMillis();
				
				ContentValues cv = null;
				
			    BufferedReader br = new BufferedReader(new FileReader(file));
			    String line;
			    boolean event_started = false;

			    while ((line = br.readLine()) != null) {
			        
			    	count += line.length();
			    	publishProgress((int)(count * 100 / length));
			    	
			    	if(line.startsWith(Const.CAL_KEY_BEGIN)){
			    		event_started = true;
			    		cv = new ContentValues();
			    		
			    		cv.put(DBHelper.COL_FILE_URL, fileUrl);
			    		cv.put(DBHelper.COL_FILE_LAST_MODIFIED, fileLastModified);
			    		cv.put(DBHelper.COL_ADDED, current_time);		    		
			    		cv.put(DBHelper.COL_STATE, state);
						cv.put(DBHelper.COL_PRIORITY, priority);
									    		
			    	}else if(line.startsWith(Const.CAL_KEY_END)){
			    		event_started = false;
			    		
			    		//insert values
			    		db.insert(
			   		         DBHelper.TABLE_NAME_SCHEDULE,
			   		         DBHelper.COL_NULL,
			   		         cv);
			    		
			    	}else{
			    		if(event_started){
			    			getValueFromLine(line, cv);
			    		}
			    	}
			    			    			    	
			    }
			    
			    br.close();
			    
			}
			catch (Exception e) {
				db.close();
			    return null;
			}			
			

			db.close();
	 
	        return null;
	    }
	 
	    protected void onProgressUpdate(Integer... progress) {
	    	setProgress(progress[0]);
	    	if(listener != null){
	    		listener.onImportProgressUpdate(progress[0]);
	    	}
	   }
	 
	    @Override
	    protected void onPostExecute(Void result) {

	        dismissDialog();

	        if(SUCCESS){
	        	File file = new File(filePath);
	        	if(file.exists()){
	        		try{
	        			file.delete();
	        		}catch(Exception e){
	        			
	        		}
	        	}
	        }
	        
	    	if(listener != null){
		        if(SUCCESS){
		        	listener.onFileImported(fileUrl);
		        }else{
		        	listener.onFailedImport(fileUrl); 
		        }
	    	}
	    }	 
	}

}
