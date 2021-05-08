package com.hcmus.tinmuser.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.hcmus.tinmuser.Activity.MessageActivity;
import com.hcmus.tinmuser.Model.Chat;
import com.hcmus.tinmuser.Model.User;
import com.hcmus.tinmuser.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OnlineUserAdapter extends RecyclerView.Adapter<OnlineUserAdapter.ViewHolder> {
    private Context context;
    private List<User> mItems;

    String lastMessage = "";
    String time = "";


    public OnlineUserAdapter(Context context, List<User> mUsers) {
        this.context = context;
        this.mItems = mUsers;
    }

    @NonNull
    @Override
    public OnlineUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.online_user_item,
                parent,
                false);
        return new OnlineUserAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull OnlineUserAdapter.ViewHolder holder, int position) {
        User user = mItems.get(position);

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
                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userId", user.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView imgOn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            imgOn = itemView.findViewById(R.id.imgOn);
        }

    }
}
