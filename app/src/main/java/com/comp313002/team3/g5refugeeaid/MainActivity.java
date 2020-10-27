package com.comp313002.team3.g5refugeeaid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private Button bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt = findViewById(R.id.enterButton);

        bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enterButton:
                Intent intent = new Intent(this, UserLoginActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}