package com.othershe.dutil.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class DownloadService extends Service {




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        for (int i = 0; i < 30; i++){
            final int finalI = i;
            Runnable downloadTask = new Runnable() {
                @Override
                public void run() {
                    Log.e("downloadTask", "start" + finalI + Thread.currentThread().getName());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.e("downloadTask", "end" + finalI);
                }
            };

            ThreadPool.THREAD_POOL_EXECUTOR.execute(downloadTask);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
