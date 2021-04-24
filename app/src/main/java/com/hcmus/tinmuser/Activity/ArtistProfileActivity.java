package com.hcmus.tinmuser.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Adapter.ArtistAdapter;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;
import java.util.List;

public class ArtistProfileActivity extends Activity {
    private RelativeLayout layoutTop;
    private ImageView btnGoBack;
    private TextView txtArtistName;
    private RecyclerView recyclerArtist;
    private ArtistAdapter artistAdapter;

    private String artistName = "";
    private String artistImageURL = "";
    private List<Artist> mArtists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_profile);

        initializeID();

        // Receive data from MenuOfSongActivity
        Intent intent = getIntent();
        artistName = intent.getStringExtra("artistName");
        artistImageURL = intent.getStringExtra("artistImageURL");

        txtArtistName.setText(artistName);
        Glide.with(this)
                .load(artistImageURL)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        layoutTop.setBackground(resource);
                    }
                });


        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtistProfileActivity.this.onBackPressed();
                finish();
            }
        });

        mArtists = new ArrayList<>();
        getArtists();
    }

    private void getArtists() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Artists");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot artistSnapshot : snapshot.getChildren()){
                    Artist artist = artistSnapshot.getValue(Artist.class);

                    if(!artist.getName().equals(artistName)) {
                        mArtists.add(artist);
                    }
                }

                artistAdapter = new ArtistAdapter(getApplicationContext(), mArtists);
                recyclerArtist.setAdapter(artistAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeID() {
        layoutTop = findViewById(R.id.layoutTop);
        btnGoBack = findViewById(R.id.btnGoBack);
        txtArtistName = findViewById(R.id.artistName);
        recyclerArtist = findViewById(R.id.recyclerArtist);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerArtist.setHasFixedSize(true);
        recyclerArtist.setLayoutManager(layoutManager);
        recyclerArtist.setItemAnimator(new DefaultItemAnimator());
    }

}