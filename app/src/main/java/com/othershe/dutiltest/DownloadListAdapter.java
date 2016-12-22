package com.othershe.dutiltest;

import android.content.Context;
import android.widget.ProgressBar;

import com.othershe.baseadapter.ViewHolder;
import com.othershe.baseadapter.base.CommonBaseAdapter;
import com.othershe.dutil.Utils.Utils;
import com.othershe.dutil.data.DownloadData;

import java.util.List;

public class DownloadListAdapter extends CommonBaseAdapter<DownloadData> {
    public DownloadListAdapter(Context context, List<DownloadData> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
    }

    @Override
    protected void convert(ViewHolder holder, DownloadData data) {
        holder.setText(R.id.name, data.getName());
        holder.setText(R.id.download_size, Utils.formatSize(data.getCurrentSize()) + "/" + Utils.formatSize(data.getTotalSize()));
        holder.setText(R.id.percentage, data.getPercentage() + "%");
        ((ProgressBar) holder.getView(R.id.progress_bar)).setProgress((int) data.getPercentage());
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_download_layout;
    }
}
