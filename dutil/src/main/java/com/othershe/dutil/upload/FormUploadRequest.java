package com.othershe.dutil.upload;

import android.graphics.Bitmap;

import com.othershe.dutil.Utils.Utils;
import com.othershe.dutil.callback.UploadCallback;
import com.othershe.dutil.data.UploadFile;
import com.othershe.dutil.net.OkHttpManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FormUploadRequest {

    private String url;
    private List<UploadFile> files;
    private Map<String, String> params;
    private Map<String, String> headers;

    private List<Bitmap> bitmaps;

    public FormUploadRequest(String url, List<UploadFile> files, List<Bitmap> bitmaps, Map<String, String> params, Map<String, String> headers) {
        this.url = url;
        this.files = files;
        this.bitmaps = bitmaps;
        this.params = params;
        this.headers = headers;
    }

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
                        callback.onFinish();
                    }
                }
            }
        });
    }

    private RequestBody initRequestBody() {
        RequestBody requestBody;

        if (files == null || files.isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.add(key, params.get(key));
                }
            }
            requestBody = builder.build();

        } else {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);

            if (params != null && params.size() > 0) {
                for (String key : params.keySet()) {
                    builder.addFormDataPart(key, params.get(key));
                }
            }

            for (UploadFile file : files) {
                RequestBody fileBody = RequestBody.create(MediaType.parse(Utils.getMimeType(file.getName())), file.getFile());
                builder.addFormDataPart(file.getKey(), file.getName(), fileBody);
            }

            requestBody = builder.build();
        }

        return requestBody;
    }

}
