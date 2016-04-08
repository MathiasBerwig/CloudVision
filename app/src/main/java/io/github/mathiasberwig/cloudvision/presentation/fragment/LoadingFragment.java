package io.github.mathiasberwig.cloudvision.presentation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.mathiasberwig.cloudvision.R;

/**
 * A simple loading fragment. Contains a GIF and animated TextView to distract user.
 */
public class LoadingFragment extends Fragment {
    private static final String TAG = LoadingFragment.class.getName();

    // UI Components
    private HTextView textLoading;

    /**
     * List of distracting phrases that are shown during the loading process.
     */
    private List<String> subtitles;

    /**
     * Interval for text loading animation.
     */
    private final static int TEXT_CHANGE_INTERVAL = 3500;

    /**
     * Set a new text to {@link #textLoading} and post a delayed message to {@link #textChangeHandler}
     * run it again.
     *
     * @see #TEXT_CHANGE_INTERVAL
     */
    private Runnable textChangeRunnable = new Runnable() {
        @Override
        public void run() {
            textLoading.setAnimateType(HTextViewType.SCALE);
            textLoading.animateText(getSubtitle());
            textChangeHandler.postDelayed(textChangeRunnable, TEXT_CHANGE_INTERVAL);
        }
    };

    /**
     * Handler that is used to delay calls to {@link #textChangeRunnable}.
     */
    private Handler textChangeHandler;

    public LoadingFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @return A new instance of fragment LoadingFragment.
     */
    public static LoadingFragment newInstance() {
        return new LoadingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews();
        setupAnimation();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        // Stop animation
        textChangeHandler.removeCallbacks(textChangeRunnable);

        super.onDetach();
    }

    /**
     * Find all views used by the fragment.
     */
    private void findViews() {
        View rootView = getView();

        // Just checking if the root view exists
        if (rootView == null) {
            Log.e(TAG, "Root view wasn't found in the fragment. Argh.");
            return;
        }

        textLoading = (HTextView) rootView.findViewById(R.id.text_loading);
    }

    /**
     * Create a new {@link Handler} and run the {@link #textChangeRunnable}.
     */
    private void setupAnimation() {

        // Create a handler and run the text updater runnable
        textChangeHandler = new Handler();
        textChangeRunnable.run();
    }

    /**
     * Loads the {@link R.array#loading_subtitles} from Resources, then shuffle it. Each call to
     * this method return a different String. When all items are read, the string array resource is
     * read and shuffled again.
     *
     * @return A random text of {@link R.array#loading_subtitles}
     */
    private String getSubtitle() {
        if (subtitles == null || subtitles.size() == 0) {
            // Get the subtitles from resources
            final String[] strings = getResources().getStringArray(R.array.loading_subtitles);
            // Creates a new list, because the one returned from Arrays.asList is read-only
            subtitles = new ArrayList<>(Arrays.asList(strings));
            // We want "random" subtitles
            Collections.shuffle(subtitles);
        }

        // Return the first item of shuffled list
        return subtitles.remove(0);
    }
}
