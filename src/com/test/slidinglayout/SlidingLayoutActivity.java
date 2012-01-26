package com.test.slidinglayout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.spidermuffin.view.SlidingLayout;

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
    	int screenWidth = m.getWidth();
    	
    	LinearLayout leftLayout = (LinearLayout) findViewById(R.id.left_layout);
    	leftLayout.setMinimumHeight(m.getHeight());
    	leftLayout.setMinimumWidth((int) (screenWidth * LEFT_LAYOUT_PERCENT_OF_SCREEN));

    	LinearLayout centerLayout = (LinearLayout) findViewById(R.id.center_layout);
    	leftLayout.setMinimumHeight(m.getHeight());
    	centerLayout.setMinimumWidth((int) (screenWidth));
    	
    	LinearLayout rightLayout = (LinearLayout) findViewById(R.id.right_layout);
    	rightLayout.setMinimumWidth((int) (screenWidth * LEFT_LAYOUT_PERCENT_OF_SCREEN));

    }
    
    public void next(View view) {
    	_slidingLayout.next(true);
    }
    
    public void previous(View view) {
    	_slidingLayout.previous(true);
    }
}