package com.hcmus.tinmuser;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PlaySongActivity extends Activity implements SongServiceCallbacks, ServiceConnection {

    private TextView txtSongName, txtArtistName, txtDurationPlayed, txtDurationTotal;
    private ImageView coverArt, btnNext, btnPrev, btnGoBack, btnShuffle, btnRepeat;
    private FloatingActionButton btnPlay;
    private SeekBar seekBar;

    private Intent songServiceIntent;
    private SongService songService;

    private boolean isPlay = false;
    private boolean isRepeat = false;
    private boolean isDone = false;
    private String uri = "";
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        initializeId();

        // Receive data from MusicAdapter
        Intent intent = getIntent();
        uri = intent.getStringExtra("uri");
        txtSongName.setText(intent.getStringExtra("songName"));
        txtArtistName.setText(intent.getStringExtra("artistName"));
        Glide.with(getApplicationContext())
                .load(intent.getStringExtra("imageURL"))
                .into(coverArt);


        songServiceIntent = new Intent(this, SongService.class);
        bindService(songServiceIntent, this, Context.BIND_AUTO_CREATE);
        songServiceIntent.putExtra("uri", uri);
        startService(songServiceIntent);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
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
                if (songService != null) {
                    int currentPosition = songService.getCurrentPosition() / 1000;

                    txtDurationPlayed.setText(formatTime(currentPosition));
                    seekBar.setProgress(currentPosition);


                    if (currentPosition == seekBar.getMax() && !isRepeat && isPlay) {
                        isPlay = false;
                        isDone = true;
                        btnPlay.setImageResource(R.drawable.ic_play);
                        songService.reset();
                    }

                }
                handler.postDelayed(this, 500);
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

                    if (isDone) {
                        isDone = false;
                        seekBar.setProgress(0);
//                        songService.reset();

                    }
                    songService.start();
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
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaySongActivity.super.onBackPressed();
            }
        });

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
        btnShuffle = findViewById(R.id.btnShuffle);
        btnRepeat = findViewById(R.id.btnRepeat);
        btnPlay = findViewById(R.id.btnPlay);
        seekBar = findViewById(R.id.seekBar);
    }


    private String formatTime(int currentPosition) {
        String res = "";

        String seconds = String.valueOf(currentPosition % 60);
        String minutes = String.valueOf(currentPosition / 60);


        if (seconds.length() == 1) {
            res = minutes + ":0" + seconds;
        } else {
            res = minutes + ":" + seconds;
        }
        return res;
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        SongService.LocalBinder binder = (SongService.LocalBinder) service;
        songService = binder.getService();
        songService.setCallbacks(PlaySongActivity.this); // register

        int duration = songService.getDuration() / 1000;
        txtDurationTotal.setText(formatTime(duration));
        seekBar.setMax(duration);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        songService = null;
        unbindService(this);
    }


}