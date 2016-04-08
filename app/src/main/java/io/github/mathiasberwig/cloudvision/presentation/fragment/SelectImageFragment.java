package io.github.mathiasberwig.cloudvision.presentation.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.presentation.activity.SelectImageActivity;

/**
 * Simple fragment containing a empty view.
 */
public class SelectImageFragment extends Fragment {
    private static final String TAG = SelectImageActivity.class.getName();

    public SelectImageFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @return A new instance of fragment LoadingFragment.
     */
    public static SelectImageFragment newInstance() {
        return new SelectImageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_image, container, false);
    }
}
