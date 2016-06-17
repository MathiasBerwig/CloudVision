package io.github.mathiasberwig.cloudvision.presentation.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.presentation.fragment.LabelsFragment;
import io.github.mathiasberwig.cloudvision.presentation.fragment.LandmarkFragment;
import io.github.mathiasberwig.cloudvision.presentation.fragment.LogoFragment;

/**
 * Fragment Page Adapter that holds MainActivity fragment pages. <p/>
 *
 * Created by MathiasBerwig on 29/03/16.
 */
public class FragmentPageAdapter extends FragmentStatePagerAdapter {

    private Fragment[] fragments;
    private Context context;

    public FragmentPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.fragments = new Fragment[3];
        this.context = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return context.getString(R.string.title_tab_labels);
            case 1: return context.getString(R.string.title_tab_logos);
            case 2: return context.getString(R.string.title_tab_landmarks);
            default: return context.getString(R.string.title_tab_default);
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return LabelsFragment.newInstance();
            case 1: return LogoFragment.newInstance();
            case 2: return LandmarkFragment.newInstance();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
