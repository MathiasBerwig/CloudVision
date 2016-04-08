package io.github.mathiasberwig.cloudvision.presentation.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.gordonwong.materialsheetfab.MaterialSheetFab;

import java.io.File;

import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.controller.PermissionUtils;
import io.github.mathiasberwig.cloudvision.presentation.custom_view.FAB;
import io.github.mathiasberwig.cloudvision.presentation.fragment.LoadingFragment;
import io.github.mathiasberwig.cloudvision.presentation.fragment.SelectImageFragment;

public class SelectImageActivity extends AppCompatActivity {
    private static final String TAG = SelectImageActivity.class.getName();

    private static final int GALLERY_IMAGE_REQUEST = 1;
    private static final int CAMERA_PERMISSIONS_REQUEST = 2;
    private static final int CAMERA_IMAGE_REQUEST = 3;

    // UI Components
    private MaterialSheetFab materialSheetFab;
    private MenuItem menuSettings;

    public static final String FILE_NAME = "cloud_vision.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);

        setupToolbar();
        setupFAB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_image, menu);

        // Get the Action Settings menu item, so we can show/hide it after
        menuSettings = menu.findItem(R.id.action_settings);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            super.onBackPressed();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
            startCamera(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // TODO: Send image to GCM
        }
        else if(requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            // TODO: Send image to GCM
        }
    }

    /**
     * Gets the {@link Toolbar} view and sets the {@code ActionBar}.
     */
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Gets the {@link R.id#fab}, {@link R.id#fab_sheet} and {@link R.id#dim_overlay}. Initializes
     * and set event listener for {@link #materialSheetFab}.
     */
    private void setupFAB() {
        FAB fab = (FAB) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.dim_overlay);
        int sheetColor = getResources().getColor(R.color.cardview_light_background);
        int fabColor = getResources().getColor(R.color.colorAccent);
        assert fab != null && sheetView != null && overlay != null;

        // Initialize material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);
    }

    /**
     * Change the layout params of the window, turning it full screen or not; toggle visibility of
     * FAB and replaces the current fragment.
     *
     * @param isLoading {@code true} to set activity full screen, hide the FAB and load the
     * {@link LoadingFragment}; {@code false} to remove activity's full screen flag, show FAB and
     * load the {@link SelectImageFragment}.
     */
    private void toggleLoading(boolean isLoading) {

        // Create the transaction to replace fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Toggle visibility of system status bar and FAB then replaces the fragment
        if (isLoading) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            materialSheetFab.hideSheetThenFab();

            LoadingFragment loadingFragment = LoadingFragment.newInstance();
            transaction.replace(R.id.fragment_container, loadingFragment);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            materialSheetFab.showFab();

            SelectImageFragment selectImageFragment = SelectImageFragment.newInstance();
            transaction.replace(R.id.fragment_container, selectImageFragment);
        }

        // Toggle visibility of Settings menu item
        menuSettings.setVisible(!isLoading);

        transaction.commit();
    }

    /**
     * Creates an intent with {@link Intent#ACTION_GET_CONTENT} to select a photo. Starts the intent
     * with {@code startActivityForResult}.
     *
     * @param view Mandatory to use with {@code onClick} event.
     */
    public void startGalleryChooser(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a photo"), GALLERY_IMAGE_REQUEST);
    }

    /**
     * Request permission to use camera and creates an intent with {@link MediaStore#ACTION_IMAGE_CAPTURE}
     * to take a photo. Starts the intent with {@code startActivityForResult}.
     *
     * @param view Mandatory to use with {@code onClick} event.
     */
    public void startCamera(View view) {
        if (PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    /**
     * Gets a new file to store the image taken with {@link #startCamera(View)} method.
     *
     * @return new file on {@link Environment#DIRECTORY_PICTURES} with {@link #FILE_NAME}.
     */
    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }
}
