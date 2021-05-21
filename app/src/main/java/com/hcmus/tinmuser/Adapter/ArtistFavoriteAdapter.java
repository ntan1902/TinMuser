package com.hcmus.tinmuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.tinmuser.Activity.ArtistProfileActivity;
import com.hcmus.tinmuser.Activity.ListArtistActivity;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.R;

import java.util.List;

public class ArtistFavoriteAdapter extends RecyclerView.Adapter<ArtistFavoriteAdapter.ViewHolder> {
    public ArtistFavoriteAdapter(Context context, List<Artist> mArtists, String playType) {
        this.context = context;
        this.mArtists = mArtists;
        this.playType = playType;
    }

    private Context context;
    private List<Artist> mArtists;
    String playType;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.artist_favorite_item,
                        parent,
                        false);
        return new ArtistFavoriteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < mArtists.size()) {
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


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentArtist = new Intent(context, ArtistProfileActivity.class);
                    intentArtist.putExtra("artistId", artist.getId());
                    intentArtist.putExtra("playType", playType);
                    context.startActivity(intentArtist);
                }
            });
        } else {
            holder.imageView.setImageResource(R.drawable.ic_add);
            holder.artistName.setText("Add Artist");
            holder.artistName.setTextColor(Color.parseColor("#FFFFFF"));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ListArtistActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mArtists.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView artistName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.artistImage);
            artistName = itemView.findViewById(R.id.artistName);
        }
    }
}
