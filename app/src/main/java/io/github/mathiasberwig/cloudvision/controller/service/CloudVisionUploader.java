package io.github.mathiasberwig.cloudvision.controller.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.mathiasberwig.cloudvision.R;
import io.github.mathiasberwig.cloudvision.controller.BitmapUtils;

public class CloudVisionUploader extends IntentService {
    private static final String TAG = CloudVisionUploader.class.getName();

    // TODO: Before you run your application, you need a Google Cloud Vision API key.
    private static final String CLOUD_VISION_API_KEY = "";

    public static final String EXTRA_LABEL_DETECTION = "EXTRA_LABEL_DETECTION";
    public static final String EXTRA_LOGO_DETECTION = "EXTRA_LOGO_DETECTION";
    public static final String EXTRA_LANDMARK_DETECTION = "EXTRA_LANDMARK_DETECTION";
    public static final String EXTRA_IMAGE_PROPERTIES = "EXTRA_IMAGE_PROPERTIES";
    public static final String EXTRA_MAX_LABELS = "EXTRA_MAX_LABELS";
    public static final String EXTRA_MAX_LOGOS = "EXTRA_MAX_LOGOS";
    public static final String EXTRA_MAX_LANDMARKS = "EXTRA_MAX_LANDMARKS";
    public static final String EXTRA_MAX_IMAGE_PROPERTIES = "EXTRA_MAX_IMAGE_PROPERTIES";
    public static final String EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI";
    public static final String EXTRA_IMAGE_RESIZE = "EXTRA_IMAGE_RESIZE";
    public static final String EXTRA_IMAGE_QUALITY = "EXTRA_IMAGE_QUALITY";
    public static final String EXTRA_RESULT_ERROR = "EXTRA_RESULT_ERROR";

    private static final int DEFAULT_MAX_LABELS = 5;
    private static final int DEFAULT_MAX_LOGOS = 5;
    private static final int DEFAULT_MAX_LANDMARKS = 5;
    private static final int DEFAULT_MAX_IMAGE_PROPERTIES = 5;
    private static final int DEFAULT_IMAGE_QUALITY = 75;

    public static final String ACTION_DONE = "io.github.mathiasberwig.cloudvision.controller.service.CloudVisionUploader.ACTION_DONE";

    public static AnnotateImageResponse lastResponse;
    public static Bitmap sentImage;

    public CloudVisionUploader() {
        super("CloudVisionUploader");
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
        intent.putExtra(EXTRA_IMAGE_RESIZE, sp.getBoolean(EXTRA_IMAGE_RESIZE, true));
        intent.putExtra(EXTRA_IMAGE_QUALITY, sp.getInt(EXTRA_IMAGE_QUALITY, DEFAULT_IMAGE_QUALITY));
        intent.putExtra(EXTRA_LABEL_DETECTION, sp.getBoolean(EXTRA_LABEL_DETECTION, true));
        intent.putExtra(EXTRA_LOGO_DETECTION, sp.getBoolean(EXTRA_LOGO_DETECTION, true));
        intent.putExtra(EXTRA_LANDMARK_DETECTION, sp.getBoolean(EXTRA_LANDMARK_DETECTION, true));
        intent.putExtra(EXTRA_IMAGE_PROPERTIES, sp.getBoolean(EXTRA_IMAGE_PROPERTIES, true));
        intent.putExtra(EXTRA_MAX_LABELS, sp.getInt(EXTRA_MAX_LABELS, DEFAULT_MAX_LABELS));
        intent.putExtra(EXTRA_MAX_LOGOS, sp.getInt(EXTRA_MAX_LOGOS, DEFAULT_MAX_LOGOS));
        intent.putExtra(EXTRA_MAX_LANDMARKS, sp.getInt(EXTRA_MAX_LANDMARKS, DEFAULT_MAX_LANDMARKS));

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null || intent.getExtras() == null) return;

        // Get the parameters to call Cloud Vision
        final Bundle options = intent.getExtras();

        // Creates the result Intent that will be sent as broadcast
        final Intent result = new Intent(ACTION_DONE);

        // Copy image URI from received intent to the result intent (we might need it on another moment)
        result.putExtra(EXTRA_IMAGE_URI, options.getParcelable(EXTRA_IMAGE_URI));

        Bitmap bitmap;

        try {
            // Get the Bitmap from intent
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), (Uri) options.getParcelable(EXTRA_IMAGE_URI));

            // Scale down the image
            if (options.getBoolean(EXTRA_IMAGE_RESIZE)) {
                bitmap = BitmapUtils.scaleBitmapDown(bitmap, 800);
            }

            // Store the scaled (or not) image sent to CloudVision server
            sentImage = bitmap;

            // Add the image
            final Image base64EncodedImage = new Image();

            // Get the image compression quality parameter
            final int imageQuality = options.getInt(EXTRA_IMAGE_QUALITY, DEFAULT_IMAGE_QUALITY);

            // Convert the bitmap to a JPEG
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);

            // Setup HttpTransport and Serialization Factory
            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
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

            // Store the response on a static field because BatchAnnotateImagesResponse does not
            // implement any serialization. As we are sending just one image, the CV API will
            // always return 1 response (or none).
            List<AnnotateImageResponse> responses = annotateRequest.execute().getResponses();
            lastResponse = responses != null && !responses.isEmpty() ? responses.get(0) : null;

            if (lastResponse != null) {
                Log.d(TAG, "CloudVision Response: \n" + lastResponse.toPrettyString());
            }

        } catch (FileNotFoundException e) {
            Log.d(TAG, "Image picking failed because " + e.getMessage());
            result.putExtra(EXTRA_RESULT_ERROR, getString(R.string.image_picker_error));
        } catch (GoogleJsonResponseException e) {
            Log.d(TAG, "failed to make API request because " + e.getContent());
            result.putExtra(EXTRA_RESULT_ERROR, getString(R.string.api_request_error));
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
            result.putExtra(EXTRA_RESULT_ERROR, getString(R.string.api_request_error));
        }

        // Broadcast the result
        sendBroadcast(result);
    }
}
