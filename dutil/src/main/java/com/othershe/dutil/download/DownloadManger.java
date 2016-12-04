package com.othershe.dutil.download;

import com.othershe.dutil.callback.FileCallback;
import com.othershe.dutil.net.OkHttpManager;

public class DownloadManger {
    private FileCallback fileCallback;

    private String url;
    private String path;
    private String name;

    public DownloadManger(String url, String path, String name) {
        this.url = url;
        this.path = path;
        this.name = name;
    }

    public void execute(FileCallback fileCallback) {
        this.fileCallback = fileCallback;
        OkHttpManager.getInatance().initRequest(url, path, name, fileCallback);
    }
}
