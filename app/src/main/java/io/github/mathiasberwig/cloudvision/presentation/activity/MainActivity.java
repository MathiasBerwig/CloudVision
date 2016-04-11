package io.github.mathiasberwig.cloudvision.presentation.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerTitleStrip;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;

import java.io.FileNotFoundException;
import java.io.InputStream;

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
    private Drawable headerImage;

    /**
     * Factory method to create a new MainActivity instance.
     *
     * @param context Context to be used to start the activity.
     * @param imageUri {@link CloudVisionUploader#EXTRA_IMAGE_URI}.
     */
    public static void newInstance(Context context, Uri imageUri) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(CloudVisionUploader.EXTRA_IMAGE_URI, imageUri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        loadHeaderImage(savedInstanceState);
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
     * Set's the adapter, {@link #headerImage} and {@link PagerTitleStrip} of {@link #viewPager}.
     */
    private void setupViewPager() {
        viewPager.getViewPager().setAdapter(new FragmentPageAdapter(getSupportFragmentManager(), this));
        viewPager.setImageDrawable(headerImage, 500);
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

    /**
     * Loads the {@link #headerImage} from {@link CloudVisionUploader#EXTRA_IMAGE_URI}, obtained from
     * {@code Intent} or {@code savedInstanceState} and sets it as {@link #viewPager}'s header image.
     *
     * @param savedInstanceState In case of app is being restored.
     */
    private void loadHeaderImage(Bundle savedInstanceState) {
        // Load the ImageUri from Intent or Saved State
        Uri imageUri = getIntent().getParcelableExtra(CloudVisionUploader.EXTRA_IMAGE_URI);
        if (imageUri == null) {
            imageUri = savedInstanceState.getParcelable(CloudVisionUploader.EXTRA_IMAGE_URI);
        }

        // Load Drawable from Uri
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            headerImage = Drawable.createFromStream(inputStream, imageUri.toString());
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(MainActivity.this, R.string.header_image_load_error, Toast.LENGTH_SHORT).show();
        }
    }
}
