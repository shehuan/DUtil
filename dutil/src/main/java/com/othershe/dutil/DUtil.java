package com.othershe.dutil;

import android.content.Context;

import com.othershe.dutil.download.DBuilder;

public class DUtil {

//    private DUtil mDUtil;
//
//    public static DUtil getInstance() {
//        return DUtilHolder.instance;
//    }
//
//    private static class DUtilHolder {
//        private static final DUtil instance = new DUtil();
//    }

    public static DBuilder initDownload(Context context) {
        return new DBuilder(context);
    }

    public static DBuilder initDownload() {
        return new DBuilder();
    }

//    public void download() {
//
//    }
//
//    public void upload() {
//
//    }

//    public static void start() {
//
//    }
//
//    public static void pause() {
//
//    }
//
//    public static void delete() {
//
//    }
}
