package com.othershe.dutil.data;

import android.os.Parcel;
import android.os.Parcelable;

import static com.othershe.dutil.data.Consts.NONE;
import static com.othershe.dutil.data.Consts.START;

public class DownloadData implements Parcelable {

    private String url;
    private String path;
    private String name;
    private int currentLength;
    private int totalLength;
    private float percentage;
    private int status = NONE;
    private int childTaskCount;
    private long date;
    private String lastModify;

    public DownloadData() {

    }

    public DownloadData(String url, String path, String name) {
        this.url = url;
        this.path = path;
        this.name = name;
    }

    public DownloadData(String url, String path, String name, int childTaskCount) {
        this.url = url;
        this.path = path;
        this.name = name;
        this.childTaskCount = childTaskCount;
    }

    public DownloadData(String url, String path, int childTaskCount, String name, int currentLength, int totalLength, String lastModify, long date) {
        this.url = url;
        this.path = path;
        this.childTaskCount = childTaskCount;
        this.currentLength = currentLength;
        this.status = START;
        this.name = name;
        this.totalLength = totalLength;
        this.lastModify = lastModify;
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(int currentLength) {
        this.currentLength = currentLength;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getChildTaskCount() {
        return childTaskCount;
    }

    public void setChildTaskCount(int childTaskCount) {
        this.childTaskCount = childTaskCount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getLastModify() {
        return lastModify;
    }

    public void setLastModify(String lastModify) {
        this.lastModify = lastModify;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.path);
        dest.writeString(this.name);
        dest.writeInt(this.currentLength);
        dest.writeInt(this.totalLength);
        dest.writeFloat(this.percentage);
        dest.writeInt(this.status);
        dest.writeInt(this.childTaskCount);
        dest.writeLong(this.date);
        dest.writeString(this.lastModify);
    }

    protected DownloadData(Parcel in) {
        this.url = in.readString();
        this.path = in.readString();
        this.name = in.readString();
        this.currentLength = in.readInt();
        this.totalLength = in.readInt();
        this.percentage = in.readFloat();
        this.status = in.readInt();
        this.childTaskCount = in.readInt();
        this.date = in.readLong();
        this.lastModify = in.readString();
    }

    public static final Parcelable.Creator<DownloadData> CREATOR = new Parcelable.Creator<DownloadData>() {
        @Override
        public DownloadData createFromParcel(Parcel source) {
            return new DownloadData(source);
        }

        @Override
        public DownloadData[] newArray(int size) {
            return new DownloadData[size];
        }
    };
}
