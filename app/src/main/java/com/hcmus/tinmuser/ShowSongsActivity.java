package com.hcmus.tinmuser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.tinmuser.Adapter.MusicListAdapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import com.bumptech.glide.Glide;

public class ShowSongsActivity extends Activity {
    RecyclerView recyclerView;
    ArrayList<String> name=new ArrayList<>(Arrays.asList("Song1","Song2","Song3","Song4"));
    ArrayList<String> artis = new ArrayList<>(Arrays.asList("Tùng núi","Tùng núi","Tùng núi","Tùng núi"));
    ArrayList<String> images= new ArrayList<>(Arrays.asList(
            "https://firebasestorage.googleapis.com/v0/b/tinmuser.appspot.com/o/avatar.png?alt=media&token=cbbc9e99-21f7-4990-937d-42bf8399b549",
            "https://firebasestorage.googleapis.com/v0/b/tinmuser.appspot.com/o/avatar.png?alt=media&token=cbbc9e99-21f7-4990-937d-42bf8399b549",
            "https://firebasestorage.googleapis.com/v0/b/tinmuser.appspot.com/o/avatar.png?alt=media&token=cbbc9e99-21f7-4990-937d-42bf8399b549",
            "https://firebasestorage.googleapis.com/v0/b/tinmuser.appspot.com/o/avatar.png?alt=media&token=cbbc9e99-21f7-4990-937d-42bf8399b549"
    ));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list_songs);
        recyclerView = findViewById(R.id.recyclerView);
        MusicListAdapter adapter = new MusicListAdapter(this, name,artis,images);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
    }
}


