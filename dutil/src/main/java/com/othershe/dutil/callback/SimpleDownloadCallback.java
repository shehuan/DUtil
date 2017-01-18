package com.othershe.dutil.callback;

import java.io.File;

public abstract class SimpleDownloadCallback implements DownloadCallback {

    @Override
    public void onStart(long currentSize, long totalSize, float progress) {

    }

    @Override
    public void onProgress(long currentSize, long totalSize, float progress) {

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
}
