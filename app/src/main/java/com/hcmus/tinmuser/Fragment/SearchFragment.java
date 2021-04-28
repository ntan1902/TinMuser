package com.hcmus.tinmuser.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Adapter.CategoryAdapter;
import com.hcmus.tinmuser.Adapter.MusicAdapter;
import com.hcmus.tinmuser.Model.Artist;
import com.hcmus.tinmuser.Model.Category;
import com.hcmus.tinmuser.Model.Song;
import com.hcmus.tinmuser.Model.Music;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    RecyclerView recyclerView;
    MusicAdapter musicAdapter;
    CategoryAdapter categoryAdapter;
    EditText searchText;

    List<Music> mMusics;
    List<Music> searchMusic;
    List<Category> mCategories;

    FirebaseUser mUser;
    ArrayList<String> mUserListFavorites;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);


        searchText = view.findViewById(R.id.searchText);

        recyclerView = view.findViewById(R.id.recyclerViewSearch);
        recyclerView.setHasFixedSize(true);

        mCategories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), mCategories);
        setCategoryView();
        getCategories();

        mMusics = new ArrayList<>();
        searchMusic = new ArrayList<>();
//        musicAdapter = new MusicAdapter(getContext(), searchMusic, "Single", "", mUserListFavorites);

        mUserListFavorites = new ArrayList<>();
        getMusics();


        searchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (searchText.hasFocus()) {
                    if (s.toString().isEmpty()) {
                        setCategoryView();
                        getCategories();
                    } else {
                        searchMusic = new ArrayList<>();
                        for (Music x : mMusics) {
                            if (x.getArtist().getName().toLowerCase().contains(s.toString().toLowerCase()) ||
                                    x.getSong().getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                searchMusic.add(x);
//                                musicAdapter.notifyDataSetChanged();
                            }
                        }
                        setMusicView(searchMusic);

                    }
                }
            }
        });

        //Generate all favorite song of user.
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Favorites").child(mUser.getUid());
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUserListFavorites.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String fav_song = dataSnapshot.getKey();
                    mUserListFavorites.add(fav_song);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void setMusicView(List<Music> list) {
        musicAdapter = new MusicAdapter(getContext(), list, "Single", "", mUserListFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(musicAdapter);
    }

    private void setCategoryView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(categoryAdapter);
    }


    private void getCategories() {
        FirebaseDatabase.getInstance().getReference("Categories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mCategories.clear();
                        for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                            Category category = categorySnapshot.getValue(Category.class);

                            mCategories.add(category);
                            categoryAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

//    void setListView(List<Category> list) {
////        musicAdapter = new MusicAdapter(getContext(), list, "Single", "", mUserListFavorites);
//        categoryAdapter = new CategoryAdapter(getContext(), list);
//        recyclerView.setAdapter(categoryAdapter);
//    }

    private void getMusics() {
        // Lấy list song
        List<Song> songs = new ArrayList<>();
        DatabaseReference songRef = FirebaseDatabase.getInstance().getReference("Songs");
        songRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mMusics.clear();
                songs.clear();

                for (DataSnapshot songSnapshot : snapshot.getChildren()) {
                    Song song = songSnapshot.getValue(Song.class);
                    songs.add(song);

                    DatabaseReference artistRef = FirebaseDatabase.getInstance().getReference("Artists").child(song.getArtistId());
                    artistRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            Artist artist = snapshot2.getValue(Artist.class);
                            Music music = new Music(song, artist);
                            mMusics.add(music);
//                            musicAdapter.notifyDataSetChanged();
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