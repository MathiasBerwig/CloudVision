package io.github.mathiasberwig.cloudvision.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.gigamole.navigationtabstrip.NavigationTabStrip;

import java.io.IOException;

import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.controller.service.CloudVisionUploader;
import io.github.mathiasberwig.cloudvision.controller.service.RestApisConsumer;
import io.github.mathiasberwig.cloudvision.presentation.adapter.FragmentPageAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Main Activity of application. Handles the ViewPager and respective fragments.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private ViewPager viewPager;
    private NavigationTabStrip tabs;
    private ImageView imageView;

    /**
     * Factory method to create a new MainActivity instance.
     *
     * @param context Context to be used to start the activity.
     */
    public static void newInstance(Context context, Bundle extras) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setupViewPager();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Find all views of Activity's layout.
     */
    private void findViews() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabs = (NavigationTabStrip) findViewById(R.id.tabs);
        imageView = (ImageView) findViewById(R.id.backdrop);
    }

    /**
     * Set's the adapter, header image and {@link PagerTitleStrip} (with custom typeface) of
     * {@link #viewPager}.
     */
    private void setupViewPager() {
        FragmentPageAdapter fragAdapter = new FragmentPageAdapter(getSupportFragmentManager(), this, getExtras());
        viewPager.setAdapter(fragAdapter);
        tabs.setViewPager(viewPager);

        // Set tab titles
        String[] titles = new String[fragAdapter.getCount()];
        for (int i = 0; i < fragAdapter.getCount(); i++)
            titles[i] = fragAdapter.getPageTitle(i).toString();
        tabs.setTitles(titles);

        imageView.setImageDrawable(new BitmapDrawable(getResources(), getHeaderImage()));
    }

    /**
     * Return data queried by {@link RestApisConsumer}
     * using annotations found by Google Cloud Vision API.
     *
     * @return Extras from {@link RestApisConsumer}
     */
    public Bundle getExtras() {
        return getIntent().getExtras();
    }

    /**
     * Gets the header image from {@link CloudVisionUploader#EXTRA_IMAGE_URI}.
     *
     * @return Loaded bitmap or {@code null}.
     */
    private Bitmap getHeaderImage() {
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                    (Uri) getExtras().getParcelable(CloudVisionUploader.EXTRA_IMAGE_URI));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }
}
