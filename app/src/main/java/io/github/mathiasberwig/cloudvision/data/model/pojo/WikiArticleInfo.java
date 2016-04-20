package io.github.mathiasberwig.cloudvision.data.model.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/**
 * POJO class containing partial response mapping of a Wikipedia's API response. <br>
 * For convenience, the class also stores the URL to access the article.
 *
 * Created by mathias.berwig on 15/04/2016.
 */
public class WikiArticleInfo {

    Query query;
    public String wikipediaArticleUrl;

    public class Query {
        Map<String, PageInfo> pages;

        public class PageInfo {
            String title;
            String extract;
            ArrayList<LangInfo> langlinks;

            public class LangInfo {
                String lang;
                @SerializedName("*")
                String title;
            }
        }
    }

    /**
     * Return the article's Title.
     *
     * @return article's title.
     * @throws NullPointerException
     */
    public String getTitle() throws NullPointerException {
        return query.pages.entrySet().iterator().next().getValue().title;
    }

    /**
     * Return the extracted text from the article.
     *
     * @return article's text.
     * @throws NullPointerException
     */
    public String getExtract() throws NullPointerException {
        return query.pages.entrySet().iterator().next().getValue().extract;
    }

    /**
     * Return the article's Title for the requested {@code locale}. Note that the webservice call
     * needs to address the locale too.
     *
     * @param locale Locale to use {@link Locale#getLanguage()} to filter results.
     * @return The Wikipedia's article title for the requested {@code locale}.
     * @throws NullPointerException
     */
    public String getTitleForLocale(Locale locale) throws NullPointerException {
        // Get the LangLinks from response
        final ArrayList<Query.PageInfo.LangInfo> langlinks =
                query.pages.entrySet().iterator().next().getValue().langlinks;

        // Search for the title in locale's language
        for (Query.PageInfo.LangInfo langInfo : langlinks) {
            if (langInfo.lang.equals(locale.getLanguage())) return langInfo.title;
        }

        // Not found
        return null;
    }
}
