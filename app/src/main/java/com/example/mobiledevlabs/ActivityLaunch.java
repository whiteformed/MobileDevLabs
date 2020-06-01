package com.example.mobiledevlabs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityLaunch extends AppCompatActivity {
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent activityLogin = new Intent(context, ActivityLogin.class);
                startActivity(activityLogin);
                finish();
            }
        };

        int delay = 2000;
        new Handler().postDelayed(runnable, delay);
    }
}
