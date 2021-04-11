package com.hcmus.tinmuser.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Adapter.MusicAdapter;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.Model.Song;
import com.hcmus.tinmuser.Model.Music;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    RecyclerView recyclerView;
    MusicAdapter musicAdapter;
    EditText searchText;
    List<Music> mMusics;


    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSearch);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchText = view.findViewById(R.id.searchText);

        mMusics = new ArrayList<>();
        getMusics();


        searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
//                if(s.toString().isEmpty()){
//                    ArrayList<Song> data = getSongData();
//                    setListSong(data);
//                }
//                else{
//                    System.out.println(s.toString());
//                    ArrayList<Song> searchResult = songList.onSearch(s.toString());
//                    setListSong(searchResult);
//                }
            }
        });
        return view;
    }

    private void getMusics() {
        // Lấy list song
        List<Song> songs = new ArrayList<>();
        DatabaseReference songRef = FirebaseDatabase.getInstance().getReference("Songs");
        songRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot songSnapshot : snapshot.getChildren()) {

                    Song song = songSnapshot.getValue(Song.class);
                    songs.add(song);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Lấy list ca sĩ
        DatabaseReference artistRef = FirebaseDatabase.getInstance().getReference("Artists");
        artistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot artistSnapshot : snapshot.getChildren()) {
                    Artist artist = artistSnapshot.getValue(Artist.class);

                    for(Song song : songs) {
                        String artistIdSong = song.getArtistId();
                        String artistId = artist.getId();

                        if(artistId.equals(artistIdSong)){
                            Music music = new Music(song, artist.getName());
                            mMusics.add(music);
                        }
                    }
                }

                musicAdapter = new MusicAdapter(getContext(), mMusics);
                recyclerView.setAdapter(musicAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}