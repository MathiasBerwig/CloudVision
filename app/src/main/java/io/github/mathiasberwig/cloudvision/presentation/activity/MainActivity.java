package io.github.mathiasberwig.cloudvision.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerTitleStrip;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;

import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.controller.service.CloudVisionUploader;
import io.github.mathiasberwig.cloudvision.presentation.adapter.FragmentPageAdapter;

/**
 * Main Activity of application. Handles the ViewPager and respective fragments. <br>
 * Must be started with a {@link Intent} containing the extra {@link CloudVisionUploader#EXTRA_IMAGE_URI}.
 * {@link CloudVisionUploader#lastResponse} can't be null (the class {@link BatchAnnotateImagesResponse}
 * isn't serializable/parcelable, so it can't be passed as an extra).
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private MaterialViewPager viewPager;

    /**
     * Factory method to create a new MainActivity instance.
     *
     * @param context Context to be used to start the activity.
     */
    public static void newInstance(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Find all views of Activity's layout.
     */
    private void findViews() {
        viewPager = (MaterialViewPager) findViewById(R.id.view_pager);
    }

    /**
     * Set's the adapter, header image and {@link PagerTitleStrip} of {@link #viewPager}.
     */
    private void setupViewPager() {
        viewPager.getViewPager().setAdapter(new FragmentPageAdapter(getSupportFragmentManager(), this));
        viewPager.setImageDrawable(new BitmapDrawable(getResources(), CloudVisionUploader.sentImage), 500);
        viewPager.getViewPager().setOffscreenPageLimit(viewPager.getViewPager().getAdapter().getCount());
        viewPager.getPagerTitleStrip().setViewPager(viewPager.getViewPager());
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
}
