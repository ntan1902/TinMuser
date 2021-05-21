package com.hcmus.tinmuser.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Activity.ArtistProfileActivity;
import com.hcmus.tinmuser.Activity.ListArtistActivity;
import com.hcmus.tinmuser.Activity.MainActivity;
import com.hcmus.tinmuser.Activity.PlaySongActivity;
import com.hcmus.tinmuser.Adapter.ArtistFavoriteAdapter;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.Model.ChatList;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;
import java.util.List;

public class FavoriteArtistFragment extends Fragment {
    private RecyclerView recyclerArtist;
    private ArtistFavoriteAdapter artistFavoriteAdapter;
    private List<Artist> mArtists;
    private FirebaseUser mUser;
    EditText searchText;
    public FavoriteArtistFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerArtist = view.findViewById(R.id.recyclerArtist);
        recyclerArtist.setHasFixedSize(true);
        recyclerArtist.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerArtist.setItemAnimator(new DefaultItemAnimator());
        searchText = view.findViewById(R.id.searchText);



        mArtists = new ArrayList<>();

        getFavoriteArtists();
        searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==0){
                    getFavoriteArtists();
                }
                else{
                    getNameSearch(s.toString());
                }
            }
        });
        return view;
    }
    private void getNameSearch(String text){
        List<Artist> temp = new ArrayList<>();
        for(Artist artist : mArtists){
            if(artist.getName().toLowerCase().contains(text.toLowerCase())){
                temp.add(artist);
            }
        }
        artistFavoriteAdapter = new ArtistFavoriteAdapter(getContext(), temp, "Single");
        recyclerArtist.setAdapter(artistFavoriteAdapter);
    }
    private void getFavoriteArtists() {
        artistFavoriteAdapter = new ArtistFavoriteAdapter(getContext(), mArtists, "Single");
        recyclerArtist.setAdapter(artistFavoriteAdapter);
        FirebaseDatabase.getInstance().getReference("FavoriteArtists")
                .child(mUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mArtists.clear();
                        artistFavoriteAdapter.notifyDataSetChanged();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String id_fav_artist = dataSnapshot.getKey();

                            FirebaseDatabase.getInstance().getReference("Artists")
                                    .child(id_fav_artist)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Artist artist = snapshot.getValue(Artist.class);
                                            mArtists.add(artist);
                                            artistFavoriteAdapter.notifyDataSetChanged();
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}