package com.othershe.dutil.upload;

import android.text.TextUtils;

import com.othershe.dutil.data.UploadByte;

import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class BytesUploadRequest extends FormUploadRequest {
    private String type;
    private List<UploadByte> byteList;


    public BytesUploadRequest(String url, List<UploadByte> byteList, String type, Map<String, String> params, Map<String, String> headers) {
        this.url = url;
        this.type = type;
        this.byteList = byteList;
        this.params = params;
        this.headers = headers;
    }

    @Override
    protected void buildRequestBody(MultipartBody.Builder builder) {
        type = TextUtils.isEmpty(type) ? "application/octet-stream" : type;
        for (UploadByte bytes : byteList) {
            RequestBody fileBody = RequestBody.create(MediaType.parse(type), bytes.getBytes());
            builder.addFormDataPart(bytes.getName(), bytes.getFilename(), fileBody);
        }
    }
}
