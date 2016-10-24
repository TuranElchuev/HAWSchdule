package com.yev.dev.haw_sched2.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.objects.Event_Item;
import com.yev.dev.haw_sched2.utils.Utility;

import java.util.ArrayList;

public class OverlapsActivity extends Activity {

    public static final String KEY_SUBJECTS = "subjects";
    public static final String KEY_HIDE_EXPIRED = "hide_expired";

    private ArrayList<Object> listData;
    private int screenWidth = 0;
    private Activity activity;

    private ArrayList<String> subjects;
    private boolean hideExpired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        subjects = intent.getStringArrayListExtra(KEY_SUBJECTS);
        hideExpired = intent.getBooleanExtra(KEY_HIDE_EXPIRED, true);

        activity = this;

        ActionBar acb = getActionBar();
        if(acb != null){
            acb.hide();
        }

        setContentView(R.layout.activity_overlaps);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        Utility utility = new Utility();

        ListView list = (ListView)findViewById(R.id.list);

        listData = utility.getOverlaps(this, subjects, hideExpired);

        if(listData.isEmpty()){
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
        }else {
            list.setAdapter(new MyAdapter(this, getLayoutInflater()));
        }

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private class MyAdapter extends SimpleAdapter {

        LayoutInflater inflater;

        public MyAdapter(Context context, LayoutInflater inflater) {

            super(context, null, 0, null, null);

            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = inflater.inflate(R.layout.events_container, null);

            ArrayList<Object> day_data = (ArrayList<Object>)listData.get(position);

            for(Object item: day_data){
                ((TextView)convertView.findViewById(R.id.t_header)).setText(
                        getString(((Event_Item)item).WEEKDAY_NAME_ID)
                                + ", " + ((Event_Item)item).START_DAY
                                + " (" + getString(R.string.week_no)
                                + " " + ((Event_Item)item).WEEK_NUMBER
                                + ")");

                View event_view = inflater.inflate(R.layout.event_with_diagram, null);
                ((Event_Item)item).setDataToView(activity, event_view, true, screenWidth);
                ((ViewGroup)convertView.findViewById(R.id.container)).addView(event_view);
            }

            return convertView;
        }
    }
}
