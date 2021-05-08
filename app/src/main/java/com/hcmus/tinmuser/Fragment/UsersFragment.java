package com.hcmus.tinmuser.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Adapter.UserAdapter;
import com.hcmus.tinmuser.Model.User;
import com.hcmus.tinmuser.R;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    private FirebaseUser mUser;
    private DatabaseReference mRef;

    private ArrayList<String> listFriendsId;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        //Get all friends of user
        DatabaseReference mFriendRef = FirebaseDatabase.getInstance().getReference("Friends").child(mUser.getUid());

        listFriendsId = new ArrayList<>();

        mFriendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFriendsId.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userId = dataSnapshot.getKey();
                    listFriendsId.add(userId);
                }
                getUsers();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void getUsers() {
        mUsers = new ArrayList<>();
        DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference("Users");
        friendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    if(listFriendsId.contains(user.getId())){
                        mUsers.add(user);
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers, false);
                recyclerView.setAdapter(userAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}