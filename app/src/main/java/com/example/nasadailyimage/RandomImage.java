package com.example.nasadailyimage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Selects random date from 1995 to now
 * loops through many images until stopped
 *
 * @author James Ching
 */
public class RandomImage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    //initially not running
    boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_image);

        //set Toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //set Nav drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String url = "https://api.nasa.gov/planetary/apod?api_key=WvdfUPArMX2zKJws6qwTEU3qoORfZsXCAUITxHUE&date=";
        //Set button event
        Button button = findViewById(R.id.start_button);
        button.setOnClickListener(click -> {
            //Button changes state of loop
            running = !running;
            DailyImage dailyImage = new DailyImage();

            //start loop if true
            if(running) {
                dailyImage.execute(url);
            }
        });

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
     * @return Return
     */
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Help Menu is the only button. Set to Alert Dialog help menu.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(R.string.help)
                .setMessage(R.string.random_help)
                .setPositiveButton(R.string.ok, (click, arg) -> {});

        alertDialogBuilder.create().show();

        return true;
    }

    /**
     * Determines what to do when a
     * navigation button is pressed
     * @param item Menu Item Selected
     * @return Return
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        int itemId = item.getItemId();
        if (itemId == R.id.daily) {
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
        } else if (itemId == R.id.random) {//Intent random = new Intent(this, );
            //startActivity(random);
        } else if (itemId == R.id.search) {
            Intent search = new Intent(this, SearchImage.class);
            startActivity(search);
        } else if (itemId == R.id.saved) {
            Intent saved = new Intent(this, ListImage.class);
            startActivity(saved);
        } else if (itemId == R.id.about) {
            Intent about = new Intent(this, About.class);
            startActivity(about);
        }
        return false;
    }

    /**
     * Does http request work on separate thread
     * Loads image and data from NASA API
     * Repeats as long as Running is true
     */
    @SuppressLint("StaticFieldLeak")
    private class DailyImage extends AsyncTask<String, Integer, String> {

        Bitmap image ;
        ImageView imageView = findViewById(R.id.dailyImage);
        ProgressBar progressBar = findViewById(R.id.progressBar);



        @Override
        protected String doInBackground(String... strings) {

            while(running) {

                try {
                    //Get URL pattern
                    String sURL = strings[0];

                    //Generate Random Date for NASA API
                    LocalDate startDate = LocalDate.of(1995, 7, 1); //start date
                    long start = startDate.toEpochDay();
                    LocalDate endDate = LocalDate.now(); //end date
                    long end = endDate.toEpochDay();
                    long randomEpochDay = ThreadLocalRandom.current().nextLong(start, end); //random date between start and end

                    //Append date to URL
                    sURL = sURL + LocalDate.ofEpochDay(randomEpochDay);

                    URL url = new URL(sURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream response = urlConnection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8), 8);
                    StringBuilder sb = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    String result = sb.toString();

                    JSONObject nasaJSON = new JSONObject(result);
                    URL imageURL = new URL(nasaJSON.getString("url"));

                    image = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    publishProgress(200);

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < 100; i++) {
                    try {
                        publishProgress(i);
                        Thread.sleep(30);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        public void onProgressUpdate(Integer... args) {

            if (args[0] == 200) {
                imageView.setImageBitmap(image);
            }
            else{
                progressBar.setProgress(args[0]);
            }


        }
    }
}