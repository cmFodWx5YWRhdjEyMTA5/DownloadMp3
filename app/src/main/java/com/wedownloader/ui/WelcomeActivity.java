package com.wedownloader.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.wedownloader.R;

import java.lang.reflect.Method;


/**
 * Created by liyanju on 2018/5/25.
 */

public class WelcomeActivity extends AppCompatActivity{

    private View container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MainActivity.sIsInActivity) {
            startMainActivity();
            return;
        }

        setContentView(R.layout.splash_activity);

        FrameLayout frameLayout = findViewById(R.id.splash_container);

        try {
            int height = getVirtualBarHeigh();
            if (height > 0) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(frameLayout.getLayoutParams());
                lp.setMargins(0, height, 0, 0);
                frameLayout.setLayoutParams(lp);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        initViews();
    }

    public int getVirtualBarHeigh() {
        int vh = 0;
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return vh;
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
