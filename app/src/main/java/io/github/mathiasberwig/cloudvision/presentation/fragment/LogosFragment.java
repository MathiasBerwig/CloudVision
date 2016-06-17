package io.github.mathiasberwig.cloudvision.presentation.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;

import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.controller.service.CloudVisionUploader;
import io.github.mathiasberwig.cloudvision.data.model.LogoInfo;
import io.github.mathiasberwig.cloudvision.presentation.activity.MainActivity;
import io.github.mathiasberwig.cloudvision.presentation.adapter.LogosAdapter;


public class LogosFragment extends Fragment {
    private static final String TAG = LogosFragment.class.getName();

    private LogoInfo logosInfo;

    public static LogosFragment newInstance() {
        return new LogosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get the extras from MainActivity
        Bundle extras = ((MainActivity) getActivity()).getExtras();
        logosInfo = extras.getParcelable(CloudVisionUploader.EXTRA_RESULT_LOGO);

        // Inflate the default layout or Empty View
        return inflater.inflate(logosInfo == null ?
                R.layout.fragment_view_pager_empty : R.layout.fragment_logos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check if we have valid info to show
        if (logosInfo == null) {
            setupEmptyView(view);
            return;
        }

        // Setup the RecyclerView
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_logos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Setup the RecyclerView Adapter
        RecyclerView.Adapter mAdapter = new RecyclerViewMaterialAdapter(new LogosAdapter(logosInfo));
        mRecyclerView.setAdapter(mAdapter);

        // Register the RecyclerView
        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    /**
     * Find all views used by the {@link R.layout#fragment_view_pager_empty} and setup it properly.
     *
     * @param view {@link R.layout#fragment_view_pager_empty} inflated.
     */
    private void setupEmptyView(View view) {
        TextView txtEmptyMessage = (TextView) view.findViewById(R.id.txt_empty_message);
        txtEmptyMessage.setText(R.string.logo_empty_message);
    }
}
