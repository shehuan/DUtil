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
    public void saveDInfo(List<DownloadData> datas) {
        for (DownloadData data : datas) {
            ContentValues values = new ContentValues();
            values.put("thread_id", data.getThreadId());
            values.put("start_pos", data.getStartPos());
            values.put("end_pos", data.getEndPos());
            values.put("complete_size", data.getCompleteSize());
            values.put("url", data.getUrl());
            sqldb.insert(TABLE_NAME_DOWNLOAD, null, values);
        }
    }

    /**
     * 获得下载信息
     */
    public List<DownloadData> getDInfo(String url) {
        List<DownloadData> list = new ArrayList<>();

        Cursor cursor = sqldb.query("City", null, "province_id = ?",
                new String[]{url}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                DownloadData data = new DownloadData();

                data.setThreadId(cursor.getInt(cursor.getColumnIndex("thread_id")));
                data.setStartPos(cursor.getInt(cursor.getColumnIndex("start_pos")));
                data.setEndPos(cursor.getInt(cursor.getColumnIndex("end_pos")));
                data.setCompleteSize(cursor.getInt(cursor.getColumnIndex("complete_size")));
                data.setUrl(cursor.getString(cursor.getColumnIndex("url")));

                list.add(data);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

    /**
     * 更新下载信息
     */
    public void updateDInfo(int threadId, int completeSize, String url) {
        ContentValues values = new ContentValues();
        values.put("complete_size", completeSize);
        sqldb.update(TABLE_NAME_DOWNLOAD, values, "threadId = ? and url = ?", new String[]{String.valueOf(threadId), url});
    }

    /**
     * 删除下载信息
     */
    public void deleteDInfo(String url) {
        sqldb.delete(TABLE_NAME_DOWNLOAD, "url = ?", new String[]{url});
    }
}
