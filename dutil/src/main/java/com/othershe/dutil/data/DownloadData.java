package com.othershe.dutil.data;

public class DownloadData {
    private int threadId;
    private int startPos;
    private int endPos;
    private int completeSize;
    private String url;

    public DownloadData() {

    }

    /**
     * @param threadId     线程id
     * @param startPos     下载开始位置
     * @param endPos       下载结束位置
     * @param completeSize 已下载大小
     * @param url          下载地址
     */
    public DownloadData(int threadId, int startPos, int endPos, int completeSize, String url) {
        this.threadId = threadId;
        this.startPos = startPos;
        this.endPos = endPos;
        this.completeSize = completeSize;
        this.url = url;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public int getCompleteSize() {
        return completeSize;
    }

    public void setCompleteSize(int completeSize) {
        this.completeSize = completeSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
