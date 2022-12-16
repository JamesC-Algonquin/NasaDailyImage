package com.example.nasadailyimage;

import android.graphics.Bitmap;

public class SavedImage {

    long id;
    String date;
    Bitmap image;
    String url;
    String urlHD;
    String explanation;

    public SavedImage(long id, String d, Bitmap i, String u, String hd, String e){
        this.id = id;
        date = d;
        image = i;
        url = u;
        urlHD = hd;
        explanation = e;
    }
    public long getID(){
        return id;
    }

    public String getDate(){
        return date;
    }

    public Bitmap getImage(){
        return image;
    }

    public String getUrl(){
        return url;
    }

    public String getUrlHD(){
        return urlHD;
    }

    public String getExplanation(){
        return explanation;
    }

}
