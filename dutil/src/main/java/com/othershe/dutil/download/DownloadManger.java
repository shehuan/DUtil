package com.othershe.dutil.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.othershe.dutil.Utils.Utils;
import com.othershe.dutil.callback.DownloadCallback;
import com.othershe.dutil.data.DownloadData;
import com.othershe.dutil.db.Db;
import com.othershe.dutil.net.OkHttpManager;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.othershe.dutil.data.Consts.CANCEL;
import static com.othershe.dutil.data.Consts.ERROR;
import static com.othershe.dutil.data.Consts.FINISH;
import static com.othershe.dutil.data.Consts.NONE;
import static com.othershe.dutil.data.Consts.PAUSE;
import static com.othershe.dutil.data.Consts.PROGRESS;
import static com.othershe.dutil.data.Consts.START;

public class DownloadManger {

    private String url;
    private String path;
    private String name;
    private int thread;
    private int task;

    private Context context;

    private DownloadCallback downloadCallback;

    private int mCurrentState = NONE;

    private int currentSize = 0;
    private int totalSize = 0;

    private FileHandler mFileHandler;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mCurrentState = msg.what;
            switch (mCurrentState) {
                case START:
                    totalSize = msg.arg1;
                    DownloadData data = Db.getInstance(context).getData(url);
                    if (data != null) {
                        currentSize = data.getCurrentSize();
                    } else {
                        Db.getInstance(context).insertData(new DownloadData(url, path, name, 0, totalSize, System.currentTimeMillis()));
                    }

                    downloadCallback.onStart(currentSize, totalSize, Utils.getPercentage(currentSize, totalSize));
                    break;
                case PROGRESS:
                    synchronized (this) {
                        currentSize += msg.arg1;
                        downloadCallback.onProgress(currentSize, totalSize, Utils.getPercentage(currentSize, totalSize));

                        if (currentSize == totalSize) {
                            sendEmptyMessage(FINISH);
                        }
                    }

                    break;
                case CANCEL:
                    if (currentSize == 0) {
                        return;
                    }
                    currentSize = 0;
                    downloadCallback.onProgress(0, totalSize, 0);
                    break;
                case PAUSE:
                    synchronized (this) {
                        Db.getInstance(context).updateData(currentSize, url);
                    }
                    break;
                case FINISH:
                    Utils.deleteFile(new File(path, name + ".temp"));
                    Db.getInstance(context).deleteData(url);
                    downloadCallback.onFinish(new File(path, name));
                    break;
                case ERROR:
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
        mFileHandler = new FileHandler(url, path, name, thread, mHandler);

        OkHttpManager.getInstance().initRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mFileHandler.onError(e.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
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
        mCurrentState = PAUSE;
    }

    public void resume() {
        if (mCurrentState != PAUSE) {
            return;
        }
        mFileHandler.onResume();
    }

    public void cancel() {
        mFileHandler.onCancel();
        mCurrentState = CANCEL;
    }

    public void restart() {
        if (mCurrentState != CANCEL) {
            return;
        }

        mFileHandler.onRestart();

        OkHttpManager.getInstance().initRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mFileHandler.onError(e.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (Utils.isSupportRange(response)) {
                    mFileHandler.saveRangeFile(response);
                } else {
                    mFileHandler.saveCommonFile(response);
                }
            }
        });
    }
}
