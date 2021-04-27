package com.hcmus.tinmuser.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Model.User;
import com.hcmus.tinmuser.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ShowProfileActivity extends Activity {

    private TextView tvName, tvFullname, tvEmail, tvPhone, tvGender, tvSchool, tvMajor, tvBeginYear;
    private ImageView ivAvatar, btnGoBack;
    private Button btnAddFriend;
    private String id;

    private DatabaseReference mRef;
    private FirebaseUser mUser;
    private ExpandableLinearLayout linearLayout;
    private Button btnArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        //Initial variables
        tvName = findViewById(R.id.tvName);
        tvFullname = findViewById(R.id.tvFullname);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);

        ivAvatar = findViewById(R.id.ivAvatar);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnGoBack = findViewById(R.id.btnGoBack);

        linearLayout = (ExpandableLinearLayout) findViewById(R.id.expandedLayout);
        btnArrow = findViewById(R.id.btnArrow);
        btnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.toggle();
                if (linearLayout.isExpanded()) {
                    btnArrow.setBackgroundResource(R.drawable.ic_arrow_down);

                } else {
                    btnArrow.setBackgroundResource(R.drawable.ic_arrow_up);
                }
            }
        });

        Intent intent = getIntent();
        id = intent.getStringExtra("userId");
        System.out.println(id);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(id);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                System.out.println(user);
                tvName.setText(user.getUserName());
                tvFullname.setText(user.getUserName());
                tvEmail.setText(user.getEmail());

                if (user.getImageURL().matches("default")) {
                    ivAvatar.setImageResource(R.drawable.profile_image);
                } else {
                    Glide.with(getApplicationContext())
                            .load(user.getImageURL())
                            .into(ivAvatar);
                }
                if (user.getPhone() == null) {
                    tvPhone.setText("");
                } else {
                    tvPhone.setText(user.getPhone());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //EDIT
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sweet Alert
                {
                    // 5. Confirm success
                    new SweetAlertDialog(ShowProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure to add?")
                            .setConfirmText("Yes")
                            .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    //Add friend here
//                                    DatabaseReference user_friendRef = FirebaseDatabase.getInstance().getReference("Friends").child(mUser.getUid()).child(id);
//
//                                    user_friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            if(!snapshot.exists()) {
//                                                user_friendRef.child("id").setValue(id);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                        }
//                                    });

                                    //Add request here
                                    DatabaseReference opponent_friendRef = FirebaseDatabase.getInstance().getReference("FriendRequests").child(id).child(mUser.getUid());

                                    opponent_friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(!snapshot.exists()) {
                                                opponent_friendRef.child("id").setValue(mUser.getUid());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    //End add friend

                                    sDialog
                                            .setTitleText("Added!")
                                            .setContentText("Add Friend successfully !")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(null)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            })
                            .show();
                }

            }
        });

        //GO BACK
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent_goBack = new Intent(UserProfileActitivy.this, MainActivity.class);
//                intent_goBack.putExtra("id", id);
//                startActivity(intent_goBack);
                ShowProfileActivity.super.onBackPressed();
                finish();
            }
        });

    }
}