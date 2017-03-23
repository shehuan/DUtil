package com.othershe.dutil.data;

public class UploadByte {
    private String name;
    private String filename;
    private byte[] bytes;

    /**
     * @param name 表单中name属性的值
     * @param filename 文件名
     * @param bytes 要上传的字节流
     */
    public UploadByte(String name, String filename, byte[] bytes) {
        this.name = name;
        this.filename = filename;
        this.bytes = bytes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
