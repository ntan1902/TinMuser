package com.hcmus.tinmuser.Activity;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinmuser.Model.User;
import com.hcmus.tinmuser.R;

public class SignUpActivity extends Activity {
    private TextInputLayout mEdtEmail, mEdtPassword, mEdtConfirmPassword;
    private Button mBtnGoBack, mBtnSignup, mBtnSignupGoogle;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    private AlertDialog alertDialog;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize ID
        initializeID();

        // Initialize Firebase Authentication
        initializeFireBaseAuth();

        // [START config_signup]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Init Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.layout_loading_dialog);
        alertDialog = builder.create();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]
    }

    private void initializeFireBaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void moveActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
        finish();
    }

    private boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void initializeID() {
        mEdtEmail = findViewById(R.id.edtEmail);
        mEdtPassword = findViewById(R.id.edtPassword);
        mEdtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        mBtnGoBack = findViewById(R.id.btnGoBack);
        mBtnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(SignUpActivity.this, SignInActivity.class);

            }
        });

        mBtnSignup = findViewById(R.id.btnSignUp);
        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEdtEmail.getEditText().getText().toString();
                final String password = mEdtPassword.getEditText().getText().toString();
                final String confirmPassword = mEdtConfirmPassword.getEditText().getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this, "Please fill in email!", Toast.LENGTH_SHORT).show();
                    //mEdtEmail.setError("Please fill in email!");
                } else if (!isEmailValid(email)) {
                    Toast.makeText(SignUpActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                    //mEdtEmail.setError("Invalid email!");
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this, "Please fill in password!", Toast.LENGTH_SHORT).show();
                    //mEdtPassword.setError("Please fill in password!");
                } else if (password.length() <= 7) {
                    Toast.makeText(SignUpActivity.this, "Password should be at least 8 characters", Toast.LENGTH_SHORT).show();
                    //mEdtPassword.setError("Password should be at least 8 characters");
                } else if (!TextUtils.equals(password, confirmPassword)) {
                    // If sign up fails, display a message to the user.
                    Toast.makeText(SignUpActivity.this, "Password don't be matched. Please check again!", Toast.LENGTH_SHORT).show();
                    //mEdtConfirmPassword.setError("Password don't be matched. Please check again!");
                } else {
                    //mProgressBar.setVisibility(View.VISIBLE);
                    signUpAccount(email, password);
                }
            }
        });

        mBtnSignupGoogle = (Button) findViewById(R.id.btnSignUpGoogle);
        mBtnSignupGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mProgressBar.setVisibility(View.VISIBLE);
                alertDialog.show();
                signInWithGoogle();
            }
        });
    }

    private void signUpAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> onCompleteTaskAuth) {
                        if (onCompleteTaskAuth.isSuccessful()) {
                            // Sign up success, update UI with the signed-in user's information

                            // Get User ID in Realtime Database
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            mRef = FirebaseDatabase.getInstance()
                                    .getReference("Users")
                                    .child(firebaseUser.getUid());

                            // Create HashMap to put into Database
                            User user = new User(firebaseUser.getUid(), email, "default");

                            // Put into Database
                            mRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> onCompleteTaskAll) {
                                    if (onCompleteTaskAll.isSuccessful()) {
//                                        moveActivity(SignUpActivity.this, MainActivity.class);
                                        moveActivity(SignUpActivity.this, MainActivity.class);
                                    }
                                }
                            });
                        } else {
                            // If sign up fails, display a message to the user.
                            alertDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, onCompleteTaskAuth.getException().getMessage(), Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    // [START signin]
    private void signInWithGoogle() {
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                signInWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google sign up failed", Toast.LENGTH_SHORT).show();
//              mProgressBar.setVisibility(View.INVISIBLE);
                alertDialog.dismiss();
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void signInWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            DatabaseReference Ref = FirebaseDatabase.getInstance()
                                    .getReference("Users")
                                    .child(firebaseUser.getUid());

                            Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        // Storing account signed in with google into Realtime
                                        // Create User class to put into Database
                                        User user = new User(firebaseUser.getUid(),
                                                firebaseUser.getEmail(),
                                                firebaseUser.getPhotoUrl().toString(),
                                                firebaseUser.getDisplayName(),
                                                firebaseUser.getPhoneNumber(),
                                                "online");
                                        Ref.setValue(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskSetValue) {
                                                        if (!taskSetValue.isSuccessful()) {
                                                            // If sign up fails, display a message to the user.
                                                            Toast.makeText(SignUpActivity.this, "Sign up failed.",
                                                                    Toast.LENGTH_SHORT).show();
//                                                           mProgressBar.setVisibility(View.INVISIBLE);
                                                            alertDialog.dismiss();
                                                        } else {
                                                            moveActivity(SignUpActivity.this, MainActivity.class);

                                                        }
                                                    }
                                                });
                                    } else {
                                        moveActivity(SignUpActivity.this, MainActivity.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });


//                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
//                            mProgressBar.setVisibility(View.INVISIBLE);
                            alertDialog.dismiss();
                        }
                    }
                });
    }
    // [END auth_with_google]

}