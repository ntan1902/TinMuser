package com.hcmus.tinmuser;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class SongService extends Service {

    private String SongServiceFilter = "SongService";
    private MediaPlayer mediaPlayer;
    private int playbackPosition = 0;
    private Thread progressThread;

//    // Binder given to clients
//    private final IBinder binder = new LocalBinder();
//    // Registered callbacks
//    private SongServiceCallbacks serviceCallbacks;
//    public void setCallbacks(SongServiceCallbacks callbacks) {
//        serviceCallbacks = callbacks;
//    }
//
//
//    // Class used for the client Binder.
//    public class LocalBinder extends Binder {
//        SongService getService() {
//            // Return this instance of MyService so clients can call public methods
//            return SongService.this;
//        }
//    }


    @Override
    public IBinder onBind(Intent intent) {
//        return binder;
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("SongService", "onCreate");

        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        // For example to set the volume of played media to maximum.
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    }

    @Override
    public void onDestroy() {
        Log.e("SongService", "onDestroy");
        killMediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String code = intent.getStringExtra("code");
        if (code.equals("start")) {
            String uriTemp = intent.getStringExtra("uri");
            playAudio(uriTemp);


        } else if (code.equals("play")) {
            mediaPlayer.seekTo(playbackPosition);
            mediaPlayer.start();
            progressThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(mediaPlayer.isPlaying()) {
                        try {
                            Thread.sleep(400);
                            Intent myFilteredResponse = new Intent(SongServiceFilter);
                            myFilteredResponse.putExtra("duration", mediaPlayer.getDuration());
                            myFilteredResponse.putExtra("currentPosition", mediaPlayer.getCurrentPosition());
                            sendBroadcast(myFilteredResponse);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            progressThread.start();
        } else if(code.equals("pause")) {
            playbackPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            try {
                progressThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if(code.equals("progressChanged")) {
            int progress = intent.getIntExtra("progress", 0);
            mediaPlayer.seekTo(progress * 1000);
        }

        return START_STICKY;
    }

    private void playAudio(String uri) {
        killMediaPlayer();

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(uri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                playbackPosition = 0;
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}