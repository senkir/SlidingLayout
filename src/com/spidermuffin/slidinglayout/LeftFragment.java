package com.spidermuffin.slidinglayout;

import com.spidermuffin.slidinglayout.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * Sample Left Hand fragment for {@link SlidingLayoutActivity}
 * @author travischurchill
 *
 */
public class LeftFragment extends Fragment {

	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.sm_left_base_layout, container, false);
	}
}
