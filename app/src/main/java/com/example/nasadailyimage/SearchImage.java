package com.example.nasadailyimage;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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
        button.setOnClickListener(click -> {

            //Get Date from DatePicker as yyyy-mm-dd
            DatePicker datePicker = findViewById(R.id.date);
            String date = datePicker.getYear() + "-" + (datePicker.getMonth()+1) + "-" + datePicker.getDayOfMonth();

            //Append date to NASA image API URL
            String url = "https://api.nasa.gov/planetary/apod?api_key=WvdfUPArMX2zKJws6qwTEU3qoORfZsXCAUITxHUE&date=";
            url = url + date;
            Log.d("url", url);

            //Send URL to Web query thread
            DailyImage dailyImage = new DailyImage();
            dailyImage.execute(url);
        });

        //Set Save button to populate list adapter array
        Button button2 = findViewById(R.id.save_button);
        button2.setOnClickListener(click -> {

            if(date == null){
                Toast.makeText(this, getResources().getString(R.string.choose_date), Toast.LENGTH_SHORT).show();
            }else {

                ImageDB dbHelper = new ImageDB(this, ImageDB.dbName, null, ImageDB.dbVersion);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                //Save data to SQLite, Image to drive
                //Insert Values to DB
                ContentValues cValues = new ContentValues();
                cValues.put(ImageDB.colDate, date);
                cValues.put(ImageDB.colUrl, urlSD);
                cValues.put(ImageDB.colUrlHD, urlHD);
                cValues.put(ImageDB.colExplain, text);
                long id = db.insert(ImageDB.tableName, null, cValues);
                Log.d("saved to db.", "saved to db");

                //Save image to device
                String path = date + ".png";
                try {
                    FileOutputStream outputStream = openFileOutput(path, Context.MODE_PRIVATE);
                    image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });


    }

    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        //Hopefully Fixes the Transaction too large exception
        super.onSaveInstanceState(oldInstanceState);
        if (oldInstanceState != null) {
            oldInstanceState.clear();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        //Nav Menu cases. Decides which activity to start
        switch(item.getItemId()) {
            case R.id.daily:
                Intent main = new Intent(this, MainActivity.class);
                startActivity(main);
                break;
            case R.id.random:
                Intent random = new Intent(this, RandomImage.class);
                startActivity(random);
                break;
            case R.id.search:

                break;
            case R.id.saved:
                Intent saved = new Intent(this, ListImage.class);
                startActivity(saved);
                break;
            case R.id.about:
                Intent about = new Intent(this, About.class);
                startActivity(about);
                break;
        }
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    private class DailyImage extends AsyncTask<String, Integer, String> {



        @Override
        protected String doInBackground(String... strings) {

            try {
                //Get URL from UI thread, open connection
                URL url = new URL(strings[0]);
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
                publishProgress(1);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onProgressUpdate(Integer... args) {

            Bundle activityData = new Bundle();
            activityData.putString("date", date);
            activityData.putParcelable("image", image);
            activityData.putString("details", text);
            activityData.putString("url", urlSD);
            activityData.putString("hdurl", urlHD);

            SearchFragment searchFragment = new SearchFragment();
            searchFragment.setArguments(activityData);

            getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout, searchFragment).commit();
        }
    }
}