package com.hcmus.tinmuser.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.ChangePasswordActivity;
import com.hcmus.tinmuser.EditProfileActivity;
import com.hcmus.tinmuser.Model.User;
import com.hcmus.tinmuser.R;
import com.hcmus.tinmuser.SignInActivity;

public class ProfileFragment extends Fragment {
    private TextView tvName, tvFullname, tvEmail, tvPhone;
    private ImageView ivAvatar;
    private Button btnEdit, btnChangePassword, btnSignOut;

    private DatabaseReference mRef;

    private ExpandableLinearLayout linearLayout;
    private Button btnArrow;

    FirebaseUser mUser;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        //Initial variables
        tvName = view.findViewById(R.id.tvName);
        tvFullname = view.findViewById(R.id.tvFullname);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnSignOut = view.findViewById(R.id.btnSignOut);

        mUser = FirebaseAuth.getInstance().getCurrentUser();


        linearLayout = (ExpandableLinearLayout) view.findViewById(R.id.expandedLayout);
        btnArrow = view.findViewById(R.id.btnArrow);
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

        mRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
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
                    Glide.with(getContext())
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
                Intent intent_edit = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent_edit);
            }
        });
        //CHANGE PASSWORD
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_changePassword = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent_changePassword);
            }
        });

        //SIGN OUT
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                System.out.println("***********************************************");
                                Intent intent = new Intent(getActivity(), SignInActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        return view;
    }
}