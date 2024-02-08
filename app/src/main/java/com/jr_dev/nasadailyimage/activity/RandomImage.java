package com.jr_dev.nasadailyimage.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.jr_dev.nasadailyimage.R;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    //ExecutorService is instantiated outside method due to recursive calling
    ExecutorService executor = Executors.newSingleThreadExecutor();

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

        //Set button event
        Button button = findViewById(R.id.start_button);
        button.setOnClickListener(click -> {
            running = !running;
            if (running) {
                randomQuery();
            }
        });

    }

    /**
     * Randomly picks date to append to url query
     * calls recursively as long as "running" is true
     */
    public void randomQuery(){

        ImageView imageView = findViewById(R.id.dailyImage);
        ProgressBar progressBar = findViewById(R.id.progressBar);


        //Handler get Main UI looper to handle UI manipulation in separate thread
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                //Get URL pattern
                String sURL = "https://api.nasa.gov/planetary/apod?api_key=WvdfUPArMX2zKJws6qwTEU3qoORfZsXCAUITxHUE&date=";

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

                Bitmap image = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

                handler.post(() -> imageView.setImageBitmap(image));

                Runnable runnable = (() -> {
                    int progress = progressBar.getProgress() + 1;
                    progressBar.setProgress(progress);

                    if (progressBar.getProgress() == 100){
                        progressBar.setProgress(0);
                        if (running) {
                            randomQuery();
                        }
                    }
                });

                for (int i=1; i<=100; i++){
                    handler.post(runnable);
                    Thread.sleep(30);
                }

            } catch (IOException | JSONException | InterruptedException e) {
                e.printStackTrace();
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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        int itemId = item.getItemId();
        if (itemId == R.id.daily) {
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
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

}