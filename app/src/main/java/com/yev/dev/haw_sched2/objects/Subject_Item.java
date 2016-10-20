package com.yev.dev.haw_sched2.objects;

import android.database.Cursor;

import com.yev.dev.haw_sched2.utils.DBHelper;
import com.yev.dev.haw_sched2.utils.Utility;

public class Subject_Item {

	public String NAME;
	public int PRIORITY_IMAGE;
	
	public Subject_Item(Cursor c, Utility utility) {
		NAME = c.getString(c.getColumnIndex(DBHelper.COL_SUMMARY));
		PRIORITY_IMAGE = utility.getPriorityImage(c.getInt(c.getColumnIndex(DBHelper.COL_PRIORITY)));
	}
	
}
