package com.freedownloader.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.freedownloader.Mp3App;
import com.freedownloader.R;
import com.freedownloader.bean.JamendoModel;
import com.freedownloader.util.Utils;
import com.freedownloader.view.GridSpacingItemDecoration;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;

/**
 * Created by liyanju on 2018/6/21.
 */

public class HomeGridActivity extends AppCompatActivity {

    private ArrayList<JamendoModel> mList = new ArrayList<>();

    private CommonAdapter mCommonAdapter;

    RecyclerView recyclerView;

    TextView empty;

    View loadingView;

    Toolbar toolbar;

    private String mTitle;
    private int mType;

    public static void launch(Activity activity, String title, int type) {
        Intent intent = new Intent(activity, HomeGridActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("type", type);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_bottom_in, 0);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("list", mList);
        outState.putString("title", mTitle);
        outState.putInt("type", mType);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_list_layout);
        toolbar = findViewById(R.id.toolbar);
        Utils.setViewBackgroud(toolbar);
        Utils.setActivityStatusColor(this);
        loadingView = findViewById(R.id.loading_pb);
        recyclerView = findViewById(R.id.recycler_view);

        if (savedInstanceState != null) {
            mType = savedInstanceState.getInt("type", 0);
            mTitle = savedInstanceState.getString("title");
            mList = (ArrayList<JamendoModel>) savedInstanceState.getSerializable("list");
        } else {
            mType = getIntent().getIntExtra("type", 0);
            mTitle = getIntent().getStringExtra("title");
            mList = (ArrayList<JamendoModel>) HomeFragment.getDataByType(mType);
        }

        setUpToolbar();
        setUpRecyclerView();

        itemHeight = (Utils.getScreenWhith() - Utils.dip2px(Mp3App.sContext, 2)*4)/3;
    }

    private int itemHeight;

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, Utils.dip2px(Mp3App.sContext, 2), false));
        recyclerView.setPadding(Utils.dip2px(Mp3App.sContext, 2), Utils.dip2px(Mp3App.sContext, 2),
                Utils.dip2px(Mp3App.sContext, 2),Utils.dip2px(Mp3App.sContext, 2));

        mCommonAdapter = new CommonAdapter<JamendoModel>(this, R.layout.home_grid_item, mList) {
            @Override
            protected void convert(ViewHolder holder, final JamendoModel jamendoModel, int position) {
                ImageView imageView = holder.getView(R.id.image);
                imageView.setImageResource(jamendoModel.imageRes);
                imageView.getLayoutParams().height = itemHeight;
                Glide.with(HomeGridActivity.this).load(jamendoModel.imageRes)
                        .apply(Utils.requestOptions).into(imageView);

                TextView titleTV = holder.getView(R.id.title);
                titleTV.setText(jamendoModel.name);

                holder.setOnClickListener(R.id.home_grid_item_frame, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HomeListActivity.launch(HomeGridActivity.this, jamendoModel.type,
                                jamendoModel.name, jamendoModel.tags);
                    }
                });
            }
        };
        recyclerView.setAdapter(mCommonAdapter);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mTitle);
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
}
