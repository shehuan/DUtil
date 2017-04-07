package com.othershe.dutil.download;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.othershe.dutil.utils.Utils;
import com.othershe.dutil.data.DownloadData;
import com.othershe.dutil.data.Ranges;
import com.othershe.dutil.db.Db;
import com.othershe.dutil.net.OkHttpManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.othershe.dutil.data.Consts.CANCEL;
import static com.othershe.dutil.data.Consts.DESTROY;
import static com.othershe.dutil.data.Consts.ERROR;
import static com.othershe.dutil.data.Consts.PAUSE;
import static com.othershe.dutil.data.Consts.PROGRESS;
import static com.othershe.dutil.data.Consts.START;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;

/**
 * 对应文件的下载线程
 */
public class FileTask implements Runnable {

    private int EACH_TEMP_SIZE = 16;
    private int TEMP_FILE_TOTAL_SIZE;//临时文件的总大小

    private int BUFFER_SIZE = 4096;

    private Context context;

    private String url;
    private String path;
    private String name;
    private int childTaskCount;
    private Handler handler;

    private boolean IS_PAUSE;
    private boolean IS_DESTROY;
    private boolean IS_CANCEL;
    private ArrayList<Call> callList;

    private int tempChildTaskCount;

    public FileTask(Context context, DownloadData downloadData, Handler handler) {
        this.context = context;
        this.url = downloadData.getUrl();
        this.path = downloadData.getPath();
        this.name = downloadData.getName();
        this.childTaskCount = downloadData.getChildTaskCount();
        this.handler = handler;

        TEMP_FILE_TOTAL_SIZE = EACH_TEMP_SIZE * childTaskCount;
    }

    @Override
    public void run() {
        try {
            File saveFile = new File(path, name);
            File tempFile = new File(path, name + ".temp");
            DownloadData data = Db.getInstance(context).getData(url);
            if (Utils.isFileExists(saveFile) && Utils.isFileExists(tempFile) && data != null && data.getStatus() != PROGRESS) {
                Response response = OkHttpManager.getInstance().initRequest(url, data.getLastModify());
                if (response != null && response.isSuccessful() && Utils.isNotServerFileChanged(response)) {
                    TEMP_FILE_TOTAL_SIZE = EACH_TEMP_SIZE * data.getChildTaskCount();
                    onStart(data.getTotalLength(), data.getCurrentLength(), "", true);
                } else {
                    prepareRangeFile(response);
                }
                saveRangeFile();
            } else {
                Response response = OkHttpManager.getInstance().initRequest(url);
                if (response != null && response.isSuccessful()) {
                    if (Utils.isSupportRange(response)) {
                        prepareRangeFile(response);
                        saveRangeFile();
                    } else {
                        saveCommonFile(response);
                    }
                }
            }
        } catch (IOException e) {
            onError(e.toString());
        }
    }

    private void prepareRangeFile(Response response) {
        RandomAccessFile saveRandomAccessFile = null;
        RandomAccessFile tempRandomAccessFile = null;
        FileChannel tempChannel = null;
        File saveFile;
        File tempFile;

        try {

            saveFile = new File(path, name);
            tempFile = new File(path, name + ".temp");

            long fileLength = response.body().contentLength();
            onStart(fileLength, 0, Utils.getLastModify(response), true);

            Db.getInstance(context).deleteData(url);
            Utils.deleteFile(saveFile, tempFile);

            saveFile = Utils.createFile(path, name);
            tempFile = Utils.createFile(path, name + ".temp");

            saveRandomAccessFile = new RandomAccessFile(saveFile, "rws");
            saveRandomAccessFile.setLength(fileLength);

            tempRandomAccessFile = new RandomAccessFile(tempFile, "rws");
            tempRandomAccessFile.setLength(TEMP_FILE_TOTAL_SIZE);
            tempChannel = tempRandomAccessFile.getChannel();
            MappedByteBuffer buffer = tempChannel.map(READ_WRITE, 0, TEMP_FILE_TOTAL_SIZE);

            long start;
            long end;
            int eachSize = (int) (fileLength / childTaskCount);
            for (int i = 0; i < childTaskCount; i++) {
                if (i == childTaskCount - 1) {
                    start = i * eachSize;
                    end = fileLength - 1;
                } else {
                    start = i * eachSize;
                    end = (i + 1) * eachSize - 1;
                }
                buffer.putLong(start);
                buffer.putLong(end);
            }
        } catch (Exception e) {
            onError(e.toString());
        } finally {
            Utils.close(saveRandomAccessFile);
            Utils.close(tempRandomAccessFile);
            Utils.close(tempChannel);
            Utils.close(response);
        }
    }

    /**
     * 开始断点下载
     */
    private void saveRangeFile() {

        final File saveFile = Utils.createFile(path, name);
        final File tempFile = Utils.createFile(path, name + ".temp");

        final Ranges range = readDownloadRange(tempFile);

        callList = new ArrayList<>();

        Db.getInstance(context).updateProgress(0, 0, PROGRESS, url);

        for (int i = 0; i < childTaskCount; i++) {
            final int tempI = i;
            Call call = OkHttpManager.getInstance().initRequest(url, range.start[i], range.end[i], new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onError(e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    startSaveRangeFile(response, tempI, range, saveFile, tempFile);
                }
            });
            callList.add(call);
        }

        while (tempChildTaskCount < childTaskCount) {
            //由于每个文件采用多个异步操作进行，发起多个异步操作后该线程已经结束，但对应文件并未下载完成，
            //则会出现线程池中同时下载的文件数量超过设定的核心线程数，所以考虑只有当前线程的所有异步任务结束后，
            //才能使结束当前线程。
        }
    }

    /**
     * 分段保存文件
     *
     * @param response
     * @param index
     * @param range
     * @param saveFile
     * @param tempFile
     */
    private void startSaveRangeFile(Response response, int index, Ranges range, File saveFile, File tempFile) {
        RandomAccessFile saveRandomAccessFile = null;
        FileChannel saveChannel = null;
        InputStream inputStream = null;

        RandomAccessFile tempRandomAccessFile = null;
        FileChannel tempChannel = null;

        try {
            saveRandomAccessFile = new RandomAccessFile(saveFile, "rws");
            saveChannel = saveRandomAccessFile.getChannel();
            MappedByteBuffer saveBuffer = saveChannel.map(READ_WRITE, range.start[index], range.end[index] - range.start[index] + 1);

            tempRandomAccessFile = new RandomAccessFile(tempFile, "rws");
            tempChannel = tempRandomAccessFile.getChannel();
            MappedByteBuffer tempBuffer = tempChannel.map(READ_WRITE, 0, TEMP_FILE_TOTAL_SIZE);

            inputStream = response.body().byteStream();
            int len;
            byte[] buffer = new byte[BUFFER_SIZE];

            while ((len = inputStream.read(buffer)) != -1) {
                if (IS_CANCEL) {
                    handler.sendEmptyMessage(CANCEL);
                    callList.get(index).cancel();
                    break;
                }

                saveBuffer.put(buffer, 0, len);
                tempBuffer.putLong(index * EACH_TEMP_SIZE, tempBuffer.getLong(index * EACH_TEMP_SIZE) + len);
                onProgress(len);

                if (IS_DESTROY) {
                    handler.sendEmptyMessage(DESTROY);
                    callList.get(index).cancel();
                    break;
                }

                if (IS_PAUSE) {
                    handler.sendEmptyMessage(PAUSE);
                    callList.get(index).cancel();
                    break;
                }
            }
            addCount();
        } catch (Exception e) {
            onError(e.toString());
        } finally {
            Utils.close(saveRandomAccessFile);
            Utils.close(saveChannel);
            Utils.close(inputStream);
            Utils.close(tempRandomAccessFile);
            Utils.close(tempChannel);
            Utils.close(response);
        }
    }

    private synchronized void addCount() {
        ++tempChildTaskCount;
    }

    private void saveCommonFile(Response response) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {

            long fileLength = response.body().contentLength();
            onStart(fileLength, 0, "", false);

            Utils.deleteFile(path, name);

            File file = Utils.createFile(path, name);
            if (file == null) {
                return;
            }

            inputStream = response.body().byteStream();
            outputStream = new FileOutputStream(file);

            int len;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((len = inputStream.read(buffer)) != -1) {
                if (IS_CANCEL) {
                    handler.sendEmptyMessage(CANCEL);
                    break;
                }

                if (IS_DESTROY) {
                    break;
                }

                outputStream.write(buffer, 0, len);
                onProgress(len);
            }

            outputStream.flush();
        } catch (Exception e) {
            onError(e.toString());
        } finally {
            Utils.close(inputStream);
            Utils.close(outputStream);
            Utils.close(response);
        }
    }

    /**
     * 读取保存的断点信息
     *
     * @param tempFile
     * @return
     */
    public Ranges readDownloadRange(File tempFile) {
        RandomAccessFile record = null;
        FileChannel channel = null;
        try {
            record = new RandomAccessFile(tempFile, "rws");
            channel = record.getChannel();
            MappedByteBuffer buffer = channel.map(READ_WRITE, 0, TEMP_FILE_TOTAL_SIZE);
            long[] startByteArray = new long[childTaskCount];
            long[] endByteArray = new long[childTaskCount];
            for (int i = 0; i < childTaskCount; i++) {
                startByteArray[i] = buffer.getLong();
                endByteArray[i] = buffer.getLong();
            }
            return new Ranges(startByteArray, endByteArray);
        } catch (Exception e) {
            onError(e.toString());
        } finally {
            Utils.close(channel);
            Utils.close(record);
        }
        return null;
    }

    private void onStart(long totalLength, long currentLength, String lastModify, boolean isSupportRange) {
        Message message = Message.obtain();
        message.what = START;
        Bundle bundle = new Bundle();
        bundle.putInt("totalLength", (int) totalLength);
        bundle.putInt("currentLength", (int) currentLength);
        bundle.putString("lastModify", lastModify);
        bundle.putBoolean("isSupportRange", isSupportRange);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public void onProgress(int length) {
        Message message = Message.obtain();
        message.what = PROGRESS;
        message.arg1 = length;
        handler.sendMessage(message);
    }

    public void onError(String msg) {
        Message message = Message.obtain();
        message.what = ERROR;
        message.obj = msg;
        handler.sendMessage(message);
    }

    public void pause() {
        IS_PAUSE = true;
    }

    public void cancel() {
        IS_CANCEL = true;
    }

    public void destroy() {
        IS_DESTROY = true;
    }
}
