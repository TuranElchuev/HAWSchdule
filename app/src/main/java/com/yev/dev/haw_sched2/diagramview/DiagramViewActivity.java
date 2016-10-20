package com.yev.dev.haw_sched2.diagramview;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.utils.Utility;
import com.yev.dev.haw_sched2.views.MySlidingPaneLayout;

import java.util.ArrayList;


public class DiagramViewActivity extends Activity {

    private MySlidingPaneLayout slider;
    private Utility util = new Utility();

    public Fragment_DiagramViewContent fragmentContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar acb = getActionBar();
        if (acb != null) {
            acb.hide();
        }

        setContentView(R.layout.activity_diagram_view);

        setupViews();

        if(savedInstanceState == null){
            setFragments();
        }
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
            }

            @Override
            public void onPanelOpened(View panel) {

            }

            @Override
            public void onPanelClosed(View panel) {

            }
        });

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

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_navigation, new Fragment_DiagramViewNavigation())
                .commit();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new Fragment_DiagramViewContent())
                .commit();

    }


    public void setSubjectsList(ArrayList<String> subjects, boolean hideExpired){
        if(fragmentContent != null){
            fragmentContent.setSubjectsList(subjects, hideExpired);
        }
    }

    public void changesSaved(){

        setResult(RESULT_OK);
        finish();

    }

}
