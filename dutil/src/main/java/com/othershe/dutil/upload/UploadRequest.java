package com.othershe.dutil.upload;

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
    private MediaType mediaType;

    public UploadRequest(String url, File file, MediaType mediaType) {
        this.url = url;
        this.file = file;

        if (mediaType == null) {
            this.mediaType = MediaType.parse("application/octet-stream");
        }
    }

    public void upload(final UploadCallback callback) {

        RequestBody fileBody = RequestBody.create(mediaType, file);
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(fileBody, callback);

        OkHttpManager.getInstance().initRequest(url, progressRequestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    callback.onFinish();
                }
            }
        });
    }
}
