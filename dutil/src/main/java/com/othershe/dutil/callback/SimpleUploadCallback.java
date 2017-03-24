package com.othershe.dutil.callback;

public abstract class SimpleUploadCallback implements UploadCallback {
    @Override
    public void onStart() {

    }

    @Override
    public void onProgress(long currentSize, long totalSize, float progress) {

    }

    @Override
    public void onFinish(String response) {

    }

    @Override
    public void onError(String error) {

    }
}
