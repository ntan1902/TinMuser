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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> mItems;
    private boolean isChat;

    String lastMessage = "";
    String time = "";


    public UserAdapter(Context context, List<User> mUsers, boolean isChat) {
        this.context = context;
        this.mItems = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_item,
                parent,
                false);
        return new UserAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = mItems.get(position);

        holder.username.setText(user.getUserName());
        if (user.getImageURL().equals("default")) {
            holder.imageView.setImageResource(R.drawable.profile_image);
        } else {
            Glide.with(context)
                    .load(user.getImageURL())
                    .into(holder.imageView);
        }

        if (user.getStatus().equals("online")) {
            holder.imgOn.setVisibility(View.VISIBLE);
        } else {
            holder.imgOn.setVisibility(View.GONE);
        }

        if (isChat) {
            getLastMessageFromUser(user.getId(), holder);
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

    private void getLastMessageFromUser(String id, ViewHolder holder) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if ((chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(id)) ||
                            (chat.getSender().equals(id) && chat.getReceiver().equals(firebaseUser.getUid()))) {
                        if (chat.getType().equals("text")) {
                            lastMessage = chat.getMessage();
                        } else if (chat.getType().equals("image")) {
                            lastMessage = "Image was sent";
                        }
                        time = chat.getTime();
                    }
                }

                if (!lastMessage.isEmpty())
                    holder.lastMessage.setText(lastMessage);
                if (!time.isEmpty())
                    holder.time.setText(holder.convertTime(time));

                lastMessage = "";
                time = "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        private TextView lastMessage;
        private TextView time;
        private ImageView imgOn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.userName);
            imageView = itemView.findViewById(R.id.imageView);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            time = itemView.findViewById(R.id.time);
            imgOn = itemView.findViewById(R.id.imgOn);
        }

        public String convertTime(String time) {

            long lastMillis = Long.parseLong(time);
            long currentMillis = Long.parseLong(String.valueOf(System.currentTimeMillis()));
            long gapMillis = currentMillis - lastMillis;

            int gapHour = (int) ((gapMillis / (1000 * 60 * 60)));

            Calendar calendar = Calendar.getInstance();
            int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); //CN -> T7 = 1 -> 7
            int currentYear = calendar.get(Calendar.YEAR);

            calendar.setTime(new Date(lastMillis));
            int lastTimeDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int lastTimeDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int lastTimeMonth = calendar.get(Calendar.MONTH) + 1;
            int lastTimeYear = calendar.get(Calendar.YEAR);
            int lastTimeHour = calendar.get(Calendar.HOUR);
            if (calendar.get(Calendar.AM_PM) == 1) //Sau 12h trua
                lastTimeHour += 12;
            int lastTimeMin = calendar.get(Calendar.MINUTE);
            //---------------------------------------------------------------------------------
            String result = "";

            if (gapHour < 24) {
                String strHour = String.valueOf(lastTimeHour);
                String strMin = String.valueOf(lastTimeMin);
                if (lastTimeHour < 10)
                    strHour = "0" + strHour;
                if (lastTimeMin < 10)
                    strMin = "0" + strMin;

                result = strHour + ":" + strMin;
            } else {
                if (gapHour < 168 && lastTimeDayOfWeek < currentDayOfWeek) { //1 tuan co 168 tieng
                    String dayOfWeek = String.valueOf(lastTimeDayOfWeek);
                    if (dayOfWeek.equals("1")) {
                        result = "CN";
                    }
                    else {
                        result = "Th " + dayOfWeek;
                    }
                } else {
                    result = String.valueOf(lastTimeDayOfMonth) + " thg " + String.valueOf(lastTimeMonth);
                    if (lastTimeYear < currentYear) {
                        result += ", " + String.valueOf(lastTimeYear);
                    }
                }
            }

            return result;
        }
    }
}
