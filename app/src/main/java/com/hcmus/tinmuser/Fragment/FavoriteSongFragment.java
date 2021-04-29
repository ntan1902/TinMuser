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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Adapter.MusicAdapter;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.Model.Music;
import com.hcmus.tinmuser.Model.Song;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;
import java.util.List;

public class FavoriteSongFragment extends Fragment {
    RecyclerView recyclerView;
    MusicAdapter musicAdapter;
    EditText searchText;
    List<Music> mMusics;

    FirebaseUser mUser;
    ArrayList<String> mUserListFavoriteSong;


    public FavoriteSongFragment() {
        // Required empty public constructor
    }

    public void setListSong(List<Song> data) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite_song, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFavorite);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchText = view.findViewById(R.id.searchText);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
//        ListSearch= new MusicList();

        mUserListFavoriteSong = new ArrayList<>();
        getFavoriteSongs();

        mMusics = new ArrayList<>();
//        getMusics();


        searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (searchText.hasFocus()) {
                    if (s.toString().isEmpty()) {
                        getMusics();
                    } else {
                        List<Music> searchMusic = new ArrayList<>();
                        for (Music x : mMusics) {
                            if (x.getArtist().getName().toLowerCase().contains(s.toString().toLowerCase()) ||
                                    x.getSong().getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                searchMusic.add(x);
                            }
                        }
                        setListView(searchMusic);
                    }
                }
            }
        });

        return view;
    }

    void setListView(List<Music> list) {
        musicAdapter = new MusicAdapter(getContext(), list, "Single", "", mUserListFavoriteSong);
        recyclerView.setAdapter(musicAdapter);
    }

    private void getMusics() {
        // Lấy list song
        List<Song> songs = new ArrayList<>();
        DatabaseReference songRef = FirebaseDatabase.getInstance().getReference("Songs");
        songRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mMusics.clear();
                for (DataSnapshot songSnapshot : snapshot.getChildren()) {
                    Song song = songSnapshot.getValue(Song.class);
                    if (mUserListFavoriteSong.contains(song.getId())) {
                        songs.add(song);
                    }
                }

                getArtistName(songs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void getArtistName(List<Song> songs) {
        // Lấy list ca sĩ
        DatabaseReference artistRef = FirebaseDatabase.getInstance().getReference("Artists");
        artistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot artistSnapshot : snapshot.getChildren()) {
                    Artist artist = artistSnapshot.getValue(Artist.class);

                    for (Song song : songs) {
                        String artistIdSong = song.getArtistId();
                        String artistId = artist.getId();

                        if (artistId.equals(artistIdSong)) {
                            Music music = new Music(song, artist);
                            mMusics.add(music);
                        }
                    }
                }
                setListView(mMusics);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFavoriteSongs(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Favorites").child(mUser.getUid());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUserListFavoriteSong.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String fav_song = dataSnapshot.getKey();
                    mUserListFavoriteSong.add(fav_song);
                }

                getMusics();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}