package com.yev.dev.haw_sched2.objects;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.utils.Const;
import com.yev.dev.haw_sched2.utils.MyCalendar;

public class EventsDay extends EventsHolder {

	public EventsDay(Activity activity, View v, LayoutInflater inflater) {
		super(activity, inflater);
		
		list = (ListView)v.findViewById(R.id.list);
		text_primary = (TextView)v.findViewById(R.id.text_primary);
		text_secondary = (TextView)v.findViewById(R.id.text_secondary);
		select = (Button)v.findViewById(R.id.select);
		select.setText(R.string.select_day);
		
		v.findViewById(R.id.icon_priority).setVisibility(View.GONE);
				
	}
	
	@Override
	public void initializeData(String data) {
		
		if(Const.DATA_DEFAULT.equals(data)){
			DATA = utility.getMostTopicalDay(activity);
			
			if(DATA.equals(Const.DATA_DEFAULT)){
				text_primary.setText(R.string.no_upcoming_events);
				text_secondary.setVisibility(View.GONE);
				return;
			}
		}else{
			DATA = data;
		}
		
		MyCalendar calendar = new MyCalendar(Const.DEF_LOCALE, DATA, Const.DATE_FORMAT_ICS);
		
		text_primary.setText(activity.getString(utility.getWeekdayNameId(calendar.get_Day_Of_Week()))
				+ ", " + calendar.getFormattedDate(Const.DATE_FORMAT_DATE));
		
		text_secondary.setVisibility(View.VISIBLE);
		
		text_secondary.setText(utility.getDaysLeftString(activity, calendar.getDaysLeft()));
		
		listData = utility.getDayData(activity, DATA);
		
		adapter = new MyAdapter(activity, inflater);
				
		list.setAdapter(adapter); 
		
		if(listData.size() > 0){
			list.setSelection(getTopicalPosition());
		}
	}

	@Override
	public int getTopicalPosition() {
		for(int i = 0; i < listData.size(); i++){
			if(((Event_Item)listData.get(i)).NOT_ENDED){
				return i;
			}			
		}
		return 0;
	}
	
	@Override
	public void next() {
		
		if(DATA.equals(Const.DATA_DEFAULT))
			return;
		
		DATA = utility.getNextDay(activity, DATA);

		initializeData(DATA);
				
	}
	
	@Override
	public void previous() {
		
		DATA = utility.getPreviousDay(activity, DATA);
		
		initializeData(DATA);
				
	}
	
	@Override
	public View setDataToView(int position, View convertView) {
		    	
    	if(convertView == null){
    		convertView = inflater.inflate(R.layout.event_with_diagram, null);
    	}
    			
		Event_Item item = (Event_Item)listData.get(position);
		item.setDataToView(activity, convertView, false, screenWidth);
		
		return convertView;
	}
	
}
