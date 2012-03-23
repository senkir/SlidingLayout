/**
 * 
 * Copyright (c) 2012 Travis castillo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, 
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial 
 * portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT 
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.spidermuffin.controller;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.spidermuffin.slidinglayout.R;
import com.spidermuffin.view.SlidingLayout;
import com.spidermuffin.view.SlidingLayout.SlidingLayoutDelegate;

/**
 * Sample activity which manages interactions with the SlidingLayout
 * @author traviscastillo
 *
 */
public abstract class SlidingLayoutActivity extends FragmentActivity implements SlidingLayoutDelegate, OnClickListener {
	////////////////////////
	// Contstants
	protected static final float LEFT_LAYOUT_PERCENT_OF_SCREEN = 	0.8f;
	////////////////////////
	// Fields
	public static final String LEFT_FRAGMENT_TAG = 					"leftFragment";
	public static final String RIGHT_FRAGMENT_TAG = 				"rightFragment";
	public static final String CENTER_FRAGMENT_TAG = 				"centerFragment";
	public static final String CENTER_HEADER_FRAGMENT_TAG = 		"centerHeaderFragment";
	public static final String BASE_FRAGMENT_TAG = 					"baseFragment";
	
	public static final String LEFT_TOUCH_TAG = 					"leftTouchArea";
	public static final String RIGHT_TOUCH_TAG = 					"rightTouchArea";
	protected boolean _rightTouchAreaEnabled;
	/**
	 * Right hotspot
	 */
	protected Button	 	 _rightTouchButton;
	protected boolean _leftTouchAreaEnabled;
	/**
	 * Left hotspot
	 */
	protected Button		 _leftTouchButton;
	
	protected SlidingLayout _slidingLayout;
	/**
	 * TODO: not implemented.  this will be  a catched bitmap that slides
	 */
	protected static Bitmap _cachedCenterBitmap;
	
	protected Fragment _rightFragment;
	protected Fragment _centerFragment;
	protected Fragment _leftFragment;
	////////////////////////
	// SlidingLayoutActivity     
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(specifyContentViewId());
        initSlidingLayout(savedInstanceState);
    }
    
    public void initSlidingLayout(Bundle savedInstanceState) {
    	_slidingLayout = (SlidingLayout) findViewById(R.id.sm_scroll_view);
    	if (savedInstanceState == null) {
    		useCenterHeader();
    		useCenterFragment();
    		if (_slidingLayout.usingLargeFormatLayout() || _slidingLayout.getCurrentPage() == 0) {
    			Log.d(this.toString(), "Starting up left fragment");
    			useLeftFragment();
    		}
    	}
    	//init 'hotspot' areas
    	_leftTouchButton = (Button) findViewById(R.id.sm_sliding_layout_left_touch_area);
    	_leftTouchButton.setOnClickListener(this);
    	_rightTouchButton = (Button) findViewById(R.id.sm_sliding_layout_right_touch_area);
    	_rightTouchButton.setOnClickListener(this);
    	if (_slidingLayout.usingLargeFormatLayout()) {
    		if (_slidingLayout.getCurrentPage() > 0) leftTouchAreaEnabled(true);
    	} else {
    		if (_slidingLayout.getCurrentPage() == 0) {
    			//first page
    			rightTouchAreaEnabled(true);
    		} else if (_slidingLayout.getCurrentPage() == _slidingLayout.getTotalPages()) {
    			//last page
    			leftTouchAreaEnabled(true);
    		}
    		
    	}
    		
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	_slidingLayout.setDelegate(this);
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		_leftFragment = null;
		_centerFragment = null;
		_rightFragment = null;
}
	
	/**
	 * Set the center fragment reference
	 * @param fragment
	 */
	public void setCenterFragment(Fragment fragment) {
		_centerFragment = fragment;
	}
	
	public void setRightFragment(Fragment fragment) {
		_rightFragment = fragment;
	}
	
	public void setLeftFragment(Fragment fragment) {
		_leftFragment = fragment;
	}
	
    @Override
    public void onClick(View v) {
    	//on click action for overlay areas
    	Log.d(this.toString(), "Activity on click");
    	if (shouldAllowSlide(v)) {
    		if (_leftTouchAreaEnabled) {
    			leftTouchAreaEnabled(false);
    			_slidingLayout.previous(true);
    		}
    		if (_rightTouchAreaEnabled) {
    			rightTouchAreaEnabled(false);
    			_slidingLayout.next(true);
    		}
    		return;
    	}
    	return;
    }
    
    /**
     * Replaces fragment matching tag in container specified with the one supplied.
     * @param fragment what to use
     * @param tag tag to use for matching fragment
     * @param containerID where to place fragment
     */
    protected void exchangeFragmentByTag(Fragment fragment, String tag, int containerID) {
    	if (fragment == null || tag == null) return;
    	Fragment oldFragment = getSupportFragmentManager().findFragmentByTag(tag);
    	if (fragment == oldFragment) return; //don't bother if they are the same
    	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    	if (getSupportFragmentManager().findFragmentByTag(tag) != null) {
	    		transaction.replace(containerID, fragment, tag);
    	} else {
    		transaction.add(containerID, fragment, tag);
    	}
    	transaction.commitAllowingStateLoss();
    }
    
    /**
     * Show the header for the center layout.
     * @see #getCenterHeaderFragment()
     */
    protected void useCenterHeader() {
    	Fragment newFragment = getCenterHeaderFragment();
    	exchangeFragmentByTag(newFragment, CENTER_HEADER_FRAGMENT_TAG, R.id.sm_center_layout_header);    
    }
    
    /**
     * Show the center panel
     * @see #getCenterFragment()
     */
    protected void useCenterFragment() {
    	Fragment newFragment = getCenterFragment();
    	exchangeFragmentByTag(newFragment, CENTER_FRAGMENT_TAG, R.id.sm_center_layout);
    }    
    /**
     * Show the left panel
     * @see #getLeftFragment()
     */
    protected void useLeftFragment() {
    	Fragment newFragment = getLeftFragment();
    	if (_slidingLayout.usingLargeFormatLayout()) {
    		exchangeFragmentByTag(newFragment, LEFT_FRAGMENT_TAG, R.id.sm_left_layout);
    	} else {
    		exchangeFragmentByTag(newFragment, BASE_FRAGMENT_TAG, R.id.sm_base_layout);
    	}
    }
    
    /**
     * Show the right panel
     * @see #getRightFragment()
     */
    protected void useRightFragment() {
    	Fragment newFragment = getRightFragment();
    	exchangeFragmentByTag(newFragment, BASE_FRAGMENT_TAG, R.id.sm_base_layout);
    }
    
    /**
     * The fragmet which should appear when the left panel is revealed
     * @return fragment
     */
    protected abstract Fragment getLeftFragment();
    
    /**
     * The fragment which should appear when the right panel is revealed
     * @return fragment
     */
    protected abstract Fragment getRightFragment();
    
    /**
     * The fragment for the center layout
     * @return fragment
     */
    protected abstract Fragment getCenterFragment();
    
    /**
     * The fragment for the center layout header.  Will appear above center fragment
     * @return fragment
     */
    protected abstract Fragment getCenterHeaderFragment();
    
    /**
     * Causes the floating center view to slide to the left
     * @param view calling view.  can be null
     */
    public void slideLeft(View view) {
    	_slidingLayout.next(true);
    }
    
    /**
     * Causes the floating center view to slide to the right
     * @param view calling view. can be null
     */
    public void slideRight(View view) {
    	_slidingLayout.previous(true);
    }
    
    /**
     * Content view layout which should be used when creating this activity.
     * @return layout ID
     */
    protected int specifyContentViewId() {
    	return R.layout.sm_three_panel_layout_parent;
    }
    
    /**
     * Get the cached bitmpa from 
     * @return cached bitmap
     */
    public Bitmap getCachedCenterBitmap() {
    	return _cachedCenterBitmap;
    }
    
    /**
     * Set the cached bitmap to use when sliding.
     * @param bitmap to cache
     */
    protected void setCachedBitmap(Bitmap bitmap) {
    	if (_cachedCenterBitmap != null && _cachedCenterBitmap != bitmap) {
    		_cachedCenterBitmap.recycle(); //prevents leaky bitmaps
    	}
    	_cachedCenterBitmap = bitmap;
    }
    
    //////////////////////////////////
	// SlidingLayoutDelegate
	@Override
	public void willShowLeftLayout() {
		rightTouchAreaEnabled(true);
		useLeftFragment();
	}

	@Override
	public void willShowRightLayout() {
		leftTouchAreaEnabled(true);
		useRightFragment();
	}
	
	@Override
	public void willHideLeftLayout() {
		Log.d(this.toString(), "SlidingLayoutActivity:willHideLeftLayout");
		rightTouchAreaEnabled(false);
		if (!_slidingLayout.usingLargeFormatLayout()) {
			//clear fragment
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			Fragment baseFragment = getSupportFragmentManager().findFragmentByTag(BASE_FRAGMENT_TAG);
			if (baseFragment != null) {
				transaction.remove(baseFragment);
				transaction.commitAllowingStateLoss();
			}
		}	
	}
	
	@Override
	public void willHideRightLayout() {
		Log.d(this.toString(), "SlidingLayoutActivity:willHideRightLayout");
		leftTouchAreaEnabled(false);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		Fragment rightFragment = getSupportFragmentManager().findFragmentByTag(BASE_FRAGMENT_TAG);
		if (rightFragment != null) {
			transaction.remove(rightFragment);
			transaction.commitAllowingStateLoss();
		}
	}
	
	protected void rightTouchAreaEnabled(boolean enabled) {
		_rightTouchAreaEnabled = enabled;
		Log.d(this.toString(), "right touch area: " + Boolean.toString(enabled));
		_rightTouchButton.setVisibility(enabled? View.VISIBLE : View.GONE);
	}
	
	protected void leftTouchAreaEnabled(boolean enabled) {
		Log.d(this.toString(), "left touch area: " + Boolean.toString(enabled));
		_leftTouchAreaEnabled = enabled;
		_leftTouchButton.setVisibility(enabled? View.VISIBLE : View.GONE);
	}
	
	@Override
	public void willShowCenterLayout() {
		// do nothing.  we always show the center layout
	}
	
	@Override
	public void willHideCenterLayout() {
		Log.d(this.toString(), "SlidingLayoutActivity:willHideCenterLayout");
		Bitmap centerBitmap;
		View rootView = getCenterFragment().getView().getRootView();
		rootView.setDrawingCacheEnabled(true);
		centerBitmap = Bitmap.createBitmap(rootView.getDrawingCache());
		setCachedBitmap(centerBitmap);
	}
	/**
	 * Determines if a sliding event should occur
	 * @return true if view tag is one of the 'hotspot' zones for switching pages
	 */
	@Override
	public boolean shouldAllowSlide(View v) {
		if (v == null) return false;
		if (LEFT_TOUCH_TAG.equalsIgnoreCase((String) v.getTag())) {
			return true;
		} else if (RIGHT_TOUCH_TAG.equalsIgnoreCase((String) v.getTag())) {
			return true;
		}
		return false;
	}
}