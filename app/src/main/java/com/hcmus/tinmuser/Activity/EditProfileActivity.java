package com.hcmus.tinmuser.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.hcmus.tinmuser.Model.User;
import com.hcmus.tinmuser.R;


public class EditProfileActivity extends Activity {

    private EditText edtFullname, edtEmail, edtPhone;
    private ImageView ivAvatar, btnGoBack;
    private Uri imageUri;
    private Button btnEdit;
    String img_link;
    private DatabaseReference mRef;
    private FirebaseStorage storage;
    private FirebaseUser mUser;
    private StorageReference storageReference;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtFullname = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);

        ivAvatar = findViewById(R.id.ivAvatar);

        btnEdit = findViewById(R.id.btnEdit);

        btnGoBack = findViewById(R.id.btnGoBack);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        mRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Init Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setCancelable(false);
                builder.setView(R.layout.layout_loading_dialog);
                alertDialog = builder.create();

                User user = snapshot.getValue(User.class);
                edtFullname.setText(user.getUserName());
                edtEmail.setText(user.getEmail());
                if (user.getImageURL().matches("default")) {
                    ivAvatar.setImageResource(R.drawable.profile_image);
                } else {
                    Glide.with(EditProfileActivity.this)
                            .load(user.getImageURL())
                            .into(ivAvatar);
                }
                img_link = user.getImageURL();

                if (user.getPhone() == null) {
                    edtPhone.setText("");
                } else {
                    edtPhone.setText(user.getPhone());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mRef.addValueEventListener(valueEventListener);

        //SAVE
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtFullname.getText().toString();
                String email = edtEmail.getText().toString();
                String phone = edtPhone.getText().toString();
                String gender;

                if (TextUtils.isEmpty(username)) {
                    edtFullname.setError("Username can't be empty");
                } else {
                    if (TextUtils.isEmpty(phone)) {
//                    edtPhone.setError("Phone can't be empty");
                        phone = "";
                    }

                    User new_user = new User(mUser.getUid(), email, img_link, username, phone);
                    mRef.setValue(new_user);
                    Toast.makeText(EditProfileActivity.this, "Save successfully !", Toast.LENGTH_SHORT).show();

                    EditProfileActivity.super.onBackPressed();
                }
            }
        });

        //GO BACK
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileActivity.super.onBackPressed();
//                if ( getFragmentManager().getBackStackEntryCount() > 0)
//                {
//                    getFragmentManager().popBackStack();
//                    return;
//                }
//                EditProfileActivity.super.onBackPressed();
            }
        });

    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivAvatar.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        StorageReference storageRef = storageReference.child("images/avatars/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
        storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String old_img = img_link;
                //add new image
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        img_link = uri.toString();
                        //delete old image
                        if (!old_img.matches("default") && old_img.contains("firebasestorage")) {
                            StorageReference deleteRef = storage.getReferenceFromUrl(old_img);
                            deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    System.out.println("Delete old image successfully !");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Delete old image failed !");
                                }
                            });
                        }
                        alertDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Upload SUCCESS", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                alertDialog.show();
//                Toast.makeText(EditProfileActivity.this, "Uploadinggg", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                alertDialog.dismiss();
                Toast.makeText(EditProfileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}