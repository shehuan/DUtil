package com.othershe.dutil.download;

import android.content.Context;

import com.othershe.dutil.callback.DownloadCallback;
import com.othershe.dutil.data.DownloadData;
import com.othershe.dutil.db.Db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.othershe.dutil.data.Consts.NONE;
import static com.othershe.dutil.data.Consts.PAUSE;

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

    /**
     * 配置线程池
     *
     * @param corePoolSize
     * @param maxPoolSize
     */
    public void setTaskPoolSize(int corePoolSize, int maxPoolSize) {
        if (maxPoolSize > corePoolSize && maxPoolSize * corePoolSize != 0) {
            ThreadPool.getInstance().setCorePoolSize(corePoolSize);
            ThreadPool.getInstance().setMaxPoolSize(maxPoolSize);
        }
    }

    public void init(String url, String path, String name, int childTaskCount) {
        downloadData = new DownloadData();
        downloadData.setUrl(url);
        downloadData.setPath(path);
        downloadData.setName(name);
        downloadData.setChildTaskCount(childTaskCount);
    }

    public DownloadManger start(DownloadCallback downloadCallback) {
        execute(downloadData, downloadCallback);
        return downloadManager;
    }

    public void start(String url) {
        execute(downloadDataMap.get(url), callbackMap.get(url));
    }

    public void setOnDownloadCallback(DownloadData downloadData, DownloadCallback downloadCallback) {
        downloadDataMap.put(downloadData.getUrl(), downloadData);
        callbackMap.put(downloadData.getUrl(), downloadCallback);
    }

    /**
     * 获得数据库中对应url下载数据
     *
     * @param url
     * @return
     */
    public DownloadData getDbData(String url) {
        return Db.getInstance(context).getData(url);
    }

    /**
     * 获得数据库中所有下载数据
     *
     * @return
     */
    public List<DownloadData> getAllDbData() {
        return Db.getInstance(context).getAllData();
    }

    /**
     * 执行下载任务
     */
    private void execute(DownloadData downloadData, DownloadCallback downloadCallback) {
        //防止同一个任务多次下载
        if (progressHandlerMap.get(downloadData.getUrl()) != null) {
            return;
        }

        //默认每个任务不通过多个异步任务下载
        if (downloadData.getChildTaskCount() == 0) {
            downloadData.setChildTaskCount(1);
        }

        ProgressHandler progressHandler = new ProgressHandler(context, downloadData, downloadCallback);
        FileTask fileTask = new FileTask(context, downloadData, progressHandler.getHandler());
        progressHandler.setFileTask(fileTask);

        downloadDataMap.put(downloadData.getUrl(), downloadData);
        callbackMap.put(downloadData.getUrl(), downloadCallback);
        fileTaskMap.put(downloadData.getUrl(), fileTask);
        progressHandlerMap.put(downloadData.getUrl(), progressHandler);

        ThreadPool.getInstance().getThreadPoolExecutor().execute(fileTask);

        //如果正在下载的任务数量等于线程池的核心线程数，则新添加的任务处于等待状态
        if (ThreadPool.getInstance().getThreadPoolExecutor().getActiveCount() == ThreadPool.getInstance().getCorePoolSize()) {
            downloadCallback.onWait();
        }
    }

    /**
     * 暂停
     *
     * @param url
     */
    public void pause(String url) {
        progressHandlerMap.get(url).pause();
    }

    /**
     * 继续
     *
     * @param url
     */
    public void resume(String url) {
        if (progressHandlerMap.get(url).getCurrentState() == PAUSE) {
            progressHandlerMap.remove(url);
            execute(downloadDataMap.get(url), callbackMap.get(url));
        }
    }

    /**
     * 重新开始
     *
     * @param url
     */
    public void restart(String url) {
        cancel(url);
        execute(downloadDataMap.get(url), callbackMap.get(url));
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
                ThreadPool.getInstance().getThreadPoolExecutor().remove(fileTaskMap.get(url));
                callbackMap.get(url).onCancel();
            } else {
                //取消已经开始下载的任务
                progressHandlerMap.get(url).cancel();
            }
            progressHandlerMap.remove(url);
            fileTaskMap.remove(url);
        }
    }

    /**
     * 退出时释放资源
     *
     * @param url
     */
    public void destroy(String url) {
        if (progressHandlerMap.containsKey(url)) {
            progressHandlerMap.get(url).destroy();
            progressHandlerMap.remove(url);
            callbackMap.remove(url);
            downloadDataMap.remove(url);
            fileTaskMap.remove(url);
        }
    }

    public void destroy(String... urls) {
        if (urls != null) {
            for (String url : urls) {
                destroy(url);
            }
        }
    }
}
