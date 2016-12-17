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
import static com.othershe.dutil.data.Consts.DESTROY;
import static com.othershe.dutil.data.Consts.ERROR;
import static com.othershe.dutil.data.Consts.FINISH;
import static com.othershe.dutil.data.Consts.NONE;
import static com.othershe.dutil.data.Consts.PAUSE;
import static com.othershe.dutil.data.Consts.PROGRESS;
import static com.othershe.dutil.data.Consts.RESTART;
import static com.othershe.dutil.data.Consts.START;

public class DownloadManger {

    private String url;
    private String path;
    private String name;
    private int thread;

    private Context context;

    private DownloadCallback downloadCallback;

    private int mCurrentState = NONE;

    private int currentSize = 0;
    private int totalSize = 0;

    private FileHandler mFileHandler;

    //记录已经暂停的线程数
    private int tempCount = 0;

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
                    synchronized (this) {
                        currentSize = 0;
                        downloadCallback.onProgress(0, totalSize, 0);
                        Db.getInstance(context).deleteData(url);
                        Utils.deleteFile(new File(path, name + ".temp"));
                        Utils.deleteFile(new File(path, name));
                        tempCount++;
                        if (tempCount == thread) {
                            downloadCallback.onPause();
                            tempCount = 0;
                            sendEmptyMessage(RESTART);
                        }
                    }
                    break;

                case RESTART:
                    init();
                    break;
                case PAUSE:
                    synchronized (this) {
                        Db.getInstance(context).updateData(currentSize, url);
                        tempCount++;
                        if (tempCount == thread) {
                            downloadCallback.onPause();
                            tempCount = 0;
                        }
                    }
                    break;
                case FINISH:
                    currentSize = 0;
                    Utils.deleteFile(new File(path, name + ".temp"));
                    Db.getInstance(context).deleteData(url);
                    downloadCallback.onFinish(new File(path, name));
                    break;
                case DESTROY:
                    synchronized (this) {
                        Db.getInstance(context).updateData(currentSize, url);
                    }
                    break;
                case ERROR:
                    downloadCallback.onError((String) msg.obj);
                    Db.getInstance(context).deleteData(url);
                    break;
            }
        }
    };

    public DownloadManger(Context context, String url, String path, String name, int thread) {
        this.context = context;
        this.url = url;
        this.path = path;
        this.name = name;
        this.thread = thread;
    }

    public DownloadManger execute(final DownloadCallback callback) {
        this.downloadCallback = callback;
        mFileHandler = new FileHandler(url, path, name, thread, mHandler);

        init();

        return this;
    }

    public void destroy() {
        if (mCurrentState == CANCEL) {
            return;
        }
        mFileHandler.onDestroy();
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
        mFileHandler.onRestart();
    }

    private void init() {
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
