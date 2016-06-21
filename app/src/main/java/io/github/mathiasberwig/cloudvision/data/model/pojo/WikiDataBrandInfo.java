package io.github.mathiasberwig.cloudvision.data.model.pojo;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.github.mathiasberwig.cloudvision.data.model.EntityProperty;

/**
 * POJO class containing partial response mapping of a Wikidata's API response. <br>
 *
 * Created by mathias.berwig on 03/05/2016.
 */
public class WikiDataBrandInfo {
    private static final String TAG = WikiDataBrandInfo.class.getName();

    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    private static final SimpleDateFormat DATE_PRINT_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());

    /**
     * The URL template to compose a Twitter profile link.
     */
    private static final String TWITTER_URL = "https://twitter.com/%s";

    /**
     * The URL template to compose a Facebook profile link.
     */
    private static final String FACEBOOK_URL = "https://facebook.com/%s";

    Results results;

    class Results {
        ArrayList<Binding> bindings;

        class Binding {
            Value website;
            Value logo;
            Value country;
            Value inception;
            Value twitter;
            Value facebook;
            Value founders;
            Value headquarters;
            Value divisions;
            Value employees;
            Value genre;
            Value awards;
            Value developers;
            Value languages;
            Value licenses;

            class Value {
                String value;

                @Override
                public String toString() {
                    return value;
                }
            }

            @Override
            public String toString() {
                return "website=" + website +
                        ", logo=" + logo +
                        ", country=" + country +
                        ", inception=" + inception +
                        ", twitter=" + twitter +
                        ", facebook=" + facebook +
                        ", founders=" + founders +
                        ", headquarters=" + headquarters +
                        ", divisions=" + divisions +
                        ", employees=" + employees +
                        ", genre=" + genre +
                        ", awards=" + awards +
                        ", developers=" + developers +
                        ", languages=" + languages +
                        ", licenses=" + licenses;
            }
        }

        @Override
        public String toString() {
            return bindings.toString();
        }
    }

    /**
     * Get an {@link EntityProperty} list with all properties found by Wikidata's API. The formats
     * are applied on this moment.
     *
     * @return the list with all properties found (can be empty, never {@link null}).
     */
    public ArrayList<EntityProperty> getProperties() {
        final ArrayList<EntityProperty> properties = new ArrayList<>();

        // Website
        final String website = getWebsiteUrl();
        if (website != null && !website.trim().isEmpty()) {
            properties.add(EntityProperty.WEBSITE.setValue(website)
                    .setOnClickListener(getOnClickListener(website)));
        }

        // Country
        final String country = getCountry();
        if (country != null && !country.trim().isEmpty()) {
            properties.add(EntityProperty.COUNTRY.setValue(country));
        }

        // Inception
        final Date inception = getInception();
        if (inception != null) {
            properties.add(EntityProperty.INCEPTION.setValue(DateFormat.getDateInstance(
                    DateFormat.MEDIUM, Locale.getDefault()).format(inception)));
        }

        // Twitter
        final String twitter = getTwitter();
        if (twitter != null && !twitter.trim().isEmpty()) {
            properties.add(EntityProperty.TWITTER.setValue(twitter)
                    .setOnClickListener(getOnClickListener(String.format(TWITTER_URL, twitter))));
        }

        // Facebook
        final String facebook = getFacebook();
        if (facebook != null && !facebook.trim().isEmpty()) {
            properties.add(EntityProperty.FACEBOOK.setValue(facebook)
                    .setOnClickListener(getOnClickListener(String.format(FACEBOOK_URL, facebook))));
        }

        // Founders
        final String founders = getFounders();
        if (founders != null && !founders.trim().isEmpty()) {
            properties.add(EntityProperty.FOUNDERS.setValue(founders));
        }

        // Headquarters
        final String headquarters = getHeadquarters();
        if (headquarters != null && !headquarters.trim().isEmpty()) {
            properties.add(EntityProperty.HEADQUARTERS.setValue(headquarters));
        }

        // Divisions
        final String divisions = getDivisions();
        if (divisions != null && !divisions.trim().isEmpty()) {
            properties.add(EntityProperty.DIVISIONS.setValue(divisions));
        }

        // Employees
        final Long employees = getEmployeeNumber();
        if (employees != null && employees > 0) {
            properties.add(EntityProperty.EMPLOYEES.setValue(String.format(Locale.getDefault(), "%,d", employees)));
        }

        // Genre
        final String genre = getGenre();
        if (genre != null && !genre.trim().isEmpty()) {
            properties.add(EntityProperty.GENRE.setValue(genre));
        }

        // Awards
        final String awards = getAwards();
        if (awards != null && !awards.trim().isEmpty()) {
            properties.add(EntityProperty.AWARDS.setValue(awards));
        }

        // Developers
        final String developers = getDevelopers();
        if (developers != null && !developers.trim().isEmpty()) {
            properties.add(EntityProperty.DEVELOPERS.setValue(developers));
        }

        // Languages
        final String languages = getLanguages();
        if (languages != null && !languages.trim().isEmpty()) {
            properties.add(EntityProperty.LANGUAGES.setValue(languages));
        }

        // Licenses
        final String licenses = getLicenses();
        if (licenses != null && !licenses.trim().isEmpty()) {
            properties.add(EntityProperty.LICENSE.setValue(licenses));
        }

        return properties;
    }

    private Results.Binding get() throws NullPointerException, IndexOutOfBoundsException {
        return results.bindings.get(0);
    }

    /**
     * Creates a new {@link android.view.View.OnClickListener} to open the {@code url} in a new Intent.
     *
     * @param url A valid URL to be opened.
     * @return the OnClickListener created.
     */
    private View.OnClickListener getOnClickListener(final String url) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                v.getContext().startActivity(intent);
            }
        };
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P856">official website (P856)</a>
     * of an entity.
     *
     * @return The official website or {@code null}.
     */
    public String getWebsiteUrl() {
        try {
            return get().website.value;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            Log.d(TAG, "Can't retrieve the website URL.", ex);
            return null;
        }
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P154">logo url (P154)</a>
     * of an entity.
     *
     * @return The logo URL or {@code null}.
     */
    public String getLogoUrl() {
        try {
            return get().logo.value;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            Log.d(TAG, "Can't retrieve the logo URL.", ex);
            return null;
        }
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P17">country (P17)</a> or
     * <a href="https://www.wikidata.org/wiki/Property:P495">country of origin(P495)</a> of an entity.
     *
     * @return The country name or {@code null}.
     */
    public String getCountry() {
        try {
            return get().country.value;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            Log.d(TAG, "Can't retrieve the country name.", ex);
            return null;
        }
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P571">inception date (P571)</a>
     * of an entity.
     *
     * @return The inception date or {@code null}.
     */
    public Date getInception() {
        try {
            return DATETIME_FORMAT.parse(get().inception.value);
        } catch (ParseException | NullPointerException | IndexOutOfBoundsException ex) {
            Log.d(TAG, "Can't retrieve the inception date.", ex);
            return null;
        }
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P2002">Twitter profile (P2002)</a>
     * link of an entity.
     *
     * @return The Twitter profile name or {@code null}.
     */
    public String getTwitter() {
        try {
            return get().twitter.value;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            Log.d(TAG, "Can't retrieve the Twitter profile name.", ex);
            return null;
        }
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P2013">Facebook profile (P2013)</a>
     * link of an entity.
     *
     * @return The Facebook profile name or {@code null}.
     */
    public String getFacebook() {
        try {
            return get().facebook.value;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            Log.d(TAG, "Can't retrieve the Facebook profile name.", ex);
            return null;
        }
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P112">founders name (P112)</a>
     * of an entity.
     *
     * @return The founders names separated by , or {@code null}.
     */
    public String getFounders() {
        try {
            return get().founders.value;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            Log.d(TAG, "Can't retrieve the founders name.", ex);
            return null;
        }
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P159">headquarters location (P159)</a>
     * of an entity.
     *
     * @return The headquarters location of the company separated by , or {@code null}.
     */
    public String getHeadquarters() {
        try {
            return get().headquarters.value;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            Log.d(TAG, "Can't retrieve the headquarters location.", ex);
            return null;
        }
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P452">industries (P452)</a>
     * of an entity.
     *
     * @return The industries/divisions of the company separated by , or {@code null}.
     */
    public String getDivisions() {
        try {
            return get().divisions.value;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            Log.d(TAG, "Can't retrieve the divisions.", ex);
            return null;
        }
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P1128">employees (P1128)</a>
     * of an entity.
     *
     * @return The employee number of the company or {@code null}.
     */
    public Long getEmployeeNumber() {
        try {
            final String employees = get().employees.value;
            return Long.parseLong(employees);
        } catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException ex) {
            Log.d(TAG, "Can't retrieve the employee number.", ex);
            return null;
        }
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P136">genre (P136)</a>
     * of an entity.
     *
     * @return The genre of the band or {@code null}.
     */
    public String getGenre() {
        try {
            return get().genre.value;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            Log.d(TAG, "Can't retrieve the genre.", ex);
            return null;
        }
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P166">awards received (P166)</a>
     * by an entity.
     *
     * @return The awards received separated by , or {@code null}.
     */
    public String getAwards() {
        try {
            return get().awards.value;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            Log.d(TAG, "Can't retrieve the awards.", ex);
            return null;
        }
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P178">developers (P178)</a>
     * of an entity.
     *
     * @return The developers names separated by , or {@code null}.
     */
    public String getDevelopers() {
        try {
            return get().developers.value;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            Log.d(TAG, "Can't retrieve the developers.", ex);
            return null;
        }
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P277">programming languages (P277)</a>
     * of an entity.
     *
     * @return The programming languages names separated by , or {@code null}.
     */
    public String getLanguages() {
        try {
            return get().languages.value;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            Log.d(TAG, "Can't retrieve the programming languages.", ex);
            return null;
        }
    }

    /**
     * Returns the <a href="https://www.wikidata.org/wiki/Property:P275">copyright license (P275)</a>
     * of an entity.
     *
     * @return The copyright license names separated by , or {@code null}.
     */
    public String getLicenses() {
        try {
            return get().licenses.value;
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            Log.d(TAG, "Can't retrieve the copyright licenses.", ex);
            return null;
        }
    }

    @Override
    public String toString() {
        return "WikiDataBrandInfo{" +
                "results=" + results +
                '}';
    }
}
