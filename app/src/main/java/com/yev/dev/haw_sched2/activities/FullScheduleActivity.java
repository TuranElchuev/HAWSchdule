package com.yev.dev.haw_sched2.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.fragments.Fragment_Diagram;
import com.yev.dev.haw_sched2.fragments.Fragment_FullCalendar;
import com.yev.dev.haw_sched2.fragments.Fragment_SubjectsNavigation;
import com.yev.dev.haw_sched2.utils.Utility;
import com.yev.dev.haw_sched2.views.MySlidingPaneLayout;

import java.util.ArrayList;


public class FullScheduleActivity extends Activity implements Fragment_SubjectsNavigation.SubjectsNavigationListener {

    public static final String KEY_TYPE = "type";
    public static final int TYPE_DIAGRAM = 1;
    public static final int TYPE_CALENDAR = 2;
    private int type;

    private final String SAVESTATE_KEY_SLIDER_IS_OPEN = "slider_is_open";

    private MySlidingPaneLayout slider;
    private Utility util = new Utility();

    private ImageView icon_back;

    boolean SLIDER_IS_OPEN = false;

    private View overlaps;

    private ArrayList<String> subjects;
    private boolean hideExpired = true;

    public interface FullScheduleActivityListener {
        public void setSubjectsList(ArrayList<String> subjects, boolean hideExpired);
    }

    private FullScheduleActivityListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar acb = getActionBar();
        if (acb != null) {
            acb.hide();
        }

        Intent intent = getIntent();
        type = intent.getIntExtra(KEY_TYPE, TYPE_CALENDAR);

        setContentView(R.layout.activity_full_schedule);

        setupViews();

        if(savedInstanceState == null){
            setFragments();
        }else{
            SLIDER_IS_OPEN = savedInstanceState.getBoolean(SAVESTATE_KEY_SLIDER_IS_OPEN);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVESTATE_KEY_SLIDER_IS_OPEN, SLIDER_IS_OPEN);
    }

    //SETUP VIEWS
    private void setupViews() {

        slider = (MySlidingPaneLayout) findViewById(R.id.slider);

        slider.setShadowResourceLeft(R.drawable.shadow_left);

        slider.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

            View navigationPane = findViewById(R.id.frame_navigation);
            View contentPane = findViewById(R.id.container);

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                /*navigationPane.setAlpha(slideOffset);
                navigationPane.setScaleX((float) (0.8 + 0.2 * slideOffset));
                navigationPane.setScaleY((float) (0.8 + 0.2 * slideOffset));

                contentPane.setAlpha((float) (1 - 0.6 * slideOffset));*/

                navigationPane.setX(navigationPane.getWidth() * (slideOffset - 1));

                icon_back.setRotation(180f * slideOffset);
            }

            @Override
            public void onPanelOpened(View panel) {
                SLIDER_IS_OPEN = true;
                setBackIcon();
            }

            @Override
            public void onPanelClosed(View panel) {
                SLIDER_IS_OPEN = false;
                setBackIcon();
            }
        });

        overlaps = findViewById(R.id.overlaps);
        ((TextView)overlaps.findViewById(R.id.text)).setText(R.string.have_overlaps);
        overlaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullScheduleActivity.this, OverlapsActivity.class);
                intent.putExtra(OverlapsActivity.KEY_SUBJECTS, subjects);
                intent.putExtra(OverlapsActivity.KEY_HIDE_EXPIRED, hideExpired);
                startActivity(intent);
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(slider.isOpen()){
                    slider.closePane();
                }else{
                    slider.openPane();
                }
            }
        });

        switch (type){
            case TYPE_DIAGRAM:
                ((TextView)findViewById(R.id.text_primary)).setText(R.string.diagram);
                break;
            case TYPE_CALENDAR:
                ((TextView)findViewById(R.id.text_primary)).setText(R.string.full_calendar);
                break;
        }

        icon_back = (ImageView)findViewById(R.id.icon_primary);

    }

    //ON RESUME
    @Override
    protected void onResume() {
        setBackIcon();
        super.onResume();
    }

    //ON BACK PRESSED
    @Override
    public void onBackPressed() {

        if (slider.isOpen()) {
            slider.closePane();
            return;
        }

        super.onBackPressed();
    }

    private void setFragments(){

        FragmentManager fm = getFragmentManager();

        fm.beginTransaction()
                .replace(R.id.frame_navigation, new Fragment_SubjectsNavigation())
                .commit();

        switch (type){
            case TYPE_CALENDAR:
                fm.beginTransaction()
                        .replace(R.id.container, new Fragment_FullCalendar())
                        .commit();
                break;
            case TYPE_DIAGRAM:
                fm.beginTransaction()
                        .replace(R.id.container, new Fragment_Diagram())
                        .commit();
                break;
        }

    }


    @Override
    public void onSubjectsListChanged(ArrayList<String> subjects, boolean hideExpired) {

        this.hideExpired = hideExpired;
        this.subjects = subjects;

        updateOverlapsButton();

        if(listener != null){
            listener.setSubjectsList(subjects, hideExpired);
        }
    }

    @Override
    public void onChangesSaved() {
        setResult(RESULT_OK);
        finish();
    }

    private void updateOverlapsButton(){
        if(util.getOverlaps(this, subjects, hideExpired).isEmpty()){
            overlaps.setVisibility(View.GONE);
        }else{
            overlaps.setVisibility(View.VISIBLE);
        }
    }

    public void setListener(FullScheduleActivityListener listener){
        this.listener = listener;
    }


    private void setBackIcon(){
        if(icon_back == null){
            return;
        }

        if(SLIDER_IS_OPEN){
            icon_back.setRotation(180f);
        }else{
            icon_back.setRotation(0f);
        }
    }
}
