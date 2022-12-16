package com.example.nasadailyimage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ImageDB extends SQLiteOpenHelper {

    protected final static String dbName = "Image";
    protected final static int dbVersion = 1;
    protected final static String tableName = "ImageData";
    protected final static String colDate = "date";
    protected final static String colUrl = "url";
    protected final static String colUrlHD = "urlhd";
    protected final static String colExplain = "explanation";

    public ImageDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + tableName
                + " ( " + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + colDate + " TEXT, "
                + colUrl + " TEXT, "
                + colUrlHD + " TEXT, "
                + colExplain + " TEXT "
                + ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);

    }
}
