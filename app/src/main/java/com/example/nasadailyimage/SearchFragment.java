package com.example.nasadailyimage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class SearchFragment extends Fragment {


    Bundle activityData;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Get Data from Activity
        activityData = getArguments();

        //Get View to inflate
        View result = inflater.inflate(R.layout.fragment_search, container, false);

        //Get Views
        TextView searchDate = result.findViewById(R.id.searchDate);
        ImageView searchImage = result.findViewById(R.id.searchImage);
        TextView searchDetails = result.findViewById(R.id.searchDetails);
        TextView urlHD = result.findViewById(R.id.url_hd);

        //Update Views
        searchDate.setText(activityData.getString("date"));
        searchImage.setImageBitmap(activityData.getParcelable("image"));
        searchDetails.setText(activityData.getString("details"));
        urlHD.setText(
                Html.fromHtml("<a href=\""
                + activityData.getString("hdurl")
                + "\">" + activityData.getString("hdurl") + "</a> "));

        //Make TextView a clickable Link
        urlHD.setMovementMethod(LinkMovementMethod.getInstance());

        return result;
    }
}