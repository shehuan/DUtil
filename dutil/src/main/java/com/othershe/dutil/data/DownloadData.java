package com.othershe.dutil.data;

public class DownloadData {

    private String url;
    private String path;
    private String name;
    private int currentSize;
    private int totalSize;
    private long date;

    public DownloadData() {

    }

    public DownloadData(String url, String path, String name, int currentSize, int totalSize, long date) {
        this.url = url;
        this.path = path;
        this.currentSize = currentSize;
        this.name = name;
        this.totalSize = totalSize;
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(int currentSize) {
        this.currentSize = currentSize;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public long getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
