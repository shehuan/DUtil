package com.othershe.dutil.upload;

import android.os.Handler;
import android.os.Message;

import com.othershe.dutil.callback.UploadCallback;
import com.othershe.dutil.net.OkHttpManager;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.othershe.dutil.data.Consts.ERROR;
import static com.othershe.dutil.data.Consts.FINISH;
import static com.othershe.dutil.data.Consts.START;

public abstract class BaseUploadRequest {

    protected String url;
    protected Map<String, String> params;
    protected Map<String, String> headers;
    private Handler handler;

    public Call upload(final UploadCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("UploadCallback can not be null");
        }

        UploadProgressHandler progressHandler = new UploadProgressHandler(callback);
        handler = progressHandler.getHandler();
        handler.sendEmptyMessage(START);

        RequestBody requestBody = initRequestBody();
        requestBody = new ProgressRequestBody(requestBody, handler);

        return OkHttpManager.getInstance().initRequest(url, requestBody, headers, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = Message.obtain();
                message.what = ERROR;
                message.obj = e.toString();
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    Message message = Message.obtain();
                    message.what = FINISH;
                    message.obj = response.body().string();
                    handler.sendMessage(message);
                }
            }
        });
    }

    protected abstract RequestBody initRequestBody();

}
