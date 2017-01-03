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

    /**
     * 根据断点请求
     *
     * @param url
     * @param start
     * @param end
     * @param callback
     * @return
     */
    public Call initRequest(String url, long start, long end, final Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .header("Range", "bytes=" + start + "-" + end)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
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

    /**
     * 文件存在的情况下可判断服务端文件是否已经更改
     *
     * @param url
     * @param lastModify
     * @return
     * @throws IOException
     */
    public Response initRequest(String url, String lastModify) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Range", "bytes=0-")
                .header("If-Range", lastModify)
                .build();

        return okHttpClient.newCall(request).execute();
    }
}
