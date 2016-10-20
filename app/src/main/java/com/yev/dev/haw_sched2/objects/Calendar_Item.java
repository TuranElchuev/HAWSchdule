package com.yev.dev.haw_sched2.objects;

import android.database.Cursor;

import com.yev.dev.haw_sched2.utils.Const;
import com.yev.dev.haw_sched2.utils.DBHelper;
import com.yev.dev.haw_sched2.utils.Utility;

public class Calendar_Item {

	public String FILE_NAME;
	public String FILE_URL;
	public String SUMMARY;
	public String ADDED_STRING;
	public long ADDED_TO_DB_MILLIS;
	public int PRIOORITY;
	public int STATE;
	public long FILE_LAST_MODIFIED_MILLIS;
	public String FILE_LAST_MODIFIED_STRING;
	public boolean SHOW_IN_DIAGRAM;
	
	public Calendar_Item(Utility utility, Cursor c) {
		
		FILE_URL = c.getString(c.getColumnIndex(DBHelper.COL_FILE_URL));
		FILE_NAME = utility.getNameFromUrl(FILE_URL);
		
		FILE_LAST_MODIFIED_MILLIS = c.getLong(c.getColumnIndex(DBHelper.COL_FILE_LAST_MODIFIED));
		FILE_LAST_MODIFIED_STRING = utility.getFormattedTime(FILE_LAST_MODIFIED_MILLIS, Const.DATE_FORMAT_DATETIME);
			
		PRIOORITY = c.getInt(c.getColumnIndex(DBHelper.COL_PRIORITY));
		STATE = c.getInt(c.getColumnIndex(DBHelper.COL_STATE));

		SHOW_IN_DIAGRAM = (STATE == Const.STATE_ENABLED);
		
		ADDED_TO_DB_MILLIS = c.getLong(c.getColumnIndex(DBHelper.COL_ADDED));		
		ADDED_STRING = utility.getFormattedTime(ADDED_TO_DB_MILLIS, Const.DATE_FORMAT_DATETIME);		
	}	
	
}
