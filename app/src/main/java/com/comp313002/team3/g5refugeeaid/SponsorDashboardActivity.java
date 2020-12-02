package com.comp313002.team3.g5refugeeaid;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.comp313002.team3.g5refugeeaid.databinding.ActivitySponsorDashboardBinding;
import com.comp313002.team3.g5refugeeaid.models.G5UserData;
import com.comp313002.team3.g5refugeeaid.models.UserType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SponsorDashboardActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "Dashboard";

    private FirebaseAuth mAuth;
    private G5UserData userData;
    private FirebaseUser fireBaseUser;
    private ActivitySponsorDashboardBinding mBinding;
    private ValueEventListener mUserListener;
    private String email;

    private List<G5UserData> refugees;
    private DatabaseReference mUserReference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySponsorDashboardBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setProgressBar(mBinding.progressBar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mBinding.label01.setText("Hello!");
        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        if(email != null){
            mBinding.label01.setText("Hello "+ email +" !");
        }

        mBinding.btnSignOut.setOnClickListener(this);

        fireBaseUser = mAuth.getCurrentUser();
        refugees = new ArrayList<G5UserData>();

        mUserReference = FirebaseDatabase.getInstance().getReference().child("users");

        Recycler_View_Adapter adapter = new Recycler_View_Adapter(refugees, getApplication());
        mBinding.recycleView.setAdapter(adapter);
        mBinding.recycleView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refugees = new ArrayList<G5UserData>();
                // Get Post object and use the values to update the UI
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    G5UserData userData =snapshot.getValue(G5UserData.class);
                    if(userData.userType == UserType.REFUGEE){
                        refugees.add(userData);
                    }
                }

                Recycler_View_Adapter adapter = new Recycler_View_Adapter(refugees, getApplication());
                mBinding.recycleView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", error.toException());
                // [START_EXCLUDE]
                Toast.makeText(SponsorDashboardActivity.this, "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mUserReference.addValueEventListener(userListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mUserListener = userListener;

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
    public void onClick(View v) {
        if(v.getId() == R.id.btnSignOut){
            showCloseAlert();
        }
    }

    @Override
    public void onBackPressed() {
        showCloseAlert();
    }

    private void signOut(){
        mAuth.signOut();
        Intent intent = new Intent(SponsorDashboardActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showCloseAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.close_alert_message)
                .setTitle(R.string.close_alert_title);

        //Setting message manually and performing action on button click
        builder.setCancelable(false)
                .setPositiveButton(R.string.close_alert_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if confirmed then sign out and close activity
                        signOut();
                    }
                })
                .setNegativeButton(R.string.close_alert_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }
}