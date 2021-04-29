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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hcmus.tinmuser.Activity.ArtistProfileActivity;
import com.hcmus.tinmuser.Activity.MenuOfSongActivity;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.Model.Chat;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;
import java.util.List;

public class Artist2Adapter extends RecyclerView.Adapter<Artist2Adapter.ViewHolder> {
    private Context context;
    private List<Artist> mArtists;

    //    public static final int ARTIST_INFO = 0;
//    public static final int LOAD_MORE = 1;
    String playType;
    String userId;
    ArrayList<String> mUserListFavoriteSong;

    public Artist2Adapter(Context context, List<Artist> mArtists, String playType, String userId, ArrayList<String> mUserListFavoriteSong) {
        this.context = context;
        this.mArtists = mArtists;
        this.playType = playType;
        this.userId = userId;
        this.mUserListFavoriteSong = mUserListFavoriteSong;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == ARTIST_INFO) {
//            View view = LayoutInflater
//                    .from(parent.getContext())
//                    .inflate(R.layout.artist_item_2,
//                            parent,
//                            false);
//            return new Artist2Adapter.ViewHolder(view);
//        } else if (viewType == LOAD_MORE) {
//            View view = LayoutInflater
//                    .from(parent.getContext())
//                    .inflate(R.layout.load_more,
//                            parent,
//                            false);
//            return new Artist2Adapter.ViewHolder(view);
//        } else {
//            return null;
//        }
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.artist_item_2,
                        parent,
                        false);
        return new Artist2Adapter.ViewHolder(view);
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
                    intentArtist.putExtra("artistName", artist.getName());
                    intentArtist.putExtra("artistImageURL", artist.getImageURL());
                    intentArtist.putExtra("playType", playType);
                    intentArtist.putExtra("userId", userId);
                    intentArtist.putExtra("listFavoriteSong", mUserListFavoriteSong);
                    context.startActivity(intentArtist);
                }
            });
        }
    }

    @Override
    public int getItemCount() {

        return mArtists.size();
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position < mArtists.size()) {
//            return ARTIST_INFO;
//        } else {
//            return LOAD_MORE;
//        }
//    }

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
