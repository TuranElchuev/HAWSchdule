package com.yev.dev.haw_sched2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.yev.dev.haw_sched2.R;
import com.yev.dev.haw_sched2.objects.EventsDay;
import com.yev.dev.haw_sched2.objects.EventsHolder;
import com.yev.dev.haw_sched2.objects.EventsSubject;
import com.yev.dev.haw_sched2.objects.EventsWeek;
import com.yev.dev.haw_sched2.objects.EventsWeekday;
import com.yev.dev.haw_sched2.utils.Const;

public class Fragment_Events extends FragmentForMainActivity implements OnClickListener {
	
	private EventsHolder eventsHolder;
	private View v;
	
	//================LIFE CYCLE======================
	
	//ON CREATE VIEW
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_events, container, false);
		
		v.findViewById(R.id.select).setOnClickListener(this);
		v.findViewById(R.id.prev).setOnClickListener(this);
		v.findViewById(R.id.next).setOnClickListener(this);
		v.findViewById(R.id.back).setOnClickListener(this);
		
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		switch (MODE) {
		case Const.MODE_DAY:
			eventsHolder = new EventsDay(activity, v, activity.getLayoutInflater());
			break;
		case Const.MODE_WEEK:
			eventsHolder = new EventsWeek(activity, v, activity.getLayoutInflater());
			break;
		case Const.MODE_WEEKDAY:
			eventsHolder = new EventsWeekday(activity, v, activity.getLayoutInflater());
			break;
		case Const.MODE_SUBJECT:
			eventsHolder = new EventsSubject(activity, v, activity.getLayoutInflater());
			break;

		default:

			DATA = Const.DATA_DEFAULT;
			eventsHolder = new EventsDay(activity, v, activity.getLayoutInflater());

			break;
		}
		
		eventsHolder.initializeData(DATA);
	}

	//MAIN ACTIVITY MESSAGE
	@Override
	public void mainActivityMessage(int message, String data) {
		
		switch (message) {
		case Const.MES_BACK_PRESSED:
			activity.finish();
			break;

		default:
			break;
		}
		
		super.mainActivityMessage(message, data);
	}

	//ON CLICK
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.prev:
			activity.closeSlider();
			eventsHolder.previous();
			break;
		case R.id.next:
			activity.closeSlider();
			eventsHolder.next();
			break;
		case R.id.select:
			activity.closeSlider();
			activity.setSelectionFragment(MODE, eventsHolder.DATA);
			break;
		case R.id.back:
			activity.slideSlider();
			break;

		default:
			break;
		}
	}
	
}
