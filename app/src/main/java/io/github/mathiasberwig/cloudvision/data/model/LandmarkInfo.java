package io.github.mathiasberwig.cloudvision.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.LocationInfo;

/**
 * Class that stores info about a Landmark.
 *
 * Created by mathias.berwig on 15/04/2016.
 */
public class LandmarkInfo implements Parcelable {

    private String name;
    private String address;
    private String description;
    private LatLng latLng;
    private String wikipediaArticleUrl;

    public static final Parcelable.Creator<LandmarkInfo> CREATOR = new Parcelable.Creator<LandmarkInfo>() {
        public LandmarkInfo createFromParcel(Parcel in) {
            return new LandmarkInfo(in);
        }

        public LandmarkInfo[] newArray(int size) {
            return new LandmarkInfo[size];
        }
    };

    private LandmarkInfo(Parcel in) {
        name = in.readString();
        address = in.readString();
        description = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        wikipediaArticleUrl = in.readString();
    }

    public LandmarkInfo(String name, String address, String description, LatLng latLng, String wikipediaArticleUrl) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.latLng = latLng;
        this.wikipediaArticleUrl = wikipediaArticleUrl;
    }

    /**
     * Create a new LandmarkInfo from a {@link EntityAnnotation}.
     *
     * @param landmarkAnnotation The annotation with info about the landmark.
     * @return A new instance of LandmarkInfo with the data provided by {@code landmarkAnnotation}.
     */
    public static LandmarkInfo createFromAnnotation(@NonNull EntityAnnotation landmarkAnnotation) {
        // Get the name of the Landmark
        final String name = landmarkAnnotation.getDescription();

        // Get LocationInfo of the Landmark
        final LocationInfo locationInfo = landmarkAnnotation.getLocations().get(0);

        // Create a com.google.android.gms.maps.model.LatLng object from com.google.api.services.vision.v1.model.LocationInfo
        final LatLng latLng = new LatLng(locationInfo.getLatLng().getLatitude(), locationInfo.getLatLng().getLongitude());

        return new LandmarkInfo(name, null, null, latLng, null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(description);
        dest.writeParcelable(latLng, flags);
        dest.writeString(wikipediaArticleUrl);
    }

    @Override
    public String toString() {
        return "LandmarkInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", latLng=" + latLng +
                ", wikipediaArticleUrl='" + wikipediaArticleUrl + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getWikipediaArticleUrl() {
        return wikipediaArticleUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setWikipediaArticleUrl(String wikipediaArticleUrl) {
        this.wikipediaArticleUrl = wikipediaArticleUrl;
    }
}