/*
 * Copyright 2009-2012 CrowdCompass, Inc.
 */

package com.crowdcompass.slidinglayout.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import com.crowdcompass.eventshortname.R;
import com.crowdcompass.eventshortname.client.view.AFlingListener;
import com.crowdcompass.slidinglayout.ISlidingLayoutManager;
import com.crowdcompass.slidinglayout.R;
import com.crowdcompass.slidinglayout.SlidingLayoutConstants;
import com.crowdcompass.util.CCLogger;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * Parent layout for everything
 * @author tcastillo
 */
public class SlidingLayout extends HorizontalScrollView {
    ////////////////////////
    // Constants
    private static final String TAG = SlidingLayout.class.getSimpleName();

    ////////////////////////
    // Fields
    private int _sideLayoutWidth           = -1;
    private int _leftLayoutWidthTwoPanel   = -1;
    private int _centerLayoutWidthTwoPanel = -1;
    /**
     * Total number of pages
     */
    private int _pages;
    /**
     * Currently active page.  (mainly used for restoring state on rotation)
     */
    private int _currentPage = -1;
    private boolean _restoringCurrentPage;
    /**
     * Currently activated panel.
     */
    private int _showingPanel = -1;
    /**
     * Parent container for paging purposes
     */
    private       ViewGroup                            _panelContainer;
    /**
     * typically a reference to {@link com.crowdcompass.slidinglayout.SlidingLayoutManager} but can be any form of
     * {@link SlidingLayoutDelegate}
     */
    private       WeakReference<ISlidingLayoutManager> _delegate;
    //soft reference to prevent leaking of this object
    /**
     * "bounce" back if we go in one direction too many times
     */
    private       boolean                              _bounceEnabled;
    /**
     * Capture 'fling' behaviors
     */
    private final GestureDetector                      _gestureDetector;
    private       SlidingListener                      _slidingListener;
    /**
     * Keeps track of whether manual sliding is possible.
     */
    private boolean _manualSlideEnabled     = false;
    /**
     * Keeps track of whether we are performing a manual slide.
     */
    private boolean _manualMotionInProgress = false;
    /**
     * Scroll values which will trigger a panel to 'reveal' itself.
     */
    private LinkedList<Integer> _revealTriggerValues;
    /**
     * Scroll values which affect 'snapping' to a page
     */
    private LinkedList<Integer> _snapTriggerValues;
    /**
     * Enable crossing from right panel to left panel
     */
    private boolean _leftPanelPassThroughEnabled  = false;
    /**
     * Enable crossing from left panel to right panel
     */
    private boolean _rightPanelPassThroughEnabled = false;

    ////////////////////////
    // SlidingLayoutDemo
    public SlidingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //set up fling behavior
        _slidingListener = new SlidingListener();
        _gestureDetector = new GestureDetector(_slidingListener);
    }

    public SlidingLayout(Context context) {
        super(context);
        _gestureDetector = new GestureDetector(_slidingListener);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        _panelContainer = (ViewGroup) findViewById(R.id.sm_sliding_layout_parent);
        _pages = _panelContainer.getChildCount() - 1; //to get base 0 count
        if (_currentPage > _pages)
            _currentPage = _pages; //make sure we don't go out of bounds when things switch around
        if (usingLargeFormatLayout()) {
            Log.d(TAG, "using large format layout!");
        }
        sizeChildViews();

    }


    public void setBounceEnabled(boolean enabled) {
        _bounceEnabled = enabled;
    }

    public boolean getBounceEnabled() {
        return _bounceEnabled;
    }

    /**
     * Convenience method to determine whether display is targeted at larger devices (ie: Tablets)
     *
     * @return true if large format layout was detected.
     */
    public boolean usingLargeFormatLayout() {
        if (_pages < 2) return true;
        return false;
    }

    /**
     * Get the current active page
     *
     * @return int current page
     */
    public int getCurrentPage() {
        return _currentPage;
    }

    public void restoreCurrentPage(int page) {
        _currentPage = page;
        _restoringCurrentPage = true;
        _showingPanel = page;
//		triggerFragmentLoad(page);
    }

    protected int getShowingPanel() {
        if (_showingPanel < 0) {
            if (usingLargeFormatLayout()) _showingPanel = 0;
            else _showingPanel = 1;
        }
        return _showingPanel;
    }

    public int getTotalPages() {
        return _pages;
    }

    /**
     * Sets the delegate of this view.
     *
     * @param delegate
     */
    public void setDelegate(com.crowdcompass.eventshortname.slidinglayout.SlidingLayoutDelegate delegate) {
        _delegate = new WeakReference<com.crowdcompass.eventshortname.slidinglayout.SlidingLayoutDelegate>(delegate);
    }

    /**
     * Will return delegate or null if it has been deallocated
     *
     * @return
     */
    public com.crowdcompass.eventshortname.slidinglayout.SlidingLayoutDelegate getDelegate() {
        if (_delegate == null) return null;
        return _delegate.get();
    }

    /**
     * Whether we should be able to slide to the right panel from the opposite side (in manual mode). <br/>
     * Default is <b>false</b>
     *
     * @param enabled true if slide should be possible
     */
    public void setRightPanelPassThroughEnabled(boolean enabled) {
        _rightPanelPassThroughEnabled = enabled;
    }

    /**
     * Whether we should be able to slide to the left panel from the opposite side (in manual mode). <br/>
     * Default is <b>false</b>
     *
     * @param enabled true if slide should be possible
     */
    public void setLeftPanelPassThroughEnabled(boolean enabled) {
        _leftPanelPassThroughEnabled = enabled;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            //layout has changed (typically because of first inflate or rotation)
            if (!usingLargeFormatLayout() && _currentPage < 0) {
                //current page has not been set
                //Phones start in the center panel
                scrolltoPage(1, false);
                return;
            }
            scrolltoPage(_currentPage, false);
            _restoringCurrentPage = false;
        }
    }

    /**
     * Convenience method for scrolling to a given page in the layout.
     *
     * @param page     to scroll to
     * @param animated if true, scroll will animate.  if false, it will 'snap' immediately into place
     */
    public void scrolltoPage(int page, boolean animated) {
        if (page > _pages) page = _pages;
        int scrollToAmount = getScrollAmountForPage(page);
        if (animated) smoothScrollTo(scrollToAmount, 0);
        else {
            scrollTo(scrollToAmount, 0);
        }
        _currentPage = page;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    public boolean onTouchEvent(View v, MotionEvent ev) {
        _slidingListener.setTouchedView(v);
        return onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //gesture detector gets to handle most events
        boolean result = _gestureDetector.onTouchEvent(ev);
        if (_manualSlideEnabled && _manualMotionInProgress) {
            //allow sliding animation
            super.onTouchEvent(ev);
        }
        if (!result && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) && _manualMotionInProgress) {
            //gesture detector didn't do any fling actions at the end of a gesture
            //snap to a page
            snapToNearestPage();
        }
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //never intercept touch events
        return false;
    }

    /**
     * Force the scroll to go to the nearest page.  This transition will always be animated.
     */
    public void snapToNearestPage() {
        CCLogger.log("SlidingLayoutDemo:snapToNearestPage");
        int scrollX = getScrollX();
        int leftPeekingPage = 0; //what page is showing on the left side of the screen
        if (scrollX > getSnapTriggerValueForPage(2)) leftPeekingPage = 1;
        boolean shouldGoNext = false;
        int triggerWidth = 0; //the width to mark as halfway point
        if (!usingLargeFormatLayout()) {
            //phone behavior
            if (leftPeekingPage == 0) {
                //we can only go forward
                triggerWidth = getSnapTriggerValueForPage(0);
            } else if (leftPeekingPage == 1) {
                boolean testingForNext = (scrollX >= getSnapTriggerValueForPage((leftPeekingPage))) ? true : false;
                if (testingForNext) {
                    //subtract the left panel
                    triggerWidth = getSnapTriggerValueForPage(1);
                } else {
                    //same as for page 0
                    triggerWidth = getSnapTriggerValueForPage(0);
                }
            } else {
                //we can only go backward
                triggerWidth = getSnapTriggerValueForPage(1);
            }
        } else {
            //tablets only have one trigger point
            triggerWidth = getSnapTriggerValueForPage(0);

        }
        if (scrollX > triggerWidth) shouldGoNext = true;
        if (shouldGoNext) {
            scrolltoPage(leftPeekingPage + 1, true);
        } else {
            scrolltoPage(leftPeekingPage, true);
        }
    }

    /**
     * Size child views so that they take up a portion of the available space.
     */
    protected void sizeChildViews() {
        //present as three panels
        ViewGroup centerLayout = (ViewGroup) findViewById(R.id.sm_center_layout);
        ViewGroup rightSpacer = (ViewGroup) findViewById(R.id.sm_right_spacer);

        if (usingLargeFormatLayout()) {
            //we're dealing with two panels instead
            LinearLayout leftLayout = (LinearLayout) findViewById(R.id.sm_left_layout);
            leftLayout.setLayoutParams(new TableRow.LayoutParams(getLeftSideLayoutWidthLargeFormat(), LayoutParams.MATCH_PARENT));
            centerLayout.setLayoutParams(new TableRow.LayoutParams(getCenterLayoutWidthLargeFormat(), LayoutParams.MATCH_PARENT));
        } else {
            //standard implementation
            LinearLayout leftLayout = (LinearLayout) findViewById(R.id.sm_left_spacer);
            leftLayout.setLayoutParams(new TableRow.LayoutParams(getSideLayoutWidth(), LayoutParams.MATCH_PARENT));
            centerLayout.setLayoutParams(new TableRow.LayoutParams(_display.getWidth(), LayoutParams.MATCH_PARENT));
        }
        //common size
        rightSpacer.setLayoutParams(new TableRow.LayoutParams(getSideLayoutWidth(), LayoutParams.MATCH_PARENT));
        initRevealTriggerValues();
        initSnapTriggerValues();
    }

    private int getCenterLayoutWidth() {
        //only valid in phone layouts
        return _display.getWidth();
    }

    private int getLeftSideLayoutWidthLargeFormat() {
        if (_leftLayoutWidthTwoPanel < 0) {
            int weight = getResources().getInteger(R.integer.sliding_layout_two_panel_left_weight);
            _leftLayoutWidthTwoPanel = (int) (_display.getWidth() * (weight / 100.0f));
        }
        return _leftLayoutWidthTwoPanel;
    }

    private int getCenterLayoutWidthLargeFormat() {
        if (_centerLayoutWidthTwoPanel < 0) {
            int weight = getResources().getInteger(R.integer.sliding_layout_two_panel_center_weight);
            _centerLayoutWidthTwoPanel = (int) (_display.getWidth() * (weight / 100.0f));
        }
        return _centerLayoutWidthTwoPanel;
    }


    private int getSideLayoutWidth() {
        if (_sideLayoutWidth < 0) {
            _sideLayoutWidth = (int) (_display.getWidth() * SIDE_PANEL_PERCENT_OF_SCREEN);
        }
        return _sideLayoutWidth;
    }

    /**
     * Initialize scroll values which will trigger a panel to 'reveal' itself.
     */
    protected void initRevealTriggerValues() {
        LinkedList<Integer> revealValues = new LinkedList<Integer>();
        if (!usingLargeFormatLayout()) {
            int leftReveal = getSideLayoutWidth();
            revealValues.add(leftReveal);
            int rightReveal = getCenterLayoutWidth();
            revealValues.add(rightReveal);
        } else {
//			int rightReveal = getLeftSideLayoutWidthLargeFormat() + getCenterLayoutWidth();   
            revealValues.add(0);
        }
        CCLogger.log("RevealValues:" + revealValues);
        _revealTriggerValues = revealValues;
    }

    /**
     * Get the point at which the page should reveal
     *
     * @param page to check for
     * @return scroll value for the page
     */
    protected int getRevealTriggerValueForPage(int page) {
        if (_revealTriggerValues == null) {
            CCLogger.warn("SlidingLayoutDemo:getRevealTriggerValueForPage trigger values not defined");
            return 0;
        }
        if (page > _revealTriggerValues.size() - 1) page = _revealTriggerValues.size() - 1;
        if (page < 0) page = _revealTriggerValues.size() - 1;
        return _revealTriggerValues.get(page);
    }

    /**
     * Initialize snap triggers.  These are the points at which the view should snap to the next page
     */
    protected void initSnapTriggerValues() {
        LinkedList<Integer> snapValues = new LinkedList<Integer>();
        if (!usingLargeFormatLayout()) {
            //left snap trigger
            snapValues.add((int) (getSideLayoutWidth() / 2.0));
            //right snap trigger
            snapValues.add(getCenterLayoutWidth() + (int) (getSideLayoutWidth() / 2.0));
        } else {
            int centerSnap = (int) (getLeftSideLayoutWidthLargeFormat() / 2.0);
            snapValues.add(centerSnap);
        }
        CCLogger.log("SnapValues:" + snapValues);
        _snapTriggerValues = snapValues;
    }

    /**
     * Get the snap trigger for the given page
     *
     * @param page to get trigger for
     * @return scroll value for the page
     */
    protected int getSnapTriggerValueForPage(int page) {
        if (_snapTriggerValues == null) {
            CCLogger.warn("SlidingLayoutDemo:getSnapTriggerValueForPage trigger values not defined");
            return 0;
        }
        if (page > _snapTriggerValues.size() - 1) page = _snapTriggerValues.size() - 1;
        if (page < 0) page = _snapTriggerValues.size() - 1;
        return _snapTriggerValues.get(page);
    }

    /**
     * does the effect "back a page".
     * If we are at the first page it will peform a {@link next()} action.
     *
     * @param smoothEnabled if false, no animation is performed.
     */
    public void previous(boolean animated) {
//		_manualSlideEnabled = manualSliding;
        int scrollX = getScrollX();
        int leftPeekingPage = 0; //what page is showing on the left side of the screen
        if (scrollX > getRevealTriggerValueForPage(0) && !usingLargeFormatLayout()) leftPeekingPage = 1;
        scrolltoPage(leftPeekingPage, animated);
    }

    /**
     * does the effect "forward a page"
     * If we are at the last page it will peform a {@link previous()} action.
     *
     * @param smoothEnabled if false, no animation is performed.
     */
    public void next(boolean animated) {
//		_manualSlideEnabled = manualSliding;
        int scrollX = getScrollX();
        int leftPeekingPage = 0; //what page is showing on the left side of the screen
        if (scrollX >= getRevealTriggerValueForPage(0)) leftPeekingPage = 1;
        scrolltoPage(leftPeekingPage + 1, animated);
    }

    /**
     * Get the snap-to value for the page specified
     *
     * @param page to snap to
     * @return int scrollTo value
     */
    protected int getScrollAmountForPage(int page) {
        int scrollAmount = 0;
        //sanity checks
        page = page < 0 ? 0 : page;
        page = page > _pages ? _pages : page;

        for (int i = 0; i < page; i++) {
            scrollAmount += _panelContainer.getChildAt(i).getMeasuredWidth();
        }
        return scrollAmount;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (!usingLargeFormatLayout()) {
            //we only care about this on the phone layout
            if (!_rightPanelPassThroughEnabled && _manualSlideEnabled && getShowingPanel() != _pages && l > getScrollAmountForPage(1)) {
                //prevent the scroll view from scrolling to the right panel
                scrollTo(getScrollAmountForPage(1), 0);
            }
            if (!_leftPanelPassThroughEnabled && _manualSlideEnabled && getShowingPanel() != 0 && l < getScrollAmountForPage(1)) {
                //prevent the scroll view from scrolling to the left panel
                scrollTo(getScrollAmountForPage(1), 0);
            }
        }
        if (getDelegate() != null && !_restoringCurrentPage) {
            if (!usingLargeFormatLayout()) {
                //phone behavior
                if (l < (Integer) _revealTriggerValues.get(0)) {
                    //less than left edge of center panel
                    triggerFragmentLoad(0);
                } else if (l > (Integer) _revealTriggerValues.get(1)) {
                    //greater than right edge of center panel
                    triggerFragmentLoad(_pages);
                } else {
                    if (getShowingPanel() == 0) getDelegate().willHideLeftLayout();
                    if (getShowingPanel() == _pages) getDelegate().willHideRightLayout();
                    _showingPanel = 1;
                }
            } else {
                //Tablet behavior
                if (l > _revealTriggerValues.get(0)) {
                    triggerFragmentLoad(1);
                } else {
                    if (getShowingPanel() == _pages) getDelegate().willHideRightLayout();
                    _showingPanel = 0;
                }
            }
        }
    }

    /**
     * Tells a fragment to load
     *
     * @param page
     */
    protected void triggerFragmentLoad(int page) {
        if (!usingLargeFormatLayout() && page == 0 && getShowingPanel() != 0) {
            _showingPanel = 0;
            //we are moving to the center page
            if (getDelegate() != null) getDelegate().willShowLeftLayout();
        } else if (page == _pages && getShowingPanel() != _pages) {
            _showingPanel = _pages;
            if (getDelegate() != null) getDelegate().willShowRightLayout();
        }
    }

    public static String getCenterHeaderTag() {
        return SlidingLayoutConstants.CENTER_HEADER_FRAGMENT_TAG;
    }

    /**
     * Internal class that handles gesture events for the {@link com.crowdcompass.eventshortname.slidinglayout.SlidingLayout}
     *
     * @author crowdcompass
     * @author tcastillo
     */
    private class SlidingListener extends AFlingListener {
        /**
         * Touched View.  For handling 'hotspot' behavior
         */
        private WeakReference<View> _touchedView = new WeakReference<View>(null);
        /**
         * Keep track of what page we're coming FROM.  Prevents overshooting the fling behavior
         */
        private int _fromPage = 1;

        @Override
        public boolean onDown(MotionEvent e) {
            if (hotspotTouched(getTouchedView())) {
                //a motion event has started
                SlidingLayout.this._manualMotionInProgress = true;
                _manualSlideEnabled = true;
                _fromPage = _currentPage;
            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            boolean result = super.onFling(e1, e2, velocityX, velocityY);
            if (result) {
                //make sure on touch doesn't handle anything else after this call
                SlidingLayout.this._manualMotionInProgress = false;
            }
            return result;
        }

        @Override
        public void previous() {
            if (usingLargeFormatLayout()) {
                SlidingLayout.this.scrolltoPage(0, true);
            } else {
                if (_fromPage == _pages) {
                    SlidingLayout.this.scrolltoPage(1, true);
                } else {
                    SlidingLayout.this.scrolltoPage(0, true);
                }
            }
        }

        @Override
        public void next() {
            if (usingLargeFormatLayout()) {
                SlidingLayout.this.scrolltoPage(1, true);
            } else {
                if (_fromPage == 0) {
                    SlidingLayout.this.scrolltoPage(1, true);
                } else {
                    SlidingLayout.this.scrolltoPage(_pages, true);
                }
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            _manualSlideEnabled = false;
            //handle 'tap' behavior
            if (getTouchedView() == null) return false;
            if (RIGHT_TOUCH_TAG.equals(getTouchedView().getTag())) {
                next();
                return true;
            } else if (LEFT_TOUCH_TAG.equals(getTouchedView().getTag())) {
                previous();
                return true;
            }
            return false;
        }

        /**
         * Determines whether the view is a 'hotspot' for the sliding layout'
         *
         * @param view to check
         * @return true if a hotspot was found
         */
        protected boolean hotspotTouched(View v) {
            if (v == null) return false;
            if (LEFT_TOUCH_TAG.equalsIgnoreCase((String) v.getTag())) {
                return true;
            } else if (RIGHT_TOUCH_TAG.equalsIgnoreCase((String) v.getTag())) {
                return true;
            }
            return false;
        }

        public void setTouchedView(View v) {
            _touchedView = new WeakReference<View>(v);
        }

        private View getTouchedView() {
            return _touchedView.get();
        }
    }
}
