package com.othershe.dutil.callback;

import com.othershe.dutil.data.DownloadData;

import java.util.List;

public interface ProgressCallback {
    void onProgress(List<DownloadData> progressDatas);
}
