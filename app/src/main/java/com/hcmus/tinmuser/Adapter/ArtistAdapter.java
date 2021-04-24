package com.hcmus.tinmuser.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.R;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    private Context context;
    private List<Artist> mArtists;

    public ArtistAdapter(Context context, List<Artist> mArtists) {
        this.context = context;
        this.mArtists = mArtists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.artist_item,
                        parent,
                        false);
        return new ArtistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artist artist = mArtists.get(position);

        holder.artistName.setText(artist.getName());
        holder.artistName.setTextColor(Color.parseColor("#FFFFFF"));

        if (artist.getImageURL().equals("default")) {
            holder.imageView.setImageResource(R.drawable.profile_image);
        } else {
            Glide.with(context)
                    .load(artist.getImageURL())
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView artistName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            artistName = itemView.findViewById(R.id.artistName);
        }
    }
}
