package io.dwak.cardboardapod;

import com.google.gson.annotations.SerializedName;

public class SpaceServiceImage {
    @SerializedName("image") String url;

    public SpaceServiceImage(String url) {
        this.url = url;
    }
}
