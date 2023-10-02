package com.jr_dev.nasadailyimage;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapts List of Images with Urls to ListView
 * @author James Ching
 */
public class ListAdapter extends BaseAdapter {

    protected ArrayList<SavedImage> imageList = new ArrayList<>();
    private final Context main;

    public ListAdapter (Context m){
        main = m;
    }

    public void addList(SavedImage save){
        imageList.add(save);
    }

    public void deleteFromList(int pos){
        imageList.remove(pos);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public SavedImage getItem(int i) {
        return imageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View localView = view;

        if (localView == null){
            localView = LayoutInflater.from(main).inflate(R.layout.list_layout, viewGroup, false);
        }
        //Get View
        SavedImage save = imageList.get(i);
        TextView date = localView.findViewById(R.id.date);
        ImageView image = localView.findViewById(R.id.image);
        TextView url = localView.findViewById(R.id.url);

        //Set view text/image
        date.setText(save.getDate());
        image.setImageBitmap(save.getImage());
        //Special hyperlink format
        url.setText(
                Html.fromHtml("<a href=\""
                + save.getUrlHD()
                + "\">"+ save.getUrlHD() +"</a> ", Html.FROM_HTML_MODE_LEGACY));

        return localView;
    }
}
