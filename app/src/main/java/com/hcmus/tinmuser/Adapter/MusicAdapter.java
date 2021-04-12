package com.hcmus.tinmuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.tinmuser.Model.Music;
import com.hcmus.tinmuser.Model.Song;
import com.hcmus.tinmuser.PlaySongActivity;
import com.hcmus.tinmuser.R;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter< MusicAdapter.MyViewHolder>{
    Context context;
    List<Music> mMusics;
    public MusicAdapter(Context context, List<Music> mMusics){
        this.context = context;
        this.mMusics = mMusics;
    }
    @NonNull
    @Override
    public  MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view =  inflater.inflate(R.layout.music_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  MusicAdapter.MyViewHolder holder, int position) {
        Song song = mMusics.get(position).getSong();
        holder.name_text.setText(song.getName());

        String artistName = mMusics.get(position).getArtistName();
        holder.artist_text.setText(artistName);
        Glide.with(context)
                .load(song.getImageURL())
                .into(holder.avatar);

        // Set on click item for PlaySongActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlaySongActivity.class);
                intent.putExtra("uri", song.getUri());
                intent.putExtra("songName", song.getName());
                intent.putExtra("imageURL", song.getImageURL());
                intent.putExtra("artistName", artistName);
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mMusics.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView artist_text, name_text;
        ImageView avatar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            artist_text = itemView.findViewById(R.id.artist_name);
            name_text = itemView.findViewById(R.id.song_name);
            avatar = itemView.findViewById(R.id.song_avatar);
        }
    }
}
