package com.freedownloader.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.freedownloader.MusicApp;
import com.freedownloader.R;
import com.freedownloader.data.DownloadTask;
import com.freedownloader.db.DownloadDao;
import com.freedownloader.util.AdViewWrapperAdapter;
import com.freedownloader.util.Constants;
import com.freedownloader.facebook.FBAdUtils;
import com.freedownloader.util.Utils;
import com.facebook.ads.NativeAd;
import com.freedownloader.util.FileDownloaderHelper;
import com.rating.RatingActivity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by liyanju on 2018/5/7.
 */

public class DownloadFragment extends SupportFragment {

    private RecyclerView mRecyclerView;

    private ArrayList<DownloadTask> mArrayList = new ArrayList<>();

    private static final String TAG = "DownloadFragment";

    private AsyncTask mLoadTask;

    private ImageView mEmptyIV;

    private AdViewWrapperAdapter mAdViewWrapperAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.download_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.download_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MusicApp.sContext));
        mAdViewWrapperAdapter = new AdViewWrapperAdapter(mCommonAdapter);
        mRecyclerView.setAdapter(mAdViewWrapperAdapter);
        mEmptyIV = view.findViewById(R.id.empty_iv);
        return view;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        FileDownloaderHelper.registerDownloadFinishListener(mRunnable);
        mRunnable.run();
    }

    Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            initData();
        }
    };

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        FileDownloaderHelper.removeDownloadFinishListener(mRunnable);
    }

    private void initData() {
        mLoadTask = new AsyncTask<Void,Void,ArrayList<DownloadTask>>(){
            @Override
            protected ArrayList<DownloadTask> doInBackground(Void... voids) {
                return DownloadDao.getAllDownloaded(MusicApp.sContext);
            }

            @Override
            protected void onPostExecute(ArrayList<DownloadTask> list) {
                super.onPostExecute(list);
                if (list.size() == 0) {
                    mEmptyIV.setVisibility(View.VISIBLE);
                } else {
                    mEmptyIV.setVisibility(View.GONE);
                }

                mAdViewWrapperAdapter.clearAdView();

                NativeAd nativeAd = FBAdUtils.nextNativieAd();
                if (nativeAd == null || !nativeAd.isAdLoaded()) {
                    nativeAd = FBAdUtils.getNativeAd();
                }

                if (nativeAd != null && nativeAd.isAdLoaded() && list.size() > 3) {

                    mAdViewWrapperAdapter.addAdView(1, new AdViewWrapperAdapter.
                            AdViewItem(FBAdUtils.setUpItemNativeAdView(_mActivity, nativeAd), 1));
                }

                Collections.reverse(list);
                mArrayList.clear();
                mArrayList.addAll(list);

                mAdViewWrapperAdapter.notifyDataSetChanged();
            }
        }.executeOnExecutor(Utils.sExecutorService2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mLoadTask != null) {
            mLoadTask.cancel(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sIsPlayMusic) {
            sIsPlayMusic = false;
            if (MusicApp.isYTB() || MusicApp.isSCloud()) {
                RatingActivity.launch(MusicApp.sContext, "",
                        MusicApp.sContext.getString(R.string.download_rating));
            }
        }
    }

    private static boolean sIsPlayMusic = false;

    private CommonAdapter mCommonAdapter = new CommonAdapter<DownloadTask>(MusicApp.sContext,
            R.layout.list_item, mArrayList) {

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.default_thumbnail)
                .error(R.drawable.default_thumbnail);

        @Override
        protected void convert(ViewHolder holder, final DownloadTask baseModel, int position) {
            ImageView itemThumbnialIV = holder.getView(R.id.itemThIV);
            Glide.with(_mActivity).load(baseModel.getImageUrl()).apply(options).into(itemThumbnialIV);

            TextView titleTV = holder.getView(R.id.itemTitleView);
            titleTV.setText(baseModel.getName());

            TextView textTV = holder.getView(R.id.itemTextView);
            textTV.setText(baseModel.getArtistName());

            holder.setOnClickListener(R.id.list_item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.playMusic(MusicApp.sContext, baseModel.getPlayUrl());
                    FBAdUtils.showAdDialog(_mActivity, Constants.NATIVE_ID_DIALOG);
                    sIsPlayMusic = true;
                }
            });
            holder.setOnLongClickListener(R.id.list_item, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDeleteDialog(_mActivity, baseModel);
                    return false;
                }
            });
        }
    };

    private void showDeleteDialog(Activity activity, final DownloadTask downloadTask) {
        new MaterialDialog.Builder(activity).content(R.string.del_des)
                .positiveText(R.string.ok_text).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
                doDeleteVideo(downloadTask);
            }
        }).negativeText(R.string.cancel_text).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void doDeleteVideo(final DownloadTask downloadTask) {
        new AsyncTask<DownloadTask, Void, Void>(){
            @Override
            protected Void doInBackground(DownloadTask... downloadTasks) {
                DownloadDao.removeDownloaded(MusicApp.sContext, downloadTasks[0].id);
                File file = new File(downloadTasks[0].getPlayUrl());
                if (file.exists()) {
                    file.delete();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (_mActivity.isFinishing()) {
                    return;
                }
                initData();
            }
        }.executeOnExecutor(Utils.sExecutorService, downloadTask);
    }
}
