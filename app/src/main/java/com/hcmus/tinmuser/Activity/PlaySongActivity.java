package com.hcmus.tinmuser.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Model.PlayDouble;
import com.hcmus.tinmuser.R;
import com.hcmus.tinmuser.Service.SongService;

import java.util.HashMap;
import java.util.Map;

public class PlaySongActivity extends Activity implements ServiceConnection {

    private TextView txtSongName, txtArtistName, txtDurationPlayed, txtDurationTotal;
    private ImageView coverArt, btnNext, btnPrev, btnGoBack, btnFavorite, btnRepeat, btnMenu;
    private FloatingActionButton btnPlay;
    private SeekBar seekBar;

    private Intent songServiceIntent;
    private SongService songService;

    private boolean isPlay = true;
    private boolean isRepeat = false;
    private boolean isFavorite = false;
    private Handler handler = new Handler();

    private String playType;
    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private String userId;
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
        Intent intent = getIntent();
        String uri = intent.getStringExtra("uri");
        String songName = intent.getStringExtra("songName");
        String artistName = intent.getStringExtra("artistName");
        String artistImageURL = intent.getStringExtra("artistImageURL");
        String imageURL = intent.getStringExtra("imageURL");
        userId = intent.getStringExtra("userId");
        playType = intent.getStringExtra("playType");


        // Nghe nhạc với nhau trong khi nhắn tin
        if (playType.equals("Double")) {
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
                            playDouble.setSongName(songName);
                            playDouble.setIsPlay(true);
                            playDouble.setIsRepeat(false);

                            playDoubleId = dataSnapshot.getKey();
                            playDoubleIdRef.child(playDoubleId).setValue(playDouble);
                        }
                    }
                    if (!isExist) {
                        PlayDouble playDouble = new PlayDouble(userId, mUser.getUid(), uri, songName, artistName, imageURL, 0, true, false);
                        playDoubleId = userId + mUser.getUid();
                        playDoubleIdRef.child(playDoubleId).setValue(playDouble);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        txtSongName.setText(songName);
        txtArtistName.setText(artistName);
        Glide.with(getApplicationContext())
                .load(imageURL)
                .into(coverArt);


        songServiceIntent = new Intent(this, SongService.class);
        songServiceIntent.putExtra("uri", uri);
        songServiceIntent.putExtra("songName", songName);
        songServiceIntent.putExtra("artistName", artistName);
        songServiceIntent.putExtra("artistImageURL", artistImageURL);
        songServiceIntent.putExtra("imageURL", imageURL);
        songServiceIntent.putExtra("playType", playType);
        songServiceIntent.putExtra("userId", userId);

        bindService(songServiceIntent, this, Context.BIND_AUTO_CREATE);
        startService(songServiceIntent);

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
                if (songService != null && songService.getMediaPlayer() != null) {
                    updateProgressBar();

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

                if(playType.equals("Double")) {
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

                if(playType.equals("Double")) {
                    setValuePlayDouble("isRepeat", isRepeat, -1);
                }
            }
        });

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    isFavorite = false;
                    btnFavorite.setImageResource(R.drawable.ic_favorite_off);
                } else {
                    isFavorite = true;
                    btnFavorite.setImageResource(R.drawable.ic_favorite_on);
                }
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PlaySongActivity.this, MenuOfSongActivity.class);
                i.putExtra("songName", songName);
                i.putExtra("imageURL", imageURL);
                i.putExtra("artistName", artistName);
                i.putExtra("artistImageURL", artistImageURL);
                i.putExtra("userId", userId);
                startActivity(i);
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

    private void setValuePlayDouble(String child, Boolean value1, Integer value2) {
        final DatabaseReference setIsPlayRef = FirebaseDatabase
                .getInstance()
                .getReference("PlayDouble")
                .child(playDoubleId)
                .child(child);
        setIsPlayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(child.equals("isPlay") || child.equals("isRepeat")) {
                    setIsPlayRef.setValue(value1);
                } else if(child.equals("progressChanged")) {
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


        if (currentPosition == seekBar.getMax() && !isRepeat && isPlay) {
            isPlay = false;
            btnPlay.setImageResource(R.drawable.ic_play);
            songService.reset();
            songService.pause();
        }
    }

    private void initializeId() {
        txtSongName = findViewById(R.id.songName);
        txtArtistName = findViewById(R.id.artistName);
        txtDurationPlayed = findViewById(R.id.durationPlayed);
        txtDurationTotal = findViewById(R.id.durationTotal);

        coverArt = findViewById(R.id.coverArt);
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
}