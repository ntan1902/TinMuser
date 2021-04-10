package com.hcmus.tinmuser.Fragment;

import android.net.http.SslCertificate;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hcmus.tinmuser.Adapter.MusicListAdapter1;
import com.hcmus.tinmuser.Model.Song;
import com.hcmus.tinmuser.Model.SongList;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchFragment extends Fragment {
    RecyclerView recyclerView;
    EditText searchText;
    SongList songList;
    ArrayList<String> name = new ArrayList<>(Arrays.asList("Song1","Song2","Song3","Song4"));
    ArrayList<String> artis = new ArrayList<>(Arrays.asList("Tùng núi 1","Tùng núi 2","Tùng núi 3","Tùng núi 4"));
    ArrayList<String> images= new ArrayList<>(Arrays.asList(
            "https://firebasestorage.googleapis.com/v0/b/tinmuser.appspot.com/o/avatar.png?alt=media&token=cbbc9e99-21f7-4990-937d-42bf8399b549",
            "https://firebasestorage.googleapis.com/v0/b/tinmuser.appspot.com/o/avatar.png?alt=media&token=cbbc9e99-21f7-4990-937d-42bf8399b549",
            "https://firebasestorage.googleapis.com/v0/b/tinmuser.appspot.com/o/avatar.png?alt=media&token=cbbc9e99-21f7-4990-937d-42bf8399b549",
            "https://firebasestorage.googleapis.com/v0/b/tinmuser.appspot.com/o/avatar.png?alt=media&token=cbbc9e99-21f7-4990-937d-42bf8399b549"
    ));

    public SearchFragment() {
        // Required empty public constructor
    }
    public ArrayList<Song> getSongData(){ /// firebase fetch in here
        ArrayList<Song> tempList = new ArrayList<>();
        for(int i =0;i<name.size();i++){
            Song temp = new Song(name.get(i),artis.get(i),images.get(i));
            tempList.add(temp);
        }
        return tempList;
    }
    public void setListSong(ArrayList<Song> list){
        MusicListAdapter1 adapter = new MusicListAdapter1(getContext(),list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager((getContext())));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewSearch);

        searchText = view.findViewById(R.id.searchText);
        songList = new SongList();

        ArrayList<Song> data = getSongData();
        setListSong(data);

        songList.setList(data);

        searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.toString().isEmpty()){
                    ArrayList<Song> data = getSongData();
                    setListSong(data);
                }
                else{
                    System.out.println(s.toString());
                    ArrayList<Song> searchResult = songList.onSearch(s.toString());
                    setListSong(searchResult);
                }
            }
        });
        return view;
    }
}