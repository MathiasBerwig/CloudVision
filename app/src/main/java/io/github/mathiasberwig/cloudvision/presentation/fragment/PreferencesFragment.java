package io.github.mathiasberwig.cloudvision.presentation.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.mathiasberwig.cloudvision.R;

/**
 * The fragment that handles all preferences from {@link R.xml#preferences}.
 *
 * Created by mathias.berwig on 27/06/2016.
 */
public class PreferencesFragment extends PreferenceFragment {
    private static final String TAG = PreferencesFragment.class.getName();

    public static PreferencesFragment newInstance() {
        return new PreferencesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        // This prevents transparent background
        if (view != null) {
            TypedValue background = new TypedValue();
            getActivity().getTheme().resolveAttribute(android.R.attr.windowBackground, background, true);
            if (background.type >= TypedValue.TYPE_FIRST_COLOR_INT && background.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                // windowBackground is a color
                view.setBackgroundColor(background.data);
            } else {
                // windowBackground is not a color, probably a drawable
                view.setBackground(getActivity().getResources().getDrawable(background.resourceId));
            }
        }
        return view;
    }
}
