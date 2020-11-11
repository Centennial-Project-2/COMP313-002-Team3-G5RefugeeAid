package com.comp313002.team3.g5refugeeaid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.comp313002.team3.g5refugeeaid.databinding.ActivitySignupBinding;
import com.comp313002.team3.models.G5UserData;
import com.comp313002.team3.models.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends BaseActivity implements
        View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "SignUp";

    private ActivitySignupBinding mBinding;
    private FirebaseAuth mAuth;
    private G5UserData userData;
    private FirebaseUser fireBaseUser;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setProgressBar(mBinding.progressBar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //spinner setup
        String[] users = {"I am a Refugee", "I am a Sponsor"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.userType.setAdapter(adapter);
        mBinding.userType.setOnItemSelectedListener(this);

        mBinding.btnConfirm.setVisibility(View.GONE);
        mBinding.btnSubmit.setOnClickListener(this);
        mBinding.btnConfirm.setOnClickListener(this);
        mBinding.btnCancel.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            finish();
        } else if (i == R.id.btn_Confirm) {
            // go to dashboard
            intent = new Intent(this, DashboardActivity.class);
            if(fireBaseUser != null){
                intent.putExtra("email", fireBaseUser.getEmail());
                startActivity(intent);
                finish();
            }

        } else if (i == R.id.btn_submit) {
            createAccount(mBinding.txtEmail.getText().toString(), mBinding.txtPassword.getText().toString());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        if(position == 0){
            mBinding.txtUnNumber.setVisibility(View.VISIBLE);
        } else {
            mBinding.txtUnNumber.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mBinding.userType.setSelection(0);
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressBar();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            fireBaseUser = mAuth.getCurrentUser();
                            // Add UserData in database
                            // check for user type selection and create user data accordingly
                            if(mBinding.userType.getSelectedItemPosition() == 0){
                                userData = new G5UserData(fireBaseUser.getEmail(), UserType.REFUGEE);
                                if(!TextUtils.isEmpty(mBinding.txtUnNumber.getText().toString())){
                                    userData.unNumber = mBinding.txtUnNumber.getText().toString();
                                }
                            } else {
                                userData = new G5UserData(fireBaseUser.getEmail(), UserType.SPONSOR);
                            }

                            userData.fName = mBinding.txtFirstName.getText().toString();
                            userData.lName = mBinding.txtLastName.getText().toString();
                            if(!TextUtils.isEmpty(mBinding.txtPhoneNumber.getText().toString())){
                                userData.phoneNumber = mBinding.txtPhoneNumber.getText().toString();
                            }
                            if(!TextUtils.isEmpty(mBinding.txtNationality.getText().toString())){
                                userData.nationality = mBinding.txtNationality.getText().toString();
                            }

                            mDatabase.child("users").child(fireBaseUser.getUid()).setValue(userData);

                            updateUI(fireBaseUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String fName = mBinding.txtFirstName.getText().toString();
        if (TextUtils.isEmpty(fName)) {
            mBinding.txtFirstName.setError("First Name Required.");
            valid = false;
        } else {
            mBinding.txtFirstName.setError(null);
        }

        String lName = mBinding.txtLastName.getText().toString();
        if (TextUtils.isEmpty(lName)) {
            mBinding.txtLastName.setError("Last Name Required.");
            valid = false;
        } else {
            mBinding.txtLastName.setError(null);
        }

        String email = mBinding.txtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mBinding.txtEmail.setError("Email Required.");
            valid = false;
        } else {
            mBinding.txtEmail.setError(null);
        }

        String password = mBinding.txtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mBinding.txtPassword.setError("Password Required.");
            valid = false;
        } else {
            mBinding.txtPassword.setError(null);
        }

        String confirmPassword = mBinding.txtConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(confirmPassword)) {
            mBinding.txtConfirmPassword.setError("Confirm Password Required.");
            valid = false;
        } else if (!confirmPassword.equals(password)) {
            mBinding.txtConfirmPassword.setError("Please enter password again.");
            valid = false;
        }else {
            mBinding.txtConfirmPassword.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressBar();
        if (user != null) {
            mBinding.status.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));

            mBinding.btnSubmit.setVisibility(View.GONE);
            mBinding.btnCancel.setVisibility(View.GONE);
            mBinding.btnConfirm.setVisibility(View.VISIBLE);
        } else {
            mBinding.status.setText(R.string.signed_out);

            mBinding.btnSubmit.setVisibility(View.VISIBLE);
            mBinding.btnCancel.setVisibility(View.VISIBLE);
            mBinding.btnConfirm.setVisibility(View.GONE);
        }
    }
}