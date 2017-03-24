package com.othershe.dutil.upload;

import com.othershe.dutil.data.UploadByte;
import com.othershe.dutil.data.UploadFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FormUploadBuilder extends BaseUploadBuilder<FormUploadBuilder> {
    private List<UploadFile> files = new ArrayList<>();//本地文件集合

    private String type;//媒体类型
    private List<UploadByte> byteList = new ArrayList<>();//字节流集合

    /**
     * 添加单个文件
     *
     * @param name
     * @param filename
     * @param file
     * @return
     */
    public FormUploadBuilder addFile(String name, String filename, File file) {
        files.add(new UploadFile(name, filename, file));
        return this;
    }

    /**
     * 添加文件集合
     *
     * @param files
     * @return
     */
    public FormUploadBuilder addFiles(List<UploadFile> files) {
        this.files = files;
        return this;
    }

    /**
     * 添加单个字节流
     *
     * @param name
     * @param filename
     * @param bytes
     * @return
     */
    public FormUploadBuilder addByte(String name, String filename, byte[] bytes) {
        byteList.add(new UploadByte(name, filename, bytes));
        return this;
    }

    /**
     * 添加字节流集合
     *
     * @param byteList
     * @return
     */
    public FormUploadBuilder addBytes(List<UploadByte> byteList) {
        this.byteList = byteList;
        return this;
    }

    /**
     * 上传字节流的媒体类型
     *
     * @param type
     * @return
     */
    public FormUploadBuilder addType(String type) {
        this.type = type;
        return this;
    }

    /**
     * 本地文件类型request（表单式）
     *
     * @return
     */
    public FileUploadRequest fileUploadBuild() {
        return new FileUploadRequest(url, files, params, headers);
    }

    /**
     * 字节流类型request（表单式）
     *
     * @return
     */
    public BytesUploadRequest bytesUploadBuild() {
        return new BytesUploadRequest(url, byteList, type, params, headers);
    }
}
