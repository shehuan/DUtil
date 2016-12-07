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
        String url = intent.getStringExtra("url");
        String path = intent.getStringExtra("path");
        String name = intent.getStringExtra("name");

        for (int i = 0; i < 8; i++) {
            final int finalI = i;
            Runnable downloadTask = new Runnable() {
                @Override
                public void run() {
                    Log.e(Thread.currentThread().getName(), "start" + finalI);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Log.e(Thread.currentThread().getName(), "end" + finalI);
                }
            };

            ThreadPool.THREAD_POOL_EXECUTOR.execute(downloadTask);
            if (i > 3){
                boolean s = ThreadPool.THREAD_POOL_EXECUTOR.remove(downloadTask);
                Log.e("sss", i + "" + s);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
