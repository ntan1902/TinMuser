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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    public MusicAdapter(Context context, List<Music> mMusics, String playType, String userId) {
        this.context = context;
        this.mMusics = mMusics;
        this.playType = playType;
        this.userId = userId;
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
                context.startActivity(intent);
            }
        });

        //here
        getIsFavorite(song.getId(), holder);
        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("Favorites").child(mUser.getUid()).child(song.getId());
                favRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) { //isFavorite
                            holder.btnFavorite.setImageResource(R.drawable.ic_favorite_off);
                            favRef.removeValue();
                        } else {
                            holder.btnFavorite.setImageResource(R.drawable.ic_favorite_on);
                            favRef.child("id").setValue(song.getId());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    public void getIsFavorite(String idSong, MusicAdapter.MyViewHolder holder) {
        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("Favorites")
                .child(mUser.getUid())
                .child(idSong);

        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.btnFavorite.setImageResource(R.drawable.ic_favorite_on);

                } else {
                    holder.btnFavorite.setImageResource(R.drawable.ic_favorite_off);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mMusics.size();
    }

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
