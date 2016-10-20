package com.yev.dev.haw_sched2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.objects.Calendar_Item;
import com.yev.dev.haw_sched2.utils.Const;
import com.yev.dev.haw_sched2.utils.DBHelper;
import com.yev.dev.haw_sched2.utils.Downloader;
import com.yev.dev.haw_sched2.utils.Downloader.OnFileDownloadedListener;
import com.yev.dev.haw_sched2.utils.ScheduleImporter;
import com.yev.dev.haw_sched2.utils.ScheduleImporter.OnFileImportedListener;

import java.util.ArrayList;

public class Fragment_Calendars extends FragmentForMainActivity implements OnClickListener {

	private ArrayList<Calendar_Item> data;
	private ListView list;
	private MyAdapter adapter;

	private View v;
	
	//FOR UPDATING PROCESS////////////////////////////
	private int CURRENT_INDEX;
	private Calendar_Item CURRENT_ITEM;
	private boolean CANCELED;
	private boolean FAILED;
	
	private int CURRENT_OPERATION_NUMBER;
	private int MAX_OPERATIONS;
	
	private String FAIL_LIST;
	
	///////////////////////////////////////////////
	
	
	//================LIFE CYCLE======================
	
	//ON CREATE
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	//ON ACTIVITY CREATED
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		setData();
	}
	
	//ON CREATE VIEW
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.fragment_calendars, container, false);
		
		setupViews();
						
		return v;
	}

	//SETUP VIEWS
	private void setupViews(){
		v.findViewById(R.id.add).setOnClickListener(this);
		v.findViewById(R.id.update_all).setOnClickListener(this);
		v.findViewById(R.id.delete_all).setOnClickListener(this);
		v.findViewById(R.id.info).setOnClickListener(this);
		v.findViewById(R.id.back).setOnClickListener(this);
		
		list = (ListView)v.findViewById(R.id.list);
		

		list.setOnItemClickListener(new OnItemClickListener() {

	        @Override
	        public void onItemClick(AdapterView<?> parent, View view,
	                final int position, long id) {
	
	        	selectItemDialog(position);	        	
	        	
	        }
	    });
		
	}

	//ON CLICK
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add:
			activity.closeSlider();
			openWeb();
			break;
		case R.id.delete_all:
			activity.closeSlider();
			deleteAll();
			break;
		case R.id.update_all:
			activity.closeSlider();
			confirmUpdateAll();			
			break;
		case R.id.info:
			activity.closeSlider();
			showInfoDialog();		
			break;
		case R.id.back:
			activity.slideSlider();
			break;

		default:
			break;
		}
	}
	
	//MAIN ACTIVITY MESSAGE
	@Override
	public void mainActivityMessage(int message, String data) {
		
		switch (message) {
		case Const.MES_BACK_PRESSED:
			activity.setEventFragment(Const.MODE_DAY, Const.DATA_DEFAULT); 
			break;

		default:
			break;
		}
		
		super.mainActivityMessage(message, data);
	}
	
	
	//DIALOG SELECT ITEM
	private void selectItemDialog(int pos){
				
		final int position = pos;
		
		Calendar_Item item = data.get(position);
		
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		dialog.setContentView(R.layout.dialog_calendar_item);
		
		((TextView)dialog.findViewById(R.id.file_name)).setText(item.FILE_NAME);
		
		ImageButton enable_disable = (ImageButton)dialog.findViewById(R.id.enable_disable);
		
		if(item.STATE == Const.STATE_DISABLED){
			enable_disable.setImageResource(R.drawable.ic_enabled);
		}else{
			enable_disable.setImageResource(R.drawable.ic_disabled);
		}
		
		enable_disable.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				dialog.cancel();
				
				enableDisable(position);
				
			}
		});
		
		dialog.findViewById(R.id.delete).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
				delete(position);
			}
		});
		
		dialog.findViewById(R.id.low).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();		
				setPriority(position, Const.PRIORITY_LOW);
			}
			});
		
		dialog.findViewById(R.id.normal).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
				setPriority(position, Const.PRIORITY_MEDIUM);
			}
		});
		
		dialog.findViewById(R.id.high).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
				setPriority(position, Const.PRIORITY_HIGH);
			}
		});
	
		dialog.show();
	}
	
	//SET PRIORITY
	private void setPriority(int position, int priority){
		
		Calendar_Item item = data.get(position);
		
		if(item.PRIOORITY == priority)
			return;
		
		ContentValues values = new ContentValues();
		
		values.put(DBHelper.COL_PRIORITY, priority);
		
		String selection = DBHelper.COL_FILE_URL + " LIKE ?";
		String[] selectionArgs = {item.FILE_URL};
		
		utility.updateData(activity, DBHelper.TABLE_NAME_SCHEDULE, values, selection, selectionArgs);
		
		//
		item.PRIOORITY = priority;
		
		adapter.notifyDataSetChanged();
	}

	//ENABLE DISABLE
	private void enableDisable(int position){
		Calendar_Item item = data.get(position);
		
		if(item.STATE == Const.STATE_ENABLED){
			item.STATE = Const.STATE_DISABLED;
		}else{
			item.STATE = Const.STATE_ENABLED;
		}
				
		ContentValues values = new ContentValues();
		
		values.put(DBHelper.COL_STATE, item.STATE);
		
		String selection = DBHelper.COL_FILE_URL + " LIKE ?";
		String[] selectionArgs = {item.FILE_URL};
		
		utility.updateData(activity, DBHelper.TABLE_NAME_SCHEDULE, values, selection, selectionArgs);
		
		//
		
		adapter.notifyDataSetChanged();
	}
	
	//DELETE ALL
	private void deleteAll(){
		
		if(data.size() == 0){
			Toast.makeText(activity, R.string.have_no_calendars, Toast.LENGTH_LONG).show();
			return;
		}
		
		final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

		alertDialog.setTitle(getString(R.string.delete_all_calendars));
				
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
				
				utility.deleteAll(activity, DBHelper.TABLE_NAME_SCHEDULE);
				
				Toast.makeText(activity, R.string.all_calendars_deleted, Toast.LENGTH_LONG).show();
				
				setData();
			}
		});
		
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});
		
		alertDialog.show();
			
	}
	
	//DELETE
	private void delete(int position){
		
		final Calendar_Item item = data.get(position);
		
		final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

		alertDialog.setTitle(getString(R.string.delete_calendar));
		
		alertDialog.setMessage(item.FILE_NAME);
		
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
								
				String selection = DBHelper.COL_FILE_URL + " LIKE ?";
				String[] selectionArgs = {item.FILE_URL};
				
				utility.deleteData(activity, DBHelper.TABLE_NAME_SCHEDULE, selection, selectionArgs);
								
				setData();
			}
		});
		
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});
		
		alertDialog.show();
			
	}
	
	
	//OPEN WEB
	private void openWeb(){
		activity.setWebFragment();
	}
	
	//SET DATA
	private void setData(){
		
		activity.dataBaseIsEmpty(false);
		
		data = utility.getCalendars(activity);
		
		if(data.isEmpty()){
			v.findViewById(R.id.empty).setVisibility(View.VISIBLE);
			return;
		}
		
		adapter = new MyAdapter(activity, activity.getLayoutInflater());
		list.setAdapter(adapter);
	}
	
	//ADAPTER
	private class MyAdapter extends SimpleAdapter{
		
		LayoutInflater inflater;
		
		public MyAdapter(Context context, LayoutInflater inflater) {
			
				super(context, null, 0, null, null);
				
				this.inflater = inflater;		        
		    }

		@Override
		public int getCount() {
			return data.size();
		}
		
		    @Override
		    public View getView(int position, View convertView, ViewGroup parent) {
		    	
		    	if(convertView == null){
		    		convertView = inflater.inflate(R.layout.list_item_calendar, null);
		    	}
		    	
		    	Calendar_Item item = data.get(position);
		    	
		    	((TextView)convertView.findViewById(R.id.file_name)).setText(item.FILE_NAME);
		    	((TextView)convertView.findViewById(R.id.version)).setText(item.FILE_LAST_MODIFIED_STRING);
		    	((TextView)convertView.findViewById(R.id.imported)).setText(item.ADDED_STRING);
		    
		    	if(item.STATE == Const.STATE_ENABLED){
		    		((ImageView)convertView.findViewById(R.id.icon_enabled)).setImageResource(R.drawable.ic_enabled);
		    		convertView.setAlpha(Const.ALPHA_ENABLED);
		    	}else{
		    		((ImageView)convertView.findViewById(R.id.icon_enabled)).setImageResource(R.drawable.ic_disabled);
		    		convertView.setAlpha(Const.ALPHA_DISABLED);
		    	}
		    	
		    	((ImageView)convertView.findViewById(R.id.icon_priority)).setImageResource(utility.getPriorityImage(item.PRIOORITY));
		    	
		    	return convertView;
		    }
	}

	
	
	//===================================UPDATING======================================
	
	//CONFIRM UPDATE ALL
	private void confirmUpdateAll(){
		
		if(data.size() == 0){
			Toast.makeText(activity, R.string.have_no_calendars, Toast.LENGTH_LONG).show();
			return;
		}
		
		final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

		alertDialog.setTitle(getString(R.string.update_all_calendars));
		
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
							
				updateAll();
			}
		});
		
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});
		
		alertDialog.show();
			
	}
		
	//UPDATE ALL
	private void updateAll(){
		
		FAIL_LIST = "";
		CURRENT_INDEX = -1;
		CANCELED = false;
		FAILED = false;
		CURRENT_OPERATION_NUMBER = -1;
		MAX_OPERATIONS = data.size() * 2;


		final Downloader downloader = new Downloader(activity);
		final ScheduleImporter importer = new ScheduleImporter(activity);
		
		final ProgressDialog pDialog = new ProgressDialog(activity);

		//downloader listener
		downloader.setOnFileDownloadedListener(new OnFileDownloadedListener() {
			
			@Override
			public void onFileDownloaded(String fileURL, String filePath, long lastModified) {
				if(CANCELED)
					return;
				
				CURRENT_OPERATION_NUMBER++;
				
				importer.importSchedule(CURRENT_ITEM.FILE_URL,
									filePath,
									CURRENT_ITEM.PRIOORITY,
									CURRENT_ITEM.STATE,
									lastModified,
									false);
				
			}
			
			@Override
			public void onFailedDownload(String fileURL) {
				FAILED = true;
				FAIL_LIST = FAIL_LIST + utility.getNameFromUrl(fileURL) + "\n";
				downloadNextItem(downloader, pDialog);
			}
			
			@Override
			public void onDownloadProgressUpdate(int progress) {
				pDialog.setProgress((int)((100f * CURRENT_OPERATION_NUMBER + progress) / MAX_OPERATIONS));				
			}
			
			@Override
			public void onCancelledDownload(String fileURL) {}
		});
		
		
		//importer listener
		importer.setOnFileImportedListener(new OnFileImportedListener() {
			
			@Override
			public void onFileImported(String fileUrl) {
				downloadNextItem(downloader, pDialog);
			}
			
			@Override
			public void onImportProgressUpdate(int progress) {
				pDialog.setProgress((int)((100f * CURRENT_OPERATION_NUMBER + progress) / MAX_OPERATIONS));
			}
			
			@Override
			public void onFailedImport(String fileUrl) {
				FAILED = true;
				FAIL_LIST = FAIL_LIST + utility.getNameFromUrl(fileUrl) + "\n";
				downloadNextItem(downloader, pDialog);
			}
		});
		
		
		//dialog		
        pDialog.setMessage(getString(R.string.updating_calendars));
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);

        pDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
        					getString(R.string.cancel),
        					new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									pDialog.cancel();	
									downloader.cancelDownloading();
									CANCELED = true;
									
									Toast.makeText(activity, R.string.canceled, Toast.LENGTH_LONG).show();
									
									setData();
								}
							});
        
        pDialog.show();
        
        
        downloadNextItem(downloader, pDialog);
		
	}
	
	//DOWNLOAD NEXT ITEM
	private void downloadNextItem(Downloader downloader, ProgressDialog pDialog){
		if(CANCELED){			
			return;
		}
		
		if(CURRENT_INDEX >= data.size() - 1
				&& !CANCELED){
			
			if(FAILED){
				utility.showWarningDialog(activity, getString(R.string.failed_to_update), FAIL_LIST);
			}else{
				Toast.makeText(activity, R.string.schedule_updated, Toast.LENGTH_LONG).show();
			}
			
			pDialog.cancel();
			setData();
			return;
		}
		
		CURRENT_OPERATION_NUMBER++;
		CURRENT_INDEX++;
		CURRENT_ITEM = data.get(data.size() - 1 - CURRENT_INDEX);//reverse order
        downloader.download(CURRENT_ITEM.FILE_URL,
        					Environment.getExternalStorageDirectory().toString() + Const.TEMP_FILE,
        					false);
	}
	
	
	//======================================INFO===========================================
	
	//SHOW INFO DIALOG
	private void showInfoDialog(){

		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		dialog.setContentView(R.layout.dialog_info);
		
		dialog.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		
		dialog.show();
	}
	
	
}
