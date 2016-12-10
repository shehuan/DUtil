package com.othershe.dutil.download;

import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Response;

public class Utils {
    /**
     * 关闭流
     *
     * @param closeable
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否支持断点续传
     *
     * @param response
     * @return
     */
    public static boolean isSupportRange(Response response) {
        Headers headers = response.headers();
        return !TextUtils.isEmpty(headers.get("Content-Range"))
                || stringToLong(headers.get("Content-Length")) != -1;
    }

    private static long stringToLong(String s) {
        if (s == null) return -1;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 服务器文件是否已更改
     *
     * @param response
     * @return
     */
    public static boolean isServerFileChanged(Response response) {
        if (response.code() == 200) {
            return true;
        } else if (response.code() == 206) {
            return false;
        }

        return false;
    }

    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        if (file != null && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 批量删除文件
     *
     * @param files
     */
    public static void deleteFile(File... files) {
        for (File file : files) {
            deleteFile(file);
        }
    }
}
