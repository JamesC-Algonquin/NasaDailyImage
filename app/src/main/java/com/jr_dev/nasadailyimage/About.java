package com.jr_dev.nasadailyimage;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

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
        }
        return false;
    }

}