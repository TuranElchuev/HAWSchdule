package com.yev.dev.haw_sched2.utils;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.objects.DiagramView_Item;
import com.yev.dev.haw_sched2.objects.CalendarRow;
import com.yev.dev.haw_sched2.objects.Calendar_Item;
import com.yev.dev.haw_sched2.objects.Event_Item;
import com.yev.dev.haw_sched2.objects.Subject_Item;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Utility {

	
	//=================================GET DATA====================================

    //GET OVERLAPS
    public ArrayList<Object>getOverlaps(Context context, ArrayList<String> subjects, boolean hideExpired){
        ArrayList<Object> data = new ArrayList<Object>();

        DBHelper dbh = new DBHelper(context);
        SQLiteDatabase db = dbh.getWritableDatabase();

        String condition = "";

        if(subjects == null || subjects.isEmpty()){
            condition = " WHERE " + DBHelper.COL_STATE + " = " + Const.STATE_ENABLED;
        }

        if(hideExpired){
            if(condition.isEmpty()) {
                condition = " WHERE ";
            }else{
                condition += " AND ";
            }
            condition += DBHelper.COL_START + " > \"" + getFormattedTime(System.currentTimeMillis(), Const.DATE_FORMAT_ICS) + "\"";
        }

        String query = "SELECT * FROM "
                + DBHelper.TABLE_NAME_SCHEDULE +

                condition

                + " ORDER BY "
                + DBHelper.COL_START + DBHelper.SORT_ASC;

        Log.d(Const.TAG, query);

        Cursor c = db.rawQuery(query, null);

        ArrayList<Event_Item> allItems = new ArrayList<Event_Item>();

        if(c.moveToFirst()){
            do{
                if(subjects != null){
                    String FILE_URL = c.getString(c.getColumnIndex(DBHelper.COL_FILE_URL));
                    String FILE_NAME = getNameFromUrl(FILE_URL);

                    if(!subjects.contains(FILE_NAME)){
                        continue;
                    }
                }

                allItems.add(new Event_Item(c, this));
            }while(c.moveToNext());
        }

        db.close();


        ArrayList<Event_Item> groupOfOverlappingItems = new ArrayList<Event_Item>();

        int firstIndex = 0;
        int secondIndex = 1;

        while (secondIndex < allItems.size()){

            Event_Item firstItem = allItems.get(firstIndex);
            Event_Item secondItem = allItems.get(secondIndex);

            if(firstItem.overlapsWith(secondItem)){

                if(!groupOfOverlappingItems.contains(firstItem)) {
                    groupOfOverlappingItems.add(firstItem);
                }
                groupOfOverlappingItems.add(secondItem);

            }else if(!groupOfOverlappingItems.isEmpty()){

                boolean overlapsWithAtLeastOneOfTheGroup = false;
                for(Event_Item item: groupOfOverlappingItems){
                    if(item.overlapsWith(secondItem)){
                        overlapsWithAtLeastOneOfTheGroup = true;
                        break;
                    }
                }

                if(overlapsWithAtLeastOneOfTheGroup){
                    groupOfOverlappingItems.add(secondItem);
                }else{
                    data.add(groupOfOverlappingItems);

                    groupOfOverlappingItems = new ArrayList<Event_Item>();
                }

            }

            firstIndex++;
            secondIndex++;

        }

		if(!groupOfOverlappingItems.isEmpty()){
			data.add(groupOfOverlappingItems);
		}

        return data;
    }

	//GET CALENDARS
	public ArrayList<Calendar_Item> getCalendars(Context context){
		ArrayList<Calendar_Item> data = new ArrayList<Calendar_Item>();

		DBHelper dbh = new DBHelper(context);
		SQLiteDatabase db = dbh.getWritableDatabase();

		Cursor c = db.query(
			DBHelper.TABLE_NAME_SCHEDULE,
			null,
		    null,
		    null,
		    null,
		    null,
		    DBHelper.COL_STATE + DBHelper.SORT_ASC + ", " +
		    DBHelper.COL_ADDED + DBHelper.SORT_ASC + ", " +
		    DBHelper.COL_FILE_URL + DBHelper.SORT_ASC
		    );

		ArrayList<String> uniqueURLs = new ArrayList<String>();

		if(c.moveToFirst()){
			do{

				String URL = c.getString(c.getColumnIndex(DBHelper.COL_FILE_URL));
				if(uniqueURLs.contains(URL)){
					continue;
				}

				uniqueURLs.add(URL);

				data.add(new Calendar_Item(this, c));

			}while(c.moveToNext());
		}

		db.close();


		return data;
	}
	
	//GET DAY DATA
	public ArrayList<Object>getDayData(Context context, String DAY_BEGINNING){
		ArrayList<Object> data = new ArrayList<Object>();
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		Cursor c = db.query(
				DBHelper.TABLE_NAME_SCHEDULE,  
				null,
			    DBHelper.COL_START_DAYBEGINNING + " LIKE ? AND " + DBHelper.COL_STATE + " = ?",
			    new String[] {DAY_BEGINNING, String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_START + DBHelper.SORT_ASC
			    );	
		
		if(c.moveToFirst()){
			
			do{
				
				data.add(new Event_Item(c, this));
				
			}while(c.moveToNext());
			
		}
		
		db.close();
		
		return data;
	}
	
	//GET WEEK DATA
	public ArrayList<Object>getWeekData(Context context, String WEEK_BEGINNING){
		ArrayList<Object> data = new ArrayList<Object>();
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		Cursor c = db.query(
				DBHelper.TABLE_NAME_SCHEDULE,  
				null,
				DBHelper.COL_START_WEEKBEGINNING + " = ? AND " + DBHelper.COL_STATE + " = ?",
			    new String[] {WEEK_BEGINNING, String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_START + DBHelper.SORT_ASC
			    );	
		
		ArrayList<Object> day_data;
		String CURRENT_DAY;
		String DAY;
		
		if(c.moveToFirst()){
			
			CURRENT_DAY = c.getString(c.getColumnIndex(DBHelper.COL_START_DAYBEGINNING));
			day_data = new ArrayList<Object>();
			
			do{
				DAY = c.getString(c.getColumnIndex(DBHelper.COL_START_DAYBEGINNING));
				
				if(!DAY.equals(CURRENT_DAY)){
					data.add(day_data);
					day_data = new ArrayList<Object>();
					CURRENT_DAY = DAY;
				}
				
				day_data.add(new Event_Item(c, this));
				
			}while(c.moveToNext());
			
			data.add(day_data);
		}
				
		return data;
	}
		
	//GET WEEKDAY DATA
	public ArrayList<Object>getWeekdayData(Context context, String WEEKDAY){
		ArrayList<Object> data = new ArrayList<Object>();
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		Cursor c = db.query(
				DBHelper.TABLE_NAME_SCHEDULE,  
				null,
			    DBHelper.COL_START_DAYOFWEEK + " = ? AND " + DBHelper.COL_STATE + " = ?",
			    new String[] {WEEKDAY, String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_START + DBHelper.SORT_ASC
			    );	
		
		ArrayList<Object> day_data;
		String CURRENT_DAY;
		String DAY;
		
		if(c.moveToFirst()){
			
			CURRENT_DAY = c.getString(c.getColumnIndex(DBHelper.COL_START_DAYBEGINNING));
			day_data = new ArrayList<Object>();
			
			do{
				DAY = c.getString(c.getColumnIndex(DBHelper.COL_START_DAYBEGINNING));
				
				if(!DAY.equals(CURRENT_DAY)){
					data.add(day_data);
					day_data = new ArrayList<Object>();
					CURRENT_DAY = DAY;
				}
				
				day_data.add(new Event_Item(c, this));
				
			}while(c.moveToNext());
			
			data.add(day_data);
		}
				
		return data;
	}
	
	//GET SUBJECT DATA
	public ArrayList<Object>getSubjectData(Context context, String SUBJECT){
		ArrayList<Object> data = new ArrayList<Object>();
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		Cursor c = db.query(
				DBHelper.TABLE_NAME_SCHEDULE,  
				null,
			    DBHelper.COL_SUMMARY + " LIKE ? AND " + DBHelper.COL_STATE + " = ?",
			    new String[] {SUBJECT, String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_START + DBHelper.SORT_ASC
			    );	
		
		ArrayList<Object> day_data;
		String CURRENT_DAY;
		String DAY;
		
		if(c.moveToFirst()){
			
			CURRENT_DAY = c.getString(c.getColumnIndex(DBHelper.COL_START_DAYBEGINNING));
			day_data = new ArrayList<Object>();
			
			do{
				DAY = c.getString(c.getColumnIndex(DBHelper.COL_START_DAYBEGINNING));
				
				if(!DAY.equals(CURRENT_DAY)){
					data.add(day_data);
					day_data = new ArrayList<Object>();
					CURRENT_DAY = DAY;
				}
				
				day_data.add(new Event_Item(c, this));
				
			}while(c.moveToNext());
			
			data.add(day_data);
		}
				
		return data;
	}


	//==========================FULL SCHEDULE DATA==================
	//GET DIAGRAM VIEW DATA
	public ArrayList<ArrayList<DiagramView_Item>> getDiagramViewData(Context context, ArrayList<String> subjects, boolean hideExpired){
		ArrayList<ArrayList<DiagramView_Item>> data = new ArrayList<ArrayList<DiagramView_Item>>();

		DBHelper dbh = new DBHelper(context);
		SQLiteDatabase db = dbh.getWritableDatabase();

        String condition = "";

        if(hideExpired){
            condition = " WHERE " + DBHelper.COL_START + " > \'" + getFormattedTime(System.currentTimeMillis(), Const.DATE_FORMAT_ICS) + "\'";
        }

		String query = "SELECT * FROM "
				+ DBHelper.TABLE_NAME_SCHEDULE +

                condition +

				" GROUP BY "
				+ DBHelper.COL_SUMMARY + ", "
				+ DBHelper.COL_START_DAYTIME + ", "
				+ DBHelper.COL_END_DAYTIME + ", "
				+ DBHelper.COL_LOCATION + ", "
				+ DBHelper.COL_START_WEEKNUMBER + ", "
				+ DBHelper.COL_START_DAYOFWEEK

				+ " ORDER BY "
				+ DBHelper.COL_START_DAYOFWEEK + DBHelper.SORT_ASC + ", "
				+ DBHelper.COL_START_DAYTIME + DBHelper.SORT_ASC + ", "
				+ DBHelper.COL_END_DAYTIME + DBHelper.SORT_ASC + ", "
				+ DBHelper.COL_SUMMARY + DBHelper.SORT_ASC + ", "
				+ DBHelper.COL_LOCATION + DBHelper.SORT_ASC + ", "

				//for ordering weeks 49, 50, 51, 52 and then 1, 2, 3 of next year
				+ DBHelper.COL_START + DBHelper.SORT_ASC + ", "

				+ DBHelper.COL_START_WEEKNUMBER + DBHelper.SORT_ASC;


		Cursor c = db.rawQuery(query, null);


		ArrayList<DiagramView_Item> beforeCombining = new ArrayList<DiagramView_Item>();

		if(c.moveToFirst()){
			do{

				DiagramView_Item item = new DiagramView_Item(this, c);

				if(subjects == null){

					if(item.STATE != Const.STATE_ENABLED){
						continue;
					}else{
						beforeCombining.add(item);
					}

				}else if(!subjects.contains(item.FILE_NAME)){

					continue;

				}else{

					beforeCombining.add(item);

				}

			}while(c.moveToNext());
		}

		db.close();


		//COMBINING ITEMS IDENICAL ITEMS WITH DIFFERENT WEEK NUMBERS
		ArrayList<DiagramView_Item> afterCombiningWeeknumbers = new ArrayList<DiagramView_Item>();

		DiagramView_Item currentItem = null;

		for(DiagramView_Item item: beforeCombining){

			if(currentItem == null){
				currentItem = item;
				continue;
			}

			if(currentItem.identicalWithDifferentWeekNumber(item)){
				currentItem.groupWith(item);
			}else{
				afterCombiningWeeknumbers.add(currentItem);

				currentItem = item;
			}
		}

		if(currentItem != null && !data.contains(currentItem)){
			afterCombiningWeeknumbers.add(currentItem);
		}

		for(DiagramView_Item item: afterCombiningWeeknumbers){
			item.calculateStartEnd();
		}

		//COMBINING ITEMS WITH IDENTICAL WEEK DAYS
		ArrayList<DiagramView_Item> arraylistForWeekday = new ArrayList<DiagramView_Item>();

		for(DiagramView_Item item: afterCombiningWeeknumbers){

			if(arraylistForWeekday.isEmpty()){

				arraylistForWeekday.add(item);

			}else{

				DiagramView_Item lastItem = arraylistForWeekday.get(arraylistForWeekday.size() - 1);

				if(lastItem.WEEKDAY == item.WEEKDAY){

					arraylistForWeekday.add(item);

				}else{

					data.add(arraylistForWeekday);

					arraylistForWeekday = new ArrayList<DiagramView_Item>();

					arraylistForWeekday.add(item);

				}

			}
		}

		if(!arraylistForWeekday.isEmpty()){
			data.add(arraylistForWeekday);
		}

		return data;
	}


	
	//==========================HELPER DATA========================
	
	//TABLE IS EMPTY
	public boolean tableIsEmpty(Context context, String table){
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
				
		Cursor c = db.query(
			table,  
			new String[] {DBHelper.COL_ID},
		    null,
		    null,
		    null,                                 
		    null,
		    null
		    );		
		
		boolean empty = !c.moveToFirst();
		
		db.close();
		
		return empty;
	}
		
	//--------------------------WHOLE CALENDAR----------------------
	//GET WHOLE CALENDAR
	public ArrayList<Object>getWholeCalendar(Context context, String CURRENT_DATA){
		ArrayList<Object>data = new ArrayList<Object>();
		
		String DATE_START = null;
		String DATE_END = null;
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		//DATE START
		Cursor c = db.query(
				DBHelper.TABLE_NAME_SCHEDULE,  
				new String[] {DBHelper.COL_START_DAYBEGINNING},
				DBHelper.COL_STATE + " = ?",
				new String[] {String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_START_DAYBEGINNING + DBHelper.SORT_ASC
			    );	
		
		if(c.moveToFirst())
			DATE_START = c.getString(c.getColumnIndex(DBHelper.COL_START_DAYBEGINNING));
		
		//DATE END
		c = db.query(
				DBHelper.TABLE_NAME_SCHEDULE,  
				new String[] {DBHelper.COL_START_DAYBEGINNING},
				DBHelper.COL_STATE + " = ?",
				new String[] {String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_START_DAYBEGINNING + DBHelper.SORT_DESC
			    );	
		
		if(c.moveToFirst())
			DATE_END = c.getString(c.getColumnIndex(DBHelper.COL_START_DAYBEGINNING));
		
		MyCalendar cal_start = new MyCalendar(Const.DEF_LOCALE, DATE_START, Const.DATE_FORMAT_ICS);
		MyCalendar cal_end = new MyCalendar(Const.DEF_LOCALE, DATE_END, Const.DATE_FORMAT_ICS);
				
		cal_start.setLowerBoundForCalendar();
		cal_end.setUpperBoundForCalendar();
		
		//BUSY DAYS
		c = db.query(
				true,
				DBHelper.TABLE_NAME_SCHEDULE,  
				new String[] {DBHelper.COL_START_DAYBEGINNING},
				DBHelper.COL_STATE + " = ?",
				new String[] {String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    null,
			    null
			    );
		
		ArrayList<String> busyDays = new ArrayList<String>();
		
		if(c.moveToFirst()){
			do{
				busyDays.add(c.getString(c.getColumnIndex(DBHelper.COL_START_DAYBEGINNING)));
			}while(c.moveToNext());
		}
		
		
		//TODAY AND THIS WEEK
		MyCalendar today_cal = new MyCalendar(Const.DEF_LOCALE, null, null);
		String TODAY = today_cal.get_Beginning_Of_Day_Date_String(Const.DATE_FORMAT_ICS);
		String THIS_WEEK = today_cal.get_Beginning_Of_Week_Date_String(Const.DATE_FORMAT_ICS);
		
		
		//WHOLE CALENDAR
		while(!cal_start.calendar.after(cal_end.calendar)){
			
			CalendarRow row = new CalendarRow();
			
			row.TODAY = TODAY;
			row.THIS_WEEK = THIS_WEEK;
			
			row.weekNumber = cal_start.get_Number_Of_Week();
						
			for(int i = 0; i < 7; i++){
				String DATE = cal_start.getFormattedDate(Const.DATE_FORMAT_ICS);
				
				if(DATE.equals(CURRENT_DATA)){
					row.CONTAINS_CURRENT_DATA = true;
				}
				
				row.DATES.add(DATE);
				row.BUSY.add(busyDays.contains(DATE));
				row.MONTHS.add(cal_start.calendar.get(Calendar.MONTH));
				
				if(cal_start.calendar.get(Calendar.DAY_OF_MONTH) == 15){ 
					row.is_middle = true;
				}
				
				cal_start.calendar.add(Calendar.DAY_OF_MONTH, 1);				
			}
			
			data.add(row);
			
		}
		
		
		db.close();

		return data;
	}
	

	//-------------------------DAY---------------------------------
	//MOST TOPICAL DAY
	public String getMostTopicalDay(Context context){
		
		MyCalendar calendar = new MyCalendar(Const.DEF_LOCALE, null, Const.DATE_FORMAT_ICS);
		
		String CURRENT_TIME = calendar.getFormattedDate(Const.DATE_FORMAT_ICS);
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		Cursor c = db.query(
				true,
				DBHelper.TABLE_NAME_SCHEDULE,  
				new String[] {DBHelper.COL_START_DAYBEGINNING},
			    DBHelper.COL_END + " >= ? AND " + DBHelper.COL_STATE + " = ?",
			    new String[] {CURRENT_TIME, String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_END + DBHelper.SORT_ASC,
			    null
			    );	
		
		String DATA;
		
		if(c.moveToFirst()){
			DATA = c.getString(c.getColumnIndex(DBHelper.COL_START_DAYBEGINNING));
		}else{
			DATA = Const.DATA_DEFAULT;
		}
		
		db.close();
		
		return DATA;
		
	}
	
	//GET NEXT DAY
	public String getNextDay(Context context, String CURRENT_DAY){
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		Cursor c = db.query(
				true,
				DBHelper.TABLE_NAME_SCHEDULE,  
				new String[] {DBHelper.COL_START_DAYBEGINNING},
			    DBHelper.COL_START_DAYBEGINNING + " > ? AND " + DBHelper.COL_STATE + " = ?",
			    new String[] {CURRENT_DAY, String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_START_DAYBEGINNING + DBHelper.SORT_ASC,
			    null
			    );	
		
		String DATA;
		
		if(c.moveToNext()){
			DATA = c.getString(c.getColumnIndex(DBHelper.COL_START_DAYBEGINNING));
		}else{
			DATA = CURRENT_DAY;
		}
		
		db.close();
		
		return DATA;
		
	}
	
	//GET PREVIOUS DAY
	public String getPreviousDay(Context context, String CURRENT_DAY){
		
		String SELECTION_ARG = CURRENT_DAY;
		String SELECTION = DBHelper.COL_START_DAYBEGINNING + " < ? AND " + DBHelper.COL_STATE + " = ?";
		
		if(CURRENT_DAY.equals(Const.DATA_DEFAULT)){
			
			MyCalendar calendar = new MyCalendar(Const.DEF_LOCALE, null, null);
			SELECTION_ARG = calendar.get_Beginning_Of_Day_Date_String(Const.DATE_FORMAT_ICS);
			SELECTION = DBHelper.COL_START_DAYBEGINNING + " <= ? AND " + DBHelper.COL_STATE + " = ?";
			
		}
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		Cursor c = db.query(
				true,
				DBHelper.TABLE_NAME_SCHEDULE,  
				new String[] {DBHelper.COL_START_DAYBEGINNING},
			    SELECTION,
			    new String[] {SELECTION_ARG, String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_START_DAYBEGINNING + DBHelper.SORT_DESC,
			    null
			    );	
		
		String DATA;
		
		if(c.moveToNext()){
			DATA = c.getString(c.getColumnIndex(DBHelper.COL_START_DAYBEGINNING));
		}else{
			DATA = CURRENT_DAY;
		}
		
		db.close();
		
		return DATA;
		
	}
		
	//-------------------------WEEK---------------------------------
	//MOST TOPICAL WEEK
	public String getMostTopicalWeek(Context context){
		MyCalendar calendar = new MyCalendar(Const.DEF_LOCALE, null, Const.DATE_FORMAT_ICS);
		
		String CURRENT_TIME = calendar.getFormattedDate(Const.DATE_FORMAT_ICS);
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		Cursor c = db.query(
				true,
				DBHelper.TABLE_NAME_SCHEDULE,  
				new String[] {DBHelper.COL_START_WEEKBEGINNING},
			    DBHelper.COL_END + " >= ? AND " + DBHelper.COL_STATE + " = ?",
			    new String[] {CURRENT_TIME, String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_END + DBHelper.SORT_ASC,
			    null
			    );	
		
		String DATA;
		
		if(c.moveToNext()){
			DATA = c.getString(c.getColumnIndex(DBHelper.COL_START_WEEKBEGINNING));
		}else{
			DATA = Const.DATA_DEFAULT;
		}
				
		db.close();
		
		return DATA;
	}
	
	//GET NEXT WEEK
	public String getNextWeek(Context context, String CURRENT_WEEK){
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		Cursor c = db.query(
				true,
				DBHelper.TABLE_NAME_SCHEDULE,  
				new String[] {DBHelper.COL_START_WEEKBEGINNING},
			    DBHelper.COL_START_WEEKBEGINNING + " > ? AND " + DBHelper.COL_STATE + " = ?",
			    new String[] {CURRENT_WEEK, String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_START_WEEKBEGINNING + DBHelper.SORT_ASC,
			    null
			    );	
		
		String DATA;
		
		if(c.moveToNext()){
			DATA = c.getString(c.getColumnIndex(DBHelper.COL_START_WEEKBEGINNING));
		}else{
			DATA = CURRENT_WEEK;
		}
		
		db.close();
		
		return DATA;
	}
	
	//GET PREVIOUS WEEK
	public String getPreviousWeek(Context context, String CURRENT_WEEK){
		String SELECTION_ARG = CURRENT_WEEK;
		String SELECTION = DBHelper.COL_START_WEEKBEGINNING + " < ? AND " + DBHelper.COL_STATE + " = ?";
		
		if(CURRENT_WEEK.equals(Const.DATA_DEFAULT)){
			
			MyCalendar calendar = new MyCalendar(Const.DEF_LOCALE, null, null);
			SELECTION_ARG = calendar.get_Beginning_Of_Week_Date_String(Const.DATE_FORMAT_ICS);
			SELECTION = DBHelper.COL_START_WEEKBEGINNING + " <= ? AND " + DBHelper.COL_STATE + " = ?";
			
		}
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		Cursor c = db.query(
				true,
				DBHelper.TABLE_NAME_SCHEDULE,  
				new String[] {DBHelper.COL_START_WEEKBEGINNING},
			    SELECTION,
			    new String[] {SELECTION_ARG, String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_START_WEEKBEGINNING + DBHelper.SORT_DESC,
			    null
			    );	
		
		String DATA;
		
		if(c.moveToNext()){
			DATA = c.getString(c.getColumnIndex(DBHelper.COL_START_WEEKBEGINNING));
		}else{
			DATA = CURRENT_WEEK;
		}
		
		db.close();
		
		return DATA;
	}
	
	//------------------------WEEKDAY-------------------------------
	//GET WEEKDAY NAME STRING ID
	public int getWeekdayNameId(int weekday){
		switch (weekday) {
		case Calendar.MONDAY:
			return R.string.monday;
		case Calendar.TUESDAY:
			return R.string.tuesday;
		case Calendar.WEDNESDAY:
			return R.string.wednesday;
		case Calendar.THURSDAY:
			return R.string.thursday;
		case Calendar.FRIDAY:
			return R.string.friday;
		case Calendar.SATURDAY:
			return R.string.saturday;
		case Calendar.SUNDAY:
			return R.string.sunday;

		default:
			break;
		}
		
		return 0;
	}
	
	//GET ALL WEEKDAYS
	public ArrayList<Object> getAllWeekdays(){
		ArrayList<Object>data = new ArrayList<Object>();
		
		data.add(Calendar.MONDAY);
		data.add(Calendar.TUESDAY);
		data.add(Calendar.WEDNESDAY);
		data.add(Calendar.THURSDAY);
		data.add(Calendar.FRIDAY);
		data.add(Calendar.SATURDAY);
		data.add(Calendar.SUNDAY);
		
		return data;
	}
	
	//-------------------------SUBJECT---------------------------------
	//GET NEXT SUBJECT
	public String getNextSubject(Context context, String CURRENT_SUBJECT){
		
		String DATA = CURRENT_SUBJECT;
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		Cursor c = db.query(
				true,
				DBHelper.TABLE_NAME_SCHEDULE,  
				new String[] {DBHelper.COL_SUMMARY},
			    DBHelper.COL_SUMMARY + " > ? AND " + DBHelper.COL_STATE + " = ?",
			    new String[] {CURRENT_SUBJECT, String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_SUMMARY + DBHelper.SORT_ASC,
			    null
			    );	
		
		if(c.moveToFirst()){
			DATA = c.getString(c.getColumnIndex(DBHelper.COL_SUMMARY));
		}
		
		db.close();
		
		return DATA;
	}
	
	//GET PREVIOUS SUBJECT
	public String getPreviousSubject(Context context, String CURRENT_SUBJECT){
		String DATA = CURRENT_SUBJECT;
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		Cursor c = db.query(
				true,
				DBHelper.TABLE_NAME_SCHEDULE,  
				new String[] {DBHelper.COL_SUMMARY},
			    DBHelper.COL_SUMMARY + " < ? AND " + DBHelper.COL_STATE + " = ?",
			    new String[] {CURRENT_SUBJECT, String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_SUMMARY + DBHelper.SORT_DESC,
			    null
			    );	
		
		if(c.moveToFirst()){
			DATA = c.getString(c.getColumnIndex(DBHelper.COL_SUMMARY));
		}
		
		db.close();
		
		return DATA;
	}
	
	//GET SUBJECT PRIORITY
	public int getSubjectPriority(Context context, String SUBJECT){
		
		int priority = Const.PRIORITY_MEDIUM;
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		Cursor c = db.query(
				DBHelper.TABLE_NAME_SCHEDULE,  
				new String[] {DBHelper.COL_PRIORITY},
			    DBHelper.COL_SUMMARY + " LIKE ?",
			    new String[] {SUBJECT},
			    null,                                 
			    null,
			    null
			    );	
		
		if(c.moveToFirst()){
			priority = c.getInt(c.getColumnIndex(DBHelper.COL_PRIORITY));
		}
		
		db.close();
		
		return getPriorityImage(priority);
	}
	
	//GET ALL SUBECTS
	public ArrayList<Object> getAllSubjects(Context context){
		ArrayList<Object>data = new ArrayList<Object>();
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		Cursor c = db.query(
				true,
				DBHelper.TABLE_NAME_SCHEDULE,  
				new String[] {DBHelper.COL_SUMMARY,
								DBHelper.COL_PRIORITY},
				DBHelper.COL_STATE + " = ?",
			    new String[] {String.valueOf(Const.STATE_ENABLED)},
			    null,                                 
			    null,
			    DBHelper.COL_SUMMARY + DBHelper.SORT_ASC,
			    null
			    );	
		
		if(c.moveToFirst()){
			do{
				data.add(new Subject_Item(c, this));
			}while(c.moveToNext());
		}
		
		db.close();
		
		return data;
	}
	
	
	
	//==============================UPDATE DATA=======================================
	public void updateData(Context context,
								String table,
								ContentValues values,
								String selection,
								String[] selectionArgs){
		
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();

		db.update(table, values, selection, selectionArgs);
		
		db.close();
		
	}
	
	
	//===============================DELETE DATA=================================
	public void deleteData(Context context,
			String table,
			String selection,
			String[] selectionArgs){

		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		db.delete(table, selection, selectionArgs);
		
		db.close();
		
	}
	
	public void deleteAll(Context context, String table){
		DBHelper dbh = new DBHelper(context);		
		SQLiteDatabase db = dbh.getWritableDatabase();
		
		db.delete(table, null, null);
		
		db.close();
	}
	
	
	//=============================UTILS====================================	
	//GET FORMATTED DATE
	public String getFormattedTime(long time_millis, String format){
		
		if(time_millis == 0){
			return "-";
		}
		
		Date date = new Date(time_millis);
		
    	SimpleDateFormat sdf = new SimpleDateFormat(format);
    	
    	return sdf.format(date);
	}
	
	
	//GET NAME FROM URL
	public String getNameFromUrl(String url){
		String name = url;
		int index_of_slash;
		
		while(true){
			index_of_slash = name.indexOf('/');
			if(index_of_slash == -1
					|| index_of_slash == name.length() - 5){
				break;
			}else{
				name = name.substring(index_of_slash + 1, name.length());
			}
		}
		
		if(name.length() > 4){
			name = name.substring(0, name.length() - 4);
		}
		
		return name;
		
	}

	//SHOW ALERT DIALOG
	public void showWarningDialog(Context context, String header, String message){

		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		alertDialog.setTitle(header);
		alertDialog.setMessage(message);
		alertDialog.setIcon(R.drawable.ic_warning);

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();

			}
		});

		alertDialog.show();

	}
	
	//GET PRIORITY IMAGE
	public int getPriorityImage(int priority){
		switch (priority) {
		case Const.PRIORITY_LOW:
			return R.drawable.ic_low;
		case Const.PRIORITY_MEDIUM:
			return R.drawable.ic_medium;
		case Const.PRIORITY_HIGH:
			return R.drawable.ic_high;

		default:
			return R.drawable.ic_medium;
		}
	}

	//GET PRIORITY COLOR
	public int getPriorityColor(Context context, int priority){
		switch (priority){
			case Const.PRIORITY_HIGH:
				return ContextCompat.getColor(context, R.color.priority_high);
			case Const.PRIORITY_MEDIUM:
				return ContextCompat.getColor(context, R.color.priority_medium);
			case Const.PRIORITY_LOW:
				return ContextCompat.getColor(context, R.color.priority_low);
			default:
				return ContextCompat.getColor(context, R.color.priority_medium);
		}

	}
	
	//GET DAYS LEFT STRING
	public String getDaysLeftString(Context context, int days_left){
		
		String result = "";
		
		Locale locale = context.getResources().getConfiguration().locale;
		
		switch (locale.getLanguage()) {
		case "de":
			
			if(days_left < -1){
				result = context.getString(R.string.ago) + " "
						+ String.valueOf(-days_left) + " "
						+ context.getString(R.string.days);
			}else if(days_left == -1){
				result = context.getString(R.string.yesterday);
			}else if(days_left == 0){
				result = context.getString(R.string.today);
			}else if(days_left == 1){
				result = context.getString(R.string.tomorrow);
			}else{
				result = context.getString(R.string.in) + " " + days_left + " " + context.getString(R.string.days);
			}
						
			break;

		default:
			
			if(days_left < -1){
				result = -days_left + " " + context.getString(R.string.days)
						+ " " + context.getString(R.string.ago);
			}else if(days_left == -1){
				result = context.getString(R.string.yesterday);
			}else if(days_left == 0){
				result = context.getString(R.string.today);
			}else if(days_left == 1){
				result = context.getString(R.string.tomorrow);
			}else{
				result = context.getString(R.string.in) + " " + days_left + " " + context.getString(R.string.days);
			}
			
			break;
		}
		
		return result;
	}

	//GET WEEKS LEFT STRING
	public String getWeeksLeftString(Context context, int weeks_left, int number_of_week){

		String result = "";
		
		Locale locale = context.getResources().getConfiguration().locale;
		
		switch (locale.getLanguage()) {
		case "de":
			
			if(weeks_left < -1){
				result = context.getString(R.string.ago) + " "
						+ String.valueOf(-weeks_left) + " "
						+ context.getString(R.string.weeks);
			}else if(weeks_left == -1){
				result = context.getString(R.string.last_week);
			}else if(weeks_left == 0){
				result = context.getString(R.string.this_week);
			}else if(weeks_left == 1){
				result = context.getString(R.string.next_week);
			}else{
				result = context.getString(R.string.in) + " " + weeks_left + " " + context.getString(R.string.weeks);
			}
			
			result = result + " (" + context.getString(R.string.week_no)
					+ " " + number_of_week + ")";
			
			break;

		default:
			
			if(weeks_left < -1){
				result = -weeks_left + " " + context.getString(R.string.weeks)
						+ " " + context.getString(R.string.ago);
			}else if(weeks_left == -1){
				result = context.getString(R.string.last_week);
			}else if(weeks_left == 0){
				result = context.getString(R.string.this_week);
			}else if(weeks_left == 1){
				result = context.getString(R.string.next_week);
			}else{
				result = context.getString(R.string.in) + " " + weeks_left + " " + context.getString(R.string.weeks);
			}
			
			result = result + " (" + context.getString(R.string.week_no)
					+ " " + number_of_week + ")";
			
			break;
		}
		
		
		return result;
	}

	/*
	CALCULATE START POSITION AND LENGTH OF DIAGRAM BAR

	START_DAYTIME and END_DAYTIME should have format hhmmss
	 */
	public float[] getDiagramBarParameters(String START_DAYTIME, String END_DAYTIME){
		/*
		first element is start position
		second element is length
		 */

		float[] params = new float[2];

		float lowestTime = 8 * 60 * 60; //08:00
		float highestTime = 20 * 60 * 60; //20:00

		float range = highestTime - lowestTime;

		int start_h = Integer.valueOf(START_DAYTIME.substring(0, 2));
		int start_m = Integer.valueOf(START_DAYTIME.substring(2, 4));
		int start_s = Integer.valueOf(START_DAYTIME.substring(4, 6));

		int end_h = Integer.valueOf(END_DAYTIME.substring(0, 2));
		int end_m = Integer.valueOf(END_DAYTIME.substring(2, 4));
		int end_s = Integer.valueOf(END_DAYTIME.substring(4, 6));

		float startLevel = (start_h * 3600 + start_m * 60 + start_s - lowestTime) / range;
		float endLevel = (end_h * 3600 + end_m * 60 + end_s - lowestTime) / range;

		if(startLevel < 0){
			startLevel = 0;
		}
		if(startLevel > 1){
			startLevel = 1;
		}


		if(endLevel < 0){
			endLevel = 0;
		}
		if(endLevel > 1){
			endLevel = 1;
		}

		float length = endLevel - startLevel;

		params[0] = startLevel;
		params[1] = length;

		return params;
	}
	
	
	//OPEN APP PAGE
	public void openAppPage(Context context){
		final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
		try {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
		}
	}

	//SHARE APP
	public void shareApp(Context context){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text) + Const.APP_LINK);
		intent.setType("text/plain");
		context.startActivity(Intent.createChooser(intent, ""));
	}

}