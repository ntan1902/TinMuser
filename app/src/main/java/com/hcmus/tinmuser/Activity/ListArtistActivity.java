package com.hcmus.tinmuser.Activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class ListArtistActivity extends Activity{
    private RecyclerView recyclerArtist;
    private ArtistAdapter artistAdapter;

    private List<Artist> mArtists;
    ArrayList<String> mUserListFavoriteSong;
    private FirebaseUser mUser;
    private ImageView backBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_list);

        backBtn = (ImageView) findViewById(R.id.btnGoBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserListFavoriteSong = new ArrayList<>();
        getFavoriteSongs();

        recyclerArtist = findViewById(R.id.recyclerArtist);
        recyclerArtist.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,
                3);
        recyclerArtist.setLayoutManager(gridLayoutManager);
        recyclerArtist.setItemAnimator(new DefaultItemAnimator());

        mArtists = new ArrayList<>();
        artistAdapter = new ArtistAdapter(this, mArtists, "Single", "");
        recyclerArtist.setAdapter(artistAdapter);
        getArtists();

    }

    private void getArtists() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Artists");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot artistSnapshot : snapshot.getChildren()) {
                    Artist artist = artistSnapshot.getValue(Artist.class);

                    mArtists.add(artist);
                    artistAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFavoriteSongs(){
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("FavoriteSongs").child(mUser.getUid());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUserListFavoriteSong.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String fav_song = dataSnapshot.getKey();
                    mUserListFavoriteSong.add(fav_song);
                    artistAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}