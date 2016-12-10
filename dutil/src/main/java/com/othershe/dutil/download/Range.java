package com.othershe.dutil.download;

/**
 * Created by dell on 2016/12/10.
 */

public class Range {
    public long[] start;
    public long[] end;

    public Range(long[] start, long[] end) {
        this.start = start;
        this.end = end;
    }
}
