package com.othershe.dutil.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.othershe.dutil.Utils.Utils;
import com.othershe.dutil.callback.DownloadCallback;
import com.othershe.dutil.data.DownloadData;
import com.othershe.dutil.db.Db;
import com.othershe.dutil.net.OkHttpManager;
import com.othershe.dutil.service.ThreadPool;

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

    private FileHandler mFileHandler;

    private int mCurrentState = NONE;
    //是否没有取消，直接重新开始
    private boolean isDirectRestart;
    //是否有之前未下载完成的文件存在
    private boolean isFileExist;
    //取消操作是否已删除本地文件和清除数据库（每次取消、重新开始需赋值为false）
    private boolean isDataDeleted;
    //是否支持断点续传
    private boolean isSupportRange = true;

    //记录已经下载的大小
    private int currentSize = 0;
    //记录文件总大小
    private int totalSize = 0;
    //记录已经暂停或取消的线程数
    private int tempCount = 0;

    private DownloadData downloadData;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mCurrentState = msg.what;

            switch (mCurrentState) {
                case START:
                    totalSize = msg.arg1;
                    if (!isFileExist && isSupportRange) {
                        Db.getInstance(context).insertData(new DownloadData(url, path, name, 0, totalSize, System.currentTimeMillis()));
                    }
                    if (downloadData == null) {
                        downloadCallback.onStart(currentSize, totalSize, Utils.getPercentage(currentSize, totalSize));
                    } else {
                        downloadData.setCurrentSize(currentSize);
                        downloadData.setTotalSize(totalSize);
                        downloadData.setPercentage(Utils.getPercentage(currentSize, totalSize));
                        downloadData.setState(START);
                    }
                    break;
                case PROGRESS:
                    synchronized (this) {
                        currentSize += msg.arg1;
                        if (downloadData == null) {
                            downloadCallback.onProgress(currentSize, totalSize, Utils.getPercentage(currentSize, totalSize));
                        } else {
                            downloadData.setCurrentSize(currentSize);
                            downloadData.setPercentage(Utils.getPercentage(currentSize, totalSize));
                            downloadData.setState(PROGRESS);
                        }
                        if (currentSize == totalSize) {
                            sendEmptyMessage(FINISH);
                        }
                    }
                    break;
                case CANCEL:
                    synchronized (this) {
                        if (!isDataDeleted) {

                            isDataDeleted = true;

                            currentSize = 0;
                            if (downloadData == null) {
                                downloadCallback.onProgress(0, totalSize, 0);
                            } else {
                                downloadData.setCurrentSize(0);
                                downloadData.setPercentage(0);
                                downloadData.setState(CANCEL);
                            }
                            if (isSupportRange) {
                                Db.getInstance(context).deleteData(url);
                                Utils.deleteFile(new File(path, name + ".temp"));
                            }
                            Utils.deleteFile(new File(path, name));
                            if (downloadData == null) {
                                downloadCallback.onCancel();
                            }

                            if (isDirectRestart) {
                                sendEmptyMessage(RESTART);
                                isDirectRestart = false;
                            }
                        }
                    }
                    break;
                case RESTART:
                    initDownload();
                    break;
                case PAUSE:
                    synchronized (this) {
                        if (isSupportRange) {
                            Db.getInstance(context).updateData(currentSize, url);
                        }
                        tempCount++;
                        if (tempCount == thread) {
                            if (downloadData == null) {
                                downloadCallback.onPause();
                            } else {
                                downloadData.setState(PAUSE);
                            }
                            tempCount = 0;
                        }
                    }
                    break;
                case FINISH:
                    currentSize = 0;
                    if (isSupportRange) {
                        Utils.deleteFile(new File(path, name + ".temp"));
                        Db.getInstance(context).deleteData(url);
                    }
                    if (downloadData == null) {
                        downloadCallback.onFinish(new File(path, name));
                    } else {
                        downloadData.setState(FINISH);
                    }
                    break;
                case DESTROY:
                    synchronized (this) {
                        if (isSupportRange) {
                            Db.getInstance(context).updateData(currentSize, url);
                        }
                    }
                    break;
                case ERROR:
                    if (downloadData == null) {
                        downloadCallback.onError((String) msg.obj);
                    } else {
                        downloadData.setState(ERROR);
                    }
                    currentSize = 0;
                    totalSize = 0;
                    if (isSupportRange) {
                        Db.getInstance(context).deleteData(url);
                        Utils.deleteFile(new File(path, name + ".temp"));
                    }
                    Utils.deleteFile(new File(path, name));
                    break;
            }
            if (downloadData != null){
                DownloadMangerPool.getInstance(context).update(downloadData);
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

    /**
     * 执行单个任务下载
     *
     * @param callback
     * @return
     */
    public DownloadManger execute(DownloadCallback callback) {
        this.downloadCallback = callback;
        mFileHandler = new FileHandler(url, path, name, thread, mHandler);

        DownloadData data = Db.getInstance(context).getData(url);
        if (data == null) {
            initDownload();
        } else {
            isFileExist = true;
            isSupportRange = true;
            currentSize = data.getCurrentSize();
            Message message = Message.obtain();
            message.what = START;
            message.arg1 = data.getTotalSize();
            mHandler.sendMessage(message);
        }

        return this;
    }

    /**
     * 执行下载管理
     *
     * @param data
     * @return
     */
    public DownloadManger execute(final DownloadData data) {
        this.downloadData = data;
        this.url = data.getUrl();
        this.path = data.getPath();
        this.name = data.getName();
        this.thread = data.getThread();

        mFileHandler = new FileHandler(url, path, name, thread, mHandler);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DownloadData oldData = Db.getInstance(context).getData(url);
                if (oldData == null) {
                    initDownload();
                } else {
                    isFileExist = true;
                    isSupportRange = true;
                    currentSize = data.getCurrentSize();
                    Message message = Message.obtain();
                    message.what = START;
                    message.arg1 = data.getTotalSize();
                    mHandler.sendMessage(message);
                }
            }
        };
        ThreadPool.THREAD_POOL_EXECUTOR.execute(runnable);

        return this;
    }

    /**
     * 下载中退出时保存数据、释放资源
     */
    public void destroy() {
        if (mCurrentState == CANCEL || mCurrentState == PAUSE) {
            return;
        }
        mFileHandler.onDestroy();
    }

    /**
     * 暂停（正在下载才可以暂停）
     * 如果文件不支持断点续传则不能进行暂停操作
     */
    public void pause() {
        if (mCurrentState == PROGRESS) {
            mFileHandler.onPause();
        }
    }

    /**
     * 继续（只有暂停、重新进入的状态可以执行继续下载）
     * 如果文件不支持断点续传则不能进行继续操作
     */
    public void resume() {
        if (isFileExist) {
            isFileExist = false;
            initDownload();
            return;
        }

        if (mCurrentState == PAUSE) {
            mFileHandler.onResume();
        }
    }

    /**
     * 取消（已经被取消、下载结束则不可取消）
     */
    public void cancel() {
        if (mCurrentState == CANCEL || mCurrentState == FINISH) {
            return;
        }
        isDataDeleted = false;
        mFileHandler.onCancel();
    }

    /**
     * 重新开始（所有状态都可重新下载）
     */
    public void restart() {
        isDataDeleted = false;
        if (isFileExist) {
            isFileExist = false;
        }
        if (mCurrentState == CANCEL || mCurrentState == FINISH || mCurrentState == ERROR) {
            initDownload();
        } else {
            isDirectRestart = true;
            mFileHandler.onRestart();
        }
    }

    /**
     * 开始从头下载
     */
    private void initDownload() {
        OkHttpManager.getInstance().initRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mFileHandler.onError(e.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!mFileHandler.startDownload(response)) {
                    thread = 1;
                    isSupportRange = false;
                }
            }
        });
    }
}
