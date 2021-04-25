package com.hcmus.tinmuser.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.hcmus.tinmuser.Activity.MainActivity;
import com.hcmus.tinmuser.Fragment.SearchFragment;
import com.hcmus.tinmuser.Model.Music;

import java.io.IOException;
import java.util.List;

public class SongService extends Service {
    private static SongService instance = null;

    public static SongService getInstance() {
        return instance;
    }

    private MediaPlayer mediaPlayer;
    private int playbackPosition = 0;

    private String uri = "";
    //    private String songName = "";
//    private String artistName = "";
//    private String artistImageURL = "";
//    private String imageURL = "";
    private String playType = "";
    private String userId = "";
    private List<Music> mMusics = SearchFragment.mMusics;
    private int position = 0;

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //returns the instance of the service
    public class LocalBinder extends Binder {
        public SongService getService() {
            return SongService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.e("SongService", "onCreate");

        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        // For example to set the volume of played media to maximum.

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_DTMF), 0);
    }

    @Override
    public void onDestroy() {
        Log.e("SongService", "onDestroy");
        instance = null;
        killMediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        position = intent.getIntExtra("position", -1);
        String uriTemp = mMusics.get(position).getSong().getUri();
        if (!uriTemp.equals(uri)) {
            playAudio(uriTemp);
            playType = intent.getStringExtra("playType");
            userId = intent.getStringExtra("userId");

            uri = uriTemp;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

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
        playbackPosition = 0;
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPlayType() {
        return playType;
    }

    public void setPlayType(String playType) {
        this.playType = playType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void playAudio(String uri) {
        killMediaPlayer();

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(uri);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

    }


    public void killMediaPlayer() {
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