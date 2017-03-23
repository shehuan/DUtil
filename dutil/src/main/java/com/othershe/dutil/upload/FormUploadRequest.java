package com.othershe.dutil.upload;

import com.othershe.dutil.Utils.Utils;
import com.othershe.dutil.data.UploadFile;

import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FormUploadRequest extends BaseUploadRequest {

    private List<UploadFile> files;


    public FormUploadRequest(String url, List<UploadFile> files, Map<String, String> params, Map<String, String> headers) {
        this.url = url;
        this.files = files;
        this.params = params;
        this.headers = headers;
    }

    @Override
    protected void buildRequestBody(MultipartBody.Builder builder) {
        for (UploadFile file : files) {
            RequestBody fileBody = RequestBody.create(MediaType.parse(Utils.getMimeType(file.getName())), file.getFile());
            builder.addFormDataPart(file.getKey(), file.getName(), fileBody);
        }

    }
}
