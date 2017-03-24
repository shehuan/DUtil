package com.othershe.dutil.upload;

import java.io.File;

public class DirectUploadBuilder extends BaseUploadBuilder<DirectUploadBuilder> {
    private File file;
    private byte[] bytes;
    private String type;

    public DirectUploadBuilder addFile(File file) {
        bytes = null;
        this.file = file;
        return this;
    }

    public DirectUploadBuilder addByte(byte[] bytes) {
        file = null;
        this.bytes = bytes;
        return this;
    }

    public DirectUploadBuilder addType(String type) {
        this.type = type;
        return this;
    }

    public DirectUploadRequest build() {
        return new DirectUploadRequest(url, file, bytes, type);
    }
}
