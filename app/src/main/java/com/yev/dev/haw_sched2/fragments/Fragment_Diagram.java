package com.yev.dev.haw_sched2.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.activities.FullScheduleActivity;
import com.yev.dev.haw_sched2.objects.DiagramView_Item;
import com.yev.dev.haw_sched2.utils.Const;
import com.yev.dev.haw_sched2.utils.Utility;

import java.util.ArrayList;


public class Fragment_Diagram extends Fragment implements FullScheduleActivity.FullScheduleActivityListener{

    private Utility utility = new Utility();

    private LayoutInflater inflater;

    private ArrayList<ArrayList<DiagramView_Item>> data;
    private ListView list;
    private MyAdapter adapter;

    private ArrayList<String> subjects = null;
    private boolean hideExpired = true;

    //ON CREATE
    @Override
    public void onCreate(Bundle savedInstanceState) {

        setRetainInstance(true);

        super.onCreate(savedInstanceState);
    }

    //ON ACTIVITY CREATED
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Activity activity = getActivity();

        if(activity instanceof FullScheduleActivity){
            ((FullScheduleActivity) activity).setListener(this);
        }

        setData();

        super.onActivityCreated(savedInstanceState);
    }

    //ON CREATE VIEW
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;

        View v = inflater.inflate(R.layout.fragment_diagram, container, false);

        setupViews(v);

        return v;
    }

    private void setupViews(View v){

        list = (ListView)v.findViewById(R.id.list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

            }
        });

    }

    @Override
    public void setSubjectsList(ArrayList<String> subjects, boolean hideExpired) {
        this.subjects = subjects;
        this.hideExpired = hideExpired;

        setData();
    }

    //SET DATA
    private void setData(){

        data = utility.getDiagramViewData(getActivity(), subjects, hideExpired);

        adapter = new MyAdapter(getActivity(), inflater);
        list.setAdapter(adapter);
    }

    //ADAPTER
    private class MyAdapter extends SimpleAdapter {


        int screenWidth;

        LayoutInflater inflater;

        public MyAdapter(Context context, LayoutInflater inflater) {

            super(context, null, 0, null, null);

            this.inflater = inflater;

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;

        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.events_container, null);
            }

            LinearLayout container = (LinearLayout)convertView.findViewById(R.id.container);

            container.removeAllViews();

            ArrayList<DiagramView_Item> weekDayData = data.get(position);

            for(DiagramView_Item item: weekDayData){

                ((TextView)convertView.findViewById(R.id.t_header)).setText(
                        getActivity().getString(utility.getWeekdayNameId(item.WEEKDAY)));

                View item_view = inflater.inflate(R.layout.list_item_diagram_item, null);

                if(item.STATE == Const.STATE_ENABLED){
                    ((ImageView)item_view.findViewById(R.id.icon_enabled)).setImageResource(R.drawable.ic_enabled);
                    item_view.setAlpha(Const.ALPHA_ENABLED);
                }else{
                    ((ImageView)item_view.findViewById(R.id.icon_enabled)).setImageResource(R.drawable.ic_disabled);
                    item_view.setAlpha(Const.ALPHA_DISABLED);
                }

                ((TextView)item_view.findViewById(R.id.t_summary)).setText(item.SUMMARY);
                ((TextView)item_view.findViewById(R.id.t_location)).setText(item.LOCATION);
                ((TextView)item_view.findViewById(R.id.t_start)).setText(item.START_FORMATTED);
                ((TextView)item_view.findViewById(R.id.t_end)).setText(item.END_FORMATTED);
                ((TextView)item_view.findViewById(R.id.t_allweeks)).setText(item.ALLWEEKS);

                View bar = (View)item_view.findViewById(R.id.diagram_bar);
                bar.setBackgroundColor(utility.getPriorityColor(getActivity(), item.PRIOORITY));

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)bar.getLayoutParams();
                lp.setMargins((int)(screenWidth * item.startLevel), 0, 0, 0);
                lp.width = (int)(screenWidth * item.length);
                bar.setLayoutParams(lp);

                container.addView(item_view);

            }

            return convertView;
        }
    }

}
