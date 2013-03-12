/*
 * Copyright 2009-2012 CrowdCompass, Inc.
 */

package com.crowdcompass.slidinglayout;

import android.view.View;

/**
 * @author tcastillo
 *         Date: 3/11/13
 *         Time: 4:14 PM
 */
public interface ISlidingLayoutManager {
    public void willHideCenterLayout();

    public void willShowCenterLayout();

    /**
     * Notify that the left layout is about to be hidden.
     */
    public void willHideLeftLayout();

    /**
     * Notify that the left layout is about to appear.
     */
    public void willShowLeftLayout();

    /**
     * Notify that the right layout is about to be hidden.
     */
    public void willHideRightLayout();

    /**
     * Notify that the right layout is about to appear.
     */
    public void willShowRightLayout();

    /**
     * Determines whether a touch event should allow the user to swipe.
     *
     * @param View view touched
     * @return true if {@link com.crowdcompass.slidinglayout.layout.SlidingLayout} should slide
     */
    public boolean shouldAllowSlide(View v);
}
