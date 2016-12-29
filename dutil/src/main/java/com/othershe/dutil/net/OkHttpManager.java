package com.othershe.dutil.net;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpManager {
    private OkHttpClient okHttpClient;

    private OkHttpManager() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
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

    /**
     * 同步请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public Response initRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Range", "bytes=0-")
                .build();

        return okHttpClient.newCall(request).execute();
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
}
