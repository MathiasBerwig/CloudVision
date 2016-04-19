package io.github.mathiasberwig.cloudvision.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.services.vision.v1.model.EntityAnnotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that stores info about a Label.
 *
 * Created by mathias.berwig on 18/04/2016.
 */
public class LabelInfo implements Parcelable {

    /**
     * Same as {@link EntityAnnotation#description}.
     */
    private String description;

    /**
     * Same as {@link EntityAnnotation#score}.
     */
    private float score;

    public static final Creator<LabelInfo> CREATOR = new Creator<LabelInfo>() {
        @Override
        public LabelInfo createFromParcel(Parcel in) {
            return new LabelInfo(in);
        }

        @Override
        public LabelInfo[] newArray(int size) {
            return new LabelInfo[size];
        }
    };

    protected LabelInfo(Parcel in) {
        description = in.readString();
        score = in.readFloat();
    }

    public LabelInfo(String description, float score) {
        this.description = description;
        this.score = score;
    }

    /**
     * Create a list of LabelInfo from a list of {@link EntityAnnotation}.
     *
     * @param entityAnnotations The annotations with info about the labels.
     * @return A list with instances of Label info with the provided by {@code entityAnnotations}.
     */
    public static ArrayList<LabelInfo> createListFromAnnotations(List<EntityAnnotation> entityAnnotations) {
        ArrayList<LabelInfo> labelsInfo = new ArrayList<>(entityAnnotations.size());

        for (EntityAnnotation ea : entityAnnotations) {
            labelsInfo.add(new LabelInfo(ea.getDescription(), ea.getScore()));
        }

        return labelsInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeFloat(score);
    }

    @Override
    public String toString() {
        return "LabelInfo{" +
                "description='" + description + '\'' +
                ", score=" + score +
                '}';
    }

    /**
     * Same as {@link EntityAnnotation#getScore()}.
     */
    public float getScore() {
        return score;
    }

    /**
     * Same as {@link EntityAnnotation#getDescription()}.
     */
    public String getDescription() {
        return description;
    }
}