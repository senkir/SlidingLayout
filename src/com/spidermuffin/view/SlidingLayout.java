package com.spidermuffin.view;

import com.test.slidinglayout.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

public class SlidingLayout extends HorizontalScrollView {

	int _pages;
	int _currentPage;
	ViewGroup _childContainer; //used for paging
	
	public SlidingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
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
		_childContainer = (ViewGroup) findViewById(R.id.sliding_layout_parent);
		_pages = _childContainer.getChildCount();
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
		
		scrollAmount = getScrollX() + _childContainer.getChildAt(_currentPage).getMeasuredWidth();
		if (smoothEnabled) {
			this.smoothScrollTo(scrollAmount, 0);
		} else {
			this.scrollTo(scrollAmount, 0);
		}
		
		_currentPage += 1;
	}
	
}
