package com.othershe.dutil.net;

import com.othershe.dutil.callback.FileCallback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpManager {
    private OkHttpClient okHttpClient;
    private FileCallback fileCallback;

    private OkHttpManager() {
        okHttpClient = new OkHttpClient.Builder()
//                .addNetworkInterceptor(getInterceptor())
                .build();
    }

    public static OkHttpManager getInstance() {
        return OkHttpHolder.instance;
    }

    private static class OkHttpHolder {
        private static final OkHttpManager instance = new OkHttpManager();
    }

    public Call initRequest(String url, long start, long end, final Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .header("Range", "bytes=" + start + "-" + end)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public void initRequest(String url, final Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .header("Range", "bytes=0-")
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    public void initRequest(String url, final Callback callback, String ifRange) {
        Request request = new Request.Builder()
                .url(url)
                .header("Range", "bytes=0-")
                .header("If-Range", ifRange)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    private Interceptor getInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), fileCallback))
                        .build();
            }
        };
    }
}
