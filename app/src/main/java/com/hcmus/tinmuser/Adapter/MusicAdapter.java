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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.Model.Music;
import com.hcmus.tinmuser.Model.Song;
import com.hcmus.tinmuser.Activity.PlaySongActivity;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    Context context;
    List<Music> mMusics;
    String playType;
    String userId;
    Boolean isFavorite;
    FirebaseUser mUser;
    ArrayList<String> mUserListFavoriteSong;

    public MusicAdapter(Context context, List<Music> mMusics, String playType, String userId, ArrayList<String> mUserListFavoriteSong) {
        this.context = context;
        this.mMusics = mMusics;
        this.playType = playType;
        this.userId = userId;
        this.mUserListFavoriteSong = mUserListFavoriteSong;
    }

    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.music_row, parent, false);


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MyViewHolder holder, int position) {
        Song song = mMusics.get(position).getSong();
        Artist artist = mMusics.get(position).getArtist();

        holder.name_text.setText(song.getName());

        String artistName = artist.getName();
        holder.artist_text.setText(artistName);
        Glide.with(context)
                .load(song.getImageURL())
                .into(holder.avatar);

        // Set on click item for PlaySongActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlaySongActivity.class);
                intent.putExtra("playType", playType);
                intent.putExtra("userId", userId);
                intent.putExtra("songId", song.getId());
                intent.putExtra("listFavoriteSong", mUserListFavoriteSong);
                context.startActivity(intent);
            }
        });

        if(getIsFavorite(song.getId())) {
            holder.btnFavorite.setImageResource(R.drawable.ic_favorite_on);
        } else {
            holder.btnFavorite.setImageResource(R.drawable.ic_favorite_off);
        }

        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = getIsFavorite(song.getId());
                if (isFavorite) {
                    holder.btnFavorite.setImageResource(R.drawable.ic_favorite_off);
                    DatabaseReference favoriteRef = FirebaseDatabase.getInstance().getReference("Favorites").child(mUser.getUid()).child(song.getId());
                    favoriteRef.removeValue();
                } else {
                    holder.btnFavorite.setImageResource(R.drawable.ic_favorite_on);
                    DatabaseReference favoriteRef = FirebaseDatabase.getInstance().getReference("Favorites").child(mUser.getUid()).child(song.getId()).child("id");
                    favoriteRef.setValue(song.getId());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mMusics.size();
    }

    public Boolean getIsFavorite(String idSong) {
        if (mUserListFavoriteSong.contains(idSong)) return true;
        return false;
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView artist_text, name_text;
        ImageView avatar, btnFavorite;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            artist_text = itemView.findViewById(R.id.artist_name);
            name_text = itemView.findViewById(R.id.song_name);
            avatar = itemView.findViewById(R.id.song_avatar);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            mUser = FirebaseAuth.getInstance().getCurrentUser();

        }
    }
}
