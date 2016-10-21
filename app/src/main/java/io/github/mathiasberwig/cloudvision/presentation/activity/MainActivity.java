package io.github.mathiasberwig.cloudvision.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerTitleStrip;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.florent37.materialviewpager.MaterialViewPager;

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

    private MaterialViewPager viewPager;

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
        setupToolbar();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Find all views of Activity's layout.
     */
    private void findViews() {
        viewPager = (MaterialViewPager) findViewById(R.id.view_pager);
    }

    /**
     * Set's the adapter, header image and {@link PagerTitleStrip} (with custom typeface) of
     * {@link #viewPager}.
     */
    private void setupViewPager() {
        FragmentPageAdapter fragAdapter = new FragmentPageAdapter(getSupportFragmentManager(), this, getExtras());
        viewPager.getViewPager().setAdapter(fragAdapter);
        viewPager.setImageDrawable(new BitmapDrawable(getResources(), getHeaderImage()), 500);
        viewPager.getViewPager().setOffscreenPageLimit(viewPager.getViewPager().getAdapter().getCount());
        viewPager.getPagerTitleStrip().setViewPager(viewPager.getViewPager());

        // Set custom typeface
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");
        viewPager.getPagerTitleStrip().setTypeface(typeface, Typeface.BOLD);
    }

    /**
     * Gets the {@link Toolbar} and {@link ActionBar} then configures it.
     */
    private void setupToolbar() {
        final Toolbar toolbar = viewPager.getToolbar();

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar == null) return;
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }
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
