package com.downloadermp3.view;

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

import com.downloadermp3.Mp3App;
import com.downloadermp3.R;
import com.downloadermp3.bean.YTbeModel;
import com.downloadermp3.facebook.FBAdUtils;
import com.downloadermp3.util.Utils;
import com.facebook.ads.NativeAd;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.downloadermp3.data.BaseModel;
import com.downloadermp3.data.youtube.VideoStream.ParseStreamMetaData;
import com.downloadermp3.data.youtube.VideoStream.StreamMetaData;
import com.downloadermp3.ui.MusicPlayerActivity;
import com.downloadermp3.facebook.FacebookReport;
import com.downloadermp3.util.FileDownloaderHelper;
import com.downloadermp3.util.LogUtil;

import java.lang.ref.WeakReference;

/**
 * Created by liyanju on 2018/5/18.
 */

public class DownloadBottomSheetDialog extends BaseBottomSheetFragment {

    public static final String TAG = "DownloadSheet";

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
        return R.layout.download_dialog;
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
                if (mSong != null && mSong instanceof YTbeModel.YTBSnippet) {
                    ((YTbeModel.YTBSnippet)mSong).downloadurl = url;
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

        rootView.findViewById(R.id.download2_linear).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (mSong.getType() == BaseModel.YOUTUBE_TYPE) {
                    parseYouTubeUrl(((YTbeModel.YTBSnippet) mSong).vid, new Runnable() {
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
                                FileDownloaderHelper.addDownloadTask(Mp3App.sContext, mSong,
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
                        FileDownloaderHelper.addDownloadTask(Mp3App.sContext, mSong, new WeakReference<>(mActivity));
                    } else {
                        Utils.showLongToastSafe(R.string.parse_url_failure);
                    }
                }

                FacebookReport.logSentStartDownload(mSong.getName());
            }
        });

        rootView.findViewById(R.id.play2_linear).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    if (mSong.getType() == BaseModel.YOUTUBE_TYPE) {
                        parseYouTubeUrl(((YTbeModel.YTBSnippet) mSong).vid, new Runnable() {
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

                                MusicPlayerActivity.launch(Mp3App.sContext, toSongInfo(), mSong);
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

                        MusicPlayerActivity.launch(Mp3App.sContext, toSongInfo(), mSong);
                        LogUtil.v(TAG, " getPlayUrl ::" + mSong.getPlayUrl());
                    }

                    FacebookReport.logSentPlayMusic();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        rootView.findViewById(R.id.license2_linear).setOnClickListener(new View.OnClickListener(){
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

        FrameLayout adContainer = rootView.findViewById(R.id.ad2_container);

        NativeAd nativeAd = FBAdUtils.nextNativieAd();
        if (nativeAd == null || !nativeAd.isAdLoaded()) {
            nativeAd = FBAdUtils.getNativeAd();
        }
        if (nativeAd != null && nativeAd.isAdLoaded()) {
            adContainer.removeAllViews();
            adContainer.addView(FBAdUtils.setUpItemNativeAdView(mActivity, nativeAd, true));
        }
    }

    private SongInfo toSongInfo() {
        SongInfo songInfo = new SongInfo();
        if (mSong instanceof YTbeModel.YTBSnippet) {
            songInfo.setSongId(((YTbeModel.YTBSnippet) mSong).vid);
        } else {
            songInfo.setSongId(String.valueOf(System.currentTimeMillis()));
        }
        songInfo.setSongUrl(mSong.getPlayUrl());
        songInfo.setSongCover(mSong.getImageUrl());
        songInfo.setSongName(mSong.getName());
        songInfo.setDuration(mSong.getDuration());
        return songInfo;
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
