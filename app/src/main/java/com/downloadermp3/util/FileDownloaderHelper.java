package com.downloadermp3.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

import com.downloadermp3.Mp3App;
import com.downloadermp3.R;
import com.downloadermp3.data.BaseModel;
import com.downloadermp3.data.DownloadTask;
import com.downloadermp3.facebook.FBAdUtils;
import com.downloadermp3.facebook.FacebookReport;
import com.downloadermp3.db.DownloadDao;
import com.downloadermp3.router.Router;
import com.downloadermp3.ui.IHomeFragment;
import com.downloadermp3.ui.MainActivity;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.notification.BaseNotificationItem;
import com.liulishuo.filedownloader.notification.FileDownloadNotificationHelper;
import com.liulishuo.filedownloader.notification.FileDownloadNotificationListener;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.rating.RatingActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by liyanju on 2018/5/21.
 */

public class FileDownloaderHelper {

    public static final String TAG = "Downloader";
    public static File defaultfile = new File(Environment.getExternalStorageDirectory(),
            Mp3App.sContext.getString(R.string.app_name));

    private static Context sContext = Mp3App.sContext;


    public static void addDownloadTask(Context context, BaseModel song, final WeakReference<Activity> activityWeakReference) {
        if (song == null) {
            return;
        }

        if (!defaultfile.exists()) {
            defaultfile.mkdirs();
        }

        if (!defaultfile.exists() || !defaultfile.canWrite() || !defaultfile.canRead()) {
            defaultfile = new File(Environment.getExternalStorageDirectory(), "FileDownloader");
        }

        if (!defaultfile.exists()) {
            defaultfile.mkdirs();
        }

        String path = defaultfile + File.separator + String.valueOf(song.getName()) + ".mp3";

        int createDownloadId = FileDownloadUtils.generateId(song.getDownloadUrl(), path);
        if (DownloadDao.getDownloadTaskById(sContext, createDownloadId) != null) {
            Utils.showLongToastSafe(R.string.download_added);
            return;
        }

        int downloadId = FileDownloader.getImpl().create(song.getDownloadUrl())
                .setPath(path)
                .setAutoRetryTimes(1)
                .setTag(song)
                .setListener(new SelfNotificationListener(new FileDownloadNotificationHelper()))
                .start();
        LogUtil.v(TAG, " addDownloadTask downloadId " + downloadId
                + " path " + path);
        LogUtil.v(TAG, "addDownloadTask url : " + song.getDownloadUrl());
        Utils.showLongToastSafe(song.getName() + " " + sContext.getString(R.string.download_add_success));

        if (activityWeakReference.get() != null) {
            FBAdUtils.showAdDialog(activityWeakReference.get(), Constants.NATIVE_ID_DIALOG);
        }

    }


    public static class SelfNotificationListener extends FileDownloadNotificationListener {

        private NotificationManager manager;

        public SelfNotificationListener(FileDownloadNotificationHelper helper) {
            super(helper);
            manager = (NotificationManager) FileDownloadHelper.getAppContext().
                    getSystemService(Context.NOTIFICATION_SERVICE);
        }

        @Override
        protected BaseNotificationItem create(BaseDownloadTask task) {
            return new NotificationItem(task.getId(), ((BaseModel) task.getTag()).getName(),
                    "");
        }

        @Override
        public void destroyNotification(final BaseDownloadTask task) {
            super.destroyNotification(task);
            Utils.runSingleThread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.v(TAG, "destroyNotification getStatus " + task.getStatus());
                    if (task.getStatus() == FileDownloadStatus.completed) {
                        BaseModel song = (BaseModel) task.getTag();
                        String path = task.getPath();
                        int id = task.getId();
                        LogUtil.v(TAG, "destroyNotification completed id :" + id + " getTitle:: "
                                + song.getName() +" path " + path);

                        Utils.showLongToastSafe(song.getName() + " " + sContext.getString(R.string.download_video_success));

                        DownloadTask downloadSong = new DownloadTask(song.getName(), path,
                                song.getImageUrl(), song.getDuration(), id, song.getArtistName());
                        DownloadDao.addDownloadTask(sContext, downloadSong);

                        notifiyDownloadFinished();

                        showCompletedNotification(task.getId(), song.getName());

                        Mp3App.sPreferences.edit().putBoolean("DownloadNew", true).apply();
                        Utils.runUIThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Router.getInstance().getReceiver(IHomeFragment.class).showRedBadge();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        if (Mp3App.isYTB() || Mp3App.isSCloud()) {
                            RatingActivity.launch(Mp3App.sContext, "",
                                    Mp3App.sContext.getString(R.string.download_rating));
                        }

                        FacebookReport.logSentDownloadFinish(song.getName());
                    } else {
                        File file = new File(task.getPath());
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
            });
        }

        private void showCompletedNotification(int id, String title) {
            NotificationCompat.Builder builder = new NotificationCompat.
                    Builder(FileDownloadHelper.getAppContext(), "download_finished");
            Intent intent = new Intent(sContext, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(sContext, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setDefaults(Notification.DEFAULT_LIGHTS)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(Mp3App.sContext.getResources().getString(R.string.finish_download))
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done);
            manager.notify(id, builder.build());
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            super.error(task, e);
            e.printStackTrace();
            Utils.showLongToastSafe(R.string.error_download);
        }
    }

    private static ArrayList<Runnable> sRunnableList = new ArrayList<>();

    public static void registerDownloadFinishListener(Runnable runnable) {
        if (sRunnableList.contains(runnable)) {
            sRunnableList.remove(runnable);
        }
        sRunnableList.add(runnable);
    }

    private static void notifiyDownloadFinished() {
        for (Runnable runnable : sRunnableList) {
            runnable.run();
        }
    }

    public static void removeDownloadFinishListener(Runnable runnable) {
        sRunnableList.remove(runnable);
    }

    public static class NotificationItem extends BaseNotificationItem {

        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        private NotificationItem(int id, String title, String desc) {
            super(id, title, desc);
            Intent intent = new Intent(sContext, MainActivity.class);

            this.pendingIntent = PendingIntent.getActivity(sContext, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("11", FileDownloadHelper.getAppContext().getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                getManager().createNotificationChannel(channel);
                builder = new NotificationCompat.Builder(FileDownloadHelper.getAppContext(), "11");
            } else {
                builder = new NotificationCompat.
                        Builder(FileDownloadHelper.getAppContext(), FileDownloadHelper.getAppContext().getString(R.string.app_name));
            }

            builder.setDefaults(Notification.DEFAULT_LIGHTS)
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setContentTitle(getTitle())
                    .setContentText(desc)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(android.R.drawable.stat_sys_download);

        }

        @Override
        public void show(boolean statusChanged, int status, boolean isShowProgress) {

            String desc = getDesc();
            switch (status) {
                case FileDownloadStatus.pending:
                    desc += " prepare";
                    break;
                case FileDownloadStatus.started:
                    desc += " started";
                    break;
                case FileDownloadStatus.progress:
                    desc += " downloading... " + (int)(getSofar() * 1f / getTotal() * 1f * 100) + "%";
                    break;
                case FileDownloadStatus.retry:
                    desc += " retry";
                    break;
                case FileDownloadStatus.error:
                    desc += " error";
                    break;
                case FileDownloadStatus.paused:
                    desc += " paused";
                    break;
                case FileDownloadStatus.completed:
                    desc += " completed";
                    break;
                case FileDownloadStatus.warn:
                    desc += " warn";
                    break;
            }

            builder.setContentTitle(getTitle())
                    .setContentText(desc);

            if (statusChanged) {
                builder.setTicker(desc);
            }

            builder.setProgress(getTotal(), getSofar(), !isShowProgress);

            getManager().notify(getId(), builder.build());
        }

    }
}
