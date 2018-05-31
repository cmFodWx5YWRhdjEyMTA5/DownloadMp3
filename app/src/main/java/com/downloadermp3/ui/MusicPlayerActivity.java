package com.downloadermp3.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.downloadermp3.Mp3App;
import com.downloadermp3.R;
import com.downloadermp3.util.Constants;
import com.downloadermp3.facebook.FBAdUtils;
import com.downloadermp3.util.FormatUtil;
import com.downloadermp3.util.SimpleSeekBarChangeListener;
import com.downloadermp3.util.Utils;
import com.facebook.ads.Ad;
import com.lzx.musiclibrary.aidl.listener.OnPlayerEventListener;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.constans.PlayMode;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.musiclibrary.manager.TimerTaskManager;
import com.downloadermp3.data.BaseModel;
import com.downloadermp3.util.FileDownloaderHelper;
import com.downloadermp3.util.LogUtil;
import com.downloadermp3.view.CircleImageView;
import com.plattysoft.leonids.ParticleSystem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SupportActivity;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by liyanju on 2018/5/23.
 */

public class MusicPlayerActivity extends SupportActivity implements OnPlayerEventListener, View.OnClickListener{

    public static final String TAG = "MusicPlayerActivity";
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private TextView mSongName, mStartTime, mTotalTime;
    private CircleImageView mMusicCover;
    private ImageView mBlueBg, mBtnPlayPause, mBtnPre, mBtnNext, mBtnDownload;
    private AppCompatImageView mBtnLoop;
    private SeekBar mSeekBar;
    private MaterialProgressBar mLoadingPB;

    private TimerTaskManager mTimerTaskManager;

    private List<SongInfo> songInfos;
    private SongInfo mSongInfo;
    private int position;

    private ObjectAnimator mCoverAnim;
    private long currentPlayTime = 0;

    public static void launch(Context context, List<SongInfo> songInfos, int position) {
        Intent intent = new Intent(context, MusicPlayerActivity.class);
        intent.putParcelableArrayListExtra("SongInfos", (ArrayList<? extends Parcelable>) songInfos);
        intent.putExtra("position", position);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void launch(Context context, SongInfo songInfo, BaseModel baseModel) {
        Intent intent = new Intent(context, MusicPlayerActivity.class);
        intent.putExtra("songInfo", songInfo);
        intent.putExtra("basemodel", baseModel);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private BaseModel mBaseModel;

    private void initIntent() {
        songInfos = getIntent().getParcelableArrayListExtra("SongInfos");
        mSongInfo = getIntent().getParcelableExtra("songInfo");
        if (songInfos != null) {
            position = getIntent().getIntExtra("position", position);
            mSongInfo = songInfos.get(position);
        }
        mBaseModel = getIntent().getParcelableExtra("basemodel");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.transparence(this);
        setContentView(R.layout.activity_playing_detail);

        initIntent();

        initView();

        updateUI(mSongInfo);

        initMusicCoverAnim();

        MusicManager.get().addPlayerEventListener(this);

        if (songInfos != null) {
            MusicManager.get().playMusic(songInfos, position);
        } else {
            MusicManager.get().playMusicByInfo(mSongInfo, true);
        }

        mTimerTaskManager = new TimerTaskManager();
        mTimerTaskManager.setUpdateProgressTask(new Runnable() {
            @Override
            public void run() {
                updateProgress();
            }
        });

        if (MusicManager.isPaused()) {
            MusicManager.get().resumeMusic();
        }

        FBAdUtils.interstitialLoad(Constants.CHA_YE_HEIGH_ID, new FBAdUtils.FBInterstitialAdListener(){
            @Override
            public void onInterstitialDismissed(Ad ad) {
                super.onInterstitialDismissed(ad);
                FBAdUtils.destoryInterstitial();
            }
        });
    }

    private void updateUI(SongInfo music) {
        if (music == null) {
            return;
        }
        mSongName.setText(music.getSongName());

        Glide.with(this).load(music.getSongCover()).into(mMusicCover);
        Glide.with(this).load(music.getSongCover()).into(mBlueBg);
    }

    private void initView() {
        mSongName = findViewById(R.id.song_name);
        mMusicCover = findViewById(R.id.music_cover);
        mBlueBg = findViewById(R.id.blue_bg);
        mBtnPlayPause = findViewById(R.id.btn_play_pause);
        mBtnPlayPause.setEnabled(false);
        mBtnPre = findViewById(R.id.btn_pre);
        mBtnPre.setEnabled(false);
        mBtnNext = findViewById(R.id.btn_next);
        mBtnNext.setEnabled(false);
        mSeekBar = findViewById(R.id.seekBar);
        mStartTime = findViewById(R.id.start_time);
        mTotalTime = findViewById(R.id.total_time);
        mLoadingPB = findViewById(R.id.loading_play);
        mBtnLoop = findViewById(R.id.song_loop_iv);
        mBtnLoop.setEnabled(false);
        LogUtil.v(TAG, "MusicManager.get().getPlayMode() " + MusicManager.get().getPlayMode());
        if (MusicManager.get().getPlayMode() == PlayMode.PLAY_IN_LIST_LOOP) {
            mBtnLoop.setImageResource(R.drawable.ic_play_mode_list);
            MusicManager.get().setPlayMode(PlayMode.PLAY_IN_LIST_LOOP);
        } else {
            mBtnLoop.setImageResource(R.drawable.ic_play_mode_single);
            MusicManager.get().setPlayMode(PlayMode.PLAY_IN_SINGLE_LOOP);
        }
        mBtnLoop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                LogUtil.v(TAG, "MusicManager.get().getPlayMode() " + MusicManager.get().getPlayMode());
                if (MusicManager.get().getPlayMode() != PlayMode.PLAY_IN_LIST_LOOP) {
                    mBtnLoop.setImageResource(R.drawable.ic_play_mode_list);
                    MusicManager.get().setPlayMode(PlayMode.PLAY_IN_LIST_LOOP);
                } else {
                    mBtnLoop.setImageResource(R.drawable.ic_play_mode_single);
                    MusicManager.get().setPlayMode(PlayMode.PLAY_IN_SINGLE_LOOP);
                }
            }
        });
        mBtnDownload = findViewById(R.id.play_download_iv);
        mBtnDownload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!Utils.checkAndStoreRequestPermissions(MusicPlayerActivity.this)) {
                    Utils.showLongToastSafe(R.string.permission_text_tips);
                    return;
                }
                FileDownloaderHelper.addDownloadTask(mBaseModel,
                        new WeakReference<Activity>(MusicPlayerActivity.this));
            }
        });

        mBtnPlayPause.setOnClickListener(this);
        mBtnPre.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                super.onStopTrackingTouch(seekBar);
                MusicManager.get().seekTo(seekBar.getProgress());
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setBtnCanEnabled() {
        mBtnPre.setEnabled(true);
        mBtnPlayPause.setEnabled(true);
        mBtnNext.setEnabled(true);
        mBtnLoop.setEnabled(true);
    }


    /**
     * 更新进度
     */
    private void updateProgress() {
        long progress = MusicManager.get().getProgress();
        long bufferProgress = MusicManager.get().getBufferedPosition();
        mSeekBar.setProgress((int) progress);
        mSeekBar.setSecondaryProgress((int) bufferProgress);

        mStartTime.setText(FormatUtil.formatMusicTime(progress));
    }

    /**
     * 转圈动画
     */
    private void initMusicCoverAnim() {
        mCoverAnim = ObjectAnimator.ofFloat(mMusicCover, "rotation", 0, 359);
        mCoverAnim.setDuration(20000);
        mCoverAnim.setInterpolator(new LinearInterpolator());
        mCoverAnim.setRepeatCount(ValueAnimator.INFINITE);
    }

    /**
     * 开始转圈
     */
    private void startCoverAnim() {
        mCoverAnim.start();
        mCoverAnim.setCurrentPlayTime(currentPlayTime);
    }

    /**
     * 停止转圈
     */
    private void pauseCoverAnim() {
        currentPlayTime = mCoverAnim.getCurrentPlayTime();
        mCoverAnim.cancel();
    }

    private void resetCoverAnim() {
        pauseCoverAnim();
        mMusicCover.setRotation(0);
    }

    private Integer[] starArray = new Integer[]{
            R.drawable.pl_blue,
            R.drawable.pl_red,
            R.drawable.pl_yellow
    };

    private ParticleSystem ps;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int random = (int) (Math.random() * starArray.length);
                ps = new ParticleSystem(this, 100, starArray[random], 800);
                ps.setScaleRange(0.7f, 1.3f);
                ps.setSpeedRange(0.05f, 0.1f);
                ps.setRotationSpeedRange(90, 180);
                ps.setFadeOut(200, new AccelerateInterpolator());
                ps.emit((int) event.getX(), (int) event.getY(), 40);
                break;
            case MotionEvent.ACTION_MOVE:
                ps.updateEmitPoint((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
                ps.stopEmitting();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play_pause:
                if (MusicManager.isPlaying()) {
                    MusicManager.get().pauseMusic();
                } else {
                    MusicManager.get().resumeMusic();
                }
                break;
            case R.id.btn_pre:
                if (MusicManager.get().hasPre()) {
                    MusicManager.get().playPre();

                } else {
                    Toast.makeText(Mp3App.sContext, R.string.not_last, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_next:
                if (MusicManager.get().hasNext()) {
                    MusicManager.get().playNext();
                } else {
                    Toast.makeText(Mp3App.sContext, R.string.not_next, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    public void onMusicSwitch(SongInfo music) {
        mSongInfo = music;
        mBtnPlayPause.setImageResource(R.drawable.ic_play);
        updateUI(music);
    }

    @Override
    public void onPlayerStart() {
        mBtnPlayPause.setImageResource(R.drawable.ic_pause);
        mTimerTaskManager.scheduleSeekBarUpdate();
        startCoverAnim();
        LogUtil.v(TAG, "onPlayerStart");
    }

    @Override
    public void onPlayerPause() {
        mBtnPlayPause.setImageResource(R.drawable.ic_play);
        mTimerTaskManager.stopSeekBarUpdate();
        pauseCoverAnim();
    }

    @Override
    public void onPlayCompletion() {
        mTimerTaskManager.stopSeekBarUpdate();
        mBtnPlayPause.setImageResource(R.drawable.ic_play);
        mSeekBar.setProgress(0);
        mStartTime.setText("00:00");
        resetCoverAnim();
    }

    @Override
    public void onPlayerStop() {

    }

    @Override
    public void onError(String errorMsg) {
        Toast.makeText(Mp3App.sContext, R.string.play_error, Toast.LENGTH_SHORT).show();
        resetCoverAnim();
        mLoadingPB.setVisibility(View.GONE);
        setBtnCanEnabled();
        LogUtil.e(TAG, " onError errorMsg " + errorMsg);
    }

    @Override
    public void onAsyncLoading(boolean isFinishLoading) {
        LogUtil.v(TAG, "onAsyncLoading  isFinishLoading " + isFinishLoading);
        if (!isFinishLoading) {
            setBtnCanEnabled();
            mTotalTime.setText(FormatUtil.formatMusicTime(MusicManager.get().getDuration()));
            mSeekBar.setMax(MusicManager.get().getDuration());
            mLoadingPB.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetCoverAnim();
        mCoverAnim = null;
        mTimerTaskManager.onRemoveUpdateProgressTask();
        MusicManager.get().removePlayerEventListener(this);
        MusicManager.get().stopMusic();
        MusicManager.get().stopNotification();

        if (FBAdUtils.isInterstitialLoaded()) {
            FBAdUtils.showInterstitial();
        }
        FBAdUtils.destoryInterstitial();
    }
}
