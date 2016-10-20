package com.yev.dev.haw_sched2.objects;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.yev.dev.haw_sched2.R;

public class CalendarRow {

	public boolean CONTAINS_CURRENT_DATA = false;
	
	public int weekNumber;
	public boolean is_middle = false;
	
	public String TODAY = "";
	public String THIS_WEEK = "";
	
	public ArrayList<String> DATES = new ArrayList<String>();
	public ArrayList<Boolean> BUSY = new ArrayList<Boolean>();
	public ArrayList<Integer> MONTHS = new ArrayList<Integer>();
				
	private boolean MARK_WHOLE_WEEK = false;
	
	public void setDataToView(Context context, OnClickListener listener, String DATA, View v, boolean DAY_MODE){
		
		CalendarRowViewHolder holder = (CalendarRowViewHolder)v.getTag();
		
		if(is_middle){
			holder.t_month.setVisibility(View.VISIBLE);
			holder.t_month.setText(getMonth(context.getResources(), MONTHS.get(0)) + " " + DATES.get(0).substring(0, 4));
		}else{
			holder.t_month.setVisibility(View.GONE);
		}
		
		holder.t_week.setText(String.valueOf(weekNumber));
		
		for(int i = 0; i < 7; i++){
			
			String DATE = DATES.get(i);
			
			TextView tv = holder.text_views.get(i);
			
			tv.setTag(DATE);
			
			tv.setText(DATE.substring(6, 8));
	
			if(DAY_MODE){
				
				tv.setOnClickListener(listener);
				
				if(DATE.equals(DATA)){
					tv.setBackgroundResource(R.drawable.selector_selected_cal_item);
					tv.setTextColor(ContextCompat.getColor(context, R.color.app_secondary_color));
				}else{
					if(MONTHS.get(i) % 2 == 1){				
						if(DATE.equals(TODAY)){
							tv.setBackgroundResource(R.drawable.selector_today_cal_item_odd_month);
						}else{
							tv.setBackgroundResource(R.drawable.selector_cal_item_odd_month);
						}
					}else{
						if(DATE.equals(TODAY)){
							tv.setBackgroundResource(R.drawable.selector_today_cal_item);
						}else{
							tv.setBackgroundResource(R.drawable.selector_cal_item);
						}
					}
					if(i > 4){
						tv.setTextColor(ContextCompat.getColor(context, R.color.calendar_red));
					}else{
						tv.setTextColor(ContextCompat.getColor(context, R.color.text_list_primary));
					}
				}		
				
			}else{
				
				if(i == 0 && DATE.equals(DATA)){
					MARK_WHOLE_WEEK = true;		
				}
			
				if(MARK_WHOLE_WEEK){
					tv.setBackgroundResource(R.drawable.cal_item);
					tv.setTextColor(ContextCompat.getColor(context, R.color.app_secondary_color));
				}else{
					if(MONTHS.get(i) % 2 == 1){
						tv.setBackgroundResource(R.drawable.cal_item_odd_month);
					}else{
						tv.setBackgroundResource(R.drawable.cal_item);
					}
					if(i > 4){
						tv.setTextColor(ContextCompat.getColor(context, R.color.calendar_red));
					}else{
						tv.setTextColor(ContextCompat.getColor(context, R.color.text_list_primary));
					}
				}

			}


			if(BUSY.get(i)){
				holder.busies.get(i).setVisibility(View.VISIBLE);
			}else{
				holder.busies.get(i).setVisibility(View.GONE);
			}

		}

		if(MARK_WHOLE_WEEK){
			holder.t_week.setTextColor(ContextCompat.getColor(context, R.color.app_secondary_color));
		}else{
			holder.t_week.setTextColor(ContextCompat.getColor(context, R.color.text_list_primary));
		}

	}

	//MONTH NAME ID
	private String getMonth(Resources res, int month){
		switch (month) {
		case Calendar.JANUARY:

			return res.getString(R.string.jan);
		case Calendar.FEBRUARY:

			return res.getString(R.string.feb);
		case Calendar.MARCH:

			return res.getString(R.string.mar);
		case Calendar.APRIL:

			return res.getString(R.string.apr);
		case Calendar.MAY:

			return res.getString(R.string.may);
		case Calendar.JUNE:

			return res.getString(R.string.jun);
		case Calendar.JULY:

			return res.getString(R.string.jul);
		case Calendar.AUGUST:

			return res.getString(R.string.auq);
		case Calendar.SEPTEMBER:

			return res.getString(R.string.sep);
		case Calendar.OCTOBER:

			return res.getString(R.string.oct);
		case Calendar.NOVEMBER:

			return res.getString(R.string.nov);
		case Calendar.DECEMBER:

			return res.getString(R.string.dec);

		default:
			break;
		}

		return "";
	}


	//VIEW HOLDER CLASS
	public static class CalendarRowViewHolder {

		public TextView t_month;
		public TextView t_week;
		public int[] textview_ids = {R.id.t_1, R.id.t_2, R.id.t_3, R.id.t_4, R.id.t_5, R.id.t_6, R.id.t_7};
		public int[] busy_ids = {R.id.busy_1, R.id.busy_2, R.id.busy_3, R.id.busy_4, R.id.busy_5, R.id.busy_6, R.id.busy_7};
		public ArrayList<TextView> text_views = new ArrayList<TextView>();
		public ArrayList<View> busies = new ArrayList<View>();

		public CalendarRowViewHolder(View v) {

			t_week = (TextView)v.findViewById(R.id.t_week);
			t_month = (TextView)v.findViewById(R.id.t_month);

			for(int i = 0; i < 7; i++){
				text_views.add((TextView)v.findViewById(textview_ids[i]));
				busies.add(v.findViewById(busy_ids[i]));
			}
		}
	}


}
