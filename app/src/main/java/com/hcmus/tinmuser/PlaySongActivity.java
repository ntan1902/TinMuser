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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PlaySongActivity extends Activity{

    private TextView txtSongName, txtArtistName, txtDurationPlayed, txtDurationTotal;
    private ImageView coverArt, btnNext, btnPrev, btnGoBack, btnShuffle, btnRepeat;
    private FloatingActionButton btnPlay;
    private SeekBar seekBar;

    private Intent songServiceIntent;
    private SongService songService;
    private BroadcastReceiver broadcastReceiver;
    private Handler handler = new Handler();


    private boolean isPlay = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

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

        IntentFilter songServiceFilter = new IntentFilter("SongService");
        broadcastReceiver = new MyBroadcastReceiver();
        registerReceiver(broadcastReceiver, songServiceFilter);

        songServiceIntent = new Intent(this, SongService.class);
        songServiceIntent.putExtra("code", "start");
        songServiceIntent.putExtra("uri", "https://firebasestorage.googleapis.com/v0/b/tinmuser.appspot.com/o/songs%2Fpho_nho.mp3?alt=media&token=383c69b9-1c93-4ea0-abfc-0f73f7343f9a");
        startService(songServiceIntent);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    songServiceIntent.putExtra("code", "progressChanged");
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
                    songServiceIntent.putExtra("code", "play");
                }
                startService(songServiceIntent);
            }
        });

    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int duration = intent.getIntExtra("duration", 0) / 1000;
            seekBar.setMax(duration);

            int currentPosition = intent.getIntExtra("currentPosition", 0) / 1000;
            seekBar.setProgress(currentPosition);

            txtDurationPlayed.setText(formatTime(currentPosition));
            txtDurationTotal.setText(formatTime(duration));

        }//onReceive
    }

    private String formatTime(int currentPosition) {
        String totalOut = "";
        String totalNew = "";

        String seconds = String.valueOf(currentPosition % 60);
        String minutes = String.valueOf(currentPosition / 60);

        totalOut = minutes + ":" +seconds;
        totalNew = minutes + ":0" +seconds;

        if(seconds.length() == 1) {
            return totalNew;
        } else {
            return totalOut;
        }
    }
}