package com.othershe.dutil.upload;

import com.othershe.dutil.data.UploadByte;

import java.util.ArrayList;
import java.util.List;

public class ByteUploadBuilder extends BaseUploadBuilder<ByteUploadBuilder> {
    private String type;//媒体类型
    private List<UploadByte> byteList = new ArrayList<>();

    public ByteUploadBuilder addByte(String name, String filename, byte[] bytes) {
        byteList.add(new UploadByte(name, filename, bytes));
        return this;
    }

    public ByteUploadBuilder addBytes(List<UploadByte> byteList) {
        this.byteList = byteList;
        return this;
    }

    public ByteUploadBuilder addType(String type) {
        this.type = type;
        return this;
    }

    public BytesUploadRequest build() {
        return new BytesUploadRequest(url, byteList, type, params, headers);
    }

}
