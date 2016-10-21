package io.github.mathiasberwig.cloudvision.presentation.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import io.github.mathiasberwig.cloudvision.CloudVision;
import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.controller.service.CloudVisionUploader;
import io.github.mathiasberwig.cloudvision.presentation.fragment.LabelsFragment;
import io.github.mathiasberwig.cloudvision.presentation.fragment.LandmarkFragment;
import io.github.mathiasberwig.cloudvision.presentation.fragment.LogoFragment;

/**
 * Fragment Page Adapter that holds MainActivity fragment pages. <p/>
 *
 * Created by MathiasBerwig on 29/03/16.
 */
public class FragmentPageAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;
    private Context context;

    public FragmentPageAdapter(FragmentManager fm, Context context, Bundle extras) {
        super(fm);
        this.fragments = new ArrayList<>();
        this.context = context;

        if (extras.containsKey(CloudVisionUploader.EXTRA_RESULT_LABELS))
            fragments.add(LabelsFragment.newInstance());

        if (extras.containsKey(CloudVisionUploader.EXTRA_RESULT_LANDMARK))
            fragments.add(LandmarkFragment.newInstance());

        if (extras.containsKey(CloudVisionUploader.EXTRA_RESULT_LOGO))
            fragments.add(LogoFragment.newInstance());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Fragment fragment = fragments.get(position);

        // This code could be better. Sorry if you are reading this.
        if (fragment instanceof  LabelsFragment) return context.getString(R.string.title_tab_labels);
        if (fragment instanceof LogoFragment) return context.getString(R.string.title_tab_logos);
        if (fragment instanceof  LandmarkFragment) return context.getString(R.string.title_tab_landmarks);
        return context.getString(R.string.title_tab_default);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
