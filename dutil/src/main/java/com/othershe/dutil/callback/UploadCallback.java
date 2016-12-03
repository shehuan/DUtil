package com.othershe.dutil.callback;

public interface UploadCallback extends FileCallback {
    /**
     * 下载开始
     */
    void onStart();

    /**
     * 下载中
     *
     * @param currentSize
     * @param totalSize
     * @param progress
     */
    void onProgress(long currentSize, long totalSize, int progress);

    /**
     * 下载暂停
     */
    void onPause();

    /**
     * 下载完成
     */
    void onFinish();

    /**
     * 下载出错
     *
     * @param error
     */
    void onError(String error);
}
