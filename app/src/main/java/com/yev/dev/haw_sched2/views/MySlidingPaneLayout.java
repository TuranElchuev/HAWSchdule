package com.yev.dev.haw_sched2.views;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MySlidingPaneLayout extends SlidingPaneLayout {
	
	private boolean enabled = true;
	
	public MySlidingPaneLayout(Context context) {
	    super(context);
	    // TODO Auto-generated constructor stub
	}

	public MySlidingPaneLayout(Context context, AttributeSet attrs,
	        int defStyle) {
	    super(context, attrs, defStyle);
	    // TODO Auto-generated constructor stub
	}

	public MySlidingPaneLayout(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    // TODO Auto-generated constructor stub
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(enabled){
			return super.onInterceptTouchEvent(ev);
		}else{
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(enabled){
			return super.onTouchEvent(ev);
		}else{
			return false;
		}
	}

	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
}
