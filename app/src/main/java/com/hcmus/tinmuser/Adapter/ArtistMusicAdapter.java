package com.hcmus.tinmuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.hcmus.tinmuser.Activity.ArtistProfileActivity;
import com.hcmus.tinmuser.Activity.PlaySongActivity;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.Model.Music;
import com.hcmus.tinmuser.Model.Song;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;
import java.util.List;

public class ArtistMusicAdapter extends RecyclerView.Adapter<ArtistMusicAdapter.ViewHolder> {
    Context context;
    List<Music> mMusics;
    String playType;
    String userId;

    public ArtistMusicAdapter(Context context, List<Music> mMusics, String playType, String userId) {
        this.context = context;
        this.mMusics = mMusics;
        this.playType = playType;
        this.userId = userId;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.artist_music_item,
                        parent,
                        false);
        return new ArtistMusicAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = mMusics.get(position).getSong();
        Artist artist = mMusics.get(position).getArtist();

        holder.songName.setText(song.getName());
        holder.songName.setTextColor(Color.parseColor("#FFFFFF"));

        if (song.getImageURL().equals("default")) {
            holder.imageView.setImageResource(R.drawable.group);
        } else {
            Glide.with(context)
                    .load(song.getImageURL())
                    .into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlaySongActivity.class);
                intent.putExtra("playType", playType);
                intent.putExtra("userId", userId);
                intent.putExtra("songId", song.getId());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMusics.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView songName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            songName = itemView.findViewById(R.id.songName);
        }
    }
}
