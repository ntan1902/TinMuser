package com.hcmus.tinmuser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import com.hcmus.tinmuser.Fragment.SearchFragment;
import com.hcmus.tinmuser.Fragment.UsersFragment;
import com.hcmus.tinmuser.Model.User;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    private FirebaseUser mUser;
    private DatabaseReference mRef;


    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPageAdapter viewPageAdapter;
    private final int[] tabIcons = {
            R.drawable.home,
            R.drawable.ic_search,
            R.drawable.chat,
            R.drawable.users
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUser = FirebaseAuth.getInstance()
                .getCurrentUser();
        mRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(mUser.getUid());

        RelativeLayout toolbar = findViewById(R.id.toolbar);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                ImageView imageView = (ImageView) toolbar.getChildAt(0);
                if (user.getImageURL().equals("default")) {
                    imageView.setImageResource(R.drawable.profile_image);
                } else {
                    Glide.with(MainActivity.this)
                            .load(user.getImageURL())
                            .into(imageView);

                }

                ImageView btnSetting = (ImageView) toolbar.getChildAt(1);
                btnSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
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
        viewPageAdapter.addFragment(new ChatFragment(), "Chat");
        viewPageAdapter.addFragment(new UsersFragment(), "Users");

        viewPager.setAdapter(viewPageAdapter);

        tabLayout.setupWithViewPager(viewPager);

        //Icons for TabLayout
        setTabIcons();
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
}