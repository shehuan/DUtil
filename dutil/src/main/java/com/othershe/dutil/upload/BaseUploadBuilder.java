package com.othershe.dutil.upload;

import java.util.HashMap;
import java.util.Map;

public class BaseUploadBuilder<T extends BaseUploadBuilder> {
    protected String url;
    protected Map<String, String> params = new HashMap<>();
    protected Map<String, String> headers = new HashMap<>();

    public T url(String url) {
        this.url = url;
        return (T) this;
    }

    public T addParam(String key, String value) {
        params.put(key, value);
        return (T) this;
    }

    public T addParams(Map<String, String> params) {
        this.params = params;
        return (T) this;
    }

    public T addHeader(String key, String value) {
        headers.put(key, value);
        return (T) this;
    }

    public T addHeaders(Map<String, String> headers) {
        this.headers = headers;
        return (T) this;
    }
}
