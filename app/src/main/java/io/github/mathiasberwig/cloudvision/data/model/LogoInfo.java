package io.github.mathiasberwig.cloudvision.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.api.services.vision.v1.model.EntityAnnotation;

import java.util.ArrayList;

/**
 * Class that stores info about a logo.
 *
 * Created by mathias.berwig on 20/04/2016.
 */
public class LogoInfo implements Parcelable {

    private String brandName;
    private String description;
    private String wikipediaArticleUrl;
    private ArrayList<EntityProperty> properties;
    private String logoUrl;

    public LogoInfo(String brandName) {
        this.brandName = brandName;
    }

    @SuppressWarnings("unchecked")
    protected LogoInfo(Parcel in) {
        brandName = in.readString();
        description = in.readString();
        wikipediaArticleUrl = in.readString();
        properties = in.readArrayList(EntityProperty.class.getClassLoader());
        logoUrl = in.readString();
    }

    public static LogoInfo createFromAnnotation(@NonNull EntityAnnotation logoAnnotation) {
        // Get the name of the brand detected in annotation
        final String brandName = logoAnnotation.getDescription();

        return new LogoInfo(brandName);
    }

    public static final Creator<LogoInfo> CREATOR = new Creator<LogoInfo>() {
        @Override
        public LogoInfo createFromParcel(Parcel in) {
            return new LogoInfo(in);
        }

        @Override
        public LogoInfo[] newArray(int size) {
            return new LogoInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(brandName);
        dest.writeString(description);
        dest.writeString(wikipediaArticleUrl);
        dest.writeList(properties);
        dest.writeString(logoUrl);
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWikipediaArticleUrl() {
        return wikipediaArticleUrl;
    }

    public void setWikipediaArticleUrl(String wikipediaArticleUrl) {
        this.wikipediaArticleUrl = wikipediaArticleUrl;
    }

    public ArrayList<EntityProperty> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<EntityProperty> properties) {
        this.properties = properties;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
