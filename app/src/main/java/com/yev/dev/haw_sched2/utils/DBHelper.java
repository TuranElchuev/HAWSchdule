package com.yev.dev.haw_sched2.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static final String TEXT_TYPE = " TEXT";
	public static final String INTEGER_TYPE = " INTEGER";
	public static final String COMMA_SEP = ",";
	
	public static final String SORT_DESC = " DESC";
	public static final String SORT_ASC = " ASC";
	
	public static final String TABLE_NAME_SCHEDULE = "table_schedule";
	
	public static final String COL_NULL = "col_null";
	
    public static final String COL_ID = "id";
        

    public static String COL_UID = "col_uid";
    public static String COL_SUMMARY = "col_summary";
    public static String COL_LOCATION = "col_location";
    public static String COL_FILE_URL = "col_file_url";
    
    public static String COL_START_DAYTIME = "col_text_1";
    public static String COL_END_DAYTIME = "col_text_2";
    public static String COL_TEXT_3 = "col_text_3";
    public static String COL_TEXT_4 = "col_text_4";
    public static String COL_TEXT_5 = "col_text_5";

    public static String COL_FILE_LAST_MODIFIED = "col_file_last_modified";
    public static String COL_START = "col_start";
    public static String COL_START_DAYBEGINNING = "col_start_daybeginning";
    public static String COL_START_WEEKBEGINNING = "col_start_weekbeginning";
    public static String COL_START_WEEKENDING = "col_start_weekending";
    public static String COL_START_WEEKNUMBER = "col_start_weeknumber";
    public static String COL_START_DAYOFWEEK = "col_start_dayofweek";
    public static String COL_END = "col_end";
    
    public static String COL_PRIORITY = "col_priority";
    public static String COL_STATE = "col_state";
    public static String COL_ADDED = "col_added";
    
    public static String COL_INT_1 = "col_int_1";
    public static String COL_INT_2 = "col_int_2";
    public static String COL_INT_3 = "col_int_3";
    public static String COL_INT_4 = "col_int_4";
    public static String COL_INT_5 = "col_int_5";
    

      
    	
	private static final String SQL_CREATE_ENTRIES =
	    "CREATE TABLE " + TABLE_NAME_SCHEDULE + " (" +
	    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
	    COL_FILE_URL + TEXT_TYPE + COMMA_SEP +
	    COL_UID + TEXT_TYPE + COMMA_SEP +
	    COL_SUMMARY + TEXT_TYPE + COMMA_SEP +
	    COL_LOCATION + TEXT_TYPE + COMMA_SEP +
	    COL_START + TEXT_TYPE + COMMA_SEP +
	    COL_END + TEXT_TYPE + COMMA_SEP +
	    COL_START_DAYBEGINNING + TEXT_TYPE + COMMA_SEP +
	    COL_START_WEEKBEGINNING + TEXT_TYPE + COMMA_SEP +
	    COL_START_WEEKENDING + TEXT_TYPE + COMMA_SEP +

        COL_START_DAYTIME + TEXT_TYPE + COMMA_SEP +
        COL_END_DAYTIME + TEXT_TYPE + COMMA_SEP +
	    COL_TEXT_3 + TEXT_TYPE + COMMA_SEP +
	    COL_TEXT_4 + TEXT_TYPE + COMMA_SEP +
	    COL_TEXT_5 + TEXT_TYPE + COMMA_SEP +	    
	    
	    COL_FILE_LAST_MODIFIED + INTEGER_TYPE + COMMA_SEP +
	    COL_START_WEEKNUMBER + INTEGER_TYPE + COMMA_SEP +
	    COL_START_DAYOFWEEK + INTEGER_TYPE + COMMA_SEP +

	    COL_INT_1 + INTEGER_TYPE + COMMA_SEP +
	    COL_INT_2 + INTEGER_TYPE + COMMA_SEP +
	    COL_INT_3 + INTEGER_TYPE + COMMA_SEP +
	    COL_INT_4 + INTEGER_TYPE + COMMA_SEP +
	    COL_INT_5 + INTEGER_TYPE + COMMA_SEP +
	    
	    COL_ADDED + INTEGER_TYPE + COMMA_SEP +
	    COL_PRIORITY + INTEGER_TYPE + COMMA_SEP +
	    COL_STATE + INTEGER_TYPE + 
	    " )";

	private static final String SQL_DELETE_ENTRIES =
	    "DROP TABLE IF EXISTS " + TABLE_NAME_SCHEDULE;
	
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "com.yev.dev.haw_sched2.database";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
