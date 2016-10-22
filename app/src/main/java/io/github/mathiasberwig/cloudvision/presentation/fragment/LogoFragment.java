package io.github.mathiasberwig.cloudvision.presentation.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.controller.service.CloudVisionUploader;
import io.github.mathiasberwig.cloudvision.data.model.LogoInfo;
import io.github.mathiasberwig.cloudvision.presentation.activity.MainActivity;
import io.github.mathiasberwig.cloudvision.presentation.adapter.LogoAdapter;


public class LogoFragment extends Fragment {
    private static final String TAG = LogoFragment.class.getName();

    private LogoInfo logosInfo;

    public static LogoFragment newInstance() {
        return new LogoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get the extras from MainActivity
        Bundle extras = ((MainActivity) getActivity()).getExtras();
        logosInfo = extras.getParcelable(CloudVisionUploader.EXTRA_RESULT_LOGO);

        return inflater.inflate(R.layout.fragment_logo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup the RecyclerView
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_logos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Setup the RecyclerView Adapter
        RecyclerView.Adapter mAdapter = new RecyclerViewMaterialAdapter(new LogoAdapter(LogoFragment.this, logosInfo));
        mRecyclerView.setAdapter(mAdapter);

        // Register the RecyclerView
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView);
    }
}
