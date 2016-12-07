package com.othershe.dutil.download;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.othershe.dutil.callback.DownloadCallback;
import com.othershe.dutil.callback.FileCallback;
import com.othershe.dutil.net.OkHttpManager;
import com.othershe.dutil.service.DownloadService;
import com.othershe.dutil.service.ThreadPool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadManger {
    private FileCallback fileCallback;

    private String url;
    private String path;
    private String name;
    private Context context;
    private long fileSize;

    public DownloadManger(Context context, String url, String path, String name) {
        this.url = url;
        this.path = path;
        this.name = name;
        this.context = context;
    }

    public void execute(final FileCallback fileCallback) {
        this.fileCallback = fileCallback;

//        Intent intent = new Intent(context, DownloadService.class);
//        intent.putExtra("url", url);
//        intent.putExtra("path", path);
//        intent.putExtra("name", name);
//        context.startService(intent);

        OkHttpManager.getInstance().initRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (fileCallback instanceof DownloadCallback) {
//                    ((DownloadCallback) fileCallback).onError(e.toString());
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
//                FileUtil.saveFile(response, 0, path, name);

                ResponseBody body = response.body();
                fileSize = body.contentLength();
//                File file = new File(path, name);
//                if (file.exists()) {
//                    file.delete();
//                }

                RandomAccessFile accessFile = new RandomAccessFile(new File(path, name), "rwd");
                accessFile.setLength(fileSize);
                accessFile.close();


                final int tc = 5;
                final long range = fileSize / tc;

                for (int i = 0; i < tc - 1; i++) {
                    final int finalI = i;
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {

                            OkHttpManager.getInstance().initRequest(url, finalI * range, (1 + finalI) * range - 1, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    if (fileCallback instanceof DownloadCallback) {
                                        ((DownloadCallback) fileCallback).onError(e.toString());
                                    }
                                }

                                @Override
                                public void onResponse(Call call, final Response response) throws IOException {
                                    Log.e(Thread.currentThread().getName(), "start");
                                    FileUtil.saveFile(response, finalI * range, range, path, name);
                                    Log.e(Thread.currentThread().getName(), "end");
                                }
                            });

                        }
                    };

                    ThreadPool.THREAD_POOL_EXECUTOR.execute(runnable);
                }

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        OkHttpManager.getInstance().initRequest(url, (tc - 1) * range, fileSize - 1, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                if (fileCallback instanceof DownloadCallback) {
                                    ((DownloadCallback) fileCallback).onError(e.toString());
                                }
                            }

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {
                                Log.e(Thread.currentThread().getName(), "start");
                                FileUtil.saveFile(response, (tc - 1) * range, range, path, name);
                                Log.e(Thread.currentThread().getName(), "end");
                            }
                        });
                    }
                };
//
                ThreadPool.THREAD_POOL_EXECUTOR.execute(runnable);
            }
        });

    }
}
