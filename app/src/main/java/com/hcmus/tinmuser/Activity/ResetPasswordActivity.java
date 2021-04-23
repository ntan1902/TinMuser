package com.hcmus.tinmuser.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.hcmus.tinmuser.R;

public class ResetPasswordActivity extends Activity {
    private TextInputLayout mEdtEmail;
    private Button mBtnReset, mBtnGoBack;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize ID
        initializeID();

        // Initialize Firebase Authentication
        initializeFireBaseAuth();
    }

    private void initializeID() {
        mEdtEmail = (TextInputLayout) findViewById(R.id.edtEmail);

        mBtnReset = (Button) findViewById(R.id.btnReset);
        mBtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEdtEmail.getEditText().getText().toString();
                if(TextUtils.isEmpty(email)) {
                    mEdtEmail.setError("Please fill in email!");
                } else {
                    resetEmail(email);

                }
            }
        });

        mBtnGoBack = (Button) findViewById(R.id.btnGoBack);
        mBtnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(com.hcmus.tinmuser.Activity.ResetPasswordActivity.this, SignInActivity.class));
                finish();
            }
        });
    }

    private void resetEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(com.hcmus.tinmuser.Activity.ResetPasswordActivity.this, "Please check you Email", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(com.hcmus.tinmuser.Activity.ResetPasswordActivity.this, SignInActivity.class));
            }
        });
    }

    private void initializeFireBaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

}