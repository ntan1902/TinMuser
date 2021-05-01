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
import com.hcmus.tinmuser.Activity.ArtistProfileActivity;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.R;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    private Context context;
    private List<Artist> mArtists;
    private ClickItemListener clickItemListener;
    //    public static final int ARTIST_INFO = 0;
//    public static final int LOAD_MORE = 1;
    String playType;
    String userId;

    public ArtistAdapter(Context context, List<Artist> mArtists, String playType, String userId, ClickItemListener clickItemListener) {
        this.context = context;
        this.mArtists = mArtists;
        this.playType = playType;
        this.userId = userId;
        this.clickItemListener = clickItemListener;
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
        return new ViewHolder(view);
//        return new ArtistAdapter.ViewHolder(view);
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

//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intentArtist = new Intent(context, ArtistProfileActivity.class);
//                    intentArtist.putExtra("artistId", artist.getId());
//                    intentArtist.putExtra("playType", playType);
//                    intentArtist.putExtra("userId", userId);
//                    context.startActivity(intentArtist);
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {

        return mArtists.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickItemListener.onClick(mArtists.get(getAdapterPosition()).getId(), playType);
                }
            });
        }
    }

    public interface ClickItemListener{
        void onClick(String path, String playType);
    }
}
