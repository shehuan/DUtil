package com.othershe.dutil.net;

import com.othershe.dutil.callback.FileCallback;
import com.othershe.dutil.download.FileUtil;

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
    private String url;
    private String path;
    private String name;

    private OkHttpManager() {
        okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(getInterceptor())
                .build();
    }

    public static OkHttpManager getInatance() {
        return OkHttpHolder.instance;
    }

    private static class OkHttpHolder {
        private static final OkHttpManager instance = new OkHttpManager();
    }

    public void initRequest(String url, final String path, final String name, FileCallback fileCallback) {
        this.url = url;
        this.path = path;
        this.name = name;
        this.fileCallback = fileCallback;

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FileUtil.saveFile(response, 0, path, name);
            }
        });
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
