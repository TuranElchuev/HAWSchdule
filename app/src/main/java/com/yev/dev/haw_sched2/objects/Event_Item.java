package com.yev.dev.haw_sched2.objects;

import android.app.Activity;
import android.database.Cursor;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.utils.Const;
import com.yev.dev.haw_sched2.utils.DBHelper;
import com.yev.dev.haw_sched2.utils.MyCalendar;
import com.yev.dev.haw_sched2.utils.Utility;

public class Event_Item {

	public boolean NOT_ENDED = false;
	
	private Utility utility;
	
	public String SUMMARY;
	public String LOCATION;
	public int PRIOORITY;	
	public String START;
	public String START_FORMATTED;
	public String END;
	public String END_FORMATTED;
	public String START_DAY;
	public int WEEKDAY_NAME_ID;
	public int WEEK_NUMBER;


	//diagram bar parameters
	public float startLevel = 0;
	public float length = 0;

	
	public Event_Item(Cursor c, Utility utility) {

		this.utility = utility;
		
		START = c.getString(c.getColumnIndex(DBHelper.COL_START));
		END = c.getString(c.getColumnIndex(DBHelper.COL_END));
		
		MyCalendar calendar = new MyCalendar(Const.DEF_LOCALE, START, Const.DATE_FORMAT_ICS);		
		START_FORMATTED = calendar.getFormattedDate(Const.DATE_FORMAT_TIME_HM);
		START_DAY = calendar.getFormattedDate(Const.DATE_FORMAT_DATE);
		
		WEEKDAY_NAME_ID = utility.getWeekdayNameId(c.getInt(c.getColumnIndex(DBHelper.COL_START_DAYOFWEEK)));
		
		WEEK_NUMBER = c.getInt(c.getColumnIndex(DBHelper.COL_START_WEEKNUMBER));
		
		calendar.changeDate(END, Const.DATE_FORMAT_ICS);		
		if(System.currentTimeMillis() < calendar.calendar.getTimeInMillis()){
			NOT_ENDED = true;
		}		
		END_FORMATTED = calendar.getFormattedDate(Const.DATE_FORMAT_TIME_HM);
		
		SUMMARY = c.getString(c.getColumnIndex(DBHelper.COL_SUMMARY));
		
		LOCATION = c.getString(c.getColumnIndex(DBHelper.COL_LOCATION));
		PRIOORITY = c.getInt(c.getColumnIndex(DBHelper.COL_PRIORITY));

		float[] diagramBarParams = utility.getDiagramBarParameters(START.substring(START.length() - 6, START.length()),
				END.substring(END.length() - 6, END.length()));
		startLevel = diagramBarParams[0];
		length = diagramBarParams[1];

	}

	
	//SET DATA TO VIEW
	public void setDataToView(Activity activity, View v, boolean SUBJECT_MODE, int screenWidth){

		/*
		//WORKS WITH VIEW event.xml
		if(SUBJECT_MODE){
			v.findViewById(R.id.t_summary).setVisibility(View.GONE);
			v.findViewById(R.id.icon_priority).setVisibility(View.GONE);
		}else{
			((TextView)v.findViewById(R.id.t_summary)).setText(SUMMARY);
			((ImageView)v.findViewById(R.id.icon_priority)).setImageResource(utility.getPriorityImage(PRIOORITY));
		}
		
		((TextView)v.findViewById(R.id.t_location)).setText(LOCATION);
		((TextView)v.findViewById(R.id.t_start)).setText(START_FORMATTED);
		((TextView)v.findViewById(R.id.t_end)).setText(END_FORMATTED);
		*/


		//WORKS WITH VIEW list_item_diagram_item.xml
		((TextView)v.findViewById(R.id.t_summary)).setText(SUMMARY);
		((TextView)v.findViewById(R.id.t_location)).setText(LOCATION);
		((TextView)v.findViewById(R.id.t_start)).setText(START_FORMATTED);
		((TextView)v.findViewById(R.id.t_end)).setText(END_FORMATTED);

		View bar = (View)v.findViewById(R.id.diagram_bar);
		bar.setBackgroundColor(utility.getPriorityColor(activity, PRIOORITY));

		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)bar.getLayoutParams();
		lp.setMargins((int)(screenWidth * startLevel), 0, 0, 0);
		lp.width = (int)(screenWidth * length);
		bar.setLayoutParams(lp);
	}

	public boolean overlapsWith(Event_Item item){
		long firstStart = new Long(this.START.replace("T", ""));
		long firstEnd = new Long(this.END.replace("T", ""));
		long secondStart = new Long(item.START.replace("T", ""));
		long secondEnd = new Long(item.END.replace("T", ""));

		boolean res = (firstStart <= secondStart && firstEnd > secondStart) || (secondStart <= firstStart && secondEnd > firstStart);

		return res;

		/*return
				(this.START.compareTo(item.START) == -1 && this.END.compareTo(item.START) != -1)
				||
				(item.START.compareTo(this.START) == -1 && item.END.compareTo(this.START) != -1);*/
	}
	
}
