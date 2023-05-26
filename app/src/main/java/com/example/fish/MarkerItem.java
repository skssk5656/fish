package com.example.fish;

import com.google.android.gms.maps.model.LatLng;

public class MarkerItem {
    private String id;
    private LatLng latLng;
    private String title;
    private String snippet;
    private String imagePath;

    public MarkerItem(String id, LatLng latLng, String title, String snippet, String imagePath) {
        this.id = id;
        this.latLng = latLng;
        this.title = title;
        this.snippet = snippet;
        this.imagePath = imagePath;
    }

    // getters and setters

    public String getId() {
        return id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getImagePath() {
        return imagePath;
    }
}
