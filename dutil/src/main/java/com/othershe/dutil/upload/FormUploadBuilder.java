package com.othershe.dutil.upload;

import com.othershe.dutil.data.UploadFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FormUploadBuilder extends BaseUploadBuilder<FormUploadBuilder> {
    private List<UploadFile> files = new ArrayList<>();

    public FormUploadBuilder addFile(String key, String name, File file) {
        files.add(new UploadFile(key, name, file));
        return this;
    }

    public FormUploadBuilder addFiles(List<UploadFile> files) {
        this.files = files;
        return this;
    }

    public FormUploadRequest build() {
        return new FormUploadRequest(url, files, params, headers);
    }

}
