package com.test.slidinglayout;

import com.spidermuffin.view.SlidingLayout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class SlidingLayoutActivity extends Activity {
	protected static final float LEFT_LAYOUT_PERCENT_OF_SCREEN = 0.8f;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        generateSubViews();
    }

    
    public void generateSubViews() {
    	final WindowManager w = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		final Display m = w.getDefaultDisplay();
		final DisplayMetrics dm = new DisplayMetrics();
		m.getMetrics(dm);
    	int width = m.getWidth();
    	LinearLayout leftLayout = (LinearLayout) findViewById(R.id.left_layout);
    	leftLayout.setMinimumHeight(m.getHeight());
//    	int leftWidth = (int) Math.floor(width * LEFT_LAYOUT_PERCENT_OF_SCREEN);
//    	leftLayout.setLayoutParams(new SlidingLayout.LayoutParams(width , LayoutParams.MATCH_PARENT));
    	LinearLayout rightLayout = (LinearLayout) findViewById(R.id.right_layout);
    	rightLayout.setMinimumHeight(m.getHeight());
//    	rightLayout.setLayoutParams(new SlidingLayout.LayoutParams(width, LayoutParams.MATCH_PARENT));
    	
//    	SlidingLayout parent = (SlidingLayout) findViewById(R.id.test_linear_layout_view);
//    	parent.requestLayout();
//    	parent.setMinimumWidth(leftWidth + width);
//    	parent.setLayoutParams(new LinearLayout.LayoutParams(leftWidth + width, LinearLayout.LayoutParams.MATCH_PARENT));
    }
}