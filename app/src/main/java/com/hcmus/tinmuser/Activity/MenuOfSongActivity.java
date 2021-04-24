package com.hcmus.tinmuser.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hcmus.tinmuser.R;

public class MenuOfSongActivity extends Activity {
    private RelativeLayout layoutFavorite, layoutArtist, layoutClose;
    private ImageView coverArt;
    private TextView txtSongName, txtArtistName;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_of_song);

        initializeID();

        // Receive data from PlaySongActivity
        Intent intent = getIntent();
        String songName = intent.getStringExtra("songName");
        String artistName = intent.getStringExtra("artistName");
        String artistImageURL = intent.getStringExtra("artistImageURL");
        String imageURL = intent.getStringExtra("imageURL");
        userId = intent.getStringExtra("userId");

        txtSongName.setText(songName);
        txtArtistName.setText(artistName);
        Glide.with(this)
                .load(imageURL)
                .into(coverArt);

        layoutArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentArtist = new Intent(MenuOfSongActivity.this, ArtistProfileActivity.class);
                intentArtist.putExtra("artistName", artistName);
                intentArtist.putExtra("artistImageURL", artistImageURL);
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

    private void initializeID() {
        layoutFavorite = findViewById(R.id.layoutFavorite);
        layoutArtist = findViewById(R.id.layoutArtist);
        layoutClose = findViewById(R.id.layoutClose);
        coverArt = findViewById(R.id.coverArt);
        txtSongName = findViewById(R.id.songName);
        txtArtistName = findViewById(R.id.artistName);
    }

}