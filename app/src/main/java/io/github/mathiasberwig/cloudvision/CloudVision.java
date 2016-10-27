package io.github.mathiasberwig.cloudvision;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * The main application class. Just.
 *
 * Created by mathias on 21/06/16.
 */
public class CloudVision extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Config Calligrahy to use Lato font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Ubuntu-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
