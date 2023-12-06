package com.example.watchesstore.models;

public class Category {
    private String imageUrl;
    private String name;
    private String type;

    public Category() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Category(String imageUrl, String name, String type) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category(String imageUrl, String name) {
        this.imageUrl = imageUrl;
        this.name = name;
    }
}
