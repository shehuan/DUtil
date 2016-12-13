package com.othershe.dutil.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.othershe.dutil.data.DownloadData;

import java.util.ArrayList;
import java.util.List;

public class Db {
    /**
     * 数据库名
     */
    private static final String DB_NAME = "othershe_dutil";

    /**
     * 数据库版本
     */
    private static final int VERSION = 1;

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
        values.put("current_size", data.getCurrentSize());
        values.put("total_size", data.getTotalSize());
        values.put("date", data.getDate());
        sqldb.insert(TABLE_NAME_DOWNLOAD, null, values);
    }

    public void insertDatas(List<DownloadData> datas) {
        for (DownloadData data : datas) {
            insertData(data);
        }
    }

    /**
     * 获得下载信息
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
        data.setCurrentSize(cursor.getInt(cursor.getColumnIndex("current_size")));
        data.setTotalSize(cursor.getInt(cursor.getColumnIndex("total_size")));
        data.setDate(cursor.getInt(cursor.getColumnIndex("date")));

        cursor.close();

        return data;
    }

    /**
     * 更新下载信息
     */
    public void updateData(int currentSize, String url) {
        ContentValues values = new ContentValues();
        values.put("current_size", currentSize);
        sqldb.update(TABLE_NAME_DOWNLOAD, values, "url = ?", new String[]{url});
    }

    /**
     * 删除下载信息
     */
    public void deleteData(String url) {
        sqldb.delete(TABLE_NAME_DOWNLOAD, "url = ?", new String[]{url});
    }
}
