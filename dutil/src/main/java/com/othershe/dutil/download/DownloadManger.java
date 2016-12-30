package com.othershe.dutil.download;

import android.content.Context;

import com.othershe.dutil.callback.DownloadCallback;
import com.othershe.dutil.data.DownloadData;
import com.othershe.dutil.service.ThreadPool;

import java.util.HashMap;
import java.util.Map;

import static com.othershe.dutil.data.Consts.NONE;
import static com.othershe.dutil.data.Consts.PAUSE;
import static com.othershe.dutil.data.Consts.PROGRESS;

public class DownloadManger {

    private Context context;

    private Map<String, ProgressHandler> progressHandlerMap = new HashMap<>();//保存任务的进度处理对象
    private Map<String, DownloadData> downloadDataMap = new HashMap<>();//保存任务数据
    private Map<String, DownloadCallback> callbackMap = new HashMap<>();//保存任务回调
    private Map<String, FileTask> fileTaskMap = new HashMap<>();//保存下载线程

    private DownloadData downloadData;

    private volatile static DownloadManger downloadManager;

    public static DownloadManger getInstance(Context context) {
        if (downloadManager == null) {
            synchronized (DownloadManger.class) {
                if (downloadManager == null) {
                    downloadManager = new DownloadManger(context);
                }
            }
        }
        return downloadManager;
    }

    private DownloadManger(Context context) {
        this.context = context;
    }

    public void init(String url, String path, String name, int childTaskCount) {
        downloadData = new DownloadData();
        downloadData.setUrl(url);
        downloadData.setPath(path);
        downloadData.setName(name);
        downloadData.setChildTaskCount(childTaskCount == 0 ? 3 : childTaskCount);//默认每个任务分割成3个异步任务

        downloadDataMap.put(url, downloadData);
    }

    public DownloadManger execute(DownloadCallback callback) {
        callbackMap.put(downloadData.getUrl(), callback);
        start(downloadData, callback);

        return downloadManager;
    }

    public DownloadManger execute() {
        start(downloadData, null);
        return downloadManager;
    }

    public void execute(DownloadData data) {
        start(data, null);
    }

    /**
     * 开始从头下载
     */
    private void start(DownloadData downloadData, DownloadCallback downloadCallback) {
        ProgressHandler progressHandler = new ProgressHandler(context, downloadData, downloadCallback);
        FileTask fileTask = new FileTask(context, downloadData, progressHandler.getHandler());
        progressHandler.setFileTask(fileTask);
        fileTaskMap.put(downloadData.getUrl(), fileTask);
        progressHandlerMap.put(downloadData.getUrl(), progressHandler);
        ThreadPool.THREAD_POOL_EXECUTOR.execute(fileTask);
    }

    /**
     * 暂停
     *
     * @param url
     */
    public void pause(String url) {
        if (progressHandlerMap.get(url).getCurrentState() == PROGRESS) {
            progressHandlerMap.get(url).pause();
        }
    }

    /**
     * 继续
     *
     * @param url
     */
    public void resume(String url) {
        if (progressHandlerMap.get(url).getCurrentState() == PAUSE) {
            progressHandlerMap.remove(url);
            start(downloadDataMap.get(url), callbackMap.get(url));
        }
    }

    /**
     * 重新开始
     *
     * @param url
     */
    public void restart(String url) {
        cancel(url);
        start(downloadDataMap.get(url), callbackMap.get(url));
    }

    /**
     * 取消
     *
     * @param url
     */
    public void cancel(String url) {
        if (progressHandlerMap.containsKey(url)) {
            if (progressHandlerMap.get(url).getCurrentState() == NONE) {
                //取消缓存队列中等待下载的任务
                ThreadPool.THREAD_POOL_EXECUTOR.remove(fileTaskMap.get(url));
            } else {
                //取消已经开始下载的任务
                progressHandlerMap.get(url).cancel();
            }
            progressHandlerMap.remove(url);
        }
    }

    /**
     * 单个退出
     *
     * @param url
     */
    public void destroy(String url) {
        if (progressHandlerMap.containsKey(url)) {
            progressHandlerMap.get(url).destroy();
            progressHandlerMap.remove(url);
        }
    }

    /**
     * 全部退出
     */
    public void destroy() {
        for (ProgressHandler progressHandler : progressHandlerMap.values()) {
            progressHandler.destroy();
        }
        progressHandlerMap.clear();
    }
}
