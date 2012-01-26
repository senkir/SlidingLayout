package com.spidermuffin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class SlidingLayout extends HorizontalScrollView {

	int _pages;
	
	public SlidingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//custom attributes go here if i ever need them
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
	}
	
	/**
	 * does the effect "back a page"
	 */
	public void previous(boolean smoothEnabled) {
		if (smoothEnabled) {
			this.smoothScrollTo(0, 0);
		} else {
			this.scrollTo(0, 0);
		}
	}

	/**
	 * does the effect "forward a page"
	 */
	public void next(boolean smoothEnabled) {
		int pageWidth = this.getWidth();
		if (smoothEnabled) {
			this.smoothScrollTo(pageWidth, 0);
		} else {
			this.scrollTo(pageWidth, 0);
		}
	}
	
}
