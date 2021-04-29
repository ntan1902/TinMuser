package com.hcmus.tinmuser.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;

public class MenuOfSongActivity extends Activity {
    private RelativeLayout layoutFavorite, layoutArtist, layoutClose;
    private ImageView coverArt, btnFavorite;
    private TextView txtSongName, txtArtistName, txtFavorite;

    private String userId;
    private String playType;
    private boolean isFavorite;

    private FirebaseUser mUser;
    private ArrayList<String> mUserListFavoriteSong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_of_song);

        initializeID();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        // Receive data from PlaySongActivity
        Intent intent = getIntent();
        String songId = intent.getStringExtra("songId");
        String songName = intent.getStringExtra("songName");
        String artistName = intent.getStringExtra("artistName");
        String artistImageURL = intent.getStringExtra("artistImageURL");
        String imageURL = intent.getStringExtra("imageURL");
        userId = intent.getStringExtra("userId");
        playType = intent.getStringExtra("playType");
        mUserListFavoriteSong = intent.getStringArrayListExtra("listFavoriteSong");

        txtSongName.setText(songName);
        txtArtistName.setText(artistName);
        Glide.with(this)
                .load(imageURL)
                .into(coverArt);

        //Favorite
        if (getIsFavorite(songId)) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_on);
            txtFavorite.setText("Your favorite song");
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_off);
            txtFavorite.setText("Add to favorite");
        }

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = getIsFavorite(songId);
                if (isFavorite) {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_off);
                    DatabaseReference favoriteRef = FirebaseDatabase.getInstance().getReference("Favorites").child(mUser.getUid()).child(songId);
                    favoriteRef.removeValue();
                    txtFavorite.setText("Add to favorite");
                    isFavorite = false;
                } else {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_on);
                    DatabaseReference favoriteRef = FirebaseDatabase.getInstance().getReference("Favorites").child(mUser.getUid()).child(songId).child("id");
                    favoriteRef.setValue(songId);
                    txtFavorite.setText("Your favorite song");
                    isFavorite = true;
                }
            }
        });

        layoutArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentArtist = new Intent(MenuOfSongActivity.this, ArtistProfileActivity.class);
                intentArtist.putExtra("artistName", artistName);
                intentArtist.putExtra("artistImageURL", artistImageURL);
                intentArtist.putExtra("userId", userId);
                intentArtist.putExtra("playType", playType);
                intentArtist.putExtra("listFavoriteSong", mUserListFavoriteSong);

                startActivity(intentArtist);
            }
        });

        layoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuOfSongActivity.this.onBackPressed();
                finish();
            }
        });
    }
    public Boolean getIsFavorite(String idSong) {
        if (mUserListFavoriteSong != null) {
            DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("Favorites").child(mUser.getUid());

            favRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mUserListFavoriteSong.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        mUserListFavoriteSong.add(dataSnapshot.getKey());
                        System.out.println("yyyy");
                        System.out.println(dataSnapshot.getKey());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (mUserListFavoriteSong.contains(idSong)) return true;
        }

        return false;
    }

    private void initializeID() {
        layoutFavorite = findViewById(R.id.layoutFavorite);
        layoutArtist = findViewById(R.id.layoutArtist);
        layoutClose = findViewById(R.id.layoutClose);
        coverArt = findViewById(R.id.songImage);
        txtSongName = findViewById(R.id.songName);
        txtArtistName = findViewById(R.id.artistName);
        btnFavorite = findViewById(R.id.btnFavorite);
        txtFavorite = findViewById(R.id.txtFavorite);
    }

}