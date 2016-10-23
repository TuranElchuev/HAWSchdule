package com.yev.dev.haw_sched2.activities;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.View.OnClickListener;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.fragments.Fragment_Calendars;
import com.yev.dev.haw_sched2.fragments.Fragment_Events;
import com.yev.dev.haw_sched2.fragments.Fragment_Selection;
import com.yev.dev.haw_sched2.fragments.Fragment_Web;
import com.yev.dev.haw_sched2.utils.Const;
import com.yev.dev.haw_sched2.utils.DBHelper;
import com.yev.dev.haw_sched2.utils.PermissionsHandler;
import com.yev.dev.haw_sched2.utils.Utility;
import com.yev.dev.haw_sched2.utils.VersionHandling;
import com.yev.dev.haw_sched2.views.MySlidingPaneLayout;

public class MainActivity extends Activity implements OnClickListener {

private MySlidingPaneLayout slider;

private Utility util = new Utility();

public interface MainActivityCallbacks {

	void mainActivityMessage(int message, String data);

}

private MainActivityCallbacks listener;

//===========LIFE CYCLE===================

//ON CREATE
@Override
protected void onCreate(Bundle savedInstanceState) {

	ActionBar acb = getActionBar();
	if (acb != null) {
		acb.hide();
	}

	////TEST
	/*
	Locale locale = new Locale("de");
	Locale.setDefault(locale);
	Configuration config = new Configuration();
	config.locale = locale;
	getBaseContext().getResources().updateConfiguration(config,
	getBaseContext().getResources().getDisplayMetrics());
	*/
	/*

	ScheduleImporter importer = new ScheduleImporter(this);
	importer.importSchedule("MyFile.ics", Environment.getExternalStorageDirectory().toString() + Const.TEMP_FILE, Const.PRIORITY_MEDIUM, Const.STATE_ENABLED, System.currentTimeMillis(), false);
	*/
	///////////

	setContentView(R.layout.activity_main);

	setupViews();

	if (savedInstanceState == null) {
		if (dataBaseIsEmpty(false)) {
			setCalendarsFragment();
		} else {
			setEventFragment(Const.MODE_DAY, Const.DATA_DEFAULT);
		}
	}


	super.onCreate(savedInstanceState);

	PermissionsHandler.verifyStoragePermissions(this);
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
			navigationPane.setAlpha(slideOffset);
			navigationPane.setScaleX((float) (0.8 + 0.2 * slideOffset));
			navigationPane.setScaleY((float) (0.8 + 0.2 * slideOffset));

			contentPane.setAlpha((float) (1 - 0.6 * slideOffset));
		}

		@Override
		public void onPanelOpened(View panel) {

		}

		@Override
		public void onPanelClosed(View panel) {

		}
	});

	findViewById(R.id.daily).setOnClickListener(this);
	findViewById(R.id.weekly).setOnClickListener(this);
	findViewById(R.id.weekday).setOnClickListener(this);
	findViewById(R.id.subject).setOnClickListener(this);
	findViewById(R.id.calendars).setOnClickListener(this);
	findViewById(R.id.full_calendar).setOnClickListener(this);
	findViewById(R.id.diagram).setOnClickListener(this);
    findViewById(R.id.overlaps).setOnClickListener(this);
    findViewById(R.id.overlaps).setOnClickListener(this);
	findViewById(R.id.share).setOnClickListener(this);

    updateOverlapsButton();
}

//ON CLICK
@Override
public void onClick(View v) {
	switch (v.getId()) {
		case R.id.daily:
			if (!slider.isOpen()) {
				return;
			}
			setEventFragment(Const.MODE_DAY, Const.DATA_DEFAULT);
			slider.closePane();
			break;
		case R.id.weekly:
			if (!slider.isOpen()) {
				return;
			}
			setEventFragment(Const.MODE_WEEK, Const.DATA_DEFAULT);
			slider.closePane();
			break;
		case R.id.weekday:
			if (!slider.isOpen()) {
				return;
			}
			setSelectionFragment(Const.MODE_WEEKDAY, Const.DATA_DEFAULT);
			slider.closePane();
			break;
		case R.id.subject:
			if (!slider.isOpen()) {
				return;
			}
			setSelectionFragment(Const.MODE_SUBJECT, Const.DATA_DEFAULT);
			slider.closePane();
			break;
		case R.id.calendars:
			if (!slider.isOpen()) {
				return;
			}
			setCalendarsFragment();
			slider.closePane();
			break;
		case R.id.full_calendar:
			if (!slider.isOpen()) {
				return;
			}

			Intent intent = new Intent(this, FullScheduleActivity.class);
			intent.putExtra(FullScheduleActivity.KEY_TYPE, FullScheduleActivity.TYPE_CALENDAR);
			startActivityForResult(intent, Const.INTENT_SHOW_FULL_SCHEDULE);

			break;
		case R.id.diagram:
			if (!slider.isOpen()) {
				return;
			}

			if(!VersionHandling.canUseDiagramView(this)){
				return;
			}

			intent = new Intent(this, FullScheduleActivity.class);
			intent.putExtra(FullScheduleActivity.KEY_TYPE, FullScheduleActivity.TYPE_DIAGRAM);
			startActivityForResult(intent, Const.INTENT_SHOW_FULL_SCHEDULE);

			break;
        case R.id.overlaps:
            if (!slider.isOpen()) {
                return;
            }

            intent = new Intent(this, OverlapsActivity.class);
            startActivity(intent);

            break;

		case R.id.share:

			slider.closePane();
			util.shareApp(this);

			break;

		default:
			break;
	}
}

//ON BACK PRESSED
@Override
public void onBackPressed() {

	if (slider.isOpen()) {
		slider.closePane();
		return;
	}

	if (listener != null) {
		listener.mainActivityMessage(Const.MES_BACK_PRESSED, null);
		return;
	}

	super.onBackPressed();
}

//SET LISTENER
public void setListener(MainActivityCallbacks listener) {
	this.listener = listener;
}

//OPEN SLIDER
public void slideSlider() {
	if (slider.isOpen()) {
		slider.closePane();
	} else {
		slider.openPane();
	}
}

//CLOSE
public void closeSlider() {
	if (slider.isOpen()) {
		slider.closePane();
	}
}

//CHECK DATABASE
public boolean dataBaseIsEmpty(boolean openPane) {
	boolean is_empty = util.tableIsEmpty(this, DBHelper.TABLE_NAME_SCHEDULE);

	if (is_empty) {
		if (openPane) {
			slider.openPane();
		}

		findViewById(R.id.empty).setVisibility(View.VISIBLE);
	} else {
		findViewById(R.id.empty).setVisibility(View.GONE);
	}

	return is_empty;
}


//ON ACTIVITY RESULT

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == Const.INTENT_SHOW_FULL_SCHEDULE) {

            updateOverlapsButton();

            if (resultCode == Activity.RESULT_OK) {
                setCalendarsFragment();
            }
        }

	}


//====================SETTING FRAGMENTS======================

//SET EVENT FRAGMENT
public void setEventFragment(int MODE, String DATA) {

	slider.setEnabled(true);

	Fragment_Events fragment = new Fragment_Events();
	fragment.MODE = MODE;
	fragment.DATA = DATA;

	getFragmentManager()
			.beginTransaction()
			//.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
			.replace(R.id.container, fragment)
			.commit();

}

//SET SELECTION FRAGMENT
public void setSelectionFragment(int MODE, String DATA) {

	slider.setEnabled(true);

	Fragment_Selection fragment = new Fragment_Selection();
	fragment.MODE = MODE;
	fragment.DATA = DATA;

	getFragmentManager()
			.beginTransaction()
			//.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
			.replace(R.id.container, fragment)
			.commit();
}

//SET CCALENDARS FRAGMENT
public void setCalendarsFragment() {

	slider.setEnabled(true);

	Fragment_Calendars fragment = new Fragment_Calendars();

	getFragmentManager()
			.beginTransaction()
			.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
			.replace(R.id.container, fragment)
			.commit();

}

//SET WEB FRAGMENT
public void setWebFragment() {

	slider.setEnabled(false);

	Fragment_Web fragment = new Fragment_Web();

	getFragmentManager()
			.beginTransaction()
			.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
			.replace(R.id.container, fragment)
			.commit();
}

    private void updateOverlapsButton(){
        if(util.getOverlaps(this, null, true).isEmpty()){
            findViewById(R.id.overlaps).setVisibility(View.GONE);
        }else{
            findViewById(R.id.overlaps).setVisibility(View.VISIBLE);
        }
    }

}
