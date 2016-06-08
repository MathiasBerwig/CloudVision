package io.github.mathiasberwig.cloudvision.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;

/**
 * Activity that shows a guide of how to use the app.
 */
public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        // Open SelectImageActivity
        Intent intent = new Intent(this, SelectImageActivity.class);
        startActivity(intent);

        // Finish this activity
        finish();
    }
}
