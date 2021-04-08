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
import com.hcmus.tinmuser.ShowZoomImage;
import com.hcmus.tinmuser.Model.Chat;
import com.hcmus.tinmuser.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<Object> mItems;
    private List<String> imgURLs;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public MessageAdapter(Context context, List<Object> mItems, List<String> imgURLs) {
        this.context = context;
        this.mItems = mItems;
        this.imgURLs = imgURLs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,
                    parent,
                    false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,
                    parent,
                    false);
            return new ViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = (Chat) mItems.get(position);

        if (chat.getType().equals("text")) {
            holder.showImage.setVisibility(View.GONE);
            holder.showMessage.setVisibility(View.VISIBLE);

            holder.showMessage.setText(chat.getMessage());
        } else if (chat.getType().equals("image")) {
            holder.showImage.setVisibility(View.VISIBLE);
            holder.showMessage.setVisibility(View.GONE);

            Glide.with(context)
                    .load(chat.getMessage())
                    .into(holder.showImage);

            holder.showImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent zoom_image = new Intent(context, ShowZoomImage.class);
                    zoom_image.putExtra("img_link", chat.getMessage());
                    context.startActivity(zoom_image);
                }
            });

        }

        if (imgURLs.get(0).equals("default")) {
            holder.profile_image.setImageResource(R.drawable.profile_image);
        } else {
            Glide.with(context)
                    .load(imgURLs.get(0))
                    .into(holder.profile_image);
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Chat chat = (Chat) mItems.get(position);
        if (chat.getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView showMessage;
        ImageView profile_image;
        ImageView showImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.showMessage);
            profile_image = itemView.findViewById(R.id.profile_image);
            showImage = itemView.findViewById(R.id.showImage);

        }

    }
}
