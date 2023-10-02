package com.jr_dev.nasadailyimage;

import android.graphics.Bitmap;

/**
 * Object for storing image data
 * Contains only getters; no need for setters
 *
 * @author James Ching
 */
public class SavedImage {

    long id;
    String date;
    Bitmap image;
    String url;
    String urlHD;
    String explanation;

    //constructor
    public SavedImage(long id, String d, Bitmap i, String u, String hd, String e){
        this.id = id;
        date = d;
        image = i;
        url = u;
        urlHD = hd;
        explanation = e;
    }

    //Getters

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
