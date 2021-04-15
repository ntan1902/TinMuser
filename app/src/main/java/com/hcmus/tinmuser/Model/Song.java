package com.hcmus.tinmuser.Model;

public class Song {
    String id;
    String artistId;
    String categoryId;
    String name;
    String imageURL;
    String uri;

    public Song(){

    }
    public Song(String id, String artistId, String categoryId, String name, String imageURL, String uri) {
        this.id = id;
        this.artistId = artistId;
        this.categoryId = categoryId;
        this.name = name;
        this.imageURL = imageURL;
        this.uri = uri;
    }

    public Song(String name, String imageURL, String uri) {
        this.name = name;
        this.imageURL = imageURL;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
