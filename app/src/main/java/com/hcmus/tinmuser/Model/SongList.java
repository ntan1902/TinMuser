package com.hcmus.tinmuser.Model;

import java.net.BindException;
import java.util.ArrayList;

public class SongList {

    ArrayList<Song> list;
    void SongList(){
        list = new ArrayList<>();
    }

    public ArrayList<Song> getList(){return list;}
    public void addSong(Song song){
        list.add(song);
    }
    public void removeSong(int index){
        list.remove(index);
    }
    public void removeSong(Song song){
        list.remove(song);
    }
    public void setList(ArrayList<Song> list){this.list = list;}
    public Song getSong(int index){return list.get(index);}
    public void setSong(int index,Song song){list.set(index,song);}

    public ArrayList<Song> onSearch(String text){
        ArrayList<Song> temp = new ArrayList<>();
        for(Song song : list){
            if(song.artis.toLowerCase().contains(text.toLowerCase()) || song.name.toLowerCase().contains(text.toLowerCase())){
                temp.add(song);
            }
        }
        return temp;
    }
}
