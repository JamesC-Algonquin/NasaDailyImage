package com.example.nasadailyimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

/**
 * Simple Text-based activity, with a short description of the author
 * Includes Toolbar and Nav Menu logic
 * Edit Text can be use to send email to author
 *
 * @author James Ching
 */
public class About extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

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

        EditText edit = findViewById(R.id.message);

        //Set to saved edit text data
        SharedPreferences prefs = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        String messaged = prefs.getString("message", "");
        if (!(messaged.equals(""))){
            edit.setText(messaged);
        }

        Button send = findViewById(R.id.send);
        send.setOnClickListener(click -> {

            //Get Edit text data
            String message = edit.toString();

            //Build Email intent
            Intent email = new Intent(Intent.ACTION_SEND);
            email.setDataAndType(Uri.parse("mailto:"), "text/plain");
            email.putExtra(Intent.EXTRA_EMAIL, "junkmailnin@gmail.com");
            email.putExtra(Intent.EXTRA_SUBJECT, "A Message");
            email.putExtra(Intent.EXTRA_TEXT, message);

            //Attempt email send, snackbar confirms success or failure
            try {
                startActivity(Intent.createChooser(email, "Send mail..."));
                Snackbar.make(send, "Message Sent!", Snackbar.LENGTH_LONG).show();
            } catch (android.content.ActivityNotFoundException ex) {
                Snackbar.make(send, "No Email Client Found!", Snackbar.LENGTH_SHORT).show();
            }


        });
    }

    /**
     * onPause method saves the edit text  data for the
     * next time it is opened.
     */
    protected void onPause(){
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        EditText editText = findViewById(R.id.message);
        edit.putString("message", String.valueOf(editText.getText()));
        edit.apply();
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
                .setMessage(R.string.about_help)
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
    @SuppressLint("NonConstantResourceId")
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
        } else if (itemId == R.id.search) {
            Intent search = new Intent(this, SearchImage.class);
            startActivity(search);
        } else if (itemId == R.id.saved) {
            Intent saved = new Intent(this, ListImage.class);
            startActivity(saved);
        } else if (itemId == R.id.about) {
        }
        return false;
    }

}