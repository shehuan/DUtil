package com.othershe.dutil.upload;


public class ByteUploadBuilder extends BaseUploadBuilder<ByteUploadBuilder> {
    private byte[] bytes;
    private String type;
    private String name;

    public ByteUploadBuilder addBytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    public ByteUploadBuilder addType(String type) {
        this.type = type;
        return this;
    }

    public ByteUploadBuilder addName(String name) {
        this.name = name;
        return this;
    }

    public BytesUploadRequest build() {
        return new BytesUploadRequest(url, bytes, name, type, params, headers);
    }

}
