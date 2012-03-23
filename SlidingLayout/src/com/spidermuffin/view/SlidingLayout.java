/**
 * 
 * Copyright (c) 2012 Travis Castillo
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
package com.spidermuffin.view;

import java.lang.ref.SoftReference;

import com.spidermuffin.slidinglayout.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Three Panel Sliding Layout which sends callback to {@link SlidingLayoutDelegate} if one is set up.
 * TODO: make this not dependent on an xml view
 * @author traviscastillo
 *
 */
public class SlidingLayout extends HorizontalScrollView {
	////////////////////////
	// Constants
	protected static final float LEFT_LAYOUT_PERCENT_OF_SCREEN = 0.9f;
	protected static final int THREE_PANEL_CENTER_PAGE = 1;
	
	////////////////////////
	// Delegate Interface
	/**
	 * Objects which implement this will receive callbacks from the {@link SlidingLayout} to which they are a delegate.
	 * @author traviscastillo
	 *
	 */
	public interface SlidingLayoutDelegate {
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
		 * @param View view touched
		 * @return true if {@link SlidingLayout} should slide
		 */
		public boolean shouldAllowSlide(View v);
	}
	////////////////////////
	// Fields
	protected int _sideLayoutWidth = -1;
	protected int _leftLayoutWidthTwoPanel = -1;
	protected int _centerLayoutWidthTwoPanel = -1;
	protected int _pages;
	protected boolean _fragmentLoadHasTriggered;
	protected static int _currentPage = -1; //base 0 to match child count
	/**
	 * "bounce" back if we go in one direction too many times
	 */
	protected boolean _bounceEnabled;
	protected Display _display;
	protected int _scrollToAmount = 0;
	protected int[] _snapValues;
	protected ViewGroup _panelContainer; //used for paging
	protected SoftReference<SlidingLayoutDelegate> _delegate; //soft reference to prevent leaking of this object
	
	////////////////////////
	// SlidingLayout
	public SlidingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setHorizontalScrollBarEnabled(false);
		//init dipslay size
    	final WindowManager w = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		_display = w.getDefaultDisplay();
		final DisplayMetrics dm = new DisplayMetrics();
		_display.getMetrics(dm);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		_panelContainer = (ViewGroup) findViewById(R.id.sm_sliding_layout_parent);
		_pages = _panelContainer.getChildCount() - 1; //to get base 0 count
		if (_currentPage > _pages) _currentPage = _pages; //make sure we don't go out of bounds when things switch around
		if (usingLargeFormatLayout()) {
			Log.d(this.toString(), "using two panels!");
		}
		sizeChildViews();
		
	}
	
	public void setBounceEnabled(boolean enabled) { _bounceEnabled = enabled; }
	public boolean getBounceEnabled() { return _bounceEnabled; }
	
	/**
	 * Convenience method to determine whether display is targeted at larger devices (ie: Tablets)
	 * @return true if large format layout was detected.
	 */
	public boolean usingLargeFormatLayout() {
		if (_pages < 2) return true;
		return false;
	}
	
	public int getCurrentPage() { return _currentPage; }
	public int getTotalPages() { return _pages; }
	
	/**
	 * Sets the delegate of this view.  
	 * @param delegate
	 */
	public void setDelegate(SlidingLayoutDelegate delegate) {
		_delegate = new SoftReference<SlidingLayoutDelegate>(delegate);
	}
	
	/**
	 * Will return delegate or null if it has been deallocated
	 * @return
	 */
	public SlidingLayoutDelegate getDelegate() {
		if (_delegate == null) return null;
		return _delegate.get();
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			//TC: these values assume a 3 panel layout
			if (usingLargeFormatLayout() && _currentPage < 0) {
				_currentPage = 0;
			} else if (_currentPage < 0) {
				_currentPage = 1;
			}
			scrolltoPage(_currentPage, false);
		}
	}
	
	/**
	 * Convenience method for scrolling to a given page in the layout.
	 * @param page to scroll to 
	 * @param animated if true, scroll will animate.  if false, it will 'snap'
	 */
	public void scrolltoPage(int page, boolean animated) {
		_scrollToAmount = 0;
		Log.d(this.toString(), "scrolling to page " + page);
		//sanity checks
		page = page < 0? 0: page;
		page = page > _pages? _pages: page;
		
		for (int i = 0; i < page; i++) {
			_scrollToAmount += _panelContainer.getChildAt(i).getMeasuredWidth();
		}
		if (animated) smoothScrollTo(_scrollToAmount, 0);
		else scrollTo(_scrollToAmount, 0);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		//TC: the following prevents background fragments from accepting touch events while the center page is visible
		if (usingLargeFormatLayout() && _currentPage == 0) {
			return true;
		} else if (!usingLargeFormatLayout() && _currentPage == THREE_PANEL_CENTER_PAGE) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//never intercept touch events
		return false;
	}
	
	/**
	 * Size child views so that they take up a portion of the available space.
	 */
    protected void sizeChildViews() {
			//present as three panels
			LinearLayout centerLayout = (LinearLayout) findViewById(R.id.sm_center_layout);
			LinearLayout rightSpacer = (LinearLayout) findViewById(R.id.sm_right_spacer);

			if (usingLargeFormatLayout()) {
				//we're dealing with two panels instead				
				LinearLayout leftLayout = (LinearLayout) findViewById(R.id.sm_left_layout);
				leftLayout.setMinimumHeight(_display.getHeight());
				leftLayout.setMinimumWidth(getLeftSideLayoutWidthTwoPanel());
				centerLayout.setMinimumWidth(getCenterLayoutWidthTwoPanel());
			} else {
				//standard implementation
				
				LinearLayout leftLayout = (LinearLayout) findViewById(R.id.sm_left_spacer);
				leftLayout.setMinimumHeight(_display.getHeight());
				leftLayout.setMinimumWidth(getSideLayoutWidth());
				centerLayout.setMinimumWidth((int) (_display.getWidth()));
			}
			//common size
			rightSpacer.setMinimumWidth(getSideLayoutWidth());
			
    	
    	
    	int centerSnap = 0;
    	int rightSnap = 0;
    	if (_pages == 3) {
    		centerSnap =  _panelContainer.getChildAt(1).getMeasuredWidth();
    		rightSnap = centerSnap + _panelContainer.getChildAt(2).getMeasuredWidth();
    	} else {
    		centerSnap =  _panelContainer.getChildAt(0).getMeasuredWidth();
    		rightSnap = centerSnap + _panelContainer.getChildAt(1).getMeasuredWidth();
    	}
    	_snapValues = new int[]{0,centerSnap,rightSnap};
    }
    
    public int getLeftSideLayoutWidthTwoPanel() {
    	if (_leftLayoutWidthTwoPanel < 0) {
    		int weight = getResources().getInteger(R.integer.sliding_layout_two_panel_left_weight);
    		_leftLayoutWidthTwoPanel = (int) (_display.getWidth() * (weight / 100.0f));
        	}
        	return _leftLayoutWidthTwoPanel;
    }
    
	public int getCenterLayoutWidthTwoPanel() {
		if (_centerLayoutWidthTwoPanel < 0) {
    		int weight = getResources().getInteger(R.integer.sliding_layout_two_panel_center_weight);
    		_centerLayoutWidthTwoPanel = (int) (_display.getWidth() * (weight /  100.0f));
        	}
	    	return _centerLayoutWidthTwoPanel;	
	}
    
    
    public int getSideLayoutWidth() {
    	if (_sideLayoutWidth < 0) {
    	_sideLayoutWidth = (int) (_display.getWidth() * LEFT_LAYOUT_PERCENT_OF_SCREEN);
    	}
    	return _sideLayoutWidth;
    }

	/**
	 * does the effect "back a page".
	 * If we are at the first page it will peform a {@link next()} action.
	 * @param smoothEnabled if false, no animation is performed.
	 */
	public void previous(boolean smoothEnabled) {
		_fragmentLoadHasTriggered = false;
		if (_currentPage == 0) {
			//we're at min
			if (_bounceEnabled) next(smoothEnabled); ///bounce
			//bail if we're at min
			return;
		}
		
		//delegate callback
		if (getDelegate() != null) {
			if (usingLargeFormatLayout()) {
				//tab behavior
				if (_currentPage == 1) {
					//moving to left panel
					getDelegate().willHideRightLayout();
				}
			} else {
				//standard behavior
				if (_currentPage == _pages) {
					//we are moving to the left page
					getDelegate().willHideRightLayout();
				}
			}
		}
		scrolltoPage(_currentPage - 1, smoothEnabled);
		_currentPage -= 1;
	}

	/**
	 * does the effect "forward a page"	 
	 * If we are at the last page it will peform a {@link previous()} action.
	 * @param smoothEnabled if false, no animation is performed.
	 */
	public void next(boolean smoothEnabled) {
		_fragmentLoadHasTriggered = false;
		if (_currentPage == _pages) {
			//we're at max
			if (_bounceEnabled) previous(smoothEnabled); ///bounce
			//bail if we're at max
			return;
		}
		
		//delegate callback
		if (getDelegate() != null) {
			if (_currentPage == 0 ) {
					//we are moving to the center page
					getDelegate().willHideLeftLayout();
			}
		}
		scrolltoPage(_currentPage + 1, smoothEnabled);
		_currentPage += 1;
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (getDelegate() != null) {
			if (l == 0) {
				Log.d(this.toString(), "scroll at zero");
				//TC: 	the following numbers trigger the delegates when the scroll is 'near'
				// 		the target value.  onScrollChanged() doesn't get called reliably enought to count
				//		on it to hit the target value.
			}  else if (l < (_scrollToAmount + 200) && l > (_scrollToAmount - 200) && !_fragmentLoadHasTriggered) {
				_fragmentLoadHasTriggered = true;
				//trigger delegates slightly before we're done scrolling
				Log.d(this.toString(), "scroll finishing! from " + oldl + " to " + l);
				if (_currentPage == _pages) getDelegate().willShowRightLayout();
				if (_currentPage == 0) getDelegate().willShowLeftLayout();
			}
			
		}
		
	}
}
