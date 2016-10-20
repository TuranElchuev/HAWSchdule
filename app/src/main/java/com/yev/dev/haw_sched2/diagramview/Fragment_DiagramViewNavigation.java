package com.yev.dev.haw_sched2.diagramview;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.objects.Calendar_Item;
import com.yev.dev.haw_sched2.utils.Const;
import com.yev.dev.haw_sched2.utils.DBHelper;
import com.yev.dev.haw_sched2.utils.Utility;

import java.util.ArrayList;


public class Fragment_DiagramViewNavigation extends Fragment {

    private DiagramViewActivity activity;

    private Utility utility = new Utility();

    private LayoutInflater inflater;

    private ArrayList<Calendar_Item> data;
    private ListView list;
    private MyAdapter adapter;

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

        activity = (DiagramViewActivity) getActivity();

        setData();

        super.onActivityCreated(savedInstanceState);
    }

    //ON CREATE VIEW
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.inflater = inflater;

        View v = inflater.inflate(R.layout.fragment_diagram_view_navigation, container, false);

        setupViews(v);

        return v;
    }

    private void setupViews(View v){

        list = (ListView)v.findViewById(R.id.list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                Calendar_Item item = data.get(position);
                item.SHOW_IN_DIAGRAM = !item.SHOW_IN_DIAGRAM;

                adapter.notifyDataSetChanged();

                subjectsListChanged();

            }
        });

        ((Button)v.findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSaveConfiguration();
            }
        });

        SwitchCompat switchCompat = (SwitchCompat)v.findViewById(R.id.hide_expired);
        switchCompat.setChecked(hideExpired);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hideExpired = isChecked;

                subjectsListChanged();

            }
        });
    }

    private void confirmSaveConfiguration(){
        String message = "";

        String enabled = "";
        String disabled = "";

        for(Calendar_Item item: data){
            if(item.STATE == Const.STATE_DISABLED && item.SHOW_IN_DIAGRAM){
                enabled += " - " + item.FILE_NAME + "\n";
            }

            if(item.STATE == Const.STATE_ENABLED && !item.SHOW_IN_DIAGRAM){
                disabled += " - " + item.FILE_NAME + "\n";
            }
        }

        if(!enabled.isEmpty()){
            message += "\n" + getString(R.string.enabled) + ":\n" + enabled;
        }

        if(!disabled.isEmpty()){
            message += "\n" + getString(R.string.disabled) + ":\n" + disabled;
        }

        if(message.isEmpty()){
            Toast.makeText(getActivity(), R.string.nothing_to_save, Toast.LENGTH_SHORT).show();
            return;
        }


        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

        alertDialog.setTitle(R.string.save_configuration);
        alertDialog.setMessage(message);

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();

                saveChanges();

            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void saveChanges(){

        for(Calendar_Item item: data){
            if(item.SHOW_IN_DIAGRAM == (item.STATE == Const.STATE_ENABLED)){
                continue;
            }

            ContentValues values = new ContentValues();

            if(item.STATE == Const.STATE_DISABLED){

                values.put(DBHelper.COL_STATE, Const.STATE_ENABLED);

            }else{

                values.put(DBHelper.COL_STATE, Const.STATE_DISABLED);

            }

            String selection = DBHelper.COL_FILE_URL + " LIKE ?";
            String[] selectionArgs = {item.FILE_URL};

            utility.updateData(activity, DBHelper.TABLE_NAME_SCHEDULE, values, selection, selectionArgs);
        }

        Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_LONG).show();

        if(activity != null){
            activity.changesSaved();
        }
    }

    private void subjectsListChanged(){

        ArrayList<String> subjects = new ArrayList<String>();

        for(Calendar_Item item: data){
            if(item.SHOW_IN_DIAGRAM){
                subjects.add(item.FILE_NAME);
            }
        }

        if(activity != null){
            activity.setSubjectsList(subjects, hideExpired);
        }
    }


    //SET DATA
    private void setData(){

        if(data == null){
            data = utility.getCalendars(activity);
        }

        adapter = new MyAdapter(activity, inflater);
        list.setAdapter(adapter);
    }

    //ADAPTER
    private class MyAdapter extends SimpleAdapter {

        LayoutInflater inflater;

        public MyAdapter(Context context, LayoutInflater inflater) {

            super(context, null, 0, null, null);

            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = inflater.inflate(R.layout.list_item_diagram_navigation, null);
            }

            Calendar_Item item = data.get(position);

            ((TextView)convertView.findViewById(R.id.file_name)).setText(item.FILE_NAME);

            ((ImageView)convertView.findViewById(R.id.icon_priority)).setImageResource(utility.getPriorityImage(item.PRIOORITY));

            ((SwitchCompat)convertView.findViewById(R.id.switchbtn)).setChecked(item.SHOW_IN_DIAGRAM);

            if(item.STATE == Const.STATE_ENABLED){
                ((ImageView)convertView.findViewById(R.id.icon_enabled)).setImageResource(R.drawable.ic_enabled);
                convertView.setAlpha(Const.ALPHA_ENABLED);
            }else{
                ((ImageView)convertView.findViewById(R.id.icon_enabled)).setImageResource(R.drawable.ic_disabled);
                convertView.setAlpha(Const.ALPHA_DISABLED);
            }

            return convertView;
        }
    }
}
