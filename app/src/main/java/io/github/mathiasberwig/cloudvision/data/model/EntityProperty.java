package io.github.mathiasberwig.cloudvision.data.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;

import java.util.Date;

import io.github.mathiasberwig.cloudvision.R;

/**
 * Enum to store info about a property of an Wikidata entity. I created this way because I think this
 * is more efficient than manually setting the icons and labels. The default layout for showing this
 * class is {@link io.github.mathiasberwig.cloudvision.R.layout#list_item_card_brand_property}.
 * </p>
 * Created by mathias.berwig on 16/06/2016.
 */
public enum EntityProperty {

    WEBSITE(R.string.p_website, R.drawable.ic_link_black_24dp),
    COUNTRY(R.string.p_country, R.drawable.ic_flag_black_24dp),
    INCEPTION(R.string.p_inception, R.drawable.ic_date_range_black_24dp),
    TWITTER(R.string.p_twitter, R.drawable.ic_twitter_black_24dp),
    FACEBOOK(R.string.p_facebook, R.drawable.ic_facebook_black_24dp),
    FOUNDERS(R.string.p_founders, R.drawable.ic_people_black_24dp),
    HEADQUARTERS(R.string.p_headquarters, R.drawable.ic_business_black_24dp),
    DIVISIONS(R.string.p_divisions, R.drawable.ic_work_black_24dp),
    EMPLOYEES(R.string.p_employees, R.drawable.ic_people_black_24dp),
    GENRE(R.string.p_genre, R.drawable.ic_library_music_black_24dp),
    AWARDS(R.string.p_awards, R.drawable.ic_trophy_award_24dp),
    DEVELOPERS(R.string.p_developers, R.drawable.ic_people_black_24dp),
    LANGUAGES(R.string.p_languages, R.drawable.ic_code_black_24dp),
    LICENSE(R.string.p_license, R.drawable.ic_description_black_24dp);

    private int name;
    private int icon;
    private String value;
    private View.OnClickListener onClickListener;

    /**
     * Default (and main) constructor.
     *
     * @param name The name of the property.
     * @param icon The icon of the property.
     */
    EntityProperty(@StringRes int name, @DrawableRes int icon) {
        this.name = name;
        this.icon = icon;
    }

    public int getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public String getValue() {
        return value;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public EntityProperty setValue(String value) {
        this.value = value;
        return this;
    }

    public EntityProperty setValue(Long value) {
        this.value = String.valueOf(value);
        return this;
    }

    public EntityProperty setValue(Date value) {
        this.value = value.toString();
        return this;
    }

    public EntityProperty setName(int name) {
        this.name = name;
        return this;
    }

    public EntityProperty setIcon(int icon) {
        this.icon = icon;
        return this;
    }

    public EntityProperty setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    @Override
    public String toString() {
        return "EntityProperty{" +
                "name=" + name +
                ", icon=" + icon +
                ", value='" + value + '\'' +
                '}';
    }
}
