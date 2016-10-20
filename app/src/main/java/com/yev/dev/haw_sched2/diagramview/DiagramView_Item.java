package com.yev.dev.haw_sched2.diagramview;

import android.database.Cursor;

import com.yev.dev.haw_sched2.utils.DBHelper;
import com.yev.dev.haw_sched2.utils.Utility;

public class DiagramView_Item {

	private Utility utility;

	public String FILE_NAME;
	public String SUMMARY;
	public String LOCATION;

	public int PRIOORITY;
	public int STATE;

	public String START_DAYTIME;
	public String END_DAYTIME;

	public String START_FORMATTED;
	public String END_FORMATTED;

	public int WEEKDAY;
	public int WEEKNUMBER;


	//----------------------------
	public String ALLWEEKS = "";

	public float startLevel = 0;
	public float length = 0;

	public DiagramView_Item(Utility utility, Cursor c) {

		this.utility = utility;
		
		String FILE_URL = c.getString(c.getColumnIndex(DBHelper.COL_FILE_URL));
		FILE_NAME = utility.getNameFromUrl(FILE_URL);

		SUMMARY = c.getString(c.getColumnIndex(DBHelper.COL_SUMMARY));
		LOCATION = c.getString(c.getColumnIndex(DBHelper.COL_LOCATION));

		PRIOORITY = c.getInt(c.getColumnIndex(DBHelper.COL_PRIORITY));
		STATE = c.getInt(c.getColumnIndex(DBHelper.COL_STATE));

		START_DAYTIME = c.getString(c.getColumnIndex(DBHelper.COL_START_DAYTIME));
		END_DAYTIME = c.getString(c.getColumnIndex(DBHelper.COL_END_DAYTIME));

		WEEKDAY = c.getInt(c.getColumnIndex(DBHelper.COL_START_DAYOFWEEK));
		WEEKNUMBER = c.getInt(c.getColumnIndex(DBHelper.COL_START_WEEKNUMBER));

		ALLWEEKS += WEEKNUMBER;

	}

	public boolean identicalWithDifferentWeekNumber(DiagramView_Item item){

		return SUMMARY.equals(item.SUMMARY)
				&& LOCATION.equals(item.LOCATION)
				&& PRIOORITY == item.PRIOORITY
				&& STATE == item.STATE
				&& START_DAYTIME.equals(item.START_DAYTIME)
				&& END_DAYTIME.equals(item.END_DAYTIME)
				&& WEEKDAY == item.WEEKDAY;

	}

	public void groupWith(DiagramView_Item item){
		ALLWEEKS += ", " + item.ALLWEEKS;
	}


	public void calculateStartEnd(){

		START_FORMATTED = START_DAYTIME.substring(0, 2) + ":" + START_DAYTIME.substring(2, 4);
		END_FORMATTED = END_DAYTIME.substring(0, 2) + ":" + END_DAYTIME.substring(2, 4);

		float[] diagramBarParams = utility.getDiagramBarParameters(START_DAYTIME, END_DAYTIME);
		startLevel = diagramBarParams[0];
		length = diagramBarParams[1];
	}

}
