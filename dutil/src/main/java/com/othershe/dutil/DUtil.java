package com.othershe.dutil;

import android.content.Context;

import com.othershe.dutil.data.DownloadData;
import com.othershe.dutil.download.DBuilder;
import com.othershe.dutil.download.DownloadManger;

public class DUtil {

    public static DBuilder init(Context context) {
        return new DBuilder(context);
    }

    public static void addTask(Context context, DownloadData data) {
        DownloadManger.getInstance(context).execute(data);
    }
}
