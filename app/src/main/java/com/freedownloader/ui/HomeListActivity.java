package com.freedownloader.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.freedownloader.MusicApp;
import com.freedownloader.R;
import com.freedownloader.bean.JamendoBean;
import com.freedownloader.bean.MusicArchiveBean;
import com.freedownloader.bean.TitleBean;
import com.freedownloader.data.Song;
import com.freedownloader.data.jamendo.JamendoApi;
import com.freedownloader.data.jamendo.JamendoService;
import com.freedownloader.facebook.FBAdUtils;
import com.freedownloader.util.AdViewWrapperAdapter;
import com.freedownloader.util.FormatUtil;
import com.freedownloader.util.LogUtil;
import com.freedownloader.util.Utils;
import com.freedownloader.view.DownloadBottomSheetDialog;
import com.facebook.ads.NativeAd;
import com.paginate.Paginate;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Response;

/**
 * Created by liyanju on 2018/6/21.
 */

public class HomeListActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private View loadingView;

    private TextView mStatusTV;

    private RecyclerView recyclerView;

    public static final String TAG = "HomeList";

    private ArrayList<Song> mList = new ArrayList<>();

    private AdViewWrapperAdapter mCommonAdapter;

    public static void launch(Activity activity, int type, String title) {
        Intent intent = new Intent(activity, HomeListActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("title", title);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_bottom_in, 0);
    }

    public static void launch(Activity activity, int type, String title, String tags) {
        Intent intent = new Intent(activity, HomeListActivity.class);
        intent.putExtra("tags", tags);
        intent.putExtra("title", title);
        intent.putExtra("type", type);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_bottom_in, 0);
    }

    public static void launch(Activity activity, String title, int key, int type) {
        Intent intent = new Intent(activity, HomeListActivity.class);
        intent.putExtra("key", key);
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

    private String mTitle;
    private int mType;
    private int mKey;
    private String mTags;
    private int curOffset = JamendoService.PAGE_LIMIT;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", mTitle);
        outState.putInt("type", mType);
        outState.putString("tags", mTags);
        outState.putInt("key", mKey);
        outState.putParcelableArrayList("list", mList);
        outState.putInt("curOffset",curOffset);
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_list_layout);

        toolbar = findViewById(R.id.toolbar);
        Utils.setViewBackgroud(toolbar);
        Utils.setActivityStatusColor(this);
        loadingView = findViewById(R.id.loading_pb);
        mStatusTV = findViewById(R.id.status_iv);
        recyclerView = findViewById(R.id.recycler_view);

        if (savedInstanceState != null) {
            mType = savedInstanceState.getInt("type", 0);
            mTitle = savedInstanceState.getString("title");
            mTags = savedInstanceState.getString("tags", "");
            mKey = savedInstanceState.getInt("key");
            mList = savedInstanceState.getParcelableArrayList("list");
            curOffset = savedInstanceState.getInt("curOffset", JamendoService.PAGE_LIMIT);
        } else {
            mType = getIntent().getIntExtra("type", 0);
            mTitle = getIntent().getStringExtra("title");
            mTags = getIntent().getStringExtra("tags");
            mKey = getIntent().getIntExtra("key", 0);

            try {
                if (TextUtils.isEmpty(mTags)) {
                    if (mType != TitleBean.RECOMMEND_TYPE) {
                        ArrayList<Song> arrayList = (ArrayList<Song>) HomeFragment.getDataByType(mType);
                        if (arrayList != null) {
                            mList.addAll(arrayList);
                        }
                    } else {
                        HashMap<Integer, MusicArchiveBean> map = (HashMap<Integer, MusicArchiveBean>) HomeFragment.getDataByType(mType);
                        if (map.get(mKey).contentList != null) {
                            mList.addAll(map.get(mKey).contentList);
                        }
                    }
                } else {
                    mList = new ArrayList<>();
                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }

        setUpRecyclerView();

        setUpToolbar();

        if (!TextUtils.isEmpty(mTags)) {
            if (mPaginate != null) {
                mPaginate.setHasMoreDataToLoad(false);
            }
            loadingView.setVisibility(View.VISIBLE);
            requestHomeList();
        }

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mTitle);
    }

    private Paginate mPaginate;

    RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.default_thumbnail)
            .error(R.drawable.default_thumbnail);

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        CommonAdapter adapter = new CommonAdapter<Song>(this, R.layout.list_item, mList) {
            @Override
            protected void convert(final ViewHolder holder, final Song song, int position) {
                ImageView imageView = holder.getView(R.id.itemThIV);
                Glide.with(HomeListActivity.this).load(song.getImageUrl())
                        .apply(options).into(imageView);

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DownloadBottomSheetDialog.newInstance(song).showBottomSheetFragment(getSupportFragmentManager());
                    }
                });

                TextView titleTV = holder.getView(R.id.itemTitleView);
                titleTV.setText(song.getName());

                TextView textTV = holder.getView(R.id.itemTextView);
                textTV.setText(song.getArtistName());

                TextView timeTV = holder.getView(R.id.time_tv);
                if (song.getDuration() != 0) {
                    timeTV.setText(FormatUtil.formatMusicTime(song.getDuration()));
                    timeTV.setVisibility(View.VISIBLE);
                } else {
                    timeTV.setVisibility(View.INVISIBLE);
                }
            }
        };

        mCommonAdapter = new AdViewWrapperAdapter(adapter);
        recyclerView.setAdapter(mCommonAdapter);
        if (mType != TitleBean.RECOMMEND_TYPE) {
            mPaginate = Paginate.with(recyclerView, callbacks)
                    .setLoadingTriggerThreshold(2)
                    .build();
            mPaginate.setHasMoreDataToLoad(true);
        }

        if (mList.size() > 3) {
            NativeAd nativeAd = FBAdUtils.nextNativieAd();
            if (nativeAd != null && nativeAd.isAdLoaded() && !mCommonAdapter.isAddAdView()) {
                mCommonAdapter.addAdView(22, new AdViewWrapperAdapter.
                        AdViewItem(FBAdUtils.setUpItemNativeAdView(this, nativeAd), 1));
            }
        }
    }

    private boolean isLoading;
    private boolean isLoaded;
    private void showLoadMore() {
        isLoading = true;
        isLoaded = false;
    }

    private void hideLoadMore() {
        isLoading = false;
        isLoaded = false;
    }

    Paginate.Callbacks callbacks = new Paginate.Callbacks() {
        @Override
        public void onLoadMore() {
            LogUtil.v(TAG, "onLoadMore>>>>>>> curOffset:" + curOffset);
            showLoadMore();
            requestHomeList();
        }

        @Override
        public boolean isLoading() {
            return isLoading;
        }

        @Override
        public boolean hasLoadedAllItems() {
            return isLoaded;
        }
    };

    private void requestHomeList() {
        String order = JamendoService.LISTEN_TOTAL_ORDER;
        if (mType == TitleBean.TOP_DOWNLOAD_TYPE) {
            order = JamendoService.DOWNLOADS_TOTAL_ORDER;
        }
        new AsyncTask<String, Void, JamendoBean>() {

            @Override
            protected JamendoBean doInBackground(String... voids) {
                try {
                    Response<JamendoBean> response;
                    if (!TextUtils.isEmpty(mTags)) {
                        response = JamendoApi.getJamendoService(MusicApp.sContext)
                                .getJamendoDataByTags(mTags, curOffset).execute();
                    } else {
                        response = JamendoApi.getJamendoService(MusicApp.sContext)
                                .getJamendoDataByOrder(voids[0], curOffset).execute();
                    }
                    return response.body();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JamendoBean jamendoModel) {
                super.onPostExecute(jamendoModel);
                if (HomeListActivity.this.isFinishing()) {
                    return;
                }
                if (loadingView.getVisibility() == View.VISIBLE) {
                    loadingView.setVisibility(View.GONE);
                    if (mPaginate != null) {
                        mPaginate.setHasMoreDataToLoad(true);
                    }
                }
                hideLoadMore();

                if (jamendoModel != null && jamendoModel.arrayList.size() > 0) {
                    LogUtil.v(TAG, "NEXT PAGE FINISHED ....");
                    curOffset = curOffset + JamendoService.PAGE_LIMIT;

                    int positionStart = mCommonAdapter.getItemCount();
                    int itemCount = jamendoModel.arrayList.size();
                    NativeAd nativeAd = FBAdUtils.nextNativieAd();
                    if (nativeAd != null && nativeAd.isAdLoaded() && !mCommonAdapter.isAddAdView()
                            && jamendoModel.arrayList.size() > 3) {
                        mCommonAdapter.addAdView(22, new AdViewWrapperAdapter.
                                AdViewItem(FBAdUtils.setUpItemNativeAdView(HomeListActivity.this, nativeAd), 1));
                        itemCount++;
                    }

                    mList.addAll(jamendoModel.arrayList);
                    mCommonAdapter.notifyItemRangeInserted(positionStart, itemCount);
                    return;
                }

                if ((jamendoModel != null && jamendoModel.arrayList.size() == 0) && mList.size() == 0) {
                    showEmptyView();
                } else if (mList.size() == 0 && jamendoModel == null) {
                    showErrorView();
                }
            }
        }.executeOnExecutor(Utils.sExecutorService2, order);

    }


    private void showErrorView() {
        recyclerView.setVisibility(View.GONE);
        mStatusTV.setVisibility(View.VISIBLE);
        Drawable drawable = ContextCompat.getDrawable(MusicApp.sContext, R.drawable.ic_error);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mStatusTV.setCompoundDrawables(null, drawable,
                null, null);
        mStatusTV.setText(R.string.network_error);
    }

    private void showEmptyView() {
        recyclerView.setVisibility(View.GONE);
        mStatusTV.setVisibility(View.VISIBLE);
        Drawable drawable = ContextCompat.getDrawable(MusicApp.sContext, R.drawable.ic_empty);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mStatusTV.setCompoundDrawables(null, drawable,
                null, null);
        mStatusTV.setText(R.string.empty_error);
    }
}
