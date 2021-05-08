package com.hcmus.tinmuser.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.Model.Music;
import com.hcmus.tinmuser.Model.PlayDouble;
import com.hcmus.tinmuser.Model.Song;
import com.hcmus.tinmuser.R;
import com.hcmus.tinmuser.Service.SongService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaySongActivity extends Activity implements ServiceConnection {

    private TextView txtSongName, txtArtistName, txtDurationPlayed, txtDurationTotal;
    private ImageView songImage, btnNext, btnPrev, btnGoBack, btnFavorite, btnRepeat, btnMenu;
    private FloatingActionButton btnPlay;
    private SeekBar seekBar;

    private Intent songServiceIntent;
    private SongService songService;

    private boolean isPlay = true;
    private boolean isRepeat = false;
    private Handler handler = new Handler();

    private String playType;
    private String userId;
    private String uri;
    private String songName;
    private String artistName;
    private String artistImageURL;
    private String imageURL;
    private String artistId;
    private String songId;
    private List<Music> mMusics;
    private int position = 0;

    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private boolean isExist = false;
    private String playDoubleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        initializeId();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        updateStatus("online");

        // Receive data from MusicAdapter
        getDataFromIntent();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (playType.equals("Double")) {

                        setValuePlayDouble("progressChanged", true, progress);

                    }
                    songService.seekTo(progress * 1000);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        PlaySongActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (songService != null &&
                        songService.getMediaPlayer() != null) {

                    if(songService.isPlaying())
                        updateProgressBar();

                    if(songService.isComplete()) {
                        if (!isRepeat) {
                            btnNext.performClick();
                        } else {
                            isPlay = false;
                            btnPlay.setImageResource(R.drawable.ic_play);
                            songService.reset();
                        }
                    }
                }
                handler.postDelayed(this, 100);
            }

        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay) {
                    isPlay = false;
                    btnPlay.setImageResource(R.drawable.ic_play);
                    songService.pause();
                } else {
                    isPlay = true;
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    songService.start();
                }

                if (playType.equals("Double")) {
                    setValuePlayDouble("isPlay", isPlay, -1);
                }
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRepeat) {
                    isRepeat = false;
                    btnRepeat.setImageResource(R.drawable.ic_repeat_off);
                    songService.setLooping(false);
                } else {
                    isRepeat = true;
                    btnRepeat.setImageResource(R.drawable.ic_repeat_on);
                    songService.setLooping(true);
                }

                if (playType.equals("Double")) {
                    setValuePlayDouble("isRepeat", isRepeat, -1);
                }
            }
        });

        //Favorite
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("FavoriteSongs").child(mUser.getUid()).child(songId);
                favRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) { //isFavorite
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

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlaySongActivity.this, MenuOfSongActivity.class);
                i.putExtra("songId", songId);
                i.putExtra("artistId", artistId);
                i.putExtra("userId", userId);
                i.putExtra("playType", playType);
                startActivity(i);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = (position - 1) < 0 ? (mMusics.size() - 1) : (position - 1);
                isPlay = true;
                btnPlay.setImageResource(R.drawable.ic_pause);

                initializeMusic(position);

                // Nghe nhạc với nhau trong khi nhắn tin
                if (playType.equals("Double")) {
                    final DatabaseReference playDoubleIdRef = FirebaseDatabase
                            .getInstance()
                            .getReference("PlayDouble")
                            .child(playDoubleId);
                    playDoubleIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Map<String, Object> map = new HashMap<>();

                            map.put("uri", uri);
                            map.put("artistName", artistName);
                            map.put("artistImageURL", artistImageURL);
                            map.put("songName", songName);
                            map.put("songId", songId);
                            map.put("imageURL", imageURL);
                            map.put("position", position);


                            playDoubleIdRef.updateChildren(map);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    getIsFavorite(songId);
                    createService();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = (position + 1) % mMusics.size();
                isPlay = true;
                btnPlay.setImageResource(R.drawable.ic_pause);
                initializeMusic(position);

                // Nghe nhạc với nhau trong khi nhắn tin
                if (playType.equals("Double")) {
                    final DatabaseReference playDoubleIdRef = FirebaseDatabase
                            .getInstance()
                            .getReference("PlayDouble")
                            .child(playDoubleId);
                    playDoubleIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Map<String, Object> map = new HashMap<>();

                            map.put("uri", uri);
                            map.put("artistName", artistName);
                            map.put("artistImageURL", artistImageURL);
                            map.put("songName", songName);
                            map.put("songId", songId);
                            map.put("imageURL", imageURL);
                            map.put("position", position);

                            playDoubleIdRef.updateChildren(map);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    getIsFavorite(songId);
                    createService();
                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaySongActivity.super.onBackPressed();
                finish();
            }
        });

    }

    private void initializePlayDouble() {
        final DatabaseReference playDoubleIdRef = FirebaseDatabase.getInstance().getReference("PlayDouble");

        playDoubleIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PlayDouble playDouble = dataSnapshot.getValue(PlayDouble.class);

                    if ((playDouble.getUser1().equals(userId) && playDouble.getUser2().equals(mUser.getUid())) ||
                            (playDouble.getUser1().equals(mUser.getUid()) && playDouble.getUser2().equals(userId))) {
                        isExist = true;

                        playDouble.setUri(uri);
                        playDouble.setArtistName(artistName);
                        playDouble.setArtistImageURL(artistImageURL);
                        playDouble.setSongName(songName);
                        playDouble.setImageURL(imageURL);
                        playDouble.setIsPlay(true);
                        playDouble.setIsRepeat(false);
                        playDouble.setSongId(songId);
                        playDouble.setPosition(position);

                        playDoubleId = dataSnapshot.getKey();
                        playDoubleIdRef.child(playDoubleId).setValue(playDouble);
                    }
                }
                if (!isExist) {
                    PlayDouble playDouble = new PlayDouble(userId, mUser.getUid(), uri, songId, songName, artistName, artistImageURL, imageURL, 0, position, true, false);
                    playDoubleId = userId + mUser.getUid();
                    playDoubleIdRef.child(playDoubleId).setValue(playDouble);
                }

                // Create SongService
                final DatabaseReference playDoubleIdRef = FirebaseDatabase
                        .getInstance()
                        .getReference("PlayDouble")
                        .child(playDoubleId)
                        .child("position");
                playDoubleIdRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        if (snapshot2.exists()) {
                            btnPlay.setImageResource(R.drawable.ic_pause);


                            position = snapshot2.getValue(Integer.class);
                            initializeMusic(position);

                            createService();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // Listen song
                final DatabaseReference isPlayRef = FirebaseDatabase
                        .getInstance()
                        .getReference("PlayDouble")
                        .child(playDoubleId)
                        .child("isPlay");
                isPlayRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        if (snapshot2.exists() && songService != null) {
                            Boolean _isPlay = snapshot2.getValue(Boolean.class);
                            if (_isPlay) {
                                isPlay = true;
                                btnPlay.setImageResource(R.drawable.ic_pause);
                                songService.start();
                            } else {
                                isPlay = false;
                                btnPlay.setImageResource(R.drawable.ic_play);
                                songService.pause();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // Set progress change
                final DatabaseReference progressChangedRef = FirebaseDatabase
                        .getInstance()
                        .getReference("PlayDouble")
                        .child(playDoubleId)
                        .child("progressChanged");
                progressChangedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        if (snapshot2.exists() && songService != null) {
                            Integer progress = snapshot2.getValue(Integer.class);
                            songService.seekTo(progress * 1000);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // Set repeat
                final DatabaseReference isRepeatRef = FirebaseDatabase
                        .getInstance()
                        .getReference("PlayDouble")
                        .child(playDoubleId)
                        .child("isRepeat");
                isRepeatRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        if (snapshot2.exists() && songService != null) {
                            Boolean _isRepeat = snapshot2.getValue(Boolean.class);
                            if (_isRepeat) {
                                isRepeat = true;
                                btnRepeat.setImageResource(R.drawable.ic_repeat_on);
                                songService.setLooping(true);

                            } else {
                                isRepeat = false;
                                btnRepeat.setImageResource(R.drawable.ic_repeat_off);
                                songService.setLooping(false);
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

    private void createService() {
        songServiceIntent = new Intent(this, SongService.class);
        songServiceIntent.putExtra("uri", uri);
        songServiceIntent.putExtra("songName", songName);
        songServiceIntent.putExtra("songId", songId);
        songServiceIntent.putExtra("artistName", artistName);
        songServiceIntent.putExtra("artistImageURL", artistImageURL);
        songServiceIntent.putExtra("imageURL", imageURL);
        songServiceIntent.putExtra("playType", playType);
        songServiceIntent.putExtra("userId", userId);
        bindService(songServiceIntent, this, Context.BIND_AUTO_CREATE);
        startService(songServiceIntent);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        songId = intent.getStringExtra("songId");
        userId = intent.getStringExtra("userId");
        playType = intent.getStringExtra("playType");
        getIsFavorite(songId);
        mMusics = new ArrayList<>();
        getMusics(songId);


    }

    private void getMusics(String songId) {
        // Lấy list song
        List<Song> songs = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Songs")
                .child(songId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String categoryId = snapshot.getValue(Song.class).getCategoryId();

                        FirebaseDatabase.getInstance()
                                .getReference("Songs")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot songSnapshot : snapshot.getChildren()) {
                                            Song song = songSnapshot.getValue(Song.class);

                                            if (song.getCategoryId().equals(categoryId)) {
                                                songs.add(song);

                                                // Lấy ca sĩ
                                                FirebaseDatabase.getInstance()
                                                        .getReference("Artists")
                                                        .child(song.getArtistId())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                Artist artist = snapshot.getValue(Artist.class);

                                                                Music music = new Music(song, artist);
                                                                mMusics.add(music);

                                                                if (song.getId().equals(songId)) {
                                                                    position = mMusics.size() - 1;
                                                                    initializeMusic(position);

                                                                    // Nghe nhạc với nhau trong khi nhắn tin
                                                                    if (playType.equals("Double")) {
                                                                        initializePlayDouble();
                                                                    } else {
                                                                        // Start SongService
                                                                        createService();
                                                                    }
                                                                }



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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void initializeMusic(int position) {
        Song song = mMusics.get(position).getSong();
        Artist artist = mMusics.get(position).getArtist();

        uri = song.getUri();
        songName = song.getName();
        imageURL = song.getImageURL();
        artistName = artist.getName();
        artistImageURL = artist.getImageURL();
        artistId = artist.getId();
        songId = song.getId();

        // Create text, image, button skip
        txtSongName.setText(songName);
        txtArtistName.setText(artistName);
        loadBitmapIntoSongImage(imageURL);
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
                            imageAnimation(PlaySongActivity.this, songImage, resource);

                            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(@Nullable Palette palette) {
                                    Palette.Swatch swatch = palette.getDominantSwatch();
                                    if (swatch != null) {
                                        ImageView gradient = findViewById(R.id.gradient);
                                        RelativeLayout container = findViewById(R.id.container);

                                        gradient.setBackgroundResource(R.drawable.gradient_bg_play_song);
                                        container.setBackgroundResource(R.color.primaryDark);

                                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                                new int[]{swatch.getRgb(), 0x0000000});
                                        gradient.setBackground(gradientDrawable);

                                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                                new int[]{swatch.getRgb(), swatch.getRgb()});
                                        container.setBackground(gradientDrawableBg);

                                        txtSongName.setTextColor(swatch.getTitleTextColor());
                                        txtArtistName.setTextColor(swatch.getBodyTextColor());
                                        txtDurationTotal.setTextColor(swatch.getTitleTextColor());
                                        txtDurationPlayed.setTextColor(swatch.getTitleTextColor());
                                    } else {
                                        ImageView gradient = findViewById(R.id.gradient);
                                        RelativeLayout container = findViewById(R.id.container);

                                        gradient.setBackgroundResource(R.drawable.gradient_bg_play_song);
                                        container.setBackgroundResource(R.color.primaryDark);

                                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                                new int[]{0xff000000, 0x0000000});
                                        gradient.setBackground(gradientDrawable);

                                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                                new int[]{0xff000000, 0xff000000});
                                        container.setBackground(gradientDrawableBg);

                                        txtSongName.setTextColor(Color.WHITE);
                                        txtArtistName.setTextColor(Color.DKGRAY);
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

    private void setValuePlayDouble(String child, Boolean value1, Integer value2) {
        final DatabaseReference setIsPlayRef = FirebaseDatabase
                .getInstance()
                .getReference("PlayDouble")
                .child(playDoubleId)
                .child(child);
        setIsPlayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (child.equals("isPlay") || child.equals("isRepeat")) {
                    setIsPlayRef.setValue(value1);
                } else if (child.equals("progressChanged")) {
                    setIsPlayRef.setValue(value2);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateProgressBar() {
        int currentPosition = songService.getCurrentPosition() / 1000;
        txtDurationPlayed.setText(formatTime(currentPosition));
        seekBar.setProgress(currentPosition);

        int duration = songService.getDuration() / 1000;
        txtDurationTotal.setText(formatTime(duration));
        seekBar.setMax(duration);


//        if (currentPosition == seekBar.getMax() && !isRepeat && isPlay) {
//            isPlay = false;
//            btnPlay.setImageResource(R.drawable.ic_play);
//
////            // TODO: BUG
////            if (!isRepeat) {
////                btnNext.performClick();
////            } else {
////                songService.reset();
////            }
//        }
    }

    private void initializeId() {
        txtSongName = findViewById(R.id.songName);
        txtSongName.setSelected(true);

        txtArtistName = findViewById(R.id.artistName);
        txtDurationPlayed = findViewById(R.id.durationPlayed);
        txtDurationTotal = findViewById(R.id.durationTotal);

        songImage = findViewById(R.id.songImage);
        btnNext = findViewById(R.id.btnSkipNext);
        btnPrev = findViewById(R.id.btnSkipPrevious);
        btnGoBack = findViewById(R.id.btnGoBack);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnRepeat = findViewById(R.id.btnRepeat);
        btnPlay = findViewById(R.id.btnPlay);
        btnMenu = findViewById(R.id.btnMenu);
        seekBar = findViewById(R.id.seekBar);
    }


    private String formatTime(int currentPosition) {

        String seconds = String.valueOf(currentPosition % 60);
        String minutes = String.valueOf(currentPosition / 60);

        return seconds.length() == 1 ? minutes + ":0" + seconds : minutes + ":" + seconds;
    }

    public void getIsFavorite(String idSong) {
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("FavoriteSongs")
                .child(mUser.getUid())
                .child(idSong);

        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_on);

                } else {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_off);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        SongService.LocalBinder binder = (SongService.LocalBinder) service;
        songService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        songService = null;
        unbindService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    private void updateStatus(String status) {
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());

        Map<String, Object> map = new HashMap<>();
        map.put("status", status);

        mRef.updateChildren(map);
    }

    public void imageAnimation(Context context, ImageView imageView, Bitmap bitmap) {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(animOut);
    }
}