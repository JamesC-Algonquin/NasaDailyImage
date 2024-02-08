package com.jr_dev.nasadailyimage.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.jr_dev.nasadailyimage.component.ListAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageDAO {

    ImageDB imageDB;
    SQLiteDatabase db;
    Context context;
    ExecutorService executorService;
    Handler handler;

    public ImageDAO(Context c){
        imageDB = new ImageDB(c, ImageDB.dbName, null, ImageDB.dbVersion);
        db = imageDB.getWritableDatabase();
        context = c;
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
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

    public void insertValues(String date, String urlSD, String urlHD, String text, Bitmap image){

        executorService.execute(() -> {

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
        });
    }

    public void selectAll(ListAdapter listAdapter){

        executorService.execute(() -> {

            String[] columns = {"_id", ImageDB.colDate, ImageDB.colUrl, ImageDB.colUrlHD, ImageDB.colExplain};
            Cursor result = db.query(false, ImageDB.tableName, columns, null, null, null, null, null, null);

            result.moveToFirst();
            result.moveToPrevious();
            //iterate results
            while (result.moveToNext()) {
                int colIDIndex = result.getColumnIndex("_id");
                int colDateIndex = result.getColumnIndex(ImageDB.colDate);
                int colUrlIndex = result.getColumnIndex(ImageDB.colUrl);
                int colUrlHDIndex = result.getColumnIndex(ImageDB.colUrlHD);
                int colExplainIndex = result.getColumnIndex(ImageDB.colExplain);

                //Save results
                long id = result.getLong(colIDIndex);
                String date = result.getString(colDateIndex);
                String url = result.getString(colUrlIndex);
                String urlHD = result.getString(colUrlHDIndex);
                String explain = result.getString(colExplainIndex);

                //Load saved image from file
                String path = date + ".png";
                Bitmap image = BitmapFactory.decodeFile(context.getFilesDir() + "/" + path);

                //create object and save to list
                SavedImage save = new SavedImage(id, date, image, url, urlHD, explain);
                listAdapter.addList(save);

            }
            //Close Cursor
            result.close();
            //Update List
            handler.post(listAdapter::notifyDataSetChanged);
        });
    }

    public void deleteImage(ListAdapter listAdapter, int position){

        SavedImage saved = listAdapter.getItem(position);

        executorService.execute(() -> {
            db.execSQL("DELETE FROM " + ImageDB.tableName + " WHERE _id=" + saved.getID());
            //Delete Image from file
            File imageFile = new File(context.getFilesDir() + "/" + saved.getDate() + ".png");
            if (imageFile.delete()) {
                Log.d("Delete", "Deleted file" + imageFile);
            }

            //Delete from ListAdapter
            listAdapter.deleteFromList(position);
            handler.post(listAdapter::notifyDataSetChanged);

        });

    }
}
