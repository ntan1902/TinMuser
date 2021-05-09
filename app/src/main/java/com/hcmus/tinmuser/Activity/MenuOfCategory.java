package com.hcmus.tinmuser.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.hcmus.tinmuser.Adapter.CategoryAdapter;
import com.hcmus.tinmuser.Adapter.MusicAdapter;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.Model.Category;
import com.hcmus.tinmuser.Model.Music;
import com.hcmus.tinmuser.Model.Song;
import com.hcmus.tinmuser.R;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MenuOfCategory extends Activity {
    private RelativeLayout layoutTop;
    private ImageView btnGoBack;
    private RecyclerView recyclerCategory;
    private RecyclerView recycleMusic;
    private TextView txtCategoryName;

    private CategoryAdapter categoryAdapter;
    private MusicAdapter musicAdapter;

    private String categoryId = "";
    private List<Category> mCategories;
    private List<Music> mMusics;

    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_of_category);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        initializeID();

        // Receive data from MenuOfSongActivity, ArtistFragment
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");

        FirebaseDatabase.getInstance().getReference("Categories").child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Category category = snapshot.getValue(Category.class);
//                        txtCategoryName.setText(category.getName());
                        Glide.with(MenuOfCategory.this)
                                .load(category.getImageURL())
                                .into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                        layoutTop.setBackground(resource);
                                    }
                                });

                        loadBitmapIntoSongImage(category.getImageURL());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MenuOfCategory.this.onBackPressed();
                finish();
            }
        });

        mCategories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(MenuOfCategory.this, mCategories);
        recyclerCategory.setAdapter(categoryAdapter);
        getCategories();

        mMusics = new ArrayList<>();
        musicAdapter = new MusicAdapter(MenuOfCategory.this, mMusics, "Single", "");
        recycleMusic.setAdapter(musicAdapter);
        getMusics();

    }

    private void getCategories() {
        FirebaseDatabase.getInstance().getReference("Categories")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Category category = dataSnapshot.getValue(Category.class);

                            if(!category.getId().equals(categoryId)){
                                mCategories.add(category);
                                categoryAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getMusics() {
        FirebaseDatabase.getInstance().getReference("Songs")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mMusics.clear();

                        for (DataSnapshot songSnapshot : snapshot.getChildren()) {
                            Song song = songSnapshot.getValue(Song.class);

                            if (song.getCategoryId().equals(categoryId)) {
                                FirebaseDatabase.getInstance().getReference("Artists").child(song.getArtistId())
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
        txtCategoryName = findViewById(R.id.categoryName);

        recyclerCategory = findViewById(R.id.recyclerCategory);
        GridLayoutManager layoutManager = new GridLayoutManager(this,
                2);
        recyclerCategory.setHasFixedSize(true);
        recyclerCategory.setLayoutManager(layoutManager);
        recyclerCategory.setItemAnimator(new DefaultItemAnimator());
        recyclerCategory.setNestedScrollingEnabled(false);

        recycleMusic = findViewById(R.id.recyclerMusic);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
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

//                                        txtCategoryName.setTextColor(swatch.getBodyTextColor());

                                    } else {
                                        RelativeLayout container = findViewById(R.id.container);
                                        container.setBackgroundResource(R.color.grey_900);

                                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                                new int[]{0xff000000, 0xff000000});
                                        container.setBackground(gradientDrawableBg);

//                                        txtCategoryName.setTextColor(Color.DKGRAY);

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