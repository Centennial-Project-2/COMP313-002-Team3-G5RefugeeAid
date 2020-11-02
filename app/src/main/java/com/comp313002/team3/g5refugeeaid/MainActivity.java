package com.comp313002.team3.g5refugeeaid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.comp313002.team3.g5refugeeaid.databinding.ActivityMainBinding;
import com.comp313002.team3.g5refugeeaid.databinding.ActivityUserLoginBinding;
import com.comp313002.team3.models.G5UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "Main";

    private ActivityMainBinding mBinding;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private G5UserData userData;
    // [END declare_auth]

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();

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
                            FirebaseUser user = mAuth.getCurrentUser();
                            // go to dashboard
                            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                            intent.putExtra("email", user.getEmail());
                            startActivity(intent);
                            mBinding.txtPassword.setText("");
                            mBinding.txtEmail.setText("");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
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
}