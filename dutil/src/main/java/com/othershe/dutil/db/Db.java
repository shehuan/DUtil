package com.othershe.dutil.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.othershe.dutil.data.DownloadData;

import java.util.ArrayList;
import java.util.List;

import static com.othershe.dutil.data.Consts.PROGRESS;

public class Db {
    /**
     * 数据库名
     */
    private static final String DB_NAME = "othershe_dutil";

    /**
     * 数据库版本
     */
    private static final int VERSION = 2;

    private String TABLE_NAME_DOWNLOAD = "download_info";

    private static Db db;

    private SQLiteDatabase sqldb;

    private Db(Context context) {
        DbOpenHelper dbHelper = new DbOpenHelper(context, DB_NAME, null, VERSION);
        sqldb = dbHelper.getWritableDatabase();
    }

    public static Db getInstance(Context context) {
        if (db == null) {
            synchronized (Db.class) {
                if (db == null) {
                    db = new Db(context);
                }
            }
        }
        return db;
    }

    /**
     * 保存下载信息
     */
    public void insertData(DownloadData data) {
        ContentValues values = new ContentValues();
        values.put("url", data.getUrl());
        values.put("path", data.getPath());
        values.put("name", data.getName());
        values.put("child_task_count", data.getChildTaskCount());
        values.put("current_length", data.getCurrentLength());
        values.put("total_length", data.getTotalLength());
        values.put("percentage", data.getPercentage());
        values.put("status", data.getStatus());
        values.put("last_modify", data.getLastModify());
        values.put("date", data.getDate());
        sqldb.insert(TABLE_NAME_DOWNLOAD, null, values);
    }

    public void insertDatas(List<DownloadData> datas) {
        for (DownloadData data : datas) {
            insertData(data);
        }
    }

    /**
     * 获得url对应的下载数据
     */
    public DownloadData getData(String url) {

        Cursor cursor = sqldb.query(TABLE_NAME_DOWNLOAD, null, "url = ?",
                new String[]{url}, null, null, null);

        if (!cursor.moveToFirst()) {
            return null;
        }

        DownloadData data = new DownloadData();

        data.setUrl(cursor.getString(cursor.getColumnIndex("url")));
        data.setPath(cursor.getString(cursor.getColumnIndex("path")));
        data.setName(cursor.getString(cursor.getColumnIndex("name")));
        data.setChildTaskCount(cursor.getInt(cursor.getColumnIndex("child_task_count")));
        data.setCurrentLength(cursor.getInt(cursor.getColumnIndex("current_length")));
        data.setTotalLength(cursor.getInt(cursor.getColumnIndex("total_length")));
        data.setPercentage(cursor.getFloat(cursor.getColumnIndex("percentage")));
        data.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        data.setLastModify(cursor.getString(cursor.getColumnIndex("last_modify")));
        data.setDate(cursor.getInt(cursor.getColumnIndex("date")));

        cursor.close();

        return data;
    }

    /**
     * 获得全部下载数据
     *
     * @return
     */
    public List<DownloadData> getAllData() {
        List<DownloadData> list = new ArrayList<>();
        Cursor cursor = sqldb.query(TABLE_NAME_DOWNLOAD, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                DownloadData data = new DownloadData();
                data.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                data.setPath(cursor.getString(cursor.getColumnIndex("path")));
                data.setName(cursor.getString(cursor.getColumnIndex("name")));
                data.setChildTaskCount(cursor.getInt(cursor.getColumnIndex("child_task_count")));
                data.setCurrentLength(cursor.getInt(cursor.getColumnIndex("current_length")));
                data.setTotalLength(cursor.getInt(cursor.getColumnIndex("total_length")));
                data.setPercentage(cursor.getFloat(cursor.getColumnIndex("percentage")));
                data.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                data.setLastModify(cursor.getString(cursor.getColumnIndex("last_modify")));
                data.setDate(cursor.getInt(cursor.getColumnIndex("date")));

                list.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * 更新下载信息
     */
    public void updateProgress(int currentSize, float percentage, int status, String url) {
        ContentValues values = new ContentValues();
        if (status != PROGRESS){
            values.put("current_length", currentSize);
            values.put("percentage", percentage);
        }
        values.put("status", status);
        sqldb.update(TABLE_NAME_DOWNLOAD, values, "url = ?", new String[]{url});
    }

    /**
     * 删除下载信息
     */
    public void deleteData(String url) {
        sqldb.delete(TABLE_NAME_DOWNLOAD, "url = ?", new String[]{url});
    }
}
