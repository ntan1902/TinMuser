package com.hcmus.tinmuser.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Activity.ArtistProfileActivity;
import com.hcmus.tinmuser.Activity.ListArtistActivity;
import com.hcmus.tinmuser.Activity.MainActivity;
import com.hcmus.tinmuser.Activity.PlaySongActivity;
import com.hcmus.tinmuser.Adapter.ArtistAdapter;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FavoriteArtistFragment extends Fragment {
    static FavoriteArtistFragment fragment;

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
        try {
            ArrayList<String> listArtist = new ArrayList<>();
            Query queryFavorite = FirebaseDatabase.getInstance().getReference("Favorite").orderByChild(mUser.getUid()).equalTo(mUser.getUid());
            queryFavorite.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot item : snapshot.getChildren()) {
                        listArtist.add(item.getKey());
                        System.out.println("snapshot " + item.getKey());
                    }
                    for (String i : listArtist)
                        System.out.println("ListFavor " + i);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Artists");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot artistSnapshot : snapshot.getChildren()) {
                                if (listArtist.contains(artistSnapshot.getKey())) {
                                    Artist artist = artistSnapshot.getValue(Artist.class);
                                    mArtists.add(artist);
                                    artistAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onClick(String path, String playType) {
//        if (path.equals("Add")) {
//            startActivityForResult(new Intent(getActivity(), ListArtistActivity.class), 1);
//        } else {
//            Intent intentArtist = new Intent(getActivity(), ArtistProfileActivity.class);
//            intentArtist.putExtra("artistId", path);
//            intentArtist.putExtra("playType", playType);
//            startActivityForResult(intentArtist, 1);
//        }
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        getActivity().recreate();
//    }
}