package com.hcmus.tinmuser;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PlaySongActivity extends Activity {

    private TextView txtSongName, txtArtistName, txtDurationPlayed, txtDurationTotal;
    private ImageView coverArt, btnNext, btnPrev, btnGoBack, btnShuffle, btnRepeat;
    private FloatingActionButton btnPlay;
    private SeekBar seekBar;

    private Intent songServiceIntent;
    private BroadcastReceiver broadcastReceiver;

    private boolean isPlay = false;
    private boolean isRepeat = false;
    private boolean isDone = false;
    private String uri = "";

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


        // Initialize intent filter for receive data from SongService
        IntentFilter songServiceFilter = new IntentFilter("SongService");
        broadcastReceiver = new MyBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, songServiceFilter);

        songServiceIntent = new Intent(this, SongService.class);
        songServiceIntent.putExtra("code", "start");
        songServiceIntent.putExtra("uri", uri);
        startService(songServiceIntent);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    songServiceIntent.putExtra("code", "progress");
                    songServiceIntent.putExtra("progress", progress);
                    startService(songServiceIntent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay) {
                    isPlay = false;
                    btnPlay.setImageResource(R.drawable.ic_play);
                    songServiceIntent.putExtra("code", "pause");

                } else {
                    isPlay = true;
                    btnPlay.setImageResource(R.drawable.ic_pause);

                    if(isDone) {
                        isDone = false;
                        seekBar.setProgress(0);
                        songServiceIntent.putExtra("code", "reset");
                    }
                    songServiceIntent.putExtra("code", "play");
                }
                startService(songServiceIntent);
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRepeat) {
                    isRepeat = false;
                    btnRepeat.setImageResource(R.drawable.ic_repeat_off);
                    songServiceIntent.putExtra("code", "repeat off");
                } else {
                    isRepeat = true;
                    btnRepeat.setImageResource(R.drawable.ic_repeat_on);
                    songServiceIntent.putExtra("code", "repeat on");
                }
                startService(songServiceIntent);
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

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra("code");
            if (code.equals("duration")) {
                int duration = intent.getIntExtra("duration", 0);
                seekBar.setMax(duration);
                txtDurationTotal.setText(formatTime(duration));
            } else if (code.equals("currentPosition")) {
                int currentPosition = intent.getIntExtra("currentPosition", 0);
                seekBar.setProgress(currentPosition);
                txtDurationPlayed.setText(formatTime(currentPosition));

                if(currentPosition == seekBar.getMax() && !isRepeat && isPlay) {
                    isPlay = false;
                    isDone = true;
                    btnPlay.setImageResource(R.drawable.ic_play);
                }
            }
        }//onReceive
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
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        stopService(songServiceIntent);
    }
}