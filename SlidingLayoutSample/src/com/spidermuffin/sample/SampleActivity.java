package com.spidermuffin.sample;

import android.support.v4.app.Fragment;

import com.spidermuffin.controller.SlidingLayoutActivity;

public class SampleActivity extends SlidingLayoutActivity {

	@Override
	protected Fragment getLeftFragment() {
		return new LeftFragment();
	}

	@Override
	protected Fragment getRightFragment() {
		return new RightFragment();
	}

	@Override
	protected Fragment getCenterFragment() {
		return new CenterFragment();
	}

	@Override
	protected Fragment getCenterHeaderFragment() {
		return null;
	}

}
