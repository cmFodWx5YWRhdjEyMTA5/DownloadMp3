package com.mp3downloader.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mp3downloader.App;
import com.mp3downloader.R;
import com.mp3downloader.model.BaseModel;
import com.mp3downloader.model.youtube.VideoStream.ParseStreamMetaData;
import com.mp3downloader.model.youtube.VideoStream.StreamMetaData;
import com.mp3downloader.model.youtube.YouTubeModel;
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
        return R.layout.download_bs_dialog;
    }

    private void parseYouTubeUrl(String vid, final Runnable runnable) {
        mParseTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                StreamMetaData streamMetaData = new ParseStreamMetaData(strings[0]).getStreamMetaDataList()
                        .getDesiredStream();
                return streamMetaData.getUri().toString();
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
                            FileDownloaderHelper.addDownloadTask(mSong, new WeakReference<>(mActivity));
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
                    FileDownloaderHelper.addDownloadTask(mSong, new WeakReference<>(mActivity));
                }
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
                                Utils.playMusic(App.sContext, mSong.getPlayUrl());
                                Utils.showLongToastSafe(R.string.music_playing);
                            }
                        });
                    } else {
                        try {
                            dismiss();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        Utils.playMusic(App.sContext, mSong.getPlayUrl());
                        Utils.showLongToastSafe(R.string.music_playing);
                    }


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
