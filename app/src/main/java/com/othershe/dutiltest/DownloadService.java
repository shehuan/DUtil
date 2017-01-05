package com.othershe.dutiltest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.othershe.dutil.DUtil;
import com.othershe.dutil.callback.SimpleDownloadCallback;
import com.othershe.dutil.download.DownloadManger;

import java.io.File;

public class DownloadService extends Service {
    private Context mContext;

    private String path;
    private String name;
    private String url;
    private int notifyId;

    private DownloadBinder mBinder = new DownloadBinder();

    class DownloadBinder extends Binder {
        public void startDownload() {
            DUtil.init(mContext)
                    .path(path)
                    .name(name)
                    .url(url)
                    .childTaskCount(3)
                    .build()
                    .start(new SimpleDownloadCallback() {
                        @Override
                        public void onStart(long currentSize, long totalSize, float progress) {
                            NotificationUtil.createProgressNotification(mContext, name, "快来玩我呀！！！", R.mipmap.ic_launcher, notifyId);
                        }

                        @Override
                        public void onProgress(long currentSize, long totalSize, float progress) {
                            NotificationUtil.updateNotification(notifyId, progress);
                        }

                        @Override
                        public void onFinish(File file) {
                            NotificationUtil.cancelNotification(notifyId);
                            Toast.makeText(mContext, "下载完成", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onWait() {

                        }
                    });
        }

        public float getProgress() {
            return 1;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            path = intent.getStringExtra("path");
            name = intent.getStringExtra("name");
            url = intent.getStringExtra("url");
            notifyId = intent.getIntExtra("notifyId", 0);
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
