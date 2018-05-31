package com.downloadermp3.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.downloadermp3.R;


/**
 * Created by liyanju on 2018/5/25.
 */

public class WelcomeActivity extends AppCompatActivity{

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.switch_service_out);
    }


    @Override
    protected void onResume() {
        super.onResume();

        startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.switch_service_in, 0);
        finish();
    }
}
