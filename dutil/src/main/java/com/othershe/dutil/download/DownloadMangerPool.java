package com.othershe.dutil.download;

import android.content.Context;

import com.othershe.dutil.callback.ProgressCallback;
import com.othershe.dutil.data.DownloadData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadMangerPool {

    private volatile static DownloadMangerPool instance;

    private Map<String, DownloadData> downloadDataMap = new HashMap<>();
    private Map<String, DownloadManger1> mangerMap = new HashMap<>();

    private Context context;

    private ProgressCallback progressCallback;

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

    public void start(DownloadData data) {

        if (data == null) {
            return;
        }
        final String url = data.getUrl();
        if (!downloadDataMap.containsKey(url)) {
            downloadDataMap.put(url, data);

            DownloadManger1 manger = new DBuilder(context)
                    .url(data.getUrl())
                    .path(data.getPath())
                    .name(data.getName())
                    .build()
                    .execute(data);

            mangerMap.put(url, manger);
        }
    }

    /**
     * 得到对应任务的DownloadManger
     *
     * @param url
     * @return
     */
    public DownloadManger1 getDownloadManager(String url) {
        return mangerMap.get(url);
    }

    /**
     * 暂停
     *
     * @param url
     */
    public void pause(String url) {
        getDownloadManager(url).pause();
    }

    /**
     * 继续
     *
     * @param url
     */
    public void resume(String url) {
        getDownloadManager(url).resume();
    }

    /**
     * 取消
     *
     * @param url
     */
    public void cancel(String url) {
        getDownloadManager(url).cancel();
    }

    /**
     * 重新开始
     *
     * @param url
     */
    public void restart(String url) {
        getDownloadManager(url).restart();
    }

    /**
     * 设置下载列表的进度监听
     *
     * @param progressCallback
     */
    public void setOnProgressCallback(ProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }

    public synchronized void update(DownloadData data) {
        String url = data.getUrl();

        downloadDataMap.put(url, data);

        List<DownloadData> datas = new ArrayList<>();
        for (DownloadData downloadData : downloadDataMap.values()) {
            datas.add(downloadData);
        }
        progressCallback.onProgress(datas);
    }
}
