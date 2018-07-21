package com.freedownloader.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.freedownloader.R;
import com.freedownloader.util.Utils;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by liyanju on 2018/7/21.
 */

public class DownloadActivity extends SupportActivity{

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_activity);

        toolbar = findViewById(R.id.toolbar);
        Utils.setViewBackgroud(toolbar);
        Utils.setActivityStatusColor(this);
        setUpToolbar();

        getSupportFragmentManager()
                .beginTransaction().replace(R.id.content_frame, new DownloadFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.download2_text2);
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, DownloadActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
