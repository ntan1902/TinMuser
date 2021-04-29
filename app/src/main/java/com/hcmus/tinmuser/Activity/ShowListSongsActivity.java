package com.hcmus.tinmuser.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    ArrayList<Music> mMusics;
    MusicAdapter musicAdapter;
    RecyclerView recyclerView;
    ImageView btnGoBack;
    String userId;
    ArrayList<String> mUserListFavorites;
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
        mUserListFavorites = new ArrayList<>();
        musicAdapter = new MusicAdapter(ShowListSongsActivity.this, mMusics, "Double", userId);
        recyclerView.setAdapter(musicAdapter);


        getFavoriteSongs();

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
    private void getFavoriteSongs(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Favorites").child(userId);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUserListFavorites.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String fav_song = dataSnapshot.getKey();
                    mUserListFavorites.add(fav_song);
                    musicAdapter.notifyDataSetChanged();
                }

                getMusics();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}