package com.othershe.dutil.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {

    /**
     * download_info表建表语句
     */
    public static final String CREATE_DOWNLOAD_INFO = "create table download_info ("
            + "id integer primary key autoincrement, "
            + "thread_id integer, "
            + "start_pos integer, "
            + "end_pos integer, "
            + "complete_size integer, "
            + "url text)";

    public DbOpenHelper(Context context, String name, CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DOWNLOAD_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}