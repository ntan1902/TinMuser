package com.hcmus.tinmuser.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Adapter.ArtistAdapter;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;
import java.util.List;

public class FavoriteArtistFragment extends Fragment {
    private RecyclerView recyclerArtist;
    private ArtistAdapter artistAdapter;
    private List<Artist> mArtists;
    private FirebaseUser mUser;

    public FavoriteArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerArtist = view.findViewById(R.id.recyclerArtist);
        recyclerArtist.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerArtist.setLayoutManager(gridLayoutManager);
        recyclerArtist.setItemAnimator(new DefaultItemAnimator());

        mArtists = new ArrayList<>();
        artistAdapter = new ArtistAdapter(getContext(), mArtists, "Single", "");
        recyclerArtist.setAdapter(artistAdapter);

        getFavoriteArtists();

        return view;
    }

    private void getFavoriteArtists() {
        FirebaseDatabase.getInstance().getReference("FavoriteArtists")
                .child(mUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mArtists.clear();
                        artistAdapter.notifyDataSetChanged();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String id_fav_artist = dataSnapshot.getKey();

                            FirebaseDatabase.getInstance().getReference("Artists")
                                    .child(id_fav_artist)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Artist artist = snapshot.getValue(Artist.class);
                                            mArtists.add(artist);
                                            artistAdapter.notifyDataSetChanged();
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