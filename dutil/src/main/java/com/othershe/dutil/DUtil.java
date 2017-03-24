package com.othershe.dutil;

import android.content.Context;

import com.othershe.dutil.download.DBuilder;
import com.othershe.dutil.upload.DirectUploadBuilder;
import com.othershe.dutil.upload.FormUploadBuilder;

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
     * 表单式文件上传
     *
     * @return
     */
    public static FormUploadBuilder initFormUpload() {
        return new FormUploadBuilder();
    }

    /**
     * 将文件作为请求体上传
     *
     * @return
     */
    public static DirectUploadBuilder initUpload() {
        return new DirectUploadBuilder();
    }
}
