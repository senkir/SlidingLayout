package com.spidermuffin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class SlidingLayout extends ViewGroup {

	int _paddingLeft, _paddingTop, _paddingRight, _paddingBottom;
	
	public SlidingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//custom attributes go here if i ever need them
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = 0;
		int height = getPaddingTop();
		
		int currentWidth = getPaddingLeft();
		int currentHeight = 0;
		
		int count = getChildCount();
		
		// Find out how big everyone wants to be
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		
		// Find rightmost and bottom-most child
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if (child.getVisibility() != GONE) {

				LayoutParams lp = (LayoutParams) child.getLayoutParams();
				lp.x = currentWidth;
				lp.y = height;

				currentWidth += child.getMeasuredWidth();
				currentHeight = Math.max(currentHeight, child.getMeasuredHeight());
				
			}
		}
		
		width = currentWidth + getPaddingRight();
		height += currentHeight + getPaddingBottom();

		setMeasuredDimension(resolveSize(width + 1000, widthMeasureSpec),
				resolveSize(height, heightMeasureSpec));
	}
	
	/**
	 * LargecontentAbsoluteLayout will always position views horizontally
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

	    int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
		}
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof LayoutParams;
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LayoutParams(getContext(), attrs);
	}
	
	@Override
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new LayoutParams(p.width, p.height);
	}
	
	public static class LayoutParams extends ViewGroup.LayoutParams {
		int x;
		int y;

		public LayoutParams(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
		
		 /**
         * Creates a new set of layout parameters with the specified width,
         * height and location.
         *
         * @param width the width, either {@link #MATCH_PARENT},
                  {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param height the height, either {@link #MATCH_PARENT},
                  {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param x the X location of the child
         * @param y the Y location of the child
         */
        public LayoutParams(int width, int height, int x, int y) {
            super(width, height);
            this.x = x;
            this.y = y;
        }
        
		public LayoutParams(int w, int h) {
			super(w, h);
		}
	}
}
