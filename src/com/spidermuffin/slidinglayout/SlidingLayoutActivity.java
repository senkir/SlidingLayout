package com.spidermuffin.slidinglayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.spidermuffin.slidinglayout.view.SlidingLayout;
import com.spidermuffin.slidinglayout.R;
/**
 * Sample activity which manages interactions wiht the SlidingLayout
 * @author travischurchill
 *
 */
public class SlidingLayoutActivity extends FragmentActivity implements SlidingLayoutDelegate{
	protected static final float LEFT_LAYOUT_PERCENT_OF_SCREEN = 0.8f;
	protected static final String LEFT_FRAGMENT_TAG = "leftFragment";
	protected static final String RIGHT_FRAGMENT_TAG = "rightFragment";
	protected static final String BASE_FRAGMENT_TAG = "baseFragment";

	protected SlidingLayout _slidingLayout;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        _slidingLayout = (SlidingLayout) findViewById(R.id.sm_scroll_view);
    }

    public void useLeftFragment() {
    	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    	Fragment newFragment = getLeftFragment();
    	if (getSupportFragmentManager().findFragmentByTag(BASE_FRAGMENT_TAG) != null) {
	    		transaction.replace(R.id.base_layout, newFragment, BASE_FRAGMENT_TAG);
    	} else {
    		transaction.add(R.id.base_layout, newFragment, BASE_FRAGMENT_TAG);
    	}
    	transaction.commitAllowingStateLoss();
    }
    
    public void useRightFragment() {
    	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    	Fragment newFragment = getRightFragment();
    	if (getSupportFragmentManager().findFragmentByTag(BASE_FRAGMENT_TAG) != null) {
			transaction.replace(R.id.base_layout, newFragment, BASE_FRAGMENT_TAG);
    	} else {
    		transaction.add(R.id.base_layout, newFragment, BASE_FRAGMENT_TAG);
    	}
    	transaction.commitAllowingStateLoss();
    }
    
    protected Fragment getLeftFragment() {
    	return new LeftFragment();
    }
    
    protected Fragment getRightFragment() {
    	return new RightFragment();
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	_slidingLayout.setDelegate(this);
    }
    
    public void next(View view) {
    	_slidingLayout.next(true);
    }
    
    public void previous(View view) {
    	_slidingLayout.previous(true);
    }

	@Override
	public void willHideLeftLayout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void willShowLeftLayout() {
		useLeftFragment();
	}

	@Override
	public void willHideRightLayout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void willShowRightLayout() {
		useRightFragment();
	}
}