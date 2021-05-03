package com.hcmus.tinmuser.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Adapter.FriendRecommendAdapter;
import com.hcmus.tinmuser.Adapter.FriendRequestAdapter;
import com.hcmus.tinmuser.Model.User;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;
import java.util.List;

public class AddFriendActivity extends Activity {
    private ImageView btnGoBack;
    private RecyclerView recyclerViewFriendRequest, recyclerViewFriendRecommend;
    private FriendRequestAdapter friendRequestAdapter;
    private FriendRecommendAdapter friendRecommendAdapter;

    private List<User> friendRecommends, friendRequests;
    private List<String> friendsOfUserId, friendRequestsOfUserId;

    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.hcmus.tinmuser.Activity.AddFriendActivity.this.onBackPressed();
            }
        });

        recyclerViewFriendRequest = findViewById(R.id.recyclerViewFriendRequest);
        recyclerViewFriendRequest.setHasFixedSize(true);
        recyclerViewFriendRequest.setLayoutManager(new LinearLayoutManager(AddFriendActivity.this));

        recyclerViewFriendRecommend = findViewById(R.id.recyclerViewFriendRecommend);
        recyclerViewFriendRecommend.setHasFixedSize(true);
        recyclerViewFriendRecommend.setLayoutManager(new LinearLayoutManager(AddFriendActivity.this));

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        //Get all friends of user
        DatabaseReference mFriendRef = FirebaseDatabase.getInstance().getReference("Friends").child(mUser.getUid());

        friendsOfUserId = new ArrayList<>();

        mFriendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsOfUserId.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userId = dataSnapshot.getKey();
                    Log.e("friends of user: ", userId);
                    friendsOfUserId.add(userId);
                }
                getFriendRecommends(mUser.getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference mRequestRef = FirebaseDatabase.getInstance().getReference("FriendRequests").child(mUser.getUid());

        friendRequestsOfUserId = new ArrayList<>();

        mRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendRequestsOfUserId.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userId = dataSnapshot.getKey();
                    Log.e("friend requests of user: ", userId);
                    friendRequestsOfUserId.add(userId);
                }
                //toi day
                getFriendRequests(mUser.getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFriendRecommends(String uid) {
        friendRecommends = new ArrayList<>();

        DatabaseReference friendRecommendRef = FirebaseDatabase
                .getInstance()
                .getReference("Users");

        friendRecommendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendRecommends.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (!user.getId().equals(uid) && !friendsOfUserId.contains(user.getId())) {
                        friendRecommends.add(user);
                    }
                }
                friendRecommendAdapter = new FriendRecommendAdapter(AddFriendActivity.this, friendRecommends);
                recyclerViewFriendRecommend.setAdapter(friendRecommendAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void getFriendRequests(String uid) {
        friendRequests = new ArrayList<>();
        DatabaseReference friendRequestRef = FirebaseDatabase
                .getInstance()
                .getReference("Users");

        friendRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendRequests.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if(friendRequestsOfUserId.contains(user.getId())) {
                        friendRequests.add(user);
                    }
                }
                friendRequestAdapter = new FriendRequestAdapter(AddFriendActivity.this, friendRequests);
                recyclerViewFriendRequest.setAdapter(friendRequestAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}