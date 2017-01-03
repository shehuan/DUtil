package com.othershe.dutiltest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.othershe.dutil.DUtil;
import com.othershe.dutil.callback.DownloadCallback;

import java.io.File;

public class DownloadService extends Service {
    private Context mContext;

    private int id;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DUtil.init(mContext)
                .path(intent.getStringExtra("path"))
                .name(intent.getStringExtra("name"))
                .url(intent.getStringExtra("url"))
                .childTaskCount(3)
                .build()
                .start(new DownloadCallback() {
                    @Override
                    public void onStart(long currentSize, long totalSize, float progress) {
                        id = NotificationUtil.createProgressNotification(mContext, "消消乐", "快来玩我呀！！！", R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                    }

                    @Override
                    public void onProgress(long currentSize, long totalSize, float progress) {
                        NotificationUtil.updateNotification(id, (int) progress);
                    }

                    @Override
                    public void onPause() {

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onFinish(File file) {

                    }

                    @Override
                    public void onWait() {

                    }

                    @Override
                    public void onError(String error) {

                    }
                });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
