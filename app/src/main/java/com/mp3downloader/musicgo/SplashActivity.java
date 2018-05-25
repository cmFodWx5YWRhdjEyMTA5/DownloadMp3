package com.mp3downloader.musicgo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.mp3downloader.App;
import com.mp3downloader.R;

/**
 * Created by liyanju on 2018/5/25.
 */

public class SplashActivity extends AppCompatActivity{

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.switch_service_out);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!App.isCoolLaunch) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startMainActivity();
                }
            }, 1000);
        } else {
            App.isCoolLaunch = false;
            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_service_in, 0);
        finish();
    }
}
