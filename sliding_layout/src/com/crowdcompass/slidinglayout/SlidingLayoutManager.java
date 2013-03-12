/*
 * Copyright 2009-2012 CrowdCompass, Inc.
 */

package com.crowdcompass.slidinglayout;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.crowdcompass.eventshortname.R;
import com.crowdcompass.eventshortname.client.ApplicationDelegate;
import com.crowdcompass.eventshortname.client.eventdirectory.controller.EventDirectoryCenterFragment;
import com.crowdcompass.eventshortname.client.global.controller.AViewController;
import com.crowdcompass.eventshortname.client.navigator.Navigator;
import com.crowdcompass.slidinglayout.layout.SlidingLayout;
import com.crowdcompass.util.CCLogger;

import java.lang.ref.WeakReference;

import static com.crowdcompass.eventshortname.slidinglayout.SlidingLayoutConstants.*;

/**
 * Sample activity which manages interactions the the SlidingLayoutDemo
 *
 * @author traviscastillo
 */
public abstract class SlidingLayoutManager {

    // //////////////////////
    // Constants

    // //////////////////////
    // Fields
    private WeakReference<FragmentActivity> _delegate;

    /**
     * Right hotspot
     */
    protected View    _rightTouchButton;
    /**
     * Left hotspot
     */
    protected View    _leftTouchButton;
    /**
     *
     */
    protected boolean _rightTouchAreaEnabled;
    protected boolean _leftTouchAreaEnabled;

    private          SlidingLayout _slidingLayout;
    /**
     * TODO: not implemented.  this will be  a catched bitmap that slides
     */
    protected static Bitmap        _cachedCenterBitmap;
    protected        Fragment      _rightFragment;
    protected        Fragment      _centerFragment;
    protected        Fragment      _leftFragment;

    // //////////////////////
    // Constructors
    public SlidingLayoutManager(FragmentActivity activity) {
        setContext(activity);
    }

    // //////////////////////
    // Getter & Setter

    public void setContext(FragmentActivity delegate) {
        _delegate = new WeakReference<FragmentActivity>(delegate);
    }

    public FragmentActivity getContext() {
        return _delegate != null ? _delegate.get() : null;
    }
    // //////////////////////
    // Methods from SuperClass/Interfaces

    // //////////////////////
    // Methods

    // //////////////////////
    // Inner and Anonymous Classes
    ////////////////////////
    // SlidingLayoutManager

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        _slidingLayout = (SlidingLayout) findViewById(R.id.sm_scroll_view);
        if (savedInstanceState == null) initSlidingLayout(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //restore state
        int page = savedInstanceState.getInt(CURRENT_PAGE_KEY);
        _slidingLayout.setDelegate(this);
        _slidingLayout.restoreCurrentPage(page);
        initSlidingLayout(savedInstanceState);
    }

    public void initSlidingLayout(Bundle savedInstanceState) {
        CCLogger.log(this.getClass().getSimpleName() + ":initSlidingLayout");
        getSlidingLayout().setDelegate(this);
        //init 'hotspot' areas
        _leftTouchButton = findViewById(R.id.sm_sliding_layout_left_touch_area);
        _leftTouchButton.setOnClickListener(this);
        _rightTouchButton = findViewById(R.id.sm_sliding_layout_right_touch_area);
        _rightTouchButton.setOnClickListener(this);
        if (savedInstanceState == null) {
            useCenterHeader();
            useCenterFragment();
            if (isTablet() || _slidingLayout.getCurrentPage() == 0) {
                CCLogger.log("Starting up left fragment");
                useLeftFragment();
            }
        } else {
            if (_slidingLayout.getCurrentPage() == 0 && !_slidingLayout.usingLargeFormatLayout()) {
                rightTouchAreaEnabled(true);
            }
            if (_slidingLayout.getCurrentPage() == 2 || (_slidingLayout.getCurrentPage() == 1 && isTablet())) {
                leftTouchAreaEnabled(true);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _leftFragment = null;
        _centerFragment = null;
        _rightFragment = null;
        _slidingLayout = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentPage", _slidingLayout.getCurrentPage());
    }

    protected SlidingLayout getSlidingLayout() {
        if (_slidingLayout == null) findViewById(R.id.sm_scroll_view);
        return _slidingLayout;
    }

    /**
     * Set the center fragment reference
     *
     * @param fragment
     */
    public void setCenterFragment(EventDirectoryCenterFragment fragment) {
        _centerFragment = fragment;
    }

    public void setRightFragment(Fragment fragment) {
        _rightFragment = fragment;
    }

    public void setLeftFragment(Fragment fragment) {
        _leftFragment = fragment;
    }


    /**
     * Convenience method for determining whether a large format layout is being used.
     * Will return false if the sliding layout has not been initialized.
     *
     * @return true if device is being rendered in large format mode.
     */
    public boolean isTablet() {
        return ApplicationDelegate.getSharedAppDelegate().isTablet();
    }

    /**
     * Replaces fragment matching tag in container specified with the one supplied.
     *
     * @param fragment    what to use
     * @param tag         tag to use for matching fragment
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
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.commit();
    }

    /**
     * Show the header for the center layout.
     *
     * @see #getCenterHeaderFragment()
     */
    protected void useCenterHeader() {
        Fragment newFragment = getCenterHeaderFragment();
        exchangeFragmentByTag(newFragment, CENTER_HEADER_FRAGMENT_TAG, R.id.sm_center_layout_header);
    }

    /**
     * Show the center panel
     *
     * @see #getCenterFragment()
     */
    protected void useCenterFragment() {
        Fragment newFragment = getCenterFragment();
        exchangeFragmentByTag(newFragment, CENTER_FRAGMENT_TAG, Navigator.DEFAULT_CONTAINER_ID);
    }

    /**
     * Show the left panel
     *
     * @see #getLeftFragment()
     */
    protected void useLeftFragment() {
        CCLogger.log("SlidingLayoutDemo:useLeftFragment");
        Fragment newFragment = getLeftFragment();
        if (isTablet()) {
            exchangeFragmentByTag(newFragment, LEFT_FRAGMENT_TAG, R.id.sm_left_layout);
        } else {
            exchangeFragmentByTag(newFragment, BASE_FRAGMENT_TAG, R.id.sm_base_layout);
        }
    }

    /**
     * Show the right panel
     *
     * @see #getRightFragment()
     */
    protected void useRightFragment() {
        CCLogger.log("SlidingLayoutDemo:useRightFragment");
        Fragment newFragment = getRightFragment();
        exchangeFragmentByTag(newFragment, BASE_FRAGMENT_TAG, R.id.sm_base_layout);
    }

    /**
     * The fragmet which should appear when the left panel is revealed
     *
     * @return fragment
     */
    protected abstract Fragment getLeftFragment();

    /**
     * The fragment which should appear when the right panel is revealed
     *
     * @return fragment
     */
    protected abstract Fragment getRightFragment();

    /**
     * The fragment for the center layout
     *
     * @return fragment
     */
    protected abstract Fragment getCenterFragment();

    /**
     * The fragment for the center layout header.  Will appear above center fragment
     *
     * @return fragment
     */
    protected abstract Fragment getCenterHeaderFragment();

    /**
     * Causes the floating center view to slide to the left
     *
     * @param view calling view.  can be null
     */
    public void slideLeft(View view) {
        _slidingLayout.next(true);
    }

    /**
     * Causes the floating center view to slide to the right
     *
     * @param view calling view. can be null
     */
    public void slideRight(View view) {
        _slidingLayout.previous(true);
    }

    /**
     * Content view layout which should be used when creating this activity.
     *
     * @return layout ID
     */
    protected int specifyContentViewId() {
        return R.layout.sm_three_panel_layout_parent;
    }

    /**
     * Get the cached bitmpa from
     *
     * @return cached bitmap
     */
    public Bitmap getCachedCenterBitmap() {
        return _cachedCenterBitmap;
    }

    /**
     * Set the cached bitmap to use when sliding.
     *
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
        CCLogger.log("SlidingLayoutManager:willHideLeftLayout");
        rightTouchAreaEnabled(false);
        if (!isTablet()) {
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
        CCLogger.log("SlidingLayoutManager:willHideRightLayout");
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
        CCLogger.log("right touch area: " + Boolean.toString(enabled));
        _rightTouchButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    protected void leftTouchAreaEnabled(boolean enabled) {
        CCLogger.log("left touch area: " + Boolean.toString(enabled));
        _leftTouchAreaEnabled = enabled;
        _leftTouchButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    @Override
    public void willShowCenterLayout() {
        // do nothing.  we always show the center layout
    }

    @Override
    public void willHideCenterLayout() {
        CCLogger.log("SlidingLayoutManager:willHideCenterLayout");
        Bitmap centerBitmap;
        View rootView = getCenterFragment().getView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        centerBitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        setCachedBitmap(centerBitmap);
    }

    /**
     * Determines if a sliding event should occur
     *
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