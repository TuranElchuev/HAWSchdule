package com.yev.dev.haw_sched2.utils;

import java.util.Locale;

public class Const {

	public static final String APP_LINK = "https://play.google.com/store/apps/details?id=com.yev.dev.haw_sched2";
	
	public static final String URL = "https://www.haw-hamburg.de/hochschule/technik-und-informatik/departments/informations-und-elektrotechnik/studium/studienorganisation/studienplaene/";
	
	public static final String TEMP_FILE = "/haw_temp.ics";
	
	public static final Locale DEF_LOCALE = Locale.GERMANY;
	
	public static final String TAG = "HAW";
	
	public static final String DATE_FORMAT_ICS = "yyyyMMdd'T'HHmmss";
	public static final String DATE_FORMAT_DATETIME = "MMM dd, yyyy HH:mm";
	public static final String DATE_FORMAT_WEEKS = "dd.MM";
	public static final String DATE_FORMAT_TIME_HM = "HH:mm";
	public static final String DATE_FORMAT_DAY = "EEE, MMM dd, yyyy";
	public static final String DATE_FORMAT_DATE = "dd.MM.yyyy";
	
	
	public static final int MES_BACK_PRESSED = 0;
	
	
	public static final int PRIORITY_LOW = 1;
	public static final int PRIORITY_MEDIUM = 2;
	public static final int PRIORITY_HIGH = 3;
	
	public static final int STATE_ENABLED = 1;
	public static final int STATE_DISABLED = 2;	
	
	public static final int MODE_DEFAULT = 0;
	public static final int MODE_DAY = 1;
	public static final int MODE_WEEK = 2;
	public static final int MODE_WEEKDAY = 3;
	public static final int MODE_SUBJECT = 4;
	
	
	public static final String DATA_DEFAULT = "def";
	
	
	//CALENDAR KEYS
	public static final String CAL_KEY_BEGIN = "BEGIN:VEVENT";
	public static final String CAL_KEY_END = "END:VEVENT";
	public static final String CAL_KEY_SUMMARY = "SUMMARY:";
	public static final String CAL_KEY_LOCATION = "LOCATION:";
	public static final String CAL_KEY_UID = "UID:";
	public static final String CAL_KEY_DATE_START = "DTSTART";
	public static final String CAL_KEY_DATE_END = "DTEND";
	
	
	public static final float ALPHA_ENABLED = 1f;
	public static final float ALPHA_DISABLED = 0.6f;


	//INTENT REQUEST CODES
	public static final int INTENT_SHOW_FULL_SCHEDULE = 1000;


	//SHARED PREFERENCES KEYS
	public static final String SPREF_CAN_USE_DIAGRAM = "can_use_diagram_view";

}


