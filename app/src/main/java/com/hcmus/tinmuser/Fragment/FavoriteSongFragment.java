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
    static FavoriteSongFragment fragment;
    RecyclerView recyclerView;
    MusicAdapter musicAdapter;
    EditText searchText;

    List<Music> mMusics;
    List<Music> searchMusic;

    FirebaseUser mUser;

    public FavoriteSongFragment() {
        // Required empty public constructor
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

        mMusics = new ArrayList<>();
        searchMusic = new ArrayList<>();
        musicAdapter = new MusicAdapter(getContext(), mMusics, "Single", "");
        recyclerView.setAdapter(musicAdapter);
        getFavoriteSongs();


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
                        musicAdapter = new MusicAdapter(getContext(), mMusics, "Single", "");
                        recyclerView.setAdapter(musicAdapter);
                        getFavoriteSongs();
                    } else {
                        searchMusic.clear();
                        musicAdapter = new MusicAdapter(getContext(), searchMusic, "Single", "");
                        recyclerView.setAdapter(musicAdapter);
                        for (Music x : mMusics) {
                            if (x.getArtist().getName().toLowerCase().contains(s.toString().toLowerCase()) ||
                                    x.getSong().getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                searchMusic.add(x);
                                musicAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });

        return view;
    }



    private void getFavoriteSongs() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Favorites").child(mUser.getUid());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mMusics.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String id_fav_song = dataSnapshot.getKey();

                    FirebaseDatabase.getInstance().getReference("Songs")
                            .child(id_fav_song)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Song song = snapshot.getValue(Song.class);

                                    FirebaseDatabase.getInstance().getReference("Artists")
                                            .child(song.getArtistId())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Artist artist = snapshot.getValue(Artist.class);

                                                    Music music = new Music(song, artist);
                                                    mMusics.add(music);
                                                    musicAdapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}