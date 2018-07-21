package com.freedownloader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.freedownloader.R;


/**
 * Created by liyanju on 2018/5/25.
 */

public class SplashActivity extends AppCompatActivity{

    private View container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MainActivity.sIsInActivity) {
            startMainActivity();
            return;
        }

        setContentView(R.layout.splash_activity);

        initViews();
    }

    private void initViews() {
        container = findViewById(R.id.splash_container);
        container.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, 3000);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
