package com.othershe.dutil.utils;

import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

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
    public static boolean isNotServerFileChanged(Response response) {
        return response.code() == 206;
    }

    /**
     * 文件最后修改时间
     *
     * @param response
     * @return
     */
    public static String getLastModify(Response response) {
        return response.headers().get("Last-Modified");
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

    /**
     * 删除文件
     *
     * @param path
     * @param name
     */
    public static void deleteFile(String path, String name) {
        deleteFile(new File(path, name));
    }

    /**
     * 创建文件
     *
     * @param path
     * @param name
     * @return
     */
    public static synchronized File createFile(String path, String name) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(name)) {
            return null;
        }

        File parentFile = new File(path);
        if (!parentFile.exists()) {
            parentFile.mkdir();
        }

        File file = new File(parentFile, name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    public static boolean isFileExists(File file) {
        if (file != null && file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 格式化文件大小
     *
     * @param size
     * @return
     */
    public static String formatSize(long size) {
        String resultSize;

        double b = size;
        double kb = size / 1024.0;
        double mb = (kb / 1024.0);
        double gb = (mb / 1024.0);
        double tb = (gb / 1024.0);

        DecimalFormat df = new DecimalFormat("0.00");

        if (tb > 1) {
            resultSize = df.format(tb).concat(" TB");
        } else if (gb > 1) {
            resultSize = df.format(gb).concat(" GB");
        } else if (mb > 1) {
            resultSize = df.format(mb).concat(" MB");
        } else if (kb > 1) {
            resultSize = df.format(kb).concat(" KB");
        } else {
            resultSize = df.format(b).concat(" B");
        }

        return resultSize;
    }

    public static float getPercentage(int currentSize, int totalSize) {
        if (currentSize > totalSize) {
            return 0;
        }

        return ((int) (currentSize * 10000.0 / totalSize)) * 1.0f / 100;
    }

    /**
     * 截取url中的默认文件名
     *
     * @param url
     * @return
     */
    public static String getSuffixName(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }

        return url.substring(url.lastIndexOf("/"));
    }

    /**
     * 将字符串进行MD5编码
     *
     * @param str
     * @return
     */
    public static String md5Encode(String str) {
        String tempStr;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(str.getBytes());
            tempStr = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            tempStr = String.valueOf(str.hashCode());
        }
        return tempStr;
    }

    /**
     * bytes to hex string
     *
     * @param bytes
     * @return
     */
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 根据文件名解析contentType
     *
     * @param name
     * @return
     */
    public static String getMimeType(String name) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = null;
        try {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(name, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }
}
