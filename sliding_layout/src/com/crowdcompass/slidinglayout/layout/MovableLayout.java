/*
 * Copyright 2009-2012 CrowdCompass, Inc.
 */

package com.crowdcompass.slidinglayout.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.crowdcompass.slidinglayout.HorizontalScrollView;

/**
 * the moving portion of the layout
 * @author tcastillo
 *         Date: 3/11/13
 *         Time: 4:24 PM
 */
public class MovableLayout extends HorizontalScrollView {

    // //////////////////////
    // Constants

    // //////////////////////
    // Fields

    // //////////////////////
    // Constructors

    public MovableLayout(Context context) {
        super(context);
        setHorizontalScrollBarEnabled(false);
    }

    public MovableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
    }

    public MovableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setHorizontalScrollBarEnabled(false);
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

    // //////////////////////
    // Inner and Anonymous Classes
}
