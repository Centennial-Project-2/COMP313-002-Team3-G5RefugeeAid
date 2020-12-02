package com.comp313002.team3.g5refugeeaid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.comp313002.team3.g5refugeeaid.databinding.ActivityMainBinding;
import com.comp313002.team3.g5refugeeaid.models.G5UserData;
import com.comp313002.team3.g5refugeeaid.models.UserType;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "Main";
    private static final int RC_SIGN_IN = 1;
    private static final String ANONYMOUS = "Anonymous" ;

    private ActivityMainBinding mBinding;

    private G5UserData userData;

    private DatabaseReference mUserReference ;
    private FirebaseUser user;
    private String mUsername;

    private FirebaseAuth mAuth;
    private ValueEventListener mUserListener;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setProgressBar(mBinding.progressBar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mBinding.signInBox.setVisibility(View.GONE);

        mBinding.btnSignInSubmit.setOnClickListener(this);
        mBinding.btnSignIn.setOnClickListener(this);
        mBinding.btnSignUp.setOnClickListener(this);
        mBinding.btnCancelSignIn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    // user signed in

                    onSignedInInitialize(user);
                } else {
                    onSignedOutCleanUp();
                }
            }
        };
    }

    private void onSignedOutCleanUp() {
        mUsername = ANONYMOUS;
    }

    private void onSignedInInitialize(FirebaseUser user) {


        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get Post object and use the values to update the UI
                userData =dataSnapshot.getValue(G5UserData.class);

                // [START_EXCLUDE]
                //binding.postAuthorLayout.postAuthor.setText(post.author);
                //binding.postTextLayout.postTitle.setText(post.title);
                //binding.postTextLayout.postBody.setText(post.body);
                // [END_EXCLUDE]
                if(userData != null){
                    goToDashboard();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", error.toException());
                // [START_EXCLUDE]
                Toast.makeText(MainActivity.this, "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };

        mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        mUserReference.addValueEventListener(userListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mUserListener = userListener;

        // Listen for comments
        //mAdapter = new CommentAdapter(this, mCommentsReference);
        //binding.recyclerPostComments.setAdapter(mAdapter);

    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnSignUp:
                intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.btnSignIn:
                mBinding.signInBox.setVisibility(View.VISIBLE);
                mBinding.signUpBox.setVisibility(View.GONE);
                break;
            case R.id.btnCancelSignIn:
                mBinding.signInBox.setVisibility(View.GONE);
                mBinding.signUpBox.setVisibility(View.VISIBLE);
                break;
            case R.id.btnSignInSubmit:
                signIn(mBinding.txtEmail.getText().toString(), mBinding.txtPassword.getText().toString());
                break;
            default:
                break;
        }
    }

    private void goToDashboard(){
        Intent intent = new Intent(MainActivity.this, RefugeeDashboardActivity.class);
        if(userData.userType == UserType.SPONSOR){
            intent = new Intent(MainActivity.this, SponsorDashboardActivity.class);
        }
        intent.putExtra("email", user.getEmail());
        startActivity(intent);
        finish();
        //mBinding.txtPassword.setText("");
        //mBinding.txtEmail.setText("");
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressBar();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            mBinding.loginStatus.setText(R.string.auth_failed);
                        }
                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mBinding.txtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mBinding.txtEmail.setError("Required.");
            valid = false;
        } else {
            mBinding.txtEmail.setError(null);
        }

        String password = mBinding.txtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mBinding.txtPassword.setError("Required.");
            valid = false;
        } else {
            mBinding.txtPassword.setError(null);
        }

        return valid;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mUserListener != null) {
            mUserReference.removeEventListener(mUserListener);
        }

        // Clean up comments listener
        //mAdapter.cleanupListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    public void vewMap(View view){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }






}