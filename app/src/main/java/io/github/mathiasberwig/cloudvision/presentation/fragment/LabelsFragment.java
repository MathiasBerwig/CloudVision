package io.github.mathiasberwig.cloudvision.presentation.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.controller.service.CloudVisionUploader;
import io.github.mathiasberwig.cloudvision.data.model.LabelInfo;
import io.github.mathiasberwig.cloudvision.presentation.activity.MainActivity;
import io.github.mathiasberwig.cloudvision.presentation.adapter.LabelsAdapter;

public class LabelsFragment extends Fragment {
    private static final String TAG = LabelsFragment.class.getName();

    private List<LabelInfo> labelsInfo;

    public static LabelsFragment newInstance() {
        return new LabelsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get the extras from MainActivity
        Bundle extras = ((MainActivity) getActivity()).getExtras();
        labelsInfo = extras.getParcelableArrayList(CloudVisionUploader.EXTRA_RESULT_LABELS);

        return inflater.inflate(R.layout.fragment_labels, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup the RecyclerView
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_labels);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Setup the RecyclerView Adapter
        RecyclerView.Adapter mAdapter = new LabelsAdapter(getString(R.string.hint_label), labelsInfo);
        mRecyclerView.setAdapter(mAdapter);
//
        // Register the RecyclerView
//        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView);
    }
}