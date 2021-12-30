package com.example.usclassifieds.data.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemPost {
    String title;
    String description;
    String category;
    double price;
    List<File> images = new ArrayList<>();

    public ItemPost(String title, String description, String category, double price, List<File> images) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.images = images;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void addImage() {

    }

    public void removeImage(int i) {
        images.remove(i);
    }

    public List<File> getImages() {
        return images;
    }
}
