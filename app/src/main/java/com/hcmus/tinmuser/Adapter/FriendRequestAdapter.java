package com.hcmus.tinmuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Activity.ShowProfileActivity;
import com.hcmus.tinmuser.Model.PlayDouble;
import com.hcmus.tinmuser.Model.User;
import com.hcmus.tinmuser.R;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<com.hcmus.tinmuser.Adapter.FriendRequestAdapter.ViewHolder> {
    private Context context;
    private List<User> mItems;
    private FirebaseUser mUser;

    public FriendRequestAdapter(Context context, List<User> mUsers) {
        this.context = context;
        this.mItems = mUsers;
    }

    @NonNull
    @Override
    public com.hcmus.tinmuser.Adapter.FriendRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.friend_request_item,
                parent,
                false);
        return new com.hcmus.tinmuser.Adapter.FriendRequestAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull com.hcmus.tinmuser.Adapter.FriendRequestAdapter.ViewHolder holder, int position) {
        User user = mItems.get(position);

        holder.username.setText(user.getUserName());
        if (user.getImageURL().equals("default")) {
            holder.imageView.setImageResource(R.drawable.profile_image);
        } else {
            Glide.with(context)
                    .load(user.getImageURL())
                    .into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ShowProfileActivity.class);
                i.putExtra("userId", user.getId());
                context.startActivity(i);
            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Delete the request
                DatabaseReference delRequest = FirebaseDatabase.getInstance().getReference("FriendRequests").child(mUser.getUid()).child(user.getId());
                delRequest.removeValue();

                //Add friend from 2 side here
                DatabaseReference user_friendRef = FirebaseDatabase.getInstance().getReference("Friends").child(mUser.getUid()).child(user.getId());
                user_friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            user_friendRef.child("id").setValue(user.getId());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                DatabaseReference opponent_friendRef = FirebaseDatabase.getInstance().getReference("Friends").child(user.getId()).child(mUser.getUid());

                user_friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            opponent_friendRef.child("id").setValue(mUser.getUid());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        holder.btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Delete the request
                DatabaseReference delRequest = FirebaseDatabase.getInstance().getReference("FriendRequests").child(mUser.getUid()).child(user.getId());
                delRequest.removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private ImageView imageView;
        private Button btnAccept, btnDecline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.userName);
            imageView = itemView.findViewById(R.id.imageView);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);

            mUser = FirebaseAuth.getInstance().getCurrentUser();
        }
    }
}
