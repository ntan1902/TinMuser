package com.hcmus.tinmuser.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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
    ArrayList<Music> mMusics;
    MusicAdapter musicAdapter;
    RecyclerView recyclerView;
    ImageView btnGoBack;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list_songs);
        // Inflate the layout for this fragment

        btnGoBack = findViewById(R.id.btnGoBack);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowListSongsActivity.this));

        Intent i = getIntent();
        userId = i.getStringExtra("userId");

        mMusics = new ArrayList<>();
        getMusics();

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

                musicAdapter = new MusicAdapter(ShowListSongsActivity.this, mMusics, "Double", userId, null);
                recyclerView.setAdapter(musicAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}