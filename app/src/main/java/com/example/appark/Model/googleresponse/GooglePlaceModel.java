package com.example.appark.Model.googleresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GooglePlaceModel {

    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("name")
    @Expose
    private String name;


    @SerializedName("geometry")
    @Expose
    private GeometryModel geometry;


    public GeometryModel getGeometry() {
        return geometry;
    }

    public void setGeometry(GeometryModel geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}


