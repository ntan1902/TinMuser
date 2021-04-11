package com.hcmus.tinmuser.Model;

import java.util.ArrayList;
import java.util.List;

public class Music {
    Song song;
    String artistName;

    public Music() {

    }

    public Music(Song song, String artistName) {
        this.song = song;
        this.artistName = artistName;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
