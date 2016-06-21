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
import io.github.mathiasberwig.cloudvision.data.model.LogoInfo;
import io.github.mathiasberwig.cloudvision.data.model.pojo.FormattedAddress;
import io.github.mathiasberwig.cloudvision.data.model.pojo.WikiArticleInfo;
import io.github.mathiasberwig.cloudvision.data.model.pojo.WikiDataBrandInfo;
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
     * The URL of Wikidata's API.
     */
    private static final String WIKIDATA_API_BASE_URL = "https://query.wikidata.org/bigdata/namespace/wdq/sparql";

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
        // The queries that uses computer vision and big data queries can easily exceed the default
        // timeouts, so we set bigger ones
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
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

        // Check if the service should query info about a logo
        if (intent.hasExtra(CloudVisionUploader.EXTRA_RESULT_LOGO)) {
            // Get the logo info from extra
            LogoInfo logoInfo = intent.getParcelableExtra(CloudVisionUploader.EXTRA_RESULT_LOGO);

            // Query the brand info from Wikipedia's API
            queryBrandInfoFromWikipedia(logoInfo, DEFAULT_WIKIPEDIA_MAX_SENTENCES);

            // Query the brand info from Wikidata's API
            queryBrandInfoFromWikidata(logoInfo);

            broadcast.putExtra(CloudVisionUploader.EXTRA_RESULT_LOGO, logoInfo);
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
     * Query the Wikipedia API to get info about a logo. It updates the ({@code name},
     * {@code description} and {@code wikipediaArticleUrl} properties of {@link LogoInfo}.
     *
     * @param logoInfo a LogoInfo object containing the brand name.
     * @param maxSentences The max number of sentences to be returned in the description field.
     */
    private void queryBrandInfoFromWikipedia(LogoInfo logoInfo, int maxSentences) {

        // Query info about the brand from Wikipedia
        final WikiArticleInfo wikiArticleInfo = queryInfoFromWikipedia(logoInfo.getBrandName(), maxSentences);

        if (wikiArticleInfo != null) {
            logoInfo.setBrandName(wikiArticleInfo.getTitle());
            logoInfo.setDescription(wikiArticleInfo.getExtract());
            logoInfo.setWikipediaArticleUrl(wikiArticleInfo.wikipediaArticleUrl);
        }
    }

    /**
     * Query the Wikidata API to get info about a logo. It updates the ({@code url} and
     * sets additional info of {@link LogoInfo} with the result data.
     *
     * @param logoInfo a LogoInfo object containing the brand name.
     */
    private void queryBrandInfoFromWikidata(LogoInfo logoInfo) {
        // Query info about the brand from Wikidata
        final WikiDataBrandInfo wikiDataBrandInfo = queryWikiDataBrandInfo(logoInfo.getBrandName());

        if (wikiDataBrandInfo != null) {
            logoInfo.setLogoUrl(wikiDataBrandInfo.getLogoUrl());
            logoInfo.setProperties(wikiDataBrandInfo.getProperties());
        }
    }

    /**
     * Uses the Wikidata's API to query info about a brand.
     *
     * @param brandName The name of the brand.
     * @return object with the response of Wikidata query, or {@code null}.
     */
    private WikiDataBrandInfo queryWikiDataBrandInfo(String brandName) {
        final Locale deviceLocale = Locale.getDefault();

        WikipediaQueryRequest qryRequest = new WikipediaQueryRequest().get(brandName, deviceLocale);

        // Build the request
        final HttpUrl url = HttpUrl.parse(WIKIDATA_API_BASE_URL)
                .newBuilder()
                .addQueryParameter("format", "json")
                .addQueryParameter("query", getWikidataQuery(qryRequest.queryLocale, qryRequest.articleName))
                .build();

        // Build the request
        final Request request = new Request.Builder().url(url).get().build();

        try {
            // Execute call
            Response response = client.newCall(request).execute();

            // De-serialize the response
            return gson.fromJson(response.body().string(), WikiDataBrandInfo.class);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
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

        WikipediaQueryRequest qryRequest = new WikipediaQueryRequest().get(articleNameInEnglish, deviceLocale);

        // Build the request to query extracted text from the article on the user's locale or English
        final HttpUrl url = HttpUrl.parse(qryRequest.wikipediaApiUrl)
                .newBuilder()
                .addQueryParameter("action", "query")
                .addQueryParameter("format", "json")
                .addQueryParameter("utf8", "1")
                .addQueryParameter("prop", "extracts")
                .addQueryParameter("exsentences", String.valueOf(maxSentences))
                .addQueryParameter("exsectionformat", "plain")
                .addQueryParameter("exintro", "1")
                .addQueryParameter("explaintext", "1")
                .addQueryParameter("titles", qryRequest.articleName)
                .build();

        // Build the request
        final Request request = new Request.Builder().url(url).get().build();

        try {
            // Execute call
            Response response = client.newCall(request).execute();

            // De-serialize the response
            WikiArticleInfo wai = gson.fromJson(response.body().string(), WikiArticleInfo.class);
            // Add the Wikipedia Article URL to response
            wai.wikipediaArticleUrl = qryRequest.wikipediaWikIUrl;
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

    /**
     * Get a locale-dependent SPARQL  query for an entity on Wikidata. In it we query for brands as
     * instances/subclasses of business enterprise, bands or softwares.
     *
     * @param locale The locale to get language and country code.
     * @param brandName The name of the brand that will be queried.
     * @return A locale-dependent SPARQL query for the {@code brandName} provided.
     */
    private static String getWikidataQuery(Locale locale, String brandName) {
        String language;
        // If the user locale language is any variant of english, we will query the default english
        // API. With different locales, we try to combine the language and country codes (like pt-BR),
        // and english is added as fallback lang (some properties doesn't have labels in other languages besides english).
        if (locale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
            language = locale.getLanguage();
        } else {
            language = locale.getCountry().equals("") ? String.format("%s,en", locale.getLanguage()) : String.format("%s-%s,en", locale.getLanguage(), locale.getCountry());
        }

        String query =
                "SELECT DISTINCT " +
                " ?brand " +
                " ?website ?logo ?country ?inception" +
                " ?twitter ?facebook" +
                " ?founders " +
                " ?headquarters ?divisions ?employees" +
                " ?genre ?awards" +
                " ?developers ?languages ?licenses" +
                "WHERE {" +
                    " ?brand ?label \"%s\" ." + // Search by Brand name
                    " { ?brand wdt:P31/wdt:P279* wd:Q4830453 . }" + // Business enterprise (instance of any subclass of)
                " UNION" +
                    " { ?brand wdt:P31/wdt:P279* wd:Q215380 . }" +  // Band (instance of any subclass of)
                " UNION" +
                    " { ?brand wdt:P31/wdt:P279* wd:Q7397 . }" +    // Software (instance of any subclass of)
                " OPTIONAL { ?brand wdt:P856 ?website . }" +
                " OPTIONAL { ?brand wdt:P154 ?logo . }" +
                " OPTIONAL { ?brand wdt:P17 ?country . }" +
                " OPTIONAL { ?brand wdt:P495 ?country . }" +
                " OPTIONAL { ?brand wdt:P1128 ?employees . }" +
                " OPTIONAL { ?brand wdt:P571 ?inception . }" +
                " OPTIONAL { ?brand wdt:P2002 ?twitter . }" +
                " OPTIONAL { ?brand wdt:P2013 ?facebook . }" +
                " OPTIONAL { ?brand wdt:P136 ?genre . }" +
                " SERVICE wikibase:label {" +
                    " bd:serviceParam wikibase:language \"%s\" ." + // Language for labels
                    " ?country rdfs:label ?country ." +
                    " ?genre rdfs:label ?genre }" +
                " { SELECT " +
                    " (GROUP_CONCAT(DISTINCT(?founderLabel); separator=\", \") as ?founders)" +
                    " (GROUP_CONCAT(DISTINCT(?industryLabel); separator=\", \") as ?divisions)" +
                    " (GROUP_CONCAT(DISTINCT(?headquarterLabel); separator=\", \") as ?headquarters)" +
                    " (GROUP_CONCAT(DISTINCT(?developerLabel); separator=\", \") as ?developers)" +
                    " (GROUP_CONCAT(DISTINCT(?languageLabel); separator=\", \") as ?languages)" +
                    " (GROUP_CONCAT(DISTINCT(?licenseLabel); separator=\", \") as ?licenses)" +
                    " (GROUP_CONCAT(DISTINCT(?awardLabel); separator=\", \") as ?awards)" +
                    " WHERE {" +
                        " ?brand ?label \"%s\" ." + // Search by Brand name
                        " { ?brand wdt:P31/wdt:P279* wd:Q4830453 . }" + // Business enterprise (instance of any subclass of)
                        " UNION" +
                        " { ?brand wdt:P31/wdt:P279* wd:Q215380 . }" +  // Band (instance of any subclass of)
                        " UNION" +
                        " { ?brand wdt:P31/wdt:P279* wd:Q7397 . }" +    // Software (instance of any subclass of)
                    " OPTIONAL { ?brand wdt:P112 ?founder . }" +
                    " OPTIONAL { ?brand wdt:P159 ?headquartersLocation . }" +
                    " OPTIONAL { ?brand wdt:P452 ?industry . }  " +
                    " OPTIONAL { ?brand wdt:P178 ?developer . }" +
                    " OPTIONAL { ?brand wdt:P277 ?language . }" +
                    " OPTIONAL { ?brand wdt:P275 ?license . }" +
                    " OPTIONAL { ?brand wdt:P166 ?award . }" +
                " SERVICE wikibase:label {" +
                    " bd:serviceParam wikibase:language \"%s\" ." + // Language for labels
                    " ?founder rdfs:label ?founderLabel ." +
                    " ?industry rdfs:label ?industryLabel ." +
                    " ?headquartersLocation rdfs:label ?headquarterLabel ." +
                    " ?developer rdfs:label ?developerLabel ." +
                    " ?language rdfs:label ?languageLabel ." +
                    " ?license rdfs:label ?licenseLabel ." +
                    " ?award rdfs:label ?awardLabel }" +
                "} } }" +
                " ORDER BY ?brand" +
                " LIMIT 1";

        return String.format(query, brandName, language, brandName, language);
    }

    private class WikipediaQueryRequest {
        String articleName;
        String wikipediaApiUrl;
        String wikipediaWikIUrl;
        Locale queryLocale;

        WikipediaQueryRequest get(String articleNameInEnglish, Locale locale) {
            // Compare the device's language with English and then set the article name that will be queried
            if (!locale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
                articleName = queryArticleTitleWikipedia(articleNameInEnglish, locale);
                wikipediaApiUrl = getWikipediasApiUrl(locale);
                wikipediaWikIUrl = getWikipediasWikiUrl(locale, articleName);
                queryLocale = locale;
            }

            if (articleName == null || wikipediaApiUrl == null) {
                articleName = articleNameInEnglish;
                wikipediaApiUrl = getWikipediasApiUrl(Locale.ENGLISH);
                wikipediaWikIUrl = getWikipediasWikiUrl(Locale.ENGLISH, articleName);
                queryLocale = Locale.ENGLISH;
            }

            return this;
        }

        @Override
        public String toString() {
            return "WikipediaQueryRequest{" +
                    "articleName='" + articleName + '\'' +
                    ", wikipediaApiUrl='" + wikipediaApiUrl + '\'' +
                    ", wikipediaWikIUrl='" + wikipediaWikIUrl + '\'' +
                    ", queryLocale=" + queryLocale +
                    '}';
        }
    }
}
