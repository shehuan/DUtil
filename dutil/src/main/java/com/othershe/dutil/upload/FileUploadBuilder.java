package com.othershe.dutil.upload;

import com.othershe.dutil.data.UploadFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUploadBuilder extends BaseUploadBuilder<FileUploadBuilder> {
    private List<UploadFile> files = new ArrayList<>();

    public FileUploadBuilder addFile(String name, String filename, File file) {
        files.add(new UploadFile(name, filename, file));
        return this;
    }

    public FileUploadBuilder addFiles(List<UploadFile> files) {
        this.files = files;
        return this;
    }

    public FileUploadRequest build() {
        return new FileUploadRequest(url, files, params, headers);
    }

}
