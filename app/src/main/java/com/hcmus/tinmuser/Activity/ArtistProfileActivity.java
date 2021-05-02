package com.hcmus.tinmuser.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Adapter.ArtistProfileAdapter;
import com.hcmus.tinmuser.Adapter.ArtistMusicAdapter;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.Model.Music;
import com.hcmus.tinmuser.Model.Song;
import com.hcmus.tinmuser.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArtistProfileActivity extends Activity {
    private RelativeLayout layoutTop;
    private ImageView btnGoBack;
    private ImageView btnFavorite;
    private TextView txtArtistName;
    private TextView txtTotalFollow;
    private RecyclerView recyclerArtist;
    private RecyclerView recycleMusic;
    private ArtistProfileAdapter artistAdapter;
    private ArtistMusicAdapter artistMusicAdapter;

    private String artistId = "";
    private List<Artist> mArtists;
    private List<Music> mMusics;

    private String playType;
    private boolean isFavorite;
    private FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_profile);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        initializeID();

        // Receive data from MenuOfSongActivity, ArtistFragment
        Intent intent = getIntent();
        artistId = intent.getStringExtra("artistId");
        playType = intent.getStringExtra("playType");


        FirebaseDatabase.getInstance().getReference("Artists").child(artistId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Artist artist = snapshot.getValue(Artist.class);
                        txtArtistName.setText(artist.getName());
                        Glide.with(ArtistProfileActivity.this)
                                .load(artist.getImageURL())
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                        layoutTop.setBackground(resource);
                                    }
                                });
                        loadBitmapIntoSongImage(artist.getImageURL());

                        String totalFollowString = NumberFormat.getNumberInstance(Locale.US).format(
                                artist.getTotalFollow()) + " total followers";
                        txtTotalFollow.setText(totalFollowString);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArtistProfileActivity.this.onBackPressed();
                finish();
            }
        });

       btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference artistRef = FirebaseDatabase.getInstance().getReference("Artists").child(artistId);
                DatabaseReference favoriteRef = FirebaseDatabase.getInstance()
                        .getReference("FavoriteArtists")
                        .child(mUser.getUid())
                        .child(artistId);
                artistRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Artist artist = snapshot.getValue(Artist.class);
                        isFavorite = !isFavorite;
                        if (snapshot.exists()) {
                            if (isFavorite) {
                                artistRef.child("totalFollow").setValue(artist.getTotalFollow() + 1);
                                btnFavorite.setImageResource(R.drawable.ic_favorite_on);
                                String totalFollowString = NumberFormat.getNumberInstance(Locale.US).format(
                                        artist.getTotalFollow() + 1) + " total followers";
                                txtTotalFollow.setText(totalFollowString);
                                favoriteRef.child("id").setValue(artistId);
                            } else {
                                artistRef.child("totalFollow").setValue(artist.getTotalFollow() - 1);
                                btnFavorite.setImageResource(R.drawable.ic_favorite_off);
                                String totalFollowString = NumberFormat.getNumberInstance(Locale.US).format(
                                        artist.getTotalFollow() - 1) + " total followers";
                                txtTotalFollow.setText(totalFollowString);
                                favoriteRef.removeValue();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }});

        mArtists = new ArrayList<>();
        artistAdapter = new ArtistProfileAdapter(ArtistProfileActivity.this, mArtists, playType, mUser.getUid());
        recyclerArtist.setAdapter(artistAdapter);
        checkFavorite();
        getArtists();

        mMusics = new ArrayList<>();
        artistMusicAdapter = new ArtistMusicAdapter(ArtistProfileActivity.this, mMusics, playType, mUser.getUid());
        recycleMusic.setAdapter(artistMusicAdapter);
        getMusics();

    }

    private void checkFavorite(){
        DatabaseReference favoriteRef = FirebaseDatabase.getInstance()
                .getReference("FavoriteArtists")
                .child(mUser.getUid())
                .child(artistId);
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isFavorite = true;
                    btnFavorite.setImageResource(R.drawable.ic_favorite_on);
                }
                else{
                    isFavorite = false;
                    btnFavorite.setImageResource(R.drawable.ic_favorite_off);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMusics() {
        // Lấy list song của artist
        FirebaseDatabase.getInstance().getReference("Artists").child(artistId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Artist artist = snapshot.getValue(Artist.class);
                        FirebaseDatabase.getInstance().getReference("Songs")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        mMusics.clear();

                                        for (DataSnapshot songSnapshot : snapshot.getChildren()) {
                                            Song song = songSnapshot.getValue(Song.class);

                                            if (song.getArtistId().equals(artist.getId())) {
                                                Music music = new Music(song, artist);
                                                mMusics.add(music);
                                                artistMusicAdapter.notifyDataSetChanged();
                                            }
                                        }
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

    private void getArtists() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Artists");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot artistSnapshot : snapshot.getChildren()) {
                    Artist artist = artistSnapshot.getValue(Artist.class);

                    if (!artist.getId().equals(artistId)) {
                        mArtists.add(artist);
                        artistAdapter.notifyDataSetChanged();
                    }
                }
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
        txtTotalFollow = findViewById(R.id.txtTotalFollow);
        btnFavorite = findViewById(R.id.btnFavorite);

        recyclerArtist = findViewById(R.id.recyclerArtist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerArtist.setHasFixedSize(true);
        recyclerArtist.setLayoutManager(layoutManager);
        recyclerArtist.setItemAnimator(new DefaultItemAnimator());
        recyclerArtist.setNestedScrollingEnabled(false);

        recycleMusic = findViewById(R.id.recyclerMusic);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false);
        recycleMusic.setHasFixedSize(true);
        recycleMusic.setLayoutManager(layoutManager2);
        recycleMusic.setItemAnimator(new DefaultItemAnimator());
        recycleMusic.setNestedScrollingEnabled(false);
    }

    private void loadBitmapIntoSongImage(String imageURL) {
        // Metadata
        try {

            Glide.with(this)
                    .asBitmap()
                    .load(imageURL)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(@Nullable Palette palette) {
                                    Palette.Swatch swatch = palette.getDominantSwatch();
                                    if (swatch != null) {
                                        RelativeLayout container = findViewById(R.id.container);
                                        container.setBackgroundResource(R.color.grey_900);

                                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                                new int[]{swatch.getRgb(), swatch.getRgb()});
                                        container.setBackground(gradientDrawableBg);

                                        txtArtistName.setTextColor(swatch.getBodyTextColor());
                                        txtTotalFollow.setTextColor(swatch.getBodyTextColor());
                                    } else {
                                        RelativeLayout container = findViewById(R.id.container);
                                        container.setBackgroundResource(R.color.grey_900);

                                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                                new int[]{0xff000000, 0xff000000});
                                        container.setBackground(gradientDrawableBg);

                                        txtArtistName.setTextColor(Color.DKGRAY);
                                        txtTotalFollow.setTextColor(Color.DKGRAY);

                                    }
                                }
                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}