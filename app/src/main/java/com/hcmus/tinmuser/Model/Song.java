package com.hcmus.tinmuser.Model;

public class Song {
    String name;
    String artis;
    String imageUrl;
    public Song(){

    }
    public Song(String name,String artis,String imageUrl){
        this.name = name;
        this.artis = artis;
        this.imageUrl = imageUrl;
    }
    public String getName(){return name;}
    public String getArtis(){return artis;}
    public String getImageUrl(){return imageUrl;}
    void setName(String name){this.name =name;}
    void setArtis(String artis){this.artis = artis;}
    void setImageUrl(String url){this.imageUrl = url;}
}
