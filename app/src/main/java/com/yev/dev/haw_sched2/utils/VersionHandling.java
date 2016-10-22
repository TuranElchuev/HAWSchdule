package com.yev.dev.haw_sched2.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yev.dev.haw_sched2.R;

/**
 * Created by turanelchuev on 03/10/2016.
 */

public class VersionHandling {

    //=============Version 1.3===========================

    /*
     in version 1.3 in database were involved 2 new columns:
     START_DAYIME and END_DAYTIME
     They are required for grouping data for Diagram view
      */

    /*
        Check if user can open Diagram view

        if there is at least 1 row with empty START_DAYTIME, schedule should be updated
     */
    public static boolean canUseDiagramView(Activity activity){

        SharedPreferences sPref = activity.getPreferences(Activity.MODE_PRIVATE);
        boolean canUseDiagramView = sPref.getBoolean(Const.SPREF_CAN_USE_DIAGRAM, false);

        if(canUseDiagramView){
            return true;
        }

        DBHelper dbh = new DBHelper(activity);
        SQLiteDatabase db = dbh.getWritableDatabase();

        String query = "SELECT * FROM " + DBHelper.TABLE_NAME_SCHEDULE + " WHERE " + DBHelper.COL_START_DAYTIME + " IS NULL OR " + DBHelper.COL_START_DAYTIME + " = ''";

        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()){

            db.close();

            final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

            alertDialog.setMessage(activity.getString(R.string.should_update_schedule));

            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();

            return false;

        }else{

            db.close();

            SharedPreferences.Editor ed = sPref.edit();
            ed.putBoolean(Const.SPREF_CAN_USE_DIAGRAM, true);
            ed.commit();
            return true;

        }

    }

}
