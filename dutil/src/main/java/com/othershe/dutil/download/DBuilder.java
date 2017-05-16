package com.othershe.dutil.download;

import android.content.Context;

public class DBuilder {
    private String url;//下载链接
    private String path;//保存路径
    private String name;//文件名
    private int childTaskCount;//单个任务采用几个线程下载

    private Context context;

    public DBuilder(Context context) {
        this.context = context.getApplicationContext();
    }

    public DBuilder() {

    }

    public DBuilder url(String url) {
        this.url = url;
        return this;
    }

    public DBuilder path(String path) {
        this.path = path;
        return this;
    }

    public DBuilder name(String name) {
        this.name = name;
        return this;
    }

    public DBuilder childTaskCount(int thread) {
        this.childTaskCount = thread;
        return this;
    }

    public DownloadManger build() {
        DownloadManger downloadManger = DownloadManger.getInstance(context);
        downloadManger.init(url, path, name, childTaskCount);
        return downloadManger;
    }
}
