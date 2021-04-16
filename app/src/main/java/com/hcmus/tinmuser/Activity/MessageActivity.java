package com.hcmus.tinmuser.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hcmus.tinmuser.Adapter.MessageAdapter;
import com.hcmus.tinmuser.Model.Chat;
import com.hcmus.tinmuser.Model.User;
import com.hcmus.tinmuser.R;
import com.hcmus.tinmuser.Service.SongService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends Activity {

    private TextView username;
    private ImageView imageView, btnGoBack, btnSendImage, btnHeadphone, imgOn;
    private RecyclerView recyclerView;
    private EditText txtSend;
    private Button btnSend;
    private Uri imageUri;

    private FirebaseUser mUser;
    private DatabaseReference mRef;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private AlertDialog alertDialog;

    private MessageAdapter messageAdapter;

    private List<Object> mItems;
    private List<String> imgURLs;
    private String userId;
    private String groupId;
    private String file_link;

    private MediaPlayer musicPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        imageView = findViewById(R.id.imageView);
        imgOn = findViewById(R.id.imgOn);
        username = findViewById(R.id.username);
        recyclerView = findViewById(R.id.recyclerView);
        txtSend = findViewById(R.id.txtSend);
        btnSend = findViewById(R.id.btnSend);
        btnGoBack = findViewById(R.id.btnGoBack);
        btnSendImage = findViewById(R.id.btnSendImage);
        btnHeadphone = findViewById(R.id.btnHeadphone);

        //Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.layout_loading_dialog);
        alertDialog = builder.create();

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Receiver
        Intent i = getIntent();
        userId = i.getStringExtra("userId");
        groupId = i.getStringExtra("groupId");

        // Sender
        mUser = FirebaseAuth
                .getInstance()
                .getCurrentUser();
        updateStatus("online");

        mRef = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(userId);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUserName());

                if (user.getImageURL().equals("default")) {
                    imageView.setImageResource(R.drawable.profile_image);
                } else {
                    Glide.with(getApplicationContext())
                            .load(user.getImageURL())
                            .into(imageView);
                }

                if(user.getStatus().equals("online")){
                    imgOn.setVisibility(View.VISIBLE);
                } else {
                    imgOn.setVisibility(View.GONE);
                }

                readMessagesFromUser(user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mRef.addValueEventListener(valueEventListener);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = txtSend.getText().toString();
                String time = String.valueOf(System.currentTimeMillis());

                if (!msg.equals("")) {
                    sendMessage(msg, time, "text");
                    txtSend.setText("");
                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                musicPlayer.stop();
                MessageActivity.super.onBackPressed();
                finish();
            }
        });

        btnSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        btnHeadphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent show_list_songs = new Intent(MessageActivity.this, ShowListSongsActivity.class);
                startActivity(show_list_songs);
                finish();
            }
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadPicture();
        }
    }

    private void uploadPicture() {
        StorageReference storageRef = storageReference.child("files/chats/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
        storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //add new image
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        file_link = uri.toString();
                        System.out.println("File link: " + file_link);
                        String time = String.valueOf(System.currentTimeMillis());
                        sendMessage(file_link, time, "image");


                        alertDialog.dismiss();
                        Toast.makeText(MessageActivity.this, "Upload SUCCESS", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                alertDialog.show();
//              Toast.makeText(EditProfileActivity.this, "Uploadinggg", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                alertDialog.dismiss();
                Toast.makeText(MessageActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendMessage(String msg, String time, String type) {
        sendMessageToUser(msg, time, type);
    }

    private void sendMessageToUser(String msg, String time, String type) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        // Create chat
        Chat chat = new Chat(mUser.getUid(), userId, msg, time, type);

        reference.push().setValue(chat);

        // Get the latest chat message for sender
        final DatabaseReference chatSenderRef = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(mUser.getUid())
                .child(userId);

        chatSenderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatSenderRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Get the latest chat message for receiver
        final DatabaseReference chatReceiverRef = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(userId)
                .child(mUser.getUid());

        chatReceiverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatReceiverRef.child("id").setValue(mUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readMessagesFromUser(String imgURL) {
        mItems = new ArrayList<>();

        imgURLs = new ArrayList<>();
        imgURLs.add(imgURL);

        mRef = FirebaseDatabase.getInstance().getReference("Chats");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mItems.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if ((chat.getReceiver().equals(mUser.getUid()) && chat.getSender().equals(userId)) ||
                            (chat.getReceiver().equals(userId) && chat.getSender().equals(mUser.getUid()))) {
                        mItems.add(chat);
                    }

                }
                messageAdapter = new MessageAdapter(MessageActivity.this, mItems, imgURLs);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

    private void updateStatus(String status) {
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());

        Map<String, Object> map = new HashMap<>();
        map.put("status", status);

        mRef.updateChildren(map);
    }
}