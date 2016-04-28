package io.github.mathiasberwig.cloudvision.controller.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.github.mathiasberwig.cloudvision.data.model.LandmarkInfo;
import io.github.mathiasberwig.cloudvision.data.model.pojo.FormattedAddress;
import io.github.mathiasberwig.cloudvision.data.model.pojo.WikiArticleInfo;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * IntentService that communicates with third-party APIs to query info about Labels, Logos and
 * Landmarks.
 */
public class RestApisConsumer extends IntentService {
    private static final String TAG = RestApisConsumer.class.getName();

    /**
     * The URL of Google Maps Geocode API.
     */
    private static final String GEOCODE_API_URL = "http://maps.googleapis.com/maps/api/geocode/json";

    /**
     * The URL template of Wikipedia's API. It can't be used directly. To get a valid URL consider
     * using the method {@link #getWikipediasApiUrl(Locale)}.
     */
    private static final String WIKIPEDIA_API_BASE_URL = "https://%s.wikipedia.org/w/api.php";

    /**
     * The URL template of Wikipedia's Wiki site. It can't be used directly. To get a valid URL
     * consider using the method {@link #getWikipediasWikiUrl(Locale, String)}.
     */
    private static final String WIKIPEDIA_WIKI_BASE_URL = "https://%s.wikipedia.org/wiki/%s";

    /**
     * The max sentences queried from Wikipedia's API on extract actions.
     */
    public static final int DEFAULT_WIKIPEDIA_MAX_SENTENCES = 4;

    /**
     * Action broadcasted when this service finishes querying the APIs.
     */
    public static final String ACTION_DONE = "io.github.mathiasberwig.cloudvision.controller.service.RestApisConsumer.ACTION_DONE";

    private OkHttpClient client;
    private Gson gson;

    public RestApisConsumer() {
        super(TAG);

        // Instantiate the OkHttp Client
        client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .build();

        // Instantiate the Gson client
        gson = new Gson();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent broadcast = new Intent(ACTION_DONE);
        broadcast.putExtras(intent.getExtras());

        // Check if the service should query info about a landmark
        if (intent.hasExtra(CloudVisionUploader.EXTRA_RESULT_LANDMARK)) {
            // Get the landmark info from extra
            LandmarkInfo landmarkInfo = intent.getParcelableExtra(CloudVisionUploader.EXTRA_RESULT_LANDMARK);

            // Query the landmark info from Wikipedia's API
            queryLandmarkInfoFromWikipedia(landmarkInfo, DEFAULT_WIKIPEDIA_MAX_SENTENCES);

            // Query the landmark address from Google's Geocoding API
            queryLandmarkAddressFromGoogleGeocoding(landmarkInfo);

            // Update the Extra on Broadcast
            broadcast.putExtra(CloudVisionUploader.EXTRA_RESULT_LANDMARK, landmarkInfo);
        }

        // Send the broadcast
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    /**
     * Query the Wikipedia API to get info about a landmark. It updates the {@link LandmarkInfo#name},
     * the {@link LandmarkInfo#description} and {@link LandmarkInfo#wikipediaArticleUrl} fields of
     * {@link LandmarkInfo}.
     *
     * @param landmarkInfo a LandmarkInfo object containing name, and latLng data.
     * @param maxSentences The max number of sentences to be returned in the description field.
     */
    private void queryLandmarkInfoFromWikipedia(LandmarkInfo landmarkInfo, int maxSentences) {

        // Query info about the landmark from Wikipedia
        final WikiArticleInfo wikiArticleInfo = queryInfoFromWikipedia(landmarkInfo.getName(), maxSentences);

        // Put the info queried from Wikipedia to LandmarkInfo
        if (wikiArticleInfo != null) {
            landmarkInfo.setName(wikiArticleInfo.getTitle());
            landmarkInfo.setDescription(wikiArticleInfo.getExtract());
            landmarkInfo.setWikipediaArticleUrl(wikiArticleInfo.wikipediaArticleUrl);
        }
    }

    /**
     * Uses the Google Maps Geocoding Web API to query for an human-readable address by passing
     * latitude and longitude parameters. It updates the {@link LandmarkInfo#address} field of
     * {@link LandmarkInfo}.
     *
     * @param landmarkInfo a LandmarkInfo instance with {@link LandmarkInfo#latLng} set.
     */
    private void queryLandmarkAddressFromGoogleGeocoding(LandmarkInfo landmarkInfo) {
        LatLng latLng = landmarkInfo.getLatLng();

        // Format the parameter as lat,long
        String parLatLng = String.format(Locale.ROOT, "%f,%f", latLng.latitude, latLng.longitude);

        // Create the URL
        final HttpUrl url = HttpUrl.parse(GEOCODE_API_URL)
                .newBuilder()
                .addQueryParameter("latlng", parLatLng)
                .build();

        // Build the request
        final Request request = new Request.Builder().url(url).get().build();

        try {
            // Execute call
            Response response = client.newCall(request).execute();

            if (response.code() == 200) {
                // De-serialize the response
                String address = gson.fromJson(response.body().string(), FormattedAddress.class).getFormattedAddress();

                // Set the address of LandmarkInfo
                landmarkInfo.setAddress(address);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * Extracts page info about an article for the current {@link Locale} using the Wikipedia's API.
     *
     * @param articleNameInEnglish The title of article in english to be queried.
     * @param maxSentences The number of sentences (1-10) that API should return.
     * @return The plain text extracted from the article's page.
     */
    private WikiArticleInfo queryInfoFromWikipedia(String articleNameInEnglish, int maxSentences) {
        final Locale deviceLocale = Locale.getDefault();

        // The URL that will be used to request info
        String urlToParse;

        // The name of the article
        String articleName = null;

        // Compare the device's language with English and then set the article name that will be queried
        if (deviceLocale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
            articleName = articleNameInEnglish;
        } else {
            articleName = queryArticleTitleWikipedia(articleNameInEnglish, deviceLocale);
        }

        // Check if an article name was found in the user's language, then set  the URL of the Wikipedia's API
        if (articleName != null) {
            urlToParse = getWikipediasApiUrl(deviceLocale);
        } else {
            // Article wasn't found by queryArticleTitleWikipedia() method
            articleName = articleNameInEnglish;
            urlToParse = getWikipediasApiUrl(Locale.ENGLISH);
        }

        // Build the request to query extracted text from the article on the user's locale or English
        final HttpUrl url = HttpUrl.parse(urlToParse)
                .newBuilder()
                .addQueryParameter("action", "query")
                .addQueryParameter("format", "json")
                .addQueryParameter("utf8", "1")
                .addQueryParameter("prop", "extracts")
                .addQueryParameter("exsentences", String.valueOf(maxSentences))
                .addQueryParameter("exsectionformat", "plain")
                .addQueryParameter("exintro", "1")
                .addQueryParameter("explaintext", "1")
                .addQueryParameter("titles", articleName)
                .build();

        // Build the request
        final Request request = new Request.Builder().url(url).get().build();

        try {
            // Execute call
            Response response = client.newCall(request).execute();

            // De-serialize the response
            WikiArticleInfo wai = gson.fromJson(response.body().string(), WikiArticleInfo.class);
            // Add the Wikipedia Article URL to response
            wai.wikipediaArticleUrl = getWikipediasWikiUrl(deviceLocale, articleName);
            // Return the response
            return wai;

        } catch (IOException | NullPointerException | JsonSyntaxException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Query the title of an article in another language using Wikipedia's API in English.
     *
     * @param englishArticleName The title of the article to be queried in english.
     * @return The title of the article in the language requested or {@code null}.
     */
    private String queryArticleTitleWikipedia(String englishArticleName, Locale locale) {
        // (Google CloudVision API) returns the names of Labels, Logos and Landmarks in English, so
        // we need to search in en.wikipedia.org.
        final HttpUrl url = HttpUrl.parse(getWikipediasApiUrl(Locale.ENGLISH))
                .newBuilder()
                .addQueryParameter("action", "query")
                .addQueryParameter("format", "json")
                .addQueryParameter("utf8", "1")
                .addQueryParameter("titles", englishArticleName)
                .addQueryParameter("prop", "langlinks")
                .addQueryParameter("lllang", locale.getLanguage())
                .build();

        // Build the request
        final Request request = new Request.Builder().url(url).get().build();

        try {
            // Execute call
            Response response = client.newCall(request).execute();

            // De-serialize the response
            return gson.fromJson(response.body().string(), WikiArticleInfo.class).getTitleForLocale(locale);

        } catch (IOException | NullPointerException | JsonSyntaxException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get a locale-dependent URL for the Wikipedia's API.
     *
     * @param locale The locale to be used with {@link Locale#getLanguage()} method.
     * @return A locale-dependent URL for the Wikipedia's API.
     */
    private static String getWikipediasApiUrl(Locale locale) {
        return String.format(WIKIPEDIA_API_BASE_URL, locale.getLanguage());
    }

    /**
     * Get a locale-dependent URL for an article on Wikipedia Wiki.
     *
     * @param locale The locale to be used with {@link Locale#getLanguage()} method.
     * @param articleName The name of the article in the specified {@code locale}.
     * @return A locale-dependent URL for the {@code articleName} provided.
     */
    private static String getWikipediasWikiUrl(Locale locale, String articleName) {
        return String.format(WIKIPEDIA_WIKI_BASE_URL, locale.getLanguage(), articleName);
    }
}
