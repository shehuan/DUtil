package com.othershe.dutil.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.othershe.dutil.Utils.Utils;
import com.othershe.dutil.callback.DownloadCallback;
import com.othershe.dutil.data.DownloadData;
import com.othershe.dutil.db.Db;
import com.othershe.dutil.service.ThreadPool;

import java.io.File;

import static com.othershe.dutil.data.Consts.CANCEL;
import static com.othershe.dutil.data.Consts.DESTROY;
import static com.othershe.dutil.data.Consts.ERROR;
import static com.othershe.dutil.data.Consts.FINISH;
import static com.othershe.dutil.data.Consts.NONE;
import static com.othershe.dutil.data.Consts.PAUSE;
import static com.othershe.dutil.data.Consts.PROGRESS;
import static com.othershe.dutil.data.Consts.RESTART;
import static com.othershe.dutil.data.Consts.START;

public class DownloadManger1 {

    private String url;
    private String path;
    private String name;
    private int childTaskCount;

    private Context context;

    private DownloadCallback downloadCallback;

    private FileTask mFileTask;

    private int mCurrentState = NONE;
    private int mLastSate = NONE;

    //是否没有取消，直接重新开始
    private boolean isRestart;
    //是否支持断点续传
    private boolean isSupportRange;
    //
    private boolean isPauseError;

    //记录已经下载的大小
    private int currentSize = 0;
    //记录文件总大小
    private int totalSize = 0;
    //记录已经暂停或取消的线程数
    private int tempChildTaskCount = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mLastSate = mCurrentState;
            mCurrentState = msg.what;

            switch (mCurrentState) {
                case START:
                    totalSize = msg.arg1;
                    currentSize = msg.arg2;
                    isSupportRange = (boolean) msg.obj;
                    if (!isSupportRange) {
                        childTaskCount = 1;
                    } else if (currentSize == 0) {
                        Db.getInstance(context).insertData(new DownloadData(url, path, childTaskCount, name, currentSize, totalSize, System.currentTimeMillis()));
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
                        tempChildTaskCount++;
                        if (tempChildTaskCount == childTaskCount || mLastSate == PAUSE || mLastSate == ERROR) {
                            tempChildTaskCount = 0;
                            downloadCallback.onProgress(0, totalSize, 0);
                            currentSize = 0;
                            if (isSupportRange) {
                                Db.getInstance(context).deleteData(url);
                                Utils.deleteFile(new File(path, name + ".temp"));
                            }
                            Utils.deleteFile(new File(path, name));

                            downloadCallback.onCancel();

                            if (isRestart) {
                                sendEmptyMessage(RESTART);
                                isRestart = false;
                            }
                        }
                    }
                    break;
                case RESTART:
                    start();
                    break;
                case PAUSE:
                    synchronized (this) {
                        if (isSupportRange) {
                            Db.getInstance(context).updateData(currentSize, url);
                        }
                        tempChildTaskCount++;
                        if (tempChildTaskCount == childTaskCount) {
                            downloadCallback.onPause();
                            tempChildTaskCount = 0;
                        }
                    }
                    break;
                case FINISH:
                    if (isSupportRange) {
                        Utils.deleteFile(new File(path, name + ".temp"));
                        Db.getInstance(context).deleteData(url);
                    }
                    downloadCallback.onFinish(new File(path, name));
                    break;
                case DESTROY:
                    synchronized (this) {
                        if (isSupportRange) {
                            Db.getInstance(context).updateData(currentSize, url);
                        }
                    }
                    break;
                case ERROR:
                    if (isSupportRange) {
                        Db.getInstance(context).updateData(currentSize, url);
                    }
                    downloadCallback.onError((String) msg.obj);
                    break;
            }
        }
    };

    public DownloadManger1(Context context, String url, String path, String name, int childTaskCount) {
        this.context = context;
        this.url = url;
        this.path = path;
        this.name = name;
        this.childTaskCount = childTaskCount == 0 ? 3 : childTaskCount;//默认每个任务分割成3个异步任务
    }

    /**
     * 执行单个任务下载
     *
     * @param callback
     * @return
     */
    public DownloadManger1 execute(DownloadCallback callback) {
        this.downloadCallback = callback;
        start();
        return this;
    }

    /**
     * 执行下载管理
     *
     * @param data
     * @return
     */
    public DownloadManger1 execute(final DownloadData data) {
        this.url = data.getUrl();
        this.path = data.getPath();
        this.name = data.getName();
        this.childTaskCount = data.getChildTaskCount();

//        mFileTask = new FileTask(context, url, path, name, childTaskCount, mHandler);

        return this;
    }

    /**
     * 下载中退出时保存数据、释放资源
     */
    public void destroy() {
        if (mCurrentState == CANCEL || mCurrentState == PAUSE) {
            return;
        }
        mFileTask.destroy();
    }

    /**
     * 暂停（正在下载才可以暂停）
     * 如果文件不支持断点续传则不能进行暂停操作
     */
    public void pause() {
        if (mCurrentState == PROGRESS) {
            mFileTask.pause();
        }
    }

    /**
     * 继续（只有暂停、重新进入的状态可以执行继续下载）
     * 如果文件不支持断点续传则不能进行继续操作
     */
    public void resume() {
        if (isSupportRange && mCurrentState == PAUSE) {
            start();
        }
    }

    /**
     * 取消（已经被取消、下载结束则不可取消）
     */
    public void cancel() {
        if (mCurrentState == PROGRESS) {
            mFileTask.cancel();
        } else if (mCurrentState == PAUSE || mCurrentState == ERROR) {
            mHandler.sendEmptyMessage(CANCEL);
        }
    }

    /**
     * 重新开始（所有状态都可重新下载）
     */
    public void restart() {
        if (mCurrentState == PROGRESS) {
            isRestart = true;
            mFileTask.cancel();
        } else if (mCurrentState == PAUSE || mCurrentState == ERROR) {
            isRestart = true;
            mHandler.sendEmptyMessage(CANCEL);
        } else {
            mHandler.sendEmptyMessage(RESTART);
        }
    }

    /**
     * 开始从头下载
     */
    private void start() {
//        mFileTask = new FileTask(context, url, path, name, childTaskCount, mHandler);
        ThreadPool.THREAD_POOL_EXECUTOR.execute(mFileTask);
    }
}
