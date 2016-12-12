package com.othershe.dutil.callback;

import java.io.File;

public abstract class SimpleDownloadCallback implements DownloadCallback {

    @Override
    public void onStart(String totalSize) {

    }

    @Override
    public void onProgress(String currentSize, String totalSize, int progress) {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onFinish(File file) {

    }

    @Override
    public void onError(String error) {

    }
}
