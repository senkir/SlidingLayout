package com.test.slidinglayout;

import com.spidermuffin.view.SlidingLayout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SlidingLayoutActivity extends Activity {
	protected static final float LEFT_LAYOUT_PERCENT_OF_SCREEN = 0.8f;
	protected SlidingLayout _slidingLayout;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        generateSubViews();
        _slidingLayout = (SlidingLayout) findViewById(R.id.test_scroll_view);
    }

    @Override
    protected void onStart() {
    	super.onStart();
    }
    
    public void generateSubViews() {
    	final WindowManager w = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		final Display m = w.getDefaultDisplay();
		final DisplayMetrics dm = new DisplayMetrics();
		m.getMetrics(dm);
    	int width = m.getWidth();
    	LinearLayout leftLayout = (LinearLayout) findViewById(R.id.left_layout);
    	leftLayout.setMinimumHeight(m.getHeight());
    	leftLayout.setMinimumWidth((int) (m.getWidth() * LEFT_LAYOUT_PERCENT_OF_SCREEN));
//    	int leftWidth = (int) Math.floor(width * LEFT_LAYOUT_PERCENT_OF_SCREEN);
//    	leftLayout.setLayoutParams(new SlidingLayout.LayoutParams(width , LayoutParams.MATCH_PARENT));
    	LinearLayout rightLayout = (LinearLayout) findViewById(R.id.right_layout);
    	rightLayout.setMinimumHeight(m.getHeight());
    	rightLayout.setMinimumWidth((int) (m.getWidth()));

//    	rightLayout.setLayoutParams(new SlidingLayout.LayoutParams(width, LayoutParams.MATCH_PARENT));
    	
    	HorizontalScrollView parent = (HorizontalScrollView) findViewById(R.id.test_scroll_view);
    	parent.setOnTouchListener(null);
    	parent.setOnTouchListener( new OnTouchListener(){ 

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
    	});

    }
    
    public void next(View view) {
    	_slidingLayout.next(true);
    }
    
    public void previous(View view) {
    	_slidingLayout.previous(true);
    }
}