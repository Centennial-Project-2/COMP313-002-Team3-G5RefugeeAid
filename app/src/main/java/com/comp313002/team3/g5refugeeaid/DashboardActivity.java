package com.comp313002.team3.g5refugeeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.comp313002.team3.g5refugeeaid.databinding.ActivityDashboardBinding;
import com.comp313002.team3.g5refugeeaid.databinding.ActivitySignupBinding;

public class DashboardActivity extends BaseActivity implements  View.OnClickListener {

    private static final String TAG = "Dashboard";

    private ActivityDashboardBinding mBinding;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        //setProgressBar(mBinding.progressBar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mBinding.label01.setText("Hello!");
        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        if(email != null){
            mBinding.label01.setText("Hello "+ email +" !");
        }

        mBinding.btnSignOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSignOut){
            finish();
        }
    }
}