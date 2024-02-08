package com.jr_dev.nasadailyimage;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Select a date to get the corresponding NASA image of the Day
 * Save the image to see it in the saved images activity
 *
 * @author James Ching
 */
public class SearchImage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Bitmap image;
    String text;
    String date;
    String urlSD;
    String urlHD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_image);

        //Set Toolbar in UI
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Set Navigation Drawer, attach to toolbar
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Search Button sends date chosen to AsyncTask with URL
        Button button = findViewById(R.id.search_button);
        button.setOnClickListener(click -> prepareUrl());

        //Set Save button to populate list adapter array
        Button button2 = findViewById(R.id.save_button);
        button2.setOnClickListener(click -> saveImage());

    }

    /**
     * Prepare Url for Query
     */
    public void prepareUrl(){
        //Get Date from DatePicker as yyyy-mm-dd
        DatePicker datePicker = findViewById(R.id.date);
        String date = datePicker.getYear() + "-" + (datePicker.getMonth()+1) + "-" + datePicker.getDayOfMonth();

        //Append date to NASA image API URL
        String url = "https://api.nasa.gov/planetary/apod?api_key=WvdfUPArMX2zKJws6qwTEU3qoORfZsXCAUITxHUE&date=";
        url = url + date;
        Log.d("url", url);

        //Send URL to Web query thread
        urlQuery(url);
    }

    /**
     * Queries NASA API on new thread
     * Updates GUI Thread upon successful query
     * @param u URL to be queried
     */
    public void urlQuery(String u){

        //new thread executor running non-UI thread
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        //Handler get Main UI looper to handle UI manipulation in separate thread
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            try {
                //Get URL from UI thread, open connection
                URL url = new URL(u);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //Get input from Web response
                InputStream response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8), 8);

                //Build String from Web Response
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                String result = sb.toString();

                //Build JSON object from returned string object
                JSONObject nasaJSON = new JSONObject(result);
                URL imageURL = new URL(nasaJSON.getString("url"));

                //Get image url, date and description string from JSON object, create Bitmap from image url web response
                image = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                text = nasaJSON.getString("explanation");
                date = nasaJSON.getString("date");
                urlSD = nasaJSON.getString("url");
                urlHD = nasaJSON.getString("hdurl");

                //Send to UI thread
                handler.post(() -> {

                    //Insert all data into Bundle
                    Bundle activityData = new Bundle();
                    activityData.putString("date", date);
                    activityData.putParcelable("image", image);
                    activityData.putString("details", text);
                    activityData.putString("url", urlSD);
                    activityData.putString("hdurl", urlHD);

                    //Pass data to display fragment
                    SearchFragment searchFragment = new SearchFragment();
                    searchFragment.setArguments(activityData);

                    getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, searchFragment).commit();
                });

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * saves selected image to SQLite DB
     */
    public void saveImage(){
        if(date == null){
            Toast.makeText(this, getResources().getString(R.string.choose_date), Toast.LENGTH_SHORT).show();
        }
        else {
            ImageDAO imageDAO = new ImageDAO(this);
            if (imageDAO.ifExistsDate(date)){
                Toast.makeText(this, getResources().getString(R.string.exists), Toast.LENGTH_SHORT).show();
                return;
            }
            imageDAO.insertValues( date, urlSD, urlHD, text, image);
            Log.d("saved to db.", "saved to db");
        }
    }

    /**
     * Hopefully a fix for Transaction Too Large Exception
     * Delete old instance states before changing activity
     * @param oldInstanceState The old instance state of the activity
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle oldInstanceState) {
        //Hopefully Fixes the Transaction too large exception
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();


    }

    /**Inflates Toolbar
     * @param menu Menu to inflate
     * @return Return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * Determines what to do when a button is pressed
     * @param item Menu Item Selected
     * @return return
     */
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Help Menu is the only button. Set to Alert Dialog help menu.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(R.string.help)
                .setMessage(R.string.search_help)
                .setPositiveButton(R.string.ok, (click, arg) -> {});
        //Show the Alert Menu
        alertDialogBuilder.create().show();

        return true;
    }

    /**
     * Determines what to do when a
     * navigation button is pressed
     * @param item Menu Item Selected
     * @return return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        //Nav Menu cases. Decides which activity to start
        int itemId = item.getItemId();
        if (itemId == R.id.daily) {
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
        } else if (itemId == R.id.random) {
            Intent random = new Intent(this, RandomImage.class);
            startActivity(random);
        } else if (itemId == R.id.saved) {
            Intent saved = new Intent(this, ListImage.class);
            startActivity(saved);
        } else if (itemId == R.id.about) {
            Intent about = new Intent(this, About.class);
            startActivity(about);
        }
        return false;
    }


}