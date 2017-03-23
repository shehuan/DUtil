package com.othershe.dutil.upload;

import android.text.TextUtils;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class BytesUploadRequest extends BaseUploadRequest {
    private byte[] bytes;
    private String type;
    private String name;


    public BytesUploadRequest(String url, byte[] bytes, String name, String type, Map<String, String> params, Map<String, String> headers) {
        this.url = url;
        this.bytes = bytes;
        this.name = name;
        this.type = type;
        this.params = params;
        this.headers = headers;
    }

    @Override
    protected void buildRequestBody(MultipartBody.Builder builder) {
        if (TextUtils.isEmpty(type)) {
            type = "application/octet-stream";
        }
        if (TextUtils.isEmpty(name)){
            name = "";
        }
        RequestBody fileBody = RequestBody.create(MediaType.parse(type), bytes);
        builder.addFormDataPart("file", "BeautyImage.jpg", fileBody);
    }
}
