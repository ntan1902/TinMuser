package com.hcmus.tinmuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.EditProfileActivity;
import com.hcmus.tinmuser.MainActivity;
import com.hcmus.tinmuser.Model.User;
import com.hcmus.tinmuser.R;

public class UserProfileActitivy extends Activity {

    private TextView tvName, tvFullname, tvEmail, tvPhone, tvGender, tvSchool, tvMajor, tvBeginYear;
    private ImageView ivAvatar, btnGoBack;
    private Button btnEdit, btnChangePassword, btnSignOut;
    private String id;

    private DatabaseReference mRef;

    private ExpandableLinearLayout linearLayout;
    private Button btnArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //Initial variables
        tvName = findViewById(R.id.tvName);
        tvFullname = findViewById(R.id.tvFullname);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);

        ivAvatar = findViewById(R.id.ivAvatar);
        btnEdit = findViewById(R.id.btnEdit);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnGoBack = findViewById(R.id.btnGoBack);
        btnSignOut = findViewById(R.id.btnSignOut);


        linearLayout = (ExpandableLinearLayout)findViewById(R.id.expandedLayout);
        btnArrow = findViewById(R.id.btnArrow);
        btnArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(linearLayout.getVisibility() == View.GONE) {
//                    Fade fadeIn = new Fade(Fade.IN);
//                    TransitionManager.beginDelayedTransition(linearLayout, fadeIn);
//                    linearLayout.setVisibility(View.VISIBLE);
//                    btnArrow.setBackgroundResource(R.drawable.ic_arrow_up);
//                } else {
//                    Fade fadeOut = new Fade(Fade.OUT);
//                    TransitionManager.beginDelayedTransition(linearLayout, fadeOut);
//                    linearLayout.setVisibility(View.GONE);
//                    btnArrow.setBackgroundResource(R.drawable.ic_arrow_down);
//                }
                linearLayout.toggle();

            }
        });

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        System.out.println(id);
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
                    Glide.with(UserProfileActitivy.this)
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
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_edit = new Intent(UserProfileActitivy.this, EditProfileActivity.class);
                intent_edit.putExtra("id", id);
                startActivity(intent_edit);
            }
        });
        //CHANGE PASSWORD
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent_changePassword = new Intent(UserProfileActitivy.this, ChangePasswordActivity.class);
//                startActivity(intent_changePassword);
            }
        });
        //GO BACK
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent_goBack = new Intent(UserProfileActitivy.this, MainActivity.class);
//                intent_goBack.putExtra("id", id);
//                startActivity(intent_goBack);
                UserProfileActitivy.super.onBackPressed();
            }
        });

        //SIGN OUT
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UserProfileActitivy.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                System.out.println("***********************************************");
                                Intent intent = new Intent(UserProfileActitivy.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }
}