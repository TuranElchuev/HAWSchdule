package com.yev.dev.haw_sched2.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.interfaces.OnSubjectsConfigurationChangeListener;
import com.yev.dev.haw_sched2.utils.Const;
import com.yev.dev.haw_sched2.utils.Downloader;
import com.yev.dev.haw_sched2.utils.Downloader.OnFileDownloadedListener;
import com.yev.dev.haw_sched2.utils.ScheduleImporter;
import com.yev.dev.haw_sched2.utils.ScheduleImporter.OnFileImportedListener;

public class Fragment_Web extends FragmentForMainActivity implements OnClickListener {

	private WebView web;
	private ProgressBar pageLoadProgress;
	private int initialScale = 400;
	
	private final int MAX_SCALE = 500; 
	private final int MIN_SCALE = 100;

	private OnSubjectsConfigurationChangeListener onSubjectsConfigurationChangeListener;

	//ON CREATE
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	//ON ACTIVITY CREATED
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		Activity activity = getActivity();
		if(activity instanceof OnSubjectsConfigurationChangeListener){
			onSubjectsConfigurationChangeListener = (OnSubjectsConfigurationChangeListener) activity;
		}
	}
	
	//ON CREATE VIEW
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_web, container, false);
		
		setupViews(v);
		
		return v;
	}

	//SETUP VIEWS
	private void setupViews(View v){
		v.findViewById(R.id.ok).setOnClickListener(this);
		v.findViewById(R.id.refresh).setOnClickListener(this);
		v.findViewById(R.id.zoom_in).setOnClickListener(this);
		v.findViewById(R.id.zoom_out).setOnClickListener(this);
		
		pageLoadProgress = (ProgressBar)v.findViewById(R.id.progress); 
		
		web = (WebView)v.findViewById(R.id.web);
		
		web.setWebViewClient(new WebViewClient() {
	        @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        	if(url.substring(url.length() - 4, url.length()).equals(".ics")){
	        		confirmDownload(url);
	        	}
				return true;
	        }
	    });

		WebSettings webSettings = web.getSettings();
		webSettings.setJavaScriptEnabled(true);

		web.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) 
		            {
		            if(progress < 100 && pageLoadProgress.getVisibility() == View.GONE){
		            	pageLoadProgress.setVisibility(View.VISIBLE);
		            }
		            pageLoadProgress.setProgress(progress);
		            if(progress == 100) {
		            	pageLoadProgress.setVisibility(View.GONE);
		            }
		         }
		     });
		
		
		web.setInitialScale(100);
				
		web.loadUrl(Const.URL);
	}

	
	//MAIN ACTIVITY MESSAGE
	@Override
	public void mainActivityMessage(int message, String data) {
		
		switch (message) {
		case Const.MES_BACK_PRESSED:
			activity.setCalendarsFragment(); 
			break;

		default:
			break;
		}
		
		super.mainActivityMessage(message, data);
	}
		
	//DOWNLOAD FILE
	private void downloadFile(String url_){
				
		Downloader downloader = new Downloader(activity);
				
		downloader.setOnFileDownloadedListener(new OnFileDownloadedListener() {
			@Override
			public void onDownloadProgressUpdate(int progress) {
			
			}
			
			@Override
			public void onFileDownloaded(String fileURL, String filePath, long lastModified) {
				getPriorityAndImportToDB(fileURL, filePath, lastModified);
			}
			
			@Override
			public void onFailedDownload(String fileURL) {
				utility.showWarningDialog(activity, getString(R.string.failed_to_download), utility.getNameFromUrl(fileURL));
			}
			
			@Override
			public void onCancelledDownload(String fileURL) {
				Toast.makeText(activity, R.string.download_cancelled, Toast.LENGTH_SHORT).show();
			}
		});
		
		downloader.download(url_,
				Environment.getExternalStorageDirectory().toString() + Const.TEMP_FILE,
				true);
	}
	
	//OPEN CALENDARS
	private void openCalendars(){
		activity.setCalendarsFragment();
	}
		
	//ON CLICK
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok:
			activity.closeSlider();
			openCalendars();
			break;
		case R.id.refresh:
			activity.closeSlider();
			web.loadUrl(Const.URL);
			break;
		case R.id.zoom_in:
			activity.closeSlider();
			initialScale = Math.min(MAX_SCALE, initialScale + 50);
			web.setInitialScale(initialScale);
			break;
		case R.id.zoom_out:
			activity.closeSlider();
			initialScale = Math.max(MIN_SCALE, initialScale - 50);
			web.setInitialScale(initialScale);
			break;

		default:
			break;
		}
	}

	//CONFIRM DOWNLOAD
	private void confirmDownload(String url_){
		
		final String url = url_;
		
		final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

		alertDialog.setTitle(getString(R.string.download_file));
		
		alertDialog.setMessage(utility.getNameFromUrl(url));
		
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
								
				downloadFile(url);
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
	
	//GET PRIORITY AND IMPORT TO DB
	private void getPriorityAndImportToDB(String url_, String filePath_, long lastModified_){
		
		final String url = url_;
		final String filePath = filePath_;
		
		final ScheduleImporter importer = new ScheduleImporter(activity);
		importer.setOnFileImportedListener(new OnFileImportedListener() {
			@Override
			public void onImportProgressUpdate(int progress) {
				
			}
			
			@Override
			public void onFileImported(String fileUrl) {

				onSubjectsConfigurationChangeListener.onSubjectsConfigurationChange();

				Toast.makeText(activity, getString(R.string.added) + ": " + utility.getNameFromUrl(fileUrl), Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onFailedImport(String fileUrl) {
				utility.showWarningDialog(activity, getString(R.string.failed_to_import), utility.getNameFromUrl(url));
			}
		});
						
		final long lastModified = lastModified_;
		final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

		alertDialog.setTitle(getString(R.string.set_prority));
		
		alertDialog.setMessage(utility.getNameFromUrl(url));
		
		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.low), new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
						
						importer.importSchedule(url, filePath, Const.PRIORITY_LOW, Const.STATE_ENABLED, lastModified, true);
												
					}
				});
				
		alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.medium), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
						
						importer.importSchedule(url, filePath, Const.PRIORITY_MEDIUM, Const.STATE_ENABLED, lastModified, true);
					}
				});
		
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.high), new DialogInterface.OnClickListener() {
			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
						
						importer.importSchedule(url, filePath, Const.PRIORITY_HIGH, Const.STATE_ENABLED, lastModified, true);
					}
				});

		
		alertDialog.show();
		
	}
	
	
	
	
}
