package com.yev.dev.haw_sched2.objects;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.utils.Const;

import java.util.ArrayList;

public class EventsSubject extends EventsHolder {

	public EventsSubject(Activity activity, View v, LayoutInflater inflater) {
		super(activity, inflater);
		
		list = (ListView)v.findViewById(R.id.list);
		text_primary = (TextView)v.findViewById(R.id.text_primary);
		text_secondary = (TextView)v.findViewById(R.id.text_secondary);
		text_secondary.setVisibility(View.GONE); 
		select = (Button)v.findViewById(R.id.select);
		select.setText(R.string.select_subject);	
		icon_priority = (ImageView)v.findViewById(R.id.icon_priority);
						
	}
	
	@Override
	public void initializeData(String data) {
		
		DATA = data;
		
		if(Const.DATA_DEFAULT.equals(DATA)){
			text_primary.setText(R.string.please_select_subject);
			return;
		}
			
		text_primary.setText(DATA);
		
		//icon_priority.setImageResource(utility.getSubjectPriority(activity, DATA));
		icon_priority.setVisibility(View.GONE);
		
		listData = utility.getSubjectData(activity, DATA);
		
		adapter = new MyAdapter(activity, inflater);
				
		list.setAdapter(adapter); 
		
		if(listData.size() > 0){
			list.setSelection(getTopicalPosition());
		}
	}

	@Override
	public int getTopicalPosition() {
		for(int i = 0; i < listData.size(); i++){
			ArrayList<Object> events = (ArrayList<Object>)listData.get(i);
			
			for(int k = 0; k < events.size(); k++){
				if(((Event_Item)events.get(k)).NOT_ENDED){
					return i;
				}
			}
			
		}
		
		return 0;
	}
	
	@Override
	public void next() {
		
		if(DATA.equals(Const.DATA_DEFAULT))
			return;
		
		DATA = utility.getNextSubject(activity, DATA);

		initializeData(DATA);
				
	}
	
	@Override
	public void previous() {
		
		if(DATA.equals(Const.DATA_DEFAULT))
			return;
		
		DATA = utility.getPreviousSubject(activity, DATA);
		
		initializeData(DATA);
				
	}
	
	@Override
	public View setDataToView(int position, View convertView) {
		  	
    	convertView = inflater.inflate(R.layout.events_container, null);
    	
    	ArrayList<Object> day_data = (ArrayList<Object>)listData.get(position);
    	
    	for(Object item: day_data){
    		((TextView)convertView.findViewById(R.id.t_header)).setText(
					activity.getString(((Event_Item)item).WEEKDAY_NAME_ID)
    				+ ", " + ((Event_Item)item).START_DAY
					+ " (" + activity.getString(R.string.week_no)
					+ " " + ((Event_Item)item).WEEK_NUMBER
					+ ")");
    		
    		View event_view = inflater.inflate(R.layout.event_with_diagram, null);
    		((Event_Item)item).setDataToView(activity, event_view, true, screenWidth);
    		((ViewGroup)convertView.findViewById(R.id.container)).addView(event_view);
    	}
    					
		return convertView;
	}

	
}
