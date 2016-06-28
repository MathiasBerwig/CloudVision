package io.github.mathiasberwig.cloudvision.presentation.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

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
}
