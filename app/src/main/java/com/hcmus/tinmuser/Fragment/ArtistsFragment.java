package com.hcmus.tinmuser.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Adapter.Artist2Adapter;
import com.hcmus.tinmuser.EndlessRecyclerOnScrollListener;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;
import java.util.List;

public class ArtistsFragment extends Fragment {
    private static final int TOTAL_ITEM_EACH_LOAD = 5;
    private RecyclerView recyclerArtist;
    private Artist2Adapter artist2Adapter;
    private ProgressBar mProgressBar;

    private List<Artist> mArtists;
    private int currentPage = 1;


    public ArtistsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        mProgressBar = view.findViewById(R.id.progressbar);

        recyclerArtist = view.findViewById(R.id.recyclerArtist);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
//                LinearLayoutManager.VERTICAL,
//                false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),
                3);
        recyclerArtist.setHasFixedSize(true);
//        recyclerArtist.setLayoutManager(layoutManager);
        recyclerArtist.setLayoutManager(gridLayoutManager);

        recyclerArtist.setItemAnimator(new DefaultItemAnimator());
        recyclerArtist.setNestedScrollingEnabled(false);

        mArtists = new ArrayList<>();
        artist2Adapter = new Artist2Adapter(getContext(), mArtists, "Single", "");
        recyclerArtist.setAdapter(artist2Adapter);

        recyclerArtist.addOnScrollListener(new EndlessRecyclerOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                loadMoreData();
            }
        });

//        getArtists();
        loadData();

        return view;
    }

    private void loadMoreData() {
        currentPage++;
        loadData();
    }

    private void loadData() {
        // example
        // at first load : currentPage = 0 -> we startAt(0 * 10 = 0)
        // at second load (first loadmore) : currentPage = 1 -> we startAt(1 * 10 = 10)
        FirebaseDatabase.getInstance()
                .getReference("Artists")
                .orderByChild("number")
                .limitToFirst(TOTAL_ITEM_EACH_LOAD)
                .startAt(currentPage * TOTAL_ITEM_EACH_LOAD)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()) {

                            for (DataSnapshot artistSnapshot : snapshot.getChildren()) {
                                Artist artist = artistSnapshot.getValue(Artist.class);

                                mArtists.add(artist);
                                artist2Adapter.notifyDataSetChanged();
                            }
                        } else {
                            currentPage--;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getArtists() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Artists");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot artistSnapshot : snapshot.getChildren()) {
                    Artist artist = artistSnapshot.getValue(Artist.class);

                    mArtists.add(artist);
                    artist2Adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}