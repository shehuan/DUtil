package com.othershe.dutil.upload;

import com.othershe.dutil.data.UploadFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseUploadBuilder {
    protected String url;
    protected List<UploadFile> files = new ArrayList<>();
    protected Map<String, String> params = new HashMap<>();
    protected Map<String, String> headers = new HashMap<>();

    public BaseUploadBuilder url(String url) {
        this.url = url;
        return this;
    }

    public BaseUploadBuilder addFile(String key, String name, File file) {
        files.add(new UploadFile(key, name, file));
        return this;
    }

    public BaseUploadBuilder addFiles(List<UploadFile> files) {
        this.files = files;
        return this;
    }

    public BaseUploadBuilder addParam(String key, String value) {
        params.put(key, value);
        return this;
    }

    public BaseUploadBuilder addParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public BaseUploadBuilder addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public BaseUploadBuilder addHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }
}
