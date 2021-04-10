package com.hcmus.tinmuser.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;

public class MusicListAdapter extends RecyclerView.Adapter< MusicListAdapter.MyViewHolder>{
    ArrayList<String> name;
    ArrayList<String> artist;
    ArrayList<String> image;
    Context context;
    public  MusicListAdapter(Context context, ArrayList<String> name, ArrayList<String> artist, ArrayList<String> image){
        this.context = context;
        this.name = name;
        this.artist = artist;
        this.image = image;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView artist_text, name_text;
        ImageView avatar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            artist_text = itemView.findViewById(R.id.artis_name);
            name_text = itemView.findViewById(R.id.song_name);
            avatar = itemView.findViewById(R.id.song_avatar);
        }
    }
    @NonNull
    @Override
    public  MusicListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view =  inflater.inflate(R.layout.music_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  MusicListAdapter.MyViewHolder holder, int position) {
        holder.name_text.setText(name.get(position));
        holder.artist_text.setText(artist.get(position));
        Glide.with(context)
                .load(image.get(position))
                .into(holder.avatar);
    }
    @Override
    public int getItemCount() {
        return artist.size();
    }

}