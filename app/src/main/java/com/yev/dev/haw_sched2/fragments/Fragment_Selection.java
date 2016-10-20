package com.yev.dev.haw_sched2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.objects.CalendarRow;
import com.yev.dev.haw_sched2.objects.CalendarRow.CalendarRowViewHolder;
import com.yev.dev.haw_sched2.objects.Subject_Item;
import com.yev.dev.haw_sched2.utils.Const;

import java.util.ArrayList;

public class Fragment_Selection extends FragmentForMainActivity implements OnClickListener{

	private OnClickListener dayClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			DATA = (String)v.getTag();
			setSelection(0);
		}
	};
	
	private ListView list;
	private ArrayList<Object> data;
	private MyAdapter adapter;
	private LayoutInflater inflater;
	
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

		this.inflater = inflater;
		
		View v = inflater.inflate(R.layout.fragment_selection, container, false);
		
		setupViews(v);
		
		return v;
	}

	//SETUP VIEWS
	private void setupViews(View v){
		v.findViewById(R.id.cancel).setOnClickListener(this);
		v.findViewById(R.id.back).setOnClickListener(this);
		
		TextView header = (TextView)v.findViewById(R.id.header);
		
		switch (MODE) {
		case Const.MODE_DAY:
			header.setText(R.string.select_day);
			break;
		case Const.MODE_WEEK:
			header.setText(R.string.select_week);
			break;
		case Const.MODE_WEEKDAY:
			header.setText(R.string.select_weekday);
			v.findViewById(R.id.calendar_header).setVisibility(View.GONE);
			break;
		case Const.MODE_SUBJECT:
			header.setText(R.string.select_subject);
			v.findViewById(R.id.calendar_header).setVisibility(View.GONE);
			break;

		default:
			break;
		}
		
		list = (ListView)v.findViewById(R.id.list);
		

		list.setOnItemClickListener(new OnItemClickListener() {

	        @Override
	        public void onItemClick(AdapterView<?> parent, View view,
	                final int position, long id) {
	
	        	setSelection(position);	        	
	        	
	        }
	    });
		
	}
	
	//SET DATA
	private void setData(){

		activity.closeSlider();
		
		switch (MODE) {
		case Const.MODE_DAY:
			data = utility.getWholeCalendar(activity, DATA);
			break;
		case Const.MODE_WEEK:
			data = utility.getWholeCalendar(activity, DATA);
			break;
		case Const.MODE_WEEKDAY:
			data = utility.getAllWeekdays();
			break;
		case Const.MODE_SUBJECT:
			data = utility.getAllSubjects(activity);
			break;

		default:
			break;
		}
				
		adapter = new MyAdapter(activity);
		
		list.setAdapter(adapter);
		
		if(MODE == Const.MODE_DAY || MODE == Const.MODE_WEEK){
			for(int i = 0; i < data.size(); i++){
				if(((CalendarRow)data.get(i)).CONTAINS_CURRENT_DATA){
					list.setSelection(i);
					break;
				}
			}
		}
		
	} 
	
	//MAIN ACTIVITY MESSAGE
	@Override
	public void mainActivityMessage(int message, String data) {
		
		switch (message) {
		case Const.MES_BACK_PRESSED:
			activity.setEventFragment(MODE, DATA); 
			break;

		default:
			break;
		}
		
		super.mainActivityMessage(message, data);
	}
	
	//ON CLICK
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			activity.closeSlider();
			activity.setEventFragment(MODE, DATA);
			break;
		case R.id.back:
			activity.closeSlider();
			activity.setEventFragment(MODE, DATA);
			break;

		default:
			break;
		}
	}
	
	//SET SELECTION
	private void setSelection(int position){
		
		activity.closeSlider();
		
		switch (MODE) {
		case Const.MODE_WEEK:
			DATA = ((CalendarRow)data.get(position)).DATES.get(0);
			break;
		case Const.MODE_WEEKDAY:
			DATA = String.valueOf(data.get(position));
			break;
		case Const.MODE_SUBJECT:
			DATA = ((Subject_Item)data.get(position)).NAME;
			break;

		default:
			break;
		}
		
		activity.setEventFragment(MODE, DATA);
	}
	
	//GET SUBJECT VIEW
	private View getSubjectView(View convertView, int position){
		
		if(convertView == null){
			convertView = inflater.inflate(R.layout.list_item_subject, null);
		}
		
		Subject_Item item = (Subject_Item)data.get(position);
		
		((TextView)convertView.findViewById(R.id.t_name)).setText(item.NAME);
		((ImageView)convertView.findViewById(R.id.icon_priority)).setImageResource(item.PRIORITY_IMAGE);
		
		return convertView;
	}

	//GET DAY VIEW
	private View getDayView(View convertView, int position){
				
		if(convertView == null){
			convertView = inflater.inflate(R.layout.list_item_calendar_row, null);
			convertView.setTag(new  CalendarRowViewHolder(convertView));
		} 
				
		CalendarRow row = (CalendarRow)data.get(position);
		
		row.setDataToView(activity, dayClickListener, DATA, convertView, true);
			
		return convertView;
	}
	
	//GET WEEK VIEW
	private View getWeekView(View convertView, int position){
				
		if(convertView == null){
			convertView = inflater.inflate(R.layout.list_item_calendar_row, null);
			convertView.setTag(new  CalendarRowViewHolder(convertView));
		} 
				
		CalendarRow row = (CalendarRow)data.get(position);
		
		row.setDataToView(activity, dayClickListener, DATA, convertView, false);
		
		if(DATA.equals(row.DATES.get(0))){
			convertView.setBackgroundResource(R.drawable.selector_selected_cal_item);
		}else if(row.THIS_WEEK.equals(row.DATES.get(0))){ 
			convertView.setBackgroundResource(R.drawable.selector_today_cal_item);
		}else{
			convertView.setBackgroundResource(R.drawable.selector_cal_item);
		}
		
		return convertView;
	}
	
	//GET WEEKDAY VIEW
	private View getWeekdayView(View convertView, int position){

		if(convertView == null){
			convertView = inflater.inflate(R.layout.list_item_weekday, null);
		}
				
		((TextView)convertView.findViewById(R.id.t_name)).setText(utility.getWeekdayNameId((int)data.get(position)));
		
		return convertView;
	}
	
	//ADAPTER
	public class MyAdapter extends SimpleAdapter{
		
		public MyAdapter(Context context) {
			
				super(context, null, 0, null, null);
						        
		    }

		@Override
		public int getCount() {
			return data.size();
		}
		
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
		    			    
	    	switch (MODE) {
			case Const.MODE_DAY:
				convertView = getDayView(convertView, position);
				break;
			case Const.MODE_WEEK:
				convertView = getWeekView(convertView, position);
				break;
			case Const.MODE_WEEKDAY:
				convertView = getWeekdayView(convertView, position);
				break;
			case Const.MODE_SUBJECT:
				convertView = getSubjectView(convertView, position);
				break;

			default:
				break;
			}
	    	
	    	return convertView;
	    }
	}

}
