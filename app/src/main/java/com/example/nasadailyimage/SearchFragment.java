package com.example.nasadailyimage;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * Fragment for search image activity
 * Allows for easy orientation re-organizing
 * Contains date label, image, and hyperlink
 */
public class SearchFragment extends Fragment {

    Bundle activityData;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the fragmentLayout
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState savedInstanceState
     * @return Return
     */
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