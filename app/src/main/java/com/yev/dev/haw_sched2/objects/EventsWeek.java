package com.yev.dev.haw_sched2.objects;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.utils.Const;
import com.yev.dev.haw_sched2.utils.MyCalendar;

import java.util.ArrayList;

public class EventsWeek extends EventsHolder {

	public EventsWeek(Activity activity, View v, LayoutInflater inflater) {
		super(activity, inflater);
		
		list = (ListView)v.findViewById(R.id.list);
		text_primary = (TextView)v.findViewById(R.id.text_primary);
		text_secondary = (TextView)v.findViewById(R.id.text_secondary);
		select = (Button)v.findViewById(R.id.select);
		select.setText(R.string.select_week);
		
		v.findViewById(R.id.icon_priority).setVisibility(View.GONE);
				
	}
	
	@Override
	public void initializeData(String data) {
		
		if(Const.DATA_DEFAULT.equals(data)){
			DATA = utility.getMostTopicalWeek(activity);
			
			if(DATA.equals(Const.DATA_DEFAULT)){
				text_primary.setText(R.string.no_upcoming_events);
				text_secondary.setVisibility(View.GONE);
				return;
			}
		}else{
			DATA = data;
		}
		
		MyCalendar calendar = new MyCalendar(Const.DEF_LOCALE, DATA, Const.DATE_FORMAT_ICS);
		
		text_primary.setText(
				calendar.get_Beginning_Of_Week_Date_String(Const.DATE_FORMAT_DATE)
				+ " - " + calendar.get_End_Of_Week_Date_String(Const.DATE_FORMAT_DATE));
		
		text_secondary.setVisibility(View.VISIBLE);
				
		text_secondary.setText(utility.getWeeksLeftString(activity,
				calendar.getWeeksLeft(),
				calendar.get_Number_Of_Week()));
		
		listData = utility.getWeekData(activity, DATA);
		
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
		
		DATA = utility.getNextWeek(activity, DATA);

		initializeData(DATA);
				
	}
	
	@Override
	public void previous() {
		
		DATA = utility.getPreviousWeek(activity, DATA);
		
		initializeData(DATA);
				
	}
	
	@Override
	public View setDataToView(int position, View convertView) {
		    	
    	convertView = inflater.inflate(R.layout.events_container, null);
    	
    	ArrayList<Object> day_data = (ArrayList<Object>)listData.get(position);
    	
    	for(Object item: day_data){
    		((TextView)convertView.findViewById(R.id.t_header)).setText(
					activity.getString(((Event_Item)item).WEEKDAY_NAME_ID)
    				+ ", " + ((Event_Item)item).START_DAY);
    		View event_view = inflater.inflate(R.layout.event_with_diagram, null);
    		((Event_Item)item).setDataToView(activity, event_view, false, screenWidth);
    		((ViewGroup)convertView.findViewById(R.id.container)).addView(event_view);
    	}
    					
		return convertView;
	}
	
}
