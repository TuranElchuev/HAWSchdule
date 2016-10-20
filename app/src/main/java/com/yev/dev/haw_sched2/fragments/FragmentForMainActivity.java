package com.yev.dev.haw_sched2.fragments;

import android.app.Fragment;
import android.os.Bundle;

import com.yev.dev.haw_sched2.main.MainActivity;
import com.yev.dev.haw_sched2.main.MainActivity.MainActivityCallbacks;
import com.yev.dev.haw_sched2.utils.Const;
import com.yev.dev.haw_sched2.utils.Utility;

public class FragmentForMainActivity extends Fragment implements MainActivityCallbacks {
	
	public int MODE = Const.MODE_DEFAULT;
	public String DATA;
	
	MainActivity activity;
	
	public Utility utility = new Utility();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setRetainInstance(true);
		
		super.onCreate(savedInstanceState);
	}
	
	//ON ACTIVITY CREATED
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		activity = (MainActivity)getActivity(); 
		
		activity.setListener(this);
								
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void mainActivityMessage(int message, String data) {
		// TODO Auto-generated method stub
		
	}
	
}
