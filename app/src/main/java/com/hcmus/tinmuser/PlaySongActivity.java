package com.hcmus.tinmuser;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class PlaySongActivity extends AppCompatActivity {

    Button btnPlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        btnPlay = findViewById(R.id.btnPlay);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer mediaPlayer = new MediaPlayer();

                try {
                    //put the url link here
                    mediaPlayer.setDataSource("https://firebasestorage.googleapis.com/v0/b/tinmuser.appspot.com/o/songs%2FRicky%20King%20-%20Song%20Song%20Blue.mp3?alt=media&token=4dd8a7ba-24d3-4128-ae8b-3d28ff6530e9");
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

}