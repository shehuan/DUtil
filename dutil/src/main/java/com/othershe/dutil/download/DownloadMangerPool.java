package com.othershe.dutil.download;

import android.content.Context;

import com.othershe.dutil.data.DownloadData;
import com.othershe.dutil.service.ThreadPool;

import java.util.HashMap;
import java.util.Map;

public class DownloadMangerPool {

    private volatile static DownloadMangerPool instance;

    private Map<String, DownloadData> downloadDataMap = new HashMap<>();
    private Map<String, DownloadManger> mangerMap = new HashMap<>();

    private Context context;

    private DownloadMangerPool(Context context) {
        this.context = context;
    }

    public static DownloadMangerPool getInstance(Context context) {
        if (instance == null) {
            synchronized (DownloadMangerPool.class) {
                if (instance == null) {
                    instance = new DownloadMangerPool(context);
                }
            }
        }
        return instance;
    }

    public void add(final String url, final DownloadData data) {
        if (!downloadDataMap.containsKey(url)) {
            downloadDataMap.put(url, data);
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DownloadManger manger = new DBuilder(context)
                        .url(data.getUrl())
                        .path(data.getPath())
                        .name(data.getName())
                        .build()
                        .execute(data);

                mangerMap.put(url, manger);
            }
        };

        ThreadPool.THREAD_POOL_EXECUTOR.execute(runnable);
    }

    public DownloadManger getDownloadManager(String url) {
        return mangerMap.get(url);
    }


}
