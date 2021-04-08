package com.hcmus.tinmuser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmus.tinmuser.Fragment.ChatFragment;
import com.hcmus.tinmuser.Fragment.HomeFragment;
import com.hcmus.tinmuser.Fragment.ProfileFragment;
import com.hcmus.tinmuser.Fragment.UsersFragment;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    private FirebaseUser mUser;
    private DatabaseReference mRef;


    TabLayout tabLayout;
    ViewPager viewPager;
    private final int[] tabIcons = {
            R.drawable.home,
            R.drawable.chat,
            R.drawable.users,
            R.drawable.profile
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

        tabLayout = findViewById(R.id.tabLayout);
        viewPager =  findViewById(R.id.viewPager);

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.addFragment(new HomeFragment(), "");
        viewPageAdapter.addFragment(new ChatFragment(), "");
        viewPageAdapter.addFragment(new UsersFragment(), "");
        viewPageAdapter.addFragment(new ProfileFragment(), "");

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