package com.othershe.dutil;

import android.content.Context;

import com.othershe.dutil.download.DBuilder;

public class DUtil {

    public static DBuilder init(Context context) {
        return new DBuilder(context);
    }
}
