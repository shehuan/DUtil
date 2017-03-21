package com.othershe.dutil.data;

import java.io.File;

public class UploadFile {
    private String key;
    private String name;
    private File file;

    public UploadFile(String key, String name, File file) {
        this.key = key;
        this.name = name;
        this.file = file;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
