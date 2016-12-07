package com.othershe.dutil.download;

import android.content.Context;

public class DBuilder {
    private String url;//下载链接
    private String path;//保存路径
    private String name;//文件名

    private Context context;

    public DBuilder(Context context) {
        this.context = context;
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

    public DownloadManger build() {
        return new DownloadManger(context, url, path, name);
    }
}
