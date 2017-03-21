package com.othershe.dutil.upload;

import android.graphics.Bitmap;

import com.othershe.dutil.data.UploadFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FormUploadBuilder extends BaseUploadBuilder {
    private List<UploadFile> files = new ArrayList<>();
    private List<Bitmap> bitmaps = new ArrayList<>();

    public FormUploadBuilder addFile(String key, String name, File file) {
        files.add(new UploadFile(key, name, file));
        return this;
    }

    public FormUploadBuilder addFiles(List<UploadFile> files) {
        this.files = files;
        return this;
    }


    public FormUploadBuilder addBitmap(String key, String name, Bitmap bitmap) {
        bitmaps.add(bitmap);
        return this;
    }

    public FormUploadBuilder addBitmaps(List<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
        return this;
    }

    public FormUploadRequest build() {
        return new FormUploadRequest(url, files, bitmaps, params, headers);
    }

}
