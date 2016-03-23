package io.github.mathiasberwig.cloudvision.controller;

import android.graphics.Bitmap;

/**
 * Helper class to process bitmaps. <p/>
 * Created by mathias.berwig on 22/03/2016.
 */
public class BitmapUtils {

    /**
     * Scale the bitmap down so that it's largest dimension is {@code targetMaxDimension}.
     * If {@code bitmap} is smaller than this, then it is returned. <br>
     * Source: <a href="https://android.googlesource.com/platform/frameworks/support/+/7aa3688/v7/palette/src/android/support/v7/graphics/Palette.java">android.support.v7.graphics</a>
     */
    public static Bitmap scaleBitmapDown(Bitmap bitmap, final int targetMaxDimension) {
        final int maxDimension = Math.max(bitmap.getWidth(), bitmap.getHeight());
        if (maxDimension <= targetMaxDimension) {
            // If the bitmap is small enough already, just return it
            return bitmap;
        }
        final float scaleRatio = targetMaxDimension / (float) maxDimension;
        return Bitmap.createScaledBitmap(bitmap,
                Math.round(bitmap.getWidth() * scaleRatio),
                Math.round(bitmap.getHeight() * scaleRatio),
                false);
    }
}
