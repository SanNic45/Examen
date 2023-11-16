package com.example.examen;

import com.google.gson.annotations.SerializedName;

public class DigimonModel {
    @SerializedName("name")
    private String name;

    @SerializedName("level")
    private String level;

    @SerializedName("img")
    private String imageUrl;

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
