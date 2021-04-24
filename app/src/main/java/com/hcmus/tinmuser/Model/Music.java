package com.hcmus.tinmuser.Model;

import java.util.ArrayList;
import java.util.List;

public class Music {
    Song song;
    Artist artist;

    public Music() {

    }

    public Music(Song song, Artist artist) {
        this.song = song;
        this.artist = artist;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
