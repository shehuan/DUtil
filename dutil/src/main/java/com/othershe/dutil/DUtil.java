package com.othershe.dutil;

import android.content.Context;

import com.othershe.dutil.download.DBuilder;
import com.othershe.dutil.upload.ByteUploadBuilder;
import com.othershe.dutil.upload.FileUploadBuilder;

public class DUtil {

    /**
     * 下载
     *
     * @param context
     * @return
     */
    public static DBuilder init(Context context) {
        return new DBuilder(context);
    }

    /**
     * 本地文件上传
     *
     * @return
     */
    public static FileUploadBuilder initFileUpload() {
        return new FileUploadBuilder();
    }

    /**
     * 内存文件上传（bitmap等）
     *
     * @return
     */
    public static ByteUploadBuilder initByteUpload() {
        return new ByteUploadBuilder();
    }
}
