package com.hcmus.tinmuser.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Fragment.ChatFragment;
import com.hcmus.tinmuser.Fragment.HomeFragment;
import com.hcmus.tinmuser.Fragment.LibraryFragment;
import com.hcmus.tinmuser.Fragment.SearchFragment;
import com.hcmus.tinmuser.Fragment.UsersFragment;
import com.hcmus.tinmuser.Model.User;
import com.hcmus.tinmuser.R;
import com.hcmus.tinmuser.Service.SongService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity {
    private FirebaseUser mUser;
    private DatabaseReference mRef;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter viewPageAdapter;

    private ImageView btnPlay, songAvatar;
    private TextView txtSongName, txtArtistName;
    private SeekBar seekBar;
    private boolean isPlay = true;

    private RelativeLayout layoutPlay;
    private SongService songService;


    private final int[] tabIcons = {
            R.drawable.ic_home,
            R.drawable.ic_search,
            R.drawable.ic_library,
            R.drawable.ic_chat,
            R.drawable.ic_users
    };

    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button Play
        btnPlay = findViewById(R.id.btnPlay);

        // Layout Play
        layoutPlay = findViewById(R.id.layoutPlay);

        txtSongName = findViewById(R.id.songName);
        txtArtistName = findViewById(R.id.artistName);
        songAvatar = findViewById(R.id.songAvatar);
        seekBar = findViewById(R.id.seekBar);
        seekBar.getThumb().mutate().setAlpha(0);

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                songService = SongService.getInstance();

                if(songService != null && songService.getMediaPlayer() != null) {
                    layoutPlay.setVisibility(View.VISIBLE);

                    Glide.with(getApplicationContext())
                            .load(songService.getImageURL())
                            .into(songAvatar);
                    txtSongName.setText(songService.getSongName());
                    txtArtistName.setText(songService.getArtistName());

                    updateProgressBar();

                    if(songService.getMediaPlayer().isPlaying()) {
                        isPlay = true;
                        btnPlay.setImageResource(R.drawable.ic_pause);
                    } else {
                        isPlay = false;
                        btnPlay.setImageResource(R.drawable.ic_play);
                    }

                    // Set on click on PlaySong
                    layoutPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, PlaySongActivity.class);

                            intent.putExtra("uri", songService.getUri());
                            intent.putExtra("songName", songService.getSongName());
                            intent.putExtra("imageURL", songService.getImageURL());
                            intent.putExtra("artistName", songService.getArtistName());
                            intent.putExtra("artistImageURL", songService.getArtistImageURL());
                            intent.putExtra("playType", songService.getPlayType());
                            intent.putExtra("userId", songService.getUserId());
                            startActivity(intent);
                        }
                    });
                } else {
//                    Log.e("MAIN>>", "SongService doesn't exist");
                    layoutPlay.setVisibility(View.GONE);
                }
                handler.postDelayed(this, 100);

            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay) {
                    isPlay = false;
                    btnPlay.setImageResource(R.drawable.ic_play);
                    songService.pause();
                } else {
                    isPlay = true;
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    songService.start();
                }
            }
        });

        mUser = FirebaseAuth.getInstance()
                .getCurrentUser();
        updateStatus("online");


        mRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(mUser.getUid());

        RelativeLayout toolbar = findViewById(R.id.toolbar);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                //Toolbar
                ImageView imageView = (ImageView) toolbar.getChildAt(0);
                if (user.getImageURL().equals("default")) {
                    imageView.setImageResource(R.drawable.profile_image);
                } else {
                    Glide.with(getApplicationContext())
                            .load(user.getImageURL())
                            .into(imageView);

                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    }
                });

                ImageView btnSetting = (ImageView) toolbar.getChildAt(1);
                btnSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        tabLayout = findViewById(R.id.tabLayout);
        viewPager =  findViewById(R.id.viewPager);

        viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.addFragment(new HomeFragment(), "Home");
        viewPageAdapter.addFragment(new SearchFragment(), "Search");
        viewPageAdapter.addFragment(new LibraryFragment(), "Library");
        viewPageAdapter.addFragment(new ChatFragment(), "Chat");
        viewPageAdapter.addFragment(new UsersFragment(), "Musers");

        viewPager.setAdapter(viewPageAdapter);

        tabLayout.setupWithViewPager(viewPager);

        //Icons for TabLayout
        setTabIcons();
    }

    private void updateProgressBar() {
        int currentPosition = songService.getCurrentPosition() / 1000;
        seekBar.setProgress(currentPosition);

        int duration = songService.getDuration() / 1000;
        seekBar.setMax(duration);


        if (currentPosition == seekBar.getMax() && isPlay) {
            isPlay = false;
            btnPlay.setImageResource(R.drawable.ic_play);
            songService.reset();
        }
    }

    private void setTabIcons() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
    }


    // ViewPageAdapter
    static class ViewPageAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> fragments;
        private final ArrayList<String> titles;

        public ViewPageAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            fragments = new ArrayList<>();
            titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void updateStatus(String status) {
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());

        Map<String, Object> map = new HashMap<>();
        map.put("status", status);

        mRef.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateStatus("offline");
    }
}