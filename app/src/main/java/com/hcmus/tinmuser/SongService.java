package com.hcmus.tinmuser;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SongService extends Service {

    //    private String SongServiceFilter = "SongService";
    private MediaPlayer mediaPlayer;
    private int playbackPosition = 0;
    private String uri = "";

    SongServiceCallbacks songServiceCallbacks;
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }


    //returns the instance of the service
    public class LocalBinder extends Binder {
        public SongService getService() {
            return SongService.this;
        }
    }

    public void setCallbacks(SongServiceCallbacks callbacks) {
        songServiceCallbacks = callbacks;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("SongService", "onCreate");

        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        // For example to set the volume of played media to maximum.

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_DTMF), 0);
    }

    @Override
    public void onDestroy() {
        Log.e("SongService", "onDestroy");
        killMediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        uri = intent.getStringExtra("uri");
        playAudio(uri);
//        if (code.equals("start")) {
//            uri = intent.getStringExtra("uri");
//            playAudio(uri);
////            sendMessage("duration", mediaPlayer.getDuration() / 1000);
//
//        } else if (code.equals("play")) {
//            mediaPlayer.seekTo(playbackPosition);
//            mediaPlayer.start();
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (mediaPlayer.isPlaying()) {
//                        try {
//                            Thread.sleep(400);
////                            sendMessage("currentPosition", mediaPlayer.getCurrentPosition() / 1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();
//
//        } else if (code.equals("pause")) {
//            playbackPosition = mediaPlayer.getCurrentPosition();
//            mediaPlayer.pause();
//
//        } else if (code.equals("progress")) {
//            int progress = intent.getIntExtra("progress", 0);
//            mediaPlayer.seekTo(progress * 1000);
//
//        } else if (code.equals("repeat on")) {
//            mediaPlayer.setLooping(true);
//
//        } else if(code.equals("repeat off")) {
//            mediaPlayer.setLooping(false);
//
//        } else if(code.equals("reset")) {
//            playAudio(uri);
//        }

        return super.onStartCommand(intent,flags,startId);
    }

//    private <T> void sendMessage(String name, T value) {
//        Intent myFilteredResponse = new Intent(SongServiceFilter);
//        myFilteredResponse.putExtra("code", name);
//        myFilteredResponse.putExtra(name, (Integer)value);
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(myFilteredResponse);
//    }

    public void seekTo(int progress) {
        playbackPosition = progress;
        mediaPlayer.seekTo(progress);
    }

    public void setLooping(boolean looping) {
        mediaPlayer.setLooping(looping);
    }

    public void start() {
        mediaPlayer.seekTo(playbackPosition);
        mediaPlayer.start();
    }

    public void pause() {
        playbackPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
    }

    public void reset() {
        playAudio(uri);
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
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