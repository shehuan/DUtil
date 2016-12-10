package com.othershe.dutil.download;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import okhttp3.Response;
import okhttp3.ResponseBody;

import static java.nio.channels.FileChannel.MapMode.READ_WRITE;

public class FileUtil {

    private static int EACH_TEMP_SIZE = 16; //long + long = 8 + 8
    private static int THREAD_COUNT = 1;
    private static int TEMP_FILE_TOTAL_SIZE = EACH_TEMP_SIZE * THREAD_COUNT;

    public static void saveFile(Response response, long startsPoint, String path, String name) {
        ResponseBody body = response.body();
        InputStream in = body.byteStream();
        FileChannel channelOut = null;
        // 随机访问文件，可以指定断点续传的起始位置
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(new File(path, name), "rwd");
            //Chanel NIO中的用法，由于RandomAccessFile没有使用缓存策略，直接使用会使得下载速度变慢，亲测缓存下载3.3秒的文件，用普通的RandomAccessFile需要20多秒。
            channelOut = randomAccessFile.getChannel();
            long dd = body.contentLength();
            // 内存映射，直接使用RandomAccessFile，是用其seek方法指定下载的起始位置，使用缓存下载，在这里指定下载位置。
            MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, startsPoint, body.contentLength());
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                mappedBuffer.put(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                if (channelOut != null) {
                    channelOut.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void prepare(Response response, File saveFile, File tempFile) {
        RandomAccessFile saveRandomAccessFile = null;
        RandomAccessFile tempRandomAccessFile = null;
        FileChannel tempChannel = null;
        long fileLength = response.body().contentLength();

        Log.e("tag", fileLength + "");

        try {
            saveRandomAccessFile = new RandomAccessFile(saveFile, "rws");
            saveRandomAccessFile.setLength(fileLength);

            tempRandomAccessFile = new RandomAccessFile(tempFile, "rws");
            tempRandomAccessFile.setLength(TEMP_FILE_TOTAL_SIZE);
            tempChannel = tempRandomAccessFile.getChannel();
            MappedByteBuffer buffer = tempChannel.map(READ_WRITE, 0, TEMP_FILE_TOTAL_SIZE);

            long start;
            long end;
            int eachSize = (int) (fileLength / THREAD_COUNT);
            for (int i = 0; i < THREAD_COUNT; i++) {
                if (i == THREAD_COUNT - 1) {
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
            e.printStackTrace();
        } finally {
            Utils.close(saveRandomAccessFile);
            Utils.close(tempRandomAccessFile);
            Utils.close(tempChannel);
        }
    }

    public static void saveFile(Response response, int index, long start, long end, File saveFile, File tempFile) {
        RandomAccessFile saveRandomAccessFile = null;
        FileChannel saveChannel = null;
        InputStream inputStream = null;

        RandomAccessFile tempRandomAccessFile = null;
        FileChannel tempChannel = null;

        try {
            Log.e("tag", "saveFile: start" + index);
            Log.e("tag", "saveFile" + response.body().contentLength() + "");
            saveRandomAccessFile = new RandomAccessFile(saveFile, "rws");
            saveChannel = saveRandomAccessFile.getChannel();
            MappedByteBuffer saveBuffer = saveChannel.map(READ_WRITE, start, end - start + 1);

            tempRandomAccessFile = new RandomAccessFile(tempFile, "rws");
            tempChannel = tempRandomAccessFile.getChannel();
            MappedByteBuffer tempBuffer = tempChannel.map(READ_WRITE, 0, TEMP_FILE_TOTAL_SIZE);

            inputStream = response.body().byteStream();
            int len;
            byte[] buffer = new byte[8192];
            while ((len = inputStream.read(buffer)) != -1) {
                saveBuffer.put(buffer, 0, len);
                tempBuffer.putLong(index * EACH_TEMP_SIZE, tempBuffer.get(index * EACH_TEMP_SIZE) + len);
            }

            Log.e("tag", "saveFile: end" + index);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(saveRandomAccessFile);
            Utils.close(saveChannel);
            Utils.close(inputStream);
            Utils.close(tempRandomAccessFile);
            Utils.close(tempChannel);
        }
    }

    public static Range readDownloadRange(File tempFile) throws IOException {
        RandomAccessFile record = null;
        FileChannel channel = null;
        try {
            record = new RandomAccessFile(tempFile, "rws");
            channel = record.getChannel();
            MappedByteBuffer buffer = channel.map(READ_WRITE, 0, TEMP_FILE_TOTAL_SIZE);
            long[] startByteArray = new long[THREAD_COUNT];
            long[] endByteArray = new long[THREAD_COUNT];
            for (int i = 0; i < THREAD_COUNT; i++) {
                startByteArray[i] = buffer.getLong();
                endByteArray[i] = buffer.getLong();
            }
            return new Range(startByteArray, endByteArray);
        } finally {
            Utils.close(channel);
            Utils.close(record);
        }
    }
}
