package com.yev.dev.haw_sched2.objects;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yev.dev.haw_sched2.utils.Utility;

import java.util.ArrayList;

public class EventsHolder {

	public String DATA;
	public Activity activity;
	public Utility utility = new Utility();

	public int screenWidth = 0;
	
	public ListView list;
	public TextView text_primary;
	public TextView text_secondary;
	public ImageView icon_priority;
	public Button select;
	
	public LayoutInflater inflater;
	
	public ArrayList<Object> listData;
	
	public MyAdapter adapter;

	public EventsHolder(Activity acivity, LayoutInflater inflater){
		this.activity = acivity;
		this.inflater = inflater;

		Display display = acivity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = size.x;
	}

	public void initializeData(String data){
		
	}	
	
	//GET TOPICAL LIST ITEM POSITION
	public int getTopicalPosition(){
		return 0;
	}
	
	//NEXT
	public void next(){
		
	}
	
	//PREVIOUS
	public void previous(){
		
	}
	
	//SET DATA TO VIEW
	public View setDataToView(int position, View convertView){
		
		return null;
	}	
	
	//ADAPTER
	public class MyAdapter extends SimpleAdapter{
		
		LayoutInflater inflater;
		
		public MyAdapter(Context context, LayoutInflater inflater) {
			
				super(context, null, 0, null, null);
				
				this.inflater = inflater;		        
		    }

		@Override
		public int getCount() {
			return listData.size();
		}
		
		    @Override
		    public View getView(int position, View convertView, ViewGroup parent) {
			    			    	
		    	return setDataToView(position, convertView);
		    }
	}
	
}
