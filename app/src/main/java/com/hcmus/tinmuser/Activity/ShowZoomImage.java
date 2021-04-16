package com.hcmus.tinmuser.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hcmus.tinmuser.R;

public class ShowZoomImage extends Activity {

    private ImageView btnGoBack, btnDownload, imageZoom;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    String img_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_zoom_image);

        btnGoBack = findViewById(R.id.btnGoBack);
        btnDownload = findViewById(R.id.btnDownload);
        imageZoom = findViewById(R.id.imageZoom);

        //Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Intent intent = getIntent();
        img_link = intent.getStringExtra("img_link");

        if (img_link.matches("default")) {
            imageZoom.setImageResource(R.drawable.profile_image);
        } else {
            Glide.with(getApplicationContext())
                    .load(img_link)
                    .into(imageZoom);
        }

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowZoomImage.super.onBackPressed();
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("********* download");
                new AlertDialog.Builder(ShowZoomImage.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to download ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String filename = URLUtil.guessFileName(img_link, null, null);
                                download(filename);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void download(String filename) {
        StorageReference storageRef = storageReference.child("files/chats/" + filename);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                downloadFile(ShowZoomImage.this, filename, Environment.DIRECTORY_DOWNLOADS, img_link);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void downloadFile(Context context, String fileName, String destinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName);

        downloadManager.enqueue(request);
    }
}