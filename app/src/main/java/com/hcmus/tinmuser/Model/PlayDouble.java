package com.hcmus.tinmuser.Model;

public class PlayDouble {
    private String user1;
    private String user2;
    private String uri;
    private String songId;
    private String songName;
    private String artistName;
    private String imageURL;
    private Integer progressChanged;


    private Boolean isPlay;
    private Boolean isRepeat;

    public PlayDouble() {}

    public PlayDouble(String user1, String user2, String uri, String songId, String songName, String artistName, String imageURL, Integer progressChanged, Boolean isPlay, Boolean isRepeat) {
        this.user1 = user1;
        this.user2 = user2;
        this.uri = uri;
        this.songId = songId;
        this.songName = songName;
        this.artistName = artistName;
        this.imageURL = imageURL;
        this.progressChanged = progressChanged;

        this.isPlay = isPlay;
        this.isRepeat = isRepeat;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Boolean getIsPlay() {
        return isPlay;
    }

    public void setIsPlay(Boolean isPlay) {
        this.isPlay = isPlay;
    }

    public Boolean getIsRepeat() {
        return isRepeat;
    }

    public void setIsRepeat(Boolean isRepeat) {
        this.isRepeat = isRepeat;
    }

    public Integer getProgressChanged() {
        return progressChanged;
    }

    public void setProgressChanged(Integer progressChanged) {
        this.progressChanged = progressChanged;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }
}
