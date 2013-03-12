/*
 * Copyright 2009-2012 CrowdCompass, Inc.
 */

package com.crowdcompass.slidinglayout.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Like a FrameLayout except it buffers the child views
 * @author tcastillo
 *         Date: 3/11/13
 *         Time: 4:24 PM
 */
public class ImmovableLayout extends ViewGroup {

    // //////////////////////
    // Constants

    // //////////////////////
    // Fields

    /**
     * Display dimensions of device
     */
    private Display _display;

    // //////////////////////
    // Constructors

    public ImmovableLayout(Context context) {
        super(context);
        initDisplaySize();
    }

    public ImmovableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDisplaySize();
    }

    public ImmovableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDisplaySize();
    }

    // //////////////////////
    // Getter & Setter

    // //////////////////////
    // Methods from SuperClass/Interfaces

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //TODO: ANDROID IMPL
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //TODO: ANDROID IMPL

    }

    // //////////////////////
    // Methods

    /**
     * Initialize the display dimensions based on the device parameters
     */
    private void initDisplaySize() {
        final WindowManager w = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        _display = w.getDefaultDisplay();
        final DisplayMetrics dm = new DisplayMetrics();
        _display.getMetrics(dm);
    }
    // //////////////////////
    // Inner and Anonymous Classes
}
