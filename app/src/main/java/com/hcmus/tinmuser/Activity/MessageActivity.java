package com.hcmus.tinmuser.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
import com.hcmus.tinmuser.Model.PlayDouble;
import com.hcmus.tinmuser.Model.User;
import com.hcmus.tinmuser.R;
import com.hcmus.tinmuser.Service.SongService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends Activity implements ServiceConnection {

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

    private List<Chat> mItems;

    private String userId;
    private String file_link;

    private SongService songService;
    private Intent songServiceIntent;
    private String playDoubleId = "";
    private Boolean isPlayDouble = false;

    private ImageView btnPlay, songAvatar;
    private TextView txtSongName, txtArtistName;
    private SeekBar seekBar;
    private boolean isPlay = true;
    private RelativeLayout layoutPlay;

    Handler handler = new Handler();

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

        // Layout Play
        layoutPlay = findViewById(R.id.layoutPlay);

        // Button Play
        btnPlay = findViewById(R.id.btnPlay);

        txtSongName = findViewById(R.id.songName);
        txtArtistName = findViewById(R.id.artistName);
        songAvatar = findViewById(R.id.songAvatar);
        seekBar = findViewById(R.id.seekBar);
        seekBar.getThumb().mutate().setAlpha(0);

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

        // Sender
        mUser = FirebaseAuth
                .getInstance()
                .getCurrentUser();
        updateStatus("online");

        mRef = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(userId);

        mRef.addValueEventListener(new ValueEventListener() {
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

                if (user.getStatus().equals("online")) {
                    imgOn.setVisibility(View.VISIBLE);
                } else {
                    imgOn.setVisibility(View.GONE);
                }

                readMessagesFromUser(user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Create SongService
        ValueEventListener playDoubleIdListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    PlayDouble playDouble = snapshot.getValue(PlayDouble.class);
                    if(playDouble.getIsPlay()) {
                        createService(playDouble);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        //Listen song
        ValueEventListener isPlayListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && songService != null) {
                    Boolean _isPlay = snapshot.getValue(Boolean.class);
                    if (_isPlay) {
                        isPlay = true;
                        btnPlay.setImageResource(R.drawable.ic_pause);
                        songService.start();
                    } else {
                        isPlay = false;
                        btnPlay.setImageResource(R.drawable.ic_play);
                        songService.pause();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        // Set progress change
        ValueEventListener progressChangedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && songService != null) {
                    Integer progress = snapshot.getValue(Integer.class);
                    songService.seekTo(progress * 1000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        // Set repeat
        ValueEventListener isRepeatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && songService != null) {
                    Boolean isRepeat = snapshot.getValue(Boolean.class);
                    System.out.println("Repeat: " + isRepeat);
                    if (isRepeat) {
                        songService.setLooping(true);
                    } else {
                        songService.setLooping(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        // Find playDoubleId
        DatabaseReference playDoubleRef = FirebaseDatabase.getInstance().getReference("PlayDouble");
        playDoubleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PlayDouble playDouble = dataSnapshot.getValue(PlayDouble.class);

                    if ((playDouble.getUser1().equals(userId) && playDouble.getUser2().equals(mUser.getUid())) ||
                            (playDouble.getUser1().equals(mUser.getUid()) && playDouble.getUser2().equals(userId))) {
                        playDoubleId = dataSnapshot.getKey();

                        playDoubleRef.removeEventListener(this);

                        // Create SongService
                        final DatabaseReference playDoubleIdRef = FirebaseDatabase
                                .getInstance()
                                .getReference("PlayDouble")
                                .child(playDoubleId);
                        playDoubleIdRef.addValueEventListener(playDoubleIdListener);

                        // Listen song
                        final DatabaseReference isPlayRef = FirebaseDatabase
                                .getInstance()
                                .getReference("PlayDouble")
                                .child(playDoubleId)
                                .child("isPlay");
                        isPlayRef.addValueEventListener(isPlayListener);

                        // Set progress change
                        final DatabaseReference progressChangedRef = FirebaseDatabase
                                .getInstance()
                                .getReference("PlayDouble")
                                .child(playDoubleId)
                                .child("progressChanged");
                        progressChangedRef.addValueEventListener(progressChangedListener);

                        // Set repeat
                        final DatabaseReference isRepeatRef = FirebaseDatabase
                                .getInstance()
                                .getReference("PlayDouble")
                                .child(playDoubleId)
                                .child("isRepeat");
                        isRepeatRef.addValueEventListener(isRepeatListener);

                        // Set UI Thread
                        isPlayDouble = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        MessageActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                songService = SongService.getInstance();

                if (songService != null && songService.getMediaPlayer() != null && isPlayDouble) {
                    layoutPlay.setVisibility(View.VISIBLE);

                    // Get current music playing

                    Glide.with(getApplicationContext())
                            .load(songService.getImageURL())
                            .into(songAvatar);
                    txtSongName.setText(songService.getSongName());
                    txtArtistName.setText(songService.getArtistName());

                    updateProgressBar();

                    // Set on click on PlaySong
                    layoutPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MessageActivity.this, PlaySongActivity.class);

                            intent.putExtra("playType", songService.getPlayType());
                            intent.putExtra("userId", songService.getUserId());
                            intent.putExtra("songId", songService.getSongId());
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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MessageActivity.this, ShowProfileActivity.class);
                i.putExtra("userId", userId);
                startActivity(i);
            }
        });

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
                show_list_songs.putExtra("userId", userId);
                startActivity(show_list_songs);
//                finish();
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
                setValuePlayDouble(isPlay);
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
                messageAdapter = new MessageAdapter(MessageActivity.this, mItems, imgURL);
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

    private void createService(PlayDouble playDouble) {
        songServiceIntent = new Intent(this, SongService.class);
        songServiceIntent.putExtra("uri", playDouble.getUri());
        songServiceIntent.putExtra("songName", playDouble.getSongName());
        songServiceIntent.putExtra("artistName", playDouble.getArtistName());
        songServiceIntent.putExtra("imageURL", playDouble.getImageURL());
        songServiceIntent.putExtra("playType", "Double");
        songServiceIntent.putExtra("userId", userId);
        songServiceIntent.putExtra("songId", playDouble.getSongId());
        bindService(songServiceIntent, this, BIND_AUTO_CREATE);
        startService(songServiceIntent);
    }
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        SongService.LocalBinder binder = (SongService.LocalBinder) service;
        songService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        songService = null;
        unbindService(this);
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

    @Override
    protected void onPause() {
        super.onPause();
//        songService = SongService.getInstance();
//        if (songService != null) {
//            unbindService(this);
//
//        }
    }

    private void setValuePlayDouble(Boolean value) {
        final DatabaseReference setIsPlayRef = FirebaseDatabase
                .getInstance()
                .getReference("PlayDouble")
                .child(playDoubleId)
                .child("isPlay");
        setIsPlayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                setIsPlayRef.setValue(value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}