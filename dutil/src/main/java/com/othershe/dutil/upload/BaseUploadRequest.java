package com.othershe.dutil.upload;

import com.othershe.dutil.callback.UploadCallback;
import com.othershe.dutil.net.OkHttpManager;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class BaseUploadRequest {

    protected String url;
    protected Map<String, String> params;
    protected Map<String, String> headers;

    public Call upload(final UploadCallback callback) {

        RequestBody requestBody = initRequestBody();

        if (callback != null) {
            callback.onStart();
            requestBody = new ProgressRequestBody(requestBody, callback);
        }

        return OkHttpManager.getInstance().initRequest(url, requestBody, headers, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
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

    private RequestBody initRequestBody() {
        RequestBody requestBody;

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                builder.addFormDataPart(key, params.get(key));
            }
        }

        buildRequestBody(builder);

        requestBody = builder.build();

        return requestBody;
    }

    protected abstract void buildRequestBody(MultipartBody.Builder builder);
}
