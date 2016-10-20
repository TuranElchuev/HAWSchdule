package com.yev.dev.haw_sched2.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyCalendar {

	private Locale locale;
	
	public Calendar calendar;
	
	public MyCalendar(Locale locale, String date_str, String input_format) {
		
		this.locale = locale;
		
		calendar = Calendar.getInstance(locale);
		
		if(date_str != null){
			try{
				
				DateFormat df = new SimpleDateFormat(input_format, locale);
				Date date = df.parse(date_str);
				calendar.setTime(date);
				
			}catch(java.text.ParseException e){}
		}
		
	}
	
	//CHANGE DATE
	public void changeDate(String date_str, String input_format){
		try{
			
			DateFormat df = new SimpleDateFormat(input_format, locale);
			Date date = df.parse(date_str);
			calendar.setTime(date);
			
		}catch(java.text.ParseException e){}
	}
	
	//GET BEGINNING OF THE DAY
	public String get_Beginning_Of_Day_Date_String(String format){
		
		DateFormat df = new SimpleDateFormat(format, locale);
		
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
				
		return df.format(calendar.getTime());
	}
	
	//GET CURRENT TIME FORMATTED
	public String getFormattedDate(String format){
		DateFormat df = new SimpleDateFormat(format, locale);
		return df.format(calendar.getTime());
	}
	
	//GET BEGINNING OF THE WEEK
	public String get_Beginning_Of_Week_Date_String(String format){
		
		DateFormat df = new SimpleDateFormat(format, locale);
		
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				
		return df.format(calendar.getTime());
	}
	
	//GET END OF THE WEEK
	public String get_End_Of_Week_Date_String(String format){
		
		DateFormat df = new SimpleDateFormat(format, locale);
		
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				
		return df.format(calendar.getTime());
	}
	
	//GET BEGINNING OF THE MONTH
	public String get_Beginning_Of_Month_Date_String(String format){
		
		DateFormat df = new SimpleDateFormat(format, locale);
		
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
						
		return df.format(calendar.getTime());
	}
	
	//GET END OF THE MONTH
	public String get_End_Of_Month_Date_String(String format){
		
		DateFormat df = new SimpleDateFormat(format, locale);
		
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
						
		return df.format(calendar.getTime());
	}

	
	//SET LOWER BOUND FOR CALENDAR
	public void setLowerBoundForCalendar(){
		
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		calendar.setTime(calendar.getTime());
		
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		
	}
	
	//SET UPPER BOUND FOR CALENDAR
	public void setUpperBoundForCalendar(){
		
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		
		calendar.setTime(calendar.getTime());
		
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
						
	}
	
	
	
	//GET DAY OF THE WEEK
	public int get_Day_Of_Week(){
		
		return calendar.get(Calendar.DAY_OF_WEEK);
	}
	
	//GET NUMBER OF THE WEEK
	public int get_Number_Of_Week(){

		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	//GET DAYS LEFT
	public int getDaysLeft(){
		
		int difference = 0;
		
		Calendar cal_now = Calendar.getInstance(locale);
		cal_now.set(Calendar.HOUR_OF_DAY, 0);
		cal_now.clear(Calendar.MINUTE);
		cal_now.clear(Calendar.SECOND);
		cal_now.clear(Calendar.MILLISECOND);
		
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
	    
		if(calendar.before(cal_now)){
			while(calendar.before(cal_now)){
				cal_now.add(Calendar.DAY_OF_MONTH, -1);
				difference--;
			}
		}else{
			while(cal_now.before(calendar)){
				cal_now.add(Calendar.DAY_OF_MONTH, 1);
				difference++;
			}
		}
				
		return difference;
	}
	
	//GET WEEKS LEFT
	public int getWeeksLeft(){
		
		int difference = 0;
		
		Calendar cal_now = Calendar.getInstance(locale);
		cal_now.set(Calendar.HOUR_OF_DAY, 0);
		cal_now.clear(Calendar.MINUTE);
		cal_now.clear(Calendar.SECOND);
		cal_now.clear(Calendar.MILLISECOND);
		cal_now.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		
		if(calendar.before(cal_now)){
			while(calendar.before(cal_now)){
				cal_now.add(Calendar.DAY_OF_MONTH, -7);
				difference--;
			}
		}else{
			while(cal_now.before(calendar)){
				cal_now.add(Calendar.DAY_OF_MONTH, 7);
				difference++;
			}
		}
				
		return difference;
	}
	
	
}
