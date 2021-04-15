package com.hcmus.tinmuser.Model;

import java.util.ArrayList;

public class MusicList {
    ArrayList<Music> list;
    public MusicList(){

    }
    public MusicList(ArrayList<Music> a){
        this.list = a;
    }
    public void setList(ArrayList<Music> a){
        this.list = a;
    }
    private ArrayList<Music> getList(){
        return this.list;
    }
    public ArrayList<Music> onSearch(String text){
        ArrayList<Music> temp = new ArrayList<>();
        for(Music x : list){
            if (x.artistName.toLowerCase().contains(text.toLowerCase()) || x.song.name.toLowerCase().contains(text.toLowerCase())){
                temp.add(x);
            }
        }
        return temp;
    }

}
