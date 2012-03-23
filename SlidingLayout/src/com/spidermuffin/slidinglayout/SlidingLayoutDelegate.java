/**
 * 
 * Copyright (c) 2012 Travis castillo
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
package com.spidermuffin.slidinglayout;

import android.view.View;


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
