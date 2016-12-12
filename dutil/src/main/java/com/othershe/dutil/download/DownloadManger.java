package com.othershe.dutil.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.othershe.dutil.Utils.Utils;
import com.othershe.dutil.callback.DownloadCallback;
import com.othershe.dutil.net.OkHttpManager;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.othershe.dutil.data.Consts.ON_ERROR;
import static com.othershe.dutil.data.Consts.ON_FINISH;
import static com.othershe.dutil.data.Consts.ON_PROGRESS;
import static com.othershe.dutil.data.Consts.ON_START;


public class DownloadManger {

    private DownloadCallback downloadCallback;

    private String url;
    private String path;
    private String name;
    private int thread;
    private int task;

    private Context context;

    private int currentSize = 0;
    private int totalSize = 0;

    private FileHandler mFileHandler;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ON_START:
                    totalSize = msg.arg1;
                    downloadCallback.onStart(Utils.formatSize(totalSize));
                    break;
                case ON_PROGRESS:
                    synchronized (this) {
                        currentSize += msg.arg1;
                        downloadCallback.onProgress(Utils.formatSize(currentSize),
                                Utils.formatSize(totalSize),
                                Utils.getPercentage(currentSize, totalSize));
                    }

                    if (currentSize >= totalSize) {
                        sendEmptyMessage(ON_FINISH);
                    }

                    break;
                case ON_FINISH:
                    Utils.deleteFile(new File(path, name + ".temp"));
                    downloadCallback.onFinish(new File(path, name));
                    break;
                case ON_ERROR:
                    downloadCallback.onError((String) msg.obj);
                    break;
            }
        }
    };

    public DownloadManger(Context context, String url, String path, String name, int thread, int task) {
        this.context = context;
        this.url = url;
        this.path = path;
        this.name = name;
        this.thread = thread;
        this.task = task;
    }

    public DownloadManger execute(final DownloadCallback callback) {
        this.downloadCallback = callback;

        OkHttpManager.getInstance().initRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mFileHandler.onError(e.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                mFileHandler = new FileHandler(url, path, name, thread, mHandler);

                if (Utils.isSupportRange(response)) {
                    mFileHandler.saveRangeFile(response);
                } else {
                    mFileHandler.saveCommonFile(response);
                }
            }
        });

        return this;
    }

    public void pause() {
        mFileHandler.onPause();
    }

    public void resume() {
        mFileHandler.onResume();
    }

    public void cancel() {
        mFileHandler.onCancel();
        mFileHandler.onProgress(0);

        Utils.deleteFile(new File(path, name + ".temp"));
        Utils.deleteFile(new File(path, name));
    }

    public void restart() {
        mFileHandler.onRestart();

        OkHttpManager.getInstance().initRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mFileHandler.onError(e.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                mFileHandler = new FileHandler(url, path, name, thread, mHandler);

                if (Utils.isSupportRange(response)) {
                    mFileHandler.saveRangeFile(response);
                } else {
                    mFileHandler.saveCommonFile(response);
                }
            }
        });
    }
}
