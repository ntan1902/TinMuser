package com.hcmus.tinmuser.Model;

public class Song {
    String id;
    String artistId;
    String categoryId;
    String name;
    String imageURL;
    String uri;
    Integer like;
    String createdAt;

    public Song(){

    }
    public Song(String id,
                String artistId,
                String categoryId,
                String name,
                String imageURL,
                String uri,
                Integer like,
                String createdAt) {
        this.id = id;
        this.artistId = artistId;
        this.categoryId = categoryId;
        this.name = name;
        this.imageURL = imageURL;
        this.uri = uri;
        this.like = like;
        this.createdAt = createdAt;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }
}
