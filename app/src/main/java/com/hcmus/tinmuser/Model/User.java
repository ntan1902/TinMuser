package com.hcmus.tinmuser.Model;

public class User {
    private String id;
    private String email;
    private String imageURL;
    private String userName;
    private String phone;
    private String status;


    public User() {

    }

    public User(String id, String email, String imageURL, String userName, String phone, String status) {
        this.id = id;
        this.email = email;
        this.imageURL = imageURL;
        this.userName = userName;
        this.phone = phone;
        this.status = status;
    }

    public User(String id, String email, String imageURL) {
        this.id = id;
        this.email = email;
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
