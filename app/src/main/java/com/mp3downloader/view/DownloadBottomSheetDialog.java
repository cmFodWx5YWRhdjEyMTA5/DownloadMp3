package com.mp3downloader.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.ads.NativeAd;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.mp3downloader.App;
import com.mp3downloader.R;
import com.mp3downloader.model.BaseModel;
import com.mp3downloader.model.youtube.VideoStream.ParseStreamMetaData;
import com.mp3downloader.model.youtube.VideoStream.StreamMetaData;
import com.mp3downloader.model.youtube.YouTubeModel;
import com.mp3downloader.musicgo.PlayingDetailActivity;
import com.mp3downloader.util.FBAdUtils;
import com.mp3downloader.util.FacebookReport;
import com.mp3downloader.util.FileDownloaderHelper;
import com.mp3downloader.util.LogUtil;
import com.mp3downloader.util.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by liyanju on 2018/5/18.
 */

public class DownloadBottomSheetDialog extends BaseBottomSheetFragment {

    public static final String TAG = "DownloadBottomSheet";

    private BaseModel mSong;

    private View mLoadingView;

    private AsyncTask mParseTask;

    private Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    public static DownloadBottomSheetDialog newInstance(BaseModel song) {
        DownloadBottomSheetDialog fragment = new DownloadBottomSheetDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("song", song);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.download_bottom_dialog;
    }

    private void parseYouTubeUrl(String vid, final Runnable runnable) {
        mParseTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    StreamMetaData streamMetaData = new ParseStreamMetaData(strings[0]).getStreamMetaDataList()
                            .getDesiredStream();
                    return streamMetaData.getUri().toString();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mLoadingView.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String url) {
                LogUtil.v(TAG, "onPostExecute url " + url);
                super.onPostExecute(url);
                mLoadingView.setVisibility(View.GONE);
                if (mSong != null && mSong instanceof YouTubeModel.Snippet) {
                    ((YouTubeModel.Snippet)mSong).downloadurl = url;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }
        }.executeOnExecutor(Utils.sExecutorService, vid);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mParseTask != null) {
            mParseTask.cancel(true);
        }
    }

    @Override
    public void initView() {
        mSong = getArguments().getParcelable("song");

        if (!Utils.checkAndStoreRequestPermissions(mActivity)) {
            try {
                dismiss();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            Utils.showLongToastSafe(R.string.permission_text_tips);
            return;
        }

        mLoadingView = rootView.findViewById(R.id.parse_load_linear);
        TextView titleTV = rootView.findViewById(R.id.title_tv);
        titleTV.setText(mSong.getName());

        rootView.findViewById(R.id.download_linear).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (mSong.getType() == BaseModel.YOUTUBE_TYPE) {
                    parseYouTubeUrl(((YouTubeModel.Snippet) mSong).vid, new Runnable() {
                        @Override
                        public void run() {
                            if (isShowing()) {
                                try {
                                    dismiss();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                            if (!TextUtils.isEmpty(mSong.getDownloadUrl())) {
                                FileDownloaderHelper.addDownloadTask(mSong,
                                        new WeakReference<>(mActivity));
                            } else {
                                Utils.showLongToastSafe(R.string.parse_url_failure);
                            }
                        }
                    });
                } else {
                    try {
                        try {
                            dismiss();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    if (!TextUtils.isEmpty(mSong.getDownloadUrl())) {
                        FileDownloaderHelper.addDownloadTask(mSong, new WeakReference<>(mActivity));
                    } else {
                        Utils.showLongToastSafe(R.string.parse_url_failure);
                    }
                }

                FacebookReport.logSentStartDownload(mSong.getName());
            }
        });

        rootView.findViewById(R.id.play_linear).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    if (mSong.getType() == BaseModel.YOUTUBE_TYPE) {
                        parseYouTubeUrl(((YouTubeModel.Snippet) mSong).vid, new Runnable() {
                            @Override
                            public void run() {
                                if (isShowing()) {
                                    try {
                                        dismiss();
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (TextUtils.isEmpty(mSong.getPlayUrl())) {
                                    Utils.showLongToastSafe(R.string.parse_url_failure);
                                    return;
                                }
                                SongInfo songInfo = new SongInfo();
                                songInfo.setSongId(((YouTubeModel.Snippet) mSong).vid);
                                songInfo.setSongUrl(mSong.getPlayUrl());
                                songInfo.setSongName(mSong.getName());
                                songInfo.setDuration(mSong.getDuration());
                                songInfo.setSongCover(mSong.getImageUrl());

                                PlayingDetailActivity.launch(App.sContext, songInfo, mSong);
                            }
                        });
                    } else {
                        try {
                            dismiss();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        if (TextUtils.isEmpty(mSong.getPlayUrl())) {
                            Utils.showLongToastSafe(R.string.parse_url_failure);
                            return;
                        }

                        SongInfo songInfo = new SongInfo();
                        songInfo.setSongId(String.valueOf(System.currentTimeMillis()));
                        songInfo.setSongUrl(mSong.getPlayUrl());
                        songInfo.setSongCover(mSong.getImageUrl());
                        songInfo.setSongName(mSong.getName());
                        songInfo.setDuration(mSong.getDuration());

                        PlayingDetailActivity.launch(App.sContext, songInfo, mSong);
                        LogUtil.v(TAG, " getPlayUrl ::" + mSong.getPlayUrl());
                    }

                    FacebookReport.logSentPlayMusic();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        rootView.findViewById(R.id.license_linear).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    try {
                        dismiss();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("https://creativecommons.org/licenses/by-nc/3.0/");
                    intent.setData(content_url);
                    startActivity(intent);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        FrameLayout adContainer = rootView.findViewById(R.id.ad_container);

        NativeAd nativeAd = FBAdUtils.nextNativieAd();
        if (nativeAd == null || !nativeAd.isAdLoaded()) {
            nativeAd = FBAdUtils.getNativeAd();
        }
        if (nativeAd != null && nativeAd.isAdLoaded()) {
            adContainer.removeAllViews();
            adContainer.addView(FBAdUtils.setUpItemNativeAdView(mActivity, nativeAd, true));
        }
    }

    public void showBottomSheetFragment(FragmentManager manager) {
        try {
            if (manager.findFragmentByTag(DownloadBottomSheetDialog.class.getName()) == null) {
                show(manager, DownloadBottomSheetDialog.class.getName());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
