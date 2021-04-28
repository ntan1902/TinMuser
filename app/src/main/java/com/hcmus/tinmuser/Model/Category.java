package com.hcmus.tinmuser.Model;

public class Category {
    String id;
    String imageURL;
    String name;

    public Category() {}

    public Category(String id, String imageURL, String name) {
        this.id = id;
        this.imageURL = imageURL;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
