package com.othershe.dutil.upload;

import android.text.TextUtils;

import com.othershe.dutil.callback.UploadCallback;
import com.othershe.dutil.net.OkHttpManager;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadRequest {

    private String url;
    private File file;
    private String type;

    public UploadRequest(String url, File file, String type) {
        this.url = url;
        this.file = file;
        this.type = type;
    }

    public void upload(final UploadCallback callback) {

        if (callback != null) {
            callback.onStart();
        }

        type = TextUtils.isEmpty(type) ? "application/octet-stream" : type;

        RequestBody fileBody = RequestBody.create(MediaType.parse(type), file);
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(fileBody, callback);

        OkHttpManager.getInstance().initRequest(url, progressRequestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null){
                    callback.onError(e.toString());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    if (callback != null) {
                        callback.onFinish(response.body().string());
                    }
                }
            }
        });
    }
}
