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
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.Model.Song;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;

public class MenuOfSongActivity extends Activity {
    private RelativeLayout layoutFavorite, layoutArtist, layoutClose;
    private ImageView coverArt, btnFavorite;
    private TextView txtSongName, txtArtistName, txtFavorite;

    private String userId;
    private String playType;
    private String songId;
    private String artistId;

    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_of_song);

        initializeID();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        // Receive data from PlaySongActivity
        Intent intent = getIntent();
        songId = intent.getStringExtra("songId");
        artistId = intent.getStringExtra("artistId");
        userId = intent.getStringExtra("userId");
        playType = intent.getStringExtra("playType");

        getIsFavorite(songId);

        FirebaseDatabase.getInstance().getReference("Songs").child(songId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Song song = snapshot.getValue(Song.class);
                        txtSongName.setText(song.getName());
                        Glide.with(MenuOfSongActivity.this)
                                .load(song.getImageURL())
                                .into(coverArt);
                        FirebaseDatabase.getInstance().getReference("Artists").child(song.getArtistId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Artist artist = snapshot.getValue(Artist.class);

                                        String artistNameOfSong = artist.getName() + " - " + song.getName();
                                        txtArtistName.setText(artistNameOfSong);

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

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("Favorites").child(mUser.getUid()).child(songId);
                favRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            btnFavorite.setImageResource(R.drawable.ic_favorite_off);
                            favRef.removeValue();
                        } else {
                            btnFavorite.setImageResource(R.drawable.ic_favorite_on);
                            favRef.child("id").setValue(songId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        layoutArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentArtist = new Intent(MenuOfSongActivity.this, ArtistProfileActivity.class);
                intentArtist.putExtra("artistId", artistId);
                intentArtist.putExtra("userId", userId);
                intentArtist.putExtra("playType", playType);

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

    public void getIsFavorite(String idSong) {
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("Favorites")
                .child(mUser.getUid())
                .child(idSong);

        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_on);
                    txtFavorite.setText("Your favorite song");

                } else {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_off);
                    txtFavorite.setText("Add to favorite");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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