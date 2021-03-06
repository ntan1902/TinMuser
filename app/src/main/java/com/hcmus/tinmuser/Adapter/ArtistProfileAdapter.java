package com.hcmus.tinmuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hcmus.tinmuser.Activity.ArtistProfileActivity;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.R;

import java.util.List;

public class ArtistProfileAdapter extends RecyclerView.Adapter<ArtistProfileAdapter.ViewHolder> {
    private Context context;
    private List<Artist> mArtists;
    String playType;
    String userId;
    public ArtistProfileAdapter(Context context, List<Artist> mArtists, String playType, String userId) {
        this.context = context;
        this.mArtists = mArtists;
        this.playType = playType;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.artist_item,
                        parent,
                        false);
        return new ArtistProfileAdapter.ViewHolder(view);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentArtist = new Intent(context, ArtistProfileActivity.class);
                intentArtist.putExtra("artistId", artist.getId());
                intentArtist.putExtra("playType", playType);
                intentArtist.putExtra("userId", userId);
                context.startActivity(intentArtist);
            }
        });
    }

    @Override
    public int getItemCount() {

        return mArtists.size();
    }

    private void loadBitmapIntoSongImage(@NonNull ViewHolder holder, String imageURL) {
        // Metadata
        try {

            Glide.with(context)
                    .asBitmap()
                    .load(imageURL)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(@Nullable Palette palette) {
                                    Palette.Swatch swatch = palette.getDominantSwatch();
                                    if (swatch != null) {

                                        holder.artistName.setTextColor(swatch.getBodyTextColor());
                                    } else {

                                        holder.artistName.setTextColor(Color.DKGRAY);

                                    }
                                }
                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
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
