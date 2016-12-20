package com.othershe.dutil.callback;

import java.io.File;

public interface DownloadCallback extends FileCallback {
    /**
     * 下载开始
     */
    void onStart(long currentSize, long totalSize, float progress);

    /**
     * 下载中
     *
     * @param currentSize
     * @param totalSize
     * @param progress
     */
    void onProgress(long currentSize, long totalSize, float progress);

    /**
     * 下载暂停
     */
    void onPause();

    void onCancel();

    /**
     * 下载完成
     *
     * @param file
     */
    void onFinish(File file);

    /**
     * 下载出错
     *
     * @param error
     */
    void onError(String error);
}
