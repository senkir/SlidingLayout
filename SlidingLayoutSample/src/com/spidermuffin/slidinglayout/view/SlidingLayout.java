/**
 * 
 * Copyright (c) 2012 Travis Churchill
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
package com.spidermuffin.slidinglayout.view;

import java.lang.ref.SoftReference;

import com.spidermuffin.slidinglayout.SlidingLayoutDelegate;
import com.spidermuffin.slidinglayout.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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
 * @author travischurchill
 *
 */
public class SlidingLayout extends HorizontalScrollView {
	protected static final float LEFT_LAYOUT_PERCENT_OF_SCREEN = 0.8f;

	int _sideLayoutWidth = -1;
	int _pages;
	int _currentPage;
	ViewGroup _childContainer; //used for paging
	SoftReference<SlidingLayoutDelegate> _delegate; //soft reference to prevent leaking of this object
	
	public SlidingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setHorizontalScrollBarEnabled(false);
    	//disable manual sliding
    	setOnTouchListener( new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
    	});
		//custom attributes go here if i ever need them
    	_currentPage = 0; //base 0
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		_childContainer = (ViewGroup) findViewById(R.id.sm_sliding_layout_parent);
		_pages = _childContainer.getChildCount();
		sizeChildViews();
		
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			//TC: these values assume a 3 panel layout
			_currentPage = 1;
			scrollTo(_childContainer.getChildAt(0).getMeasuredWidth(), 0);
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
	}
	
	/**
	 * Size child views so that they take up a portion of the available space.
	 */
    protected void sizeChildViews() {
    	final WindowManager w = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		final Display m = w.getDefaultDisplay();
		final DisplayMetrics dm = new DisplayMetrics();
		m.getMetrics(dm);
		
    	LinearLayout leftLayout = (LinearLayout) findViewById(R.id.sm_left_layout);
    	leftLayout.setMinimumHeight(m.getHeight());
    	leftLayout.setMinimumWidth(getSideLayoutWidth());

    	LinearLayout centerLayout = (LinearLayout) findViewById(R.id.sm_center_layout);
    	leftLayout.setMinimumHeight(m.getHeight());
    	centerLayout.setMinimumWidth((int) (m.getWidth()));
    	
    	LinearLayout rightLayout = (LinearLayout) findViewById(R.id.sm_right_layout);
    	rightLayout.setMinimumWidth(getSideLayoutWidth());

    }
    
    public int getSideLayoutWidth() {
    	if (_sideLayoutWidth < 0) {
    	final WindowManager w = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		final Display m = w.getDefaultDisplay();
		final DisplayMetrics dm = new DisplayMetrics();
		m.getMetrics(dm);
    	_sideLayoutWidth = (int) (m.getWidth() * LEFT_LAYOUT_PERCENT_OF_SCREEN);
    	}
    	return _sideLayoutWidth;
    }
	
	/**
	 * does the effect "back a page".
	 * If we are at the first page it will peform a {@link next()} action.
	 */
	public void previous(boolean smoothEnabled) {
		int scrollAmount = 0;
		if (_currentPage == 0) {
			//we're at min
			next(smoothEnabled); ///bounce
			return;
		}
		
		//delegate callback
		if (getDelegate() != null) {
			if (_currentPage == 1) {
				//we are about to move to the first page
				getDelegate().willShowLeftLayout();
			} else if (_currentPage == _pages - 1 ) {
				//we are moving to the center page
				getDelegate().willHideRightLayout();
			}
		}
		
		scrollAmount = getScrollX() - _childContainer.getChildAt(_currentPage).getMeasuredWidth();
		if (smoothEnabled) {
			this.smoothScrollTo(scrollAmount, 0);
		} else {
			this.scrollTo(scrollAmount, 0);
		}
		
		_currentPage -= 1;
	}

	/**
	 * does the effect "forward a page"	 
	 * If we are at the last page it will peform a {@link previous()} action.
	 */
	public void next(boolean smoothEnabled) {
		int scrollAmount = 0;
		if (_currentPage == _pages - 1) {
			//we're at max
			previous(smoothEnabled); ///bounce
			return;
		}
		
		//delegate callback
		if (getDelegate() != null) {
			if (_currentPage == _pages - 2) {
				//we are about to move to the last page
				getDelegate().willShowRightLayout();
			} else if (_currentPage == 0 ) {
				//we are moving to the center page
				getDelegate().willHideLeftLayout();
			}
		}
		
		scrollAmount = getScrollX() + _childContainer.getChildAt(_currentPage).getMeasuredWidth();
		if (smoothEnabled) {
			this.smoothScrollTo(scrollAmount, 0);
		} else {
			this.scrollTo(scrollAmount, 0);
		}
		
		_currentPage += 1;
	}
	
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
	
}
