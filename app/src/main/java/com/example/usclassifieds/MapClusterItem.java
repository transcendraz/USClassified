package com.example.usclassifieds;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

class MapClusterItem implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private int image;

    public MapClusterItem(LatLng position, String title, String snippet, int image) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.image = image;
    }

    public MapClusterItem() {

    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public LatLng getPosition() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
