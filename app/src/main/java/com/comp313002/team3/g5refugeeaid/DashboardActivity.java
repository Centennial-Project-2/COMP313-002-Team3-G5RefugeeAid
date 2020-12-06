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

import com.comp313002.team3.g5refugeeaid.databinding.ActivityDashboardBinding;
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

public class DashboardActivity extends BaseActivity implements  View.OnClickListener {

    private static final String TAG = "Dashboard";

    private FirebaseAuth mAuth;
    private G5UserData userData;
    private FirebaseUser fireBaseUser;
    private ActivityDashboardBinding mBinding;

    private List<G5UserData> usersList;
    private DatabaseReference mUsersReference;
    private ValueEventListener mUsersListener;

    private DatabaseReference mUserDataReference;
    private ValueEventListener mUserDataListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setProgressBar(mBinding.progressBar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        fireBaseUser = mAuth.getCurrentUser();

        if(fireBaseUser != null){
            mBinding.label01.setText("Hello "+ fireBaseUser.getEmail() +" !");
        }

        usersList = new ArrayList<G5UserData>();

        Recycler_View_Adapter adapter = new Recycler_View_Adapter(usersList, getApplication());
        mBinding.recycleView.setAdapter(adapter);
        mBinding.recycleView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.btnSignOut.setOnClickListener(this);
        mBinding.btnLocation.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    G5UserData currentUser =snapshot.getValue(G5UserData.class);
                    if((userData.userType == UserType.REFUGEE && currentUser.userType == UserType.SPONSOR) ||
                                    (userData.userType == UserType.SPONSOR && currentUser.userType == UserType.REFUGEE)){
                        usersList.add(currentUser);
                    }
                }
                //populate results into list
                Recycler_View_Adapter adapter = new Recycler_View_Adapter(usersList, getApplication());
                mBinding.recycleView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", error.toException());
                // [START_EXCLUDE]
                Toast.makeText(DashboardActivity.this, "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };

        // Add value event listener to the userData
        ValueEventListener userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get Post object and use the values to update the UI
                userData = dataSnapshot.getValue(G5UserData.class);
                if(userData != null){
                    StringBuilder greet = new StringBuilder();
                    greet.append("Hello ").append(userData.fName).append(", ").append(userData.lName).append("!\n");
                    greet.append("Email: ").append(userData.email).append("\n");
                    greet.append("Phone: ").append(userData.phoneNumber);
                    mBinding.label01.setText(greet);

                    if(userData.userType == UserType.REFUGEE){
                        mBinding.label02.setText("List of Sponsors: ");
                    } else {
                        mBinding.label02.setText("List of Refugees: ");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", error.toException());
                // [START_EXCLUDE]
                Toast.makeText(DashboardActivity.this, "Failed to load user.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };

        // assign database reference fort userData

        mUserDataReference = FirebaseDatabase.getInstance().getReference().child("users").child(fireBaseUser.getUid());
        mUsersReference = FirebaseDatabase.getInstance().getReference().child("users");
        mUserDataReference.addValueEventListener(userDataListener);
        mUsersReference.addValueEventListener(usersListener);

        // Keep copy of post listener so we can remove it when app stops
        mUsersListener = usersListener;
        mUserDataListener = userDataListener;
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove event listener
        if (mUsersListener != null) {
            mUsersReference.removeEventListener(mUsersListener);
        }
        if (mUserDataListener != null) {
            mUserDataReference.removeEventListener(mUserDataListener);
        }

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
        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
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