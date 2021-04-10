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
import com.hcmus.tinmuser.Model.Song;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;

public class MusicListAdapter1 extends RecyclerView.Adapter< MusicListAdapter1.MyViewHolder>{
    ArrayList<Song> list;
    Context context;
    public  MusicListAdapter1(Context context,ArrayList<Song> list){
        this.context = context;
        this.list = list;
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
    public  MusicListAdapter1.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view =  inflater.inflate(R.layout.music_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  MusicListAdapter1.MyViewHolder holder, int position) {
        holder.name_text.setText(list.get(position).getName());
        holder.artist_text.setText(list.get(position).getArtis());
        Glide.with(context)
                .load(list.get(position).getImageUrl())
                .into(holder.avatar);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

}