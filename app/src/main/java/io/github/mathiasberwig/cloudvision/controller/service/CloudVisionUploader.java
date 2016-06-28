package io.github.mathiasberwig.cloudvision.controller.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.data.model.LabelInfo;
import io.github.mathiasberwig.cloudvision.data.model.LandmarkInfo;
import io.github.mathiasberwig.cloudvision.data.model.LogoInfo;

/**
 * <p>IntentService that communicates with Google Cloud Vision API. It sends an image to the REST service
 * and gets a response with annotations about the image. </p>
 *
 * <p>To use this class you first need to set the {@link #CLOUD_VISION_API_KEY}.</p>
 *
 * <p>There is a default initializer to create a new Intent and start this IntentService, but you
 * can personalize and create your custom call just passing the extras:
 * <li>{@link #EXTRA_LABEL_DETECTION}</li>
 * <li>{@link #EXTRA_LOGO_DETECTION}</li>
 * <li>{@link #EXTRA_LANDMARK_DETECTION}</li>
 * <li>{@link #EXTRA_IMAGE_PROPERTIES}</li>
 * <li>{@link #EXTRA_MAX_LABELS}</li>
 * <li>{@link #EXTRA_MAX_LOGOS}</li>
 * <li>{@link #EXTRA_MAX_LANDMARKS}</li>
 * <li>{@link #EXTRA_MAX_IMAGE_PROPERTIES}</li>
 * <li>{@link #EXTRA_IMAGE_URI}</li>
 * <li>{@link #EXTRA_IMAGE_QUALITY}</li></p>
 *
 * <p>The results of the query are sent as extras ({@link #EXTRA_RESULT_ERROR},
 * {@link #EXTRA_RESULT_LABELS}, {@link #EXTRA_RESULT_LOGO}, {@link #EXTRA_RESULT_LANDMARK}) and
 * broadcasted with the action {@link #ACTION_DONE}.</p>
 */
public class CloudVisionUploader extends IntentService {
    private static final String TAG = CloudVisionUploader.class.getName();

    private static String CLOUD_VISION_API_KEY;

    // Parameters Extras
    public static final String EXTRA_LABEL_DETECTION = "EXTRA_LABEL_DETECTION";
    public static final String EXTRA_LOGO_DETECTION = "EXTRA_LOGO_DETECTION";
    public static final String EXTRA_LANDMARK_DETECTION = "EXTRA_LANDMARK_DETECTION";
    public static final String EXTRA_IMAGE_PROPERTIES = "EXTRA_IMAGE_PROPERTIES";
    public static final String EXTRA_MAX_LABELS = "pref_max_labels";
    public static final String EXTRA_MAX_LOGOS = "EXTRA_MAX_LOGOS";
    public static final String EXTRA_MAX_LANDMARKS = "EXTRA_MAX_LANDMARKS";
    public static final String EXTRA_MAX_IMAGE_PROPERTIES = "EXTRA_MAX_IMAGE_PROPERTIES";
    public static final String EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI";
    public static final String EXTRA_IMAGE_QUALITY = "pref_image_quality";

    // Response Extras
    /**
     * Extra that stores a string error message, if it was trowed.
     */
    public static final String EXTRA_RESULT_ERROR = "EXTRA_RESULT_ERROR";

    /**
     * Extra that stores a parcelable array list of {@link LabelInfo}.
     */
    public static final String EXTRA_RESULT_LABELS = "EXTRA_RESULT_LABELS";

    /**
     * Extra that stores a parcelable array list of {@link LogoInfo}
     */
    public static final String EXTRA_RESULT_LOGO = "EXTRA_RESULT_LOGO";

    /**
     * Extra that stores a parcelable {@link LandmarkInfo}.
     */
    public static final String EXTRA_RESULT_LANDMARK = "EXTRA_RESULT_LANDMARK";

    // Default values
    private static final int DEFAULT_MAX_LABELS = 5;
    private static final int DEFAULT_MAX_LOGOS = 5;
    private static final int DEFAULT_MAX_LANDMARKS = 1;
    private static final int DEFAULT_MAX_IMAGE_PROPERTIES = 5;
    private static final int DEFAULT_IMAGE_QUALITY = 75;

    public static final String ACTION_DONE = "io.github.mathiasberwig.cloudvision.controller.service.CloudVisionUploader.ACTION_DONE";

    public CloudVisionUploader() {
        super(TAG);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void start(Context context, Uri image) {
        Intent intent = new Intent(context, CloudVisionUploader.class);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        intent.putExtra(EXTRA_IMAGE_URI, image);
        intent.putExtra(EXTRA_IMAGE_QUALITY, sp.getInt(EXTRA_IMAGE_QUALITY, DEFAULT_IMAGE_QUALITY));
        intent.putExtra(EXTRA_LABEL_DETECTION, sp.getBoolean(EXTRA_LABEL_DETECTION, true));
        intent.putExtra(EXTRA_LOGO_DETECTION, sp.getBoolean(EXTRA_LOGO_DETECTION, true));
        intent.putExtra(EXTRA_LANDMARK_DETECTION, sp.getBoolean(EXTRA_LANDMARK_DETECTION, true));
        intent.putExtra(EXTRA_IMAGE_PROPERTIES, sp.getBoolean(EXTRA_IMAGE_PROPERTIES, false));
        intent.putExtra(EXTRA_MAX_LABELS, sp.getInt(EXTRA_MAX_LABELS, DEFAULT_MAX_LABELS));
        intent.putExtra(EXTRA_MAX_LOGOS, sp.getInt(EXTRA_MAX_LOGOS, DEFAULT_MAX_LOGOS));
        intent.putExtra(EXTRA_MAX_LANDMARKS, sp.getInt(EXTRA_MAX_LANDMARKS, DEFAULT_MAX_LANDMARKS));

        // Set the Cloud Vision API Key from resources
        CLOUD_VISION_API_KEY = context.getString(R.string.google_apis_key);

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getExtras() == null || CLOUD_VISION_API_KEY == null) {
            Log.e(TAG, "onHandleIntent: intent.getExtras() or CLOUD_VISION_API_KEY is null");
            return;
        }

        // Get the parameters to call Cloud Vision
        final Bundle options = intent.getExtras();

        // Creates the result Intent that will be sent as broadcast
        final Intent result = new Intent(ACTION_DONE);

        // Copy image URI from received intent to the result intent (we might need it on another moment)
        result.putExtra(EXTRA_IMAGE_URI, options.getParcelable(EXTRA_IMAGE_URI));

        Bitmap bitmap;

        try {
            // Get the Bitmap from intent
            final Uri imageUri = options.getParcelable(EXTRA_IMAGE_URI);
            if (imageUri == null) throw new FileNotFoundException();

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
            bmpOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, bmpOptions);

            // Find the correct scale value (It should be the power of 2) then decode bitmap with
            // inSampleSize set
            bmpOptions.inSampleSize = calculateInSampleSize(bmpOptions, 1000, 1000);
            bmpOptions.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, bmpOptions);

            // Add the image
            final Image base64EncodedImage = new Image();

            // Get the image compression quality parameter
            final int imageQuality = options.getInt(EXTRA_IMAGE_QUALITY, DEFAULT_IMAGE_QUALITY);

            // Convert the bitmap to a JPEG
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, byteArrayOutputStream);
            bitmap.recycle();

            // Write the compressed image back to file
            writeToFile(byteArrayOutputStream, imageUri);

            // Convert the output stream to a byte array
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);

            // Setup HttpTransport and Serialization Factory
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

            // Setup Vision instance
            Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
            builder.setVisionRequestInitializer(new VisionRequestInitializer(CLOUD_VISION_API_KEY));
            Vision vision = builder.build();

            // Setup the Request
            BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
            batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{

                AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
                annotateImageRequest.setImage(base64EncodedImage);

                // add the features we want
                annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                    // Label Detection
                    if (options.getBoolean(EXTRA_LABEL_DETECTION)) {
                        final int maxLabels = options.getInt(EXTRA_MAX_LABELS, DEFAULT_MAX_LABELS);
                        Feature labelDetection = new Feature();
                        labelDetection.setType("LABEL_DETECTION");
                        labelDetection.setMaxResults(maxLabels);
                        add(labelDetection);
                    }

                    // Logo Detection
                    if (options.getBoolean(EXTRA_LOGO_DETECTION)) {
                        final int maxLogos = options.getInt(EXTRA_MAX_LOGOS, DEFAULT_MAX_LOGOS);
                        Feature logoDetection = new Feature();
                        logoDetection.setType("LOGO_DETECTION");
                        logoDetection.setMaxResults(maxLogos);
                        add(logoDetection);
                    }

                    // Landmark Detection
                    if (options.getBoolean(EXTRA_LANDMARK_DETECTION)) {
                        final int maxLandmarks = options.getInt(EXTRA_MAX_LANDMARKS, DEFAULT_MAX_LANDMARKS);
                        Feature landmarkDetection = new Feature();
                        landmarkDetection.setType("LANDMARK_DETECTION");
                        landmarkDetection.setMaxResults(maxLandmarks);
                        add(landmarkDetection);
                    }

                    // Image Properties
                    if (options.getBoolean(EXTRA_IMAGE_PROPERTIES)) {
                        final int maxImageProperties = options.getInt(EXTRA_MAX_IMAGE_PROPERTIES, DEFAULT_MAX_IMAGE_PROPERTIES);
                        Feature propertiesDetection = new Feature();
                        propertiesDetection.setType("IMAGE_PROPERTIES");
                        propertiesDetection.setMaxResults(maxImageProperties);
                        add(propertiesDetection);
                    }
                }});

                // Add the list of detections to the request
                add(annotateImageRequest);
            }});

            // Here we create the request
            Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);

            // Due to a bug: requests to Vision API containing large images fail when GZipped.
            annotateRequest.setDisableGZipContent(true);

            Log.d(TAG, "created Cloud Vision request object, sending request: " + batchAnnotateImagesRequest.toPrettyString());

            final List<AnnotateImageResponse> responses = annotateRequest.execute().getResponses();
            if (responses != null && !responses.isEmpty()) {
                // As we are sending just one image, the CV API will always return 1 response (or none).
                final AnnotateImageResponse response = responses.get(0);

                // Prepare the extras with info about the image
                prepareExtras(result, response);

                Log.d(TAG, "CloudVision Response: \n" + response.toPrettyString());
            }

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Image picking failed because " + e.getMessage());
            result.putExtra(EXTRA_RESULT_ERROR, getString(R.string.image_picker_error));
        } catch (GoogleJsonResponseException e) {
            Log.e(TAG, "failed to make API request because " + e.getContent());
            result.putExtra(EXTRA_RESULT_ERROR, getString(R.string.api_request_error));
        } catch (IOException e) {
            Log.e(TAG, "failed to make API request because of other IOException " + e.getMessage());
            result.putExtra(EXTRA_RESULT_ERROR, getString(R.string.api_request_error));
        }

        // Broadcast the result
        LocalBroadcastManager.getInstance(this).sendBroadcast(result);
    }

    /**
     * Check the {@code AnnotateImageResponse} and creates a list of {@link LabelInfo} and a
     * {@link LandmarkInfo}, then store it on the {@code extras}.
     *
     * @param intent The intent where the extras will be stored.
     * @param response The Google Cloud Vision response.
     */
    private void prepareExtras(Intent intent, AnnotateImageResponse response) {
        if (response == null) return;

        // Get the Labels Annotations
        final List<EntityAnnotation> labelsAnnotations = response.getLabelAnnotations();

        // Check if any label was detected in the image then store it on the extras
        if (labelsAnnotations != null && !labelsAnnotations.isEmpty()) {
            ArrayList<LabelInfo> labelsInfo = LabelInfo.createListFromAnnotations(labelsAnnotations);
            intent.putExtra(EXTRA_RESULT_LABELS, labelsInfo);
        }

        // Get the Logos Annotations
        final List<EntityAnnotation> logosAnnotations = response.getLogoAnnotations();

        // Check if any logo was detected in the image then store it on the extras
        if (logosAnnotations != null && !logosAnnotations.isEmpty()) {
            LogoInfo logoInfo = LogoInfo.createFromAnnotation(logosAnnotations.get(0));
            intent.putExtra(EXTRA_RESULT_LOGO, logoInfo);
        }

        // Get the Landmark Annotations
        final List<EntityAnnotation> landmarkAnnotations = response.getLandmarkAnnotations();

        // Check if any landmark was found in the image then store it on the extras
        if (landmarkAnnotations != null && !landmarkAnnotations.isEmpty()) {
            LandmarkInfo landmarkInfo = LandmarkInfo.createFromAnnotation(landmarkAnnotations.get(0));
            intent.putExtra(EXTRA_RESULT_LANDMARK, landmarkInfo);
        }
    }

    /**
     * Write a {@link ByteArrayOutputStream} to a {@link java.io.File} from an {@link Uri}.
     *
     * @param byteArrayOutputStream The data that will be write to file.
     * @param outputFile The Uri to the output file.
     * @throws IOException
     */
    private void writeToFile(ByteArrayOutputStream byteArrayOutputStream, Uri outputFile) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = getContentResolver().openOutputStream(outputFile);
            byteArrayOutputStream.writeTo(outputStream);
        }
        finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    /**
     * Gets the height and width of the image from {@code options} then calculate the largest
     * inSampleSize value that is a power of 2 and keeps both height and width larger than the
     * requested height and width.
     *
     * @see <a href="http://developer.android.com/intl/pt-br/training/displaying-bitmaps/load-bitmap.html#load-bitmap">
     *     Android Developers: Loading Large Bitmaps Efficiently</a>
     * @param options Options that will be used to decode the image.
     * @param reqWidth The requested width.
     * @param reqHeight The requested height.
     * @return The best scale value (power of 2).
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}