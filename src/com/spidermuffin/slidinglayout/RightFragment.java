package com.spidermuffin.slidinglayout;
import com.spidermuffin.slidinglayout.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Sample Right Hand fragment for {@link SlidingLayoutActivity}
 * @author travischurchill
 *
 */
public class RightFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.sm_right_base_layout, container, false);
	}
}
