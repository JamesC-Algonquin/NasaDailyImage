package com.jr_dev.nasadailyimage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import java.io.FileOutputStream;
import java.io.IOException;

public class ImageDAO {

    ImageDB imageDB;
    SQLiteDatabase db;

    public ImageDAO(Context context){
        imageDB = new ImageDB(context, ImageDB.dbName, null, ImageDB.dbVersion);
        db = imageDB.getWritableDatabase();
    }

    public boolean ifExistsDate(String date){
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + ImageDB.tableName + " WHERE " + ImageDB.colDate + "=?", new String[] {date});
        if (cursor!=null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);

            if (count >= 1) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void insertValues(Context context, String date, String urlSD, String urlHD, String text, Bitmap image){
        //Save data to SQLite, Image to drive
        //Insert Values to DB
        ContentValues cValues = new ContentValues();
        cValues.put(ImageDB.colDate, date);
        cValues.put(ImageDB.colUrl, urlSD);
        cValues.put(ImageDB.colUrlHD, urlHD);
        cValues.put(ImageDB.colExplain, text);
        db.insert(ImageDB.tableName, null, cValues);

        //Save image to device
        String path = date + ".png";
        try {
            FileOutputStream outputStream = context.openFileOutput(path, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
