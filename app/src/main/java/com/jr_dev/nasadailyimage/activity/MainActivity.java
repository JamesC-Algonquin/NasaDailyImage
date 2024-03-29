package com.jr_dev.nasadailyimage.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main Activity
 * Automatically loads the image based on current date
 * Displays description of image
 * Includes Nav menu and Toolbar
 *
 * @author James Ching
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //Automatically begin URL query
        prepareUrl();
    }

    /**
     * Uses current date to prepare URL Query to NASA API
     *
     */
    public void prepareUrl(){
        //Get current date, format
        Date date = new Date();
        String format = "yyyy-MM-dd";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String currentDate = dateFormat.format(date);

        //Append date to NASA image API URL
        String url = "https://api.nasa.gov/planetary/apod?api_key=WvdfUPArMX2zKJws6qwTEU3qoORfZsXCAUITxHUE&date=";
        url = url + currentDate;

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
        ExecutorService executor = Executors.newSingleThreadExecutor();
        //Handler get Main UI looper to handle UI manipulation in separate thread
        Handler handler = new Handler(Looper.getMainLooper());

        //execute web query on non-UI thread
        executor.execute(() -> {

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

                //Get image url and description string from JSON object, create Bitmap from image url web response
                Bitmap image = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                String text = nasaJSON.getString("explanation");

                handler.post(() -> {
                    //Set UI image to bitmap
                    ImageView imageView = findViewById(R.id.dailyImage);
                    imageView.setImageBitmap(image);
                    //Set description to text from JSON Object
                    TextView textView = findViewById(R.id.description);
                    textView.setText(text);
                });
            } catch (IOException | JSONException e) {
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
                .setMessage(R.string.daily_help)
                .setPositiveButton(R.string.ok, (click, arg) -> {});
        //Show the Alert Menu
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

        //Nav Menu cases. Decides which activity to start
        int itemId = item.getItemId();
        if (itemId == R.id.random) {
            Intent random = new Intent(this, RandomImage.class);
            startActivity(random);
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