package com.othershe.dutiltest;

import android.content.Context;
import android.widget.ProgressBar;

import com.othershe.baseadapter.ViewHolder;
import com.othershe.baseadapter.base.CommonBaseAdapter;
import com.othershe.dutil.callback.DownloadCallback;
import com.othershe.dutil.data.DownloadData;
import com.othershe.dutil.download.DownloadManger;
import com.othershe.dutil.utils.Utils;

import java.io.File;
import java.util.List;

public class DownloadListAdapter extends CommonBaseAdapter<DownloadData> {
    public DownloadListAdapter(Context context, List<DownloadData> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
    }

    @Override
    protected void convert(ViewHolder holder, DownloadData data) {
        holder.setText(R.id.name, data.getName());
        holder.setText(R.id.download_size, Utils.formatSize(data.getCurrentLength()) + "/" + Utils.formatSize(data.getTotalLength()));
        holder.setText(R.id.percentage, data.getPercentage() + "%");
        ((ProgressBar) holder.getView(R.id.progress_bar)).setProgress((int) data.getPercentage());

        setListener(holder, data);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_download_layout;
    }

    private void setListener(final ViewHolder viewHolder, final DownloadData downloadData) {
        DownloadManger.getInstance(mContext).setOnDownloadCallback(downloadData, new DownloadCallback() {
            @Override
            public void onStart(long currentSize, long totalSize, float progress) {
                viewHolder.setText(R.id.name, downloadData.getName() + ":准备中");
            }

            @Override
            public void onProgress(long currentSize, long totalSize, float progress) {
                viewHolder.setText(R.id.name, downloadData.getName() + ":下载中");
                viewHolder.setText(R.id.download_size, Utils.formatSize(currentSize) + "/" + Utils.formatSize(totalSize));
                viewHolder.setText(R.id.percentage, progress + "%");
                ((ProgressBar) viewHolder.getView(R.id.progress_bar)).setProgress((int) progress);
            }

            @Override
            public void onPause() {
                viewHolder.setText(R.id.name, downloadData.getName() + ":已暂停");
            }

            @Override
            public void onCancel() {
                viewHolder.setText(R.id.name, downloadData.getName() + ":已取消");
            }

            @Override
            public void onFinish(File file) {
                viewHolder.setText(R.id.name, downloadData.getName() + ":已完成");
            }

            @Override
            public void onWait() {
                viewHolder.setText(R.id.name, downloadData.getName() + ":等待中");
            }

            @Override
            public void onError(String error) {
                viewHolder.setText(R.id.name, downloadData.getName() + ":出错");
            }
        });
    }
}
