package com.hcmus.tinmuser.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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

public class ShowListSongsActivity extends Activity {
    MusicAdapter musicAdapter;
    RecyclerView recyclerView;
    ImageView btnGoBack;
    String userId;
    EditText searchText;
    List<Music> mMusics;
    List<Music> searchMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list_songs);
        // Inflate the layout for this fragment

        searchText = findViewById(R.id.searchText);
        btnGoBack = findViewById(R.id.btnGoBack);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowListSongsActivity.this));


        Intent i = getIntent();
        userId = i.getStringExtra("userId");

        mMusics = new ArrayList<>();
        getMusics();

        searchMusic = new ArrayList<>();
        musicAdapter = new MusicAdapter(ShowListSongsActivity.this, searchMusic, "Double", userId);

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
                        musicAdapter = new MusicAdapter(ShowListSongsActivity.this, mMusics, "Double", userId);
                        recyclerView.setAdapter(musicAdapter);
//                        getMusics();
                    } else {
                        searchMusic.clear();
                        musicAdapter = new MusicAdapter(ShowListSongsActivity.this, searchMusic, "Double", userId);
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
//        recyclerView.setAdapter(musicAdapter);


        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowListSongsActivity.super.onBackPressed();
                finish();
            }
        });
    }

    private void getMusics() {
        // Lấy list song
        List<Song> songs = new ArrayList<>();
        DatabaseReference songRef = FirebaseDatabase.getInstance().getReference("Songs");
        songRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot songSnapshot : snapshot.getChildren()) {

                    Song song = songSnapshot.getValue(Song.class);
                    songs.add(song);

                    DatabaseReference artistRef = FirebaseDatabase.getInstance().getReference("Artists").child(song.getArtistId());
                    artistRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            Artist artist = snapshot2.getValue(Artist.class);
                            Music music = new Music(song, artist);
                            mMusics.add(music);
                            musicAdapter.notifyDataSetChanged();
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