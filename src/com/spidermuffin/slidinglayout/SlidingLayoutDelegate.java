package com.spidermuffin.slidinglayout;
/**
 * Objects which implement this will receive callbacks from the {@link SlidingLayout} to which they are a delegate.
 * @author travischurchill
 *
 */
public interface SlidingLayoutDelegate {
	public void willHideLeftLayout();
	public void willShowLeftLayout();
	public void willHideRightLayout();
	public void willShowRightLayout();
}
