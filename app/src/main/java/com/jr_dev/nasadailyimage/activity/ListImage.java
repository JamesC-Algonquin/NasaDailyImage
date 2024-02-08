package com.jr_dev.nasadailyimage.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.jr_dev.nasadailyimage.data.ImageDAO;
import com.jr_dev.nasadailyimage.component.ListAdapter;
import com.jr_dev.nasadailyimage.R;

/**
 * Activity Displays all Saved Images from Image Search
 *
 * @author James Ching
 */
public class ListImage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_image);
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

        inflateList();
    }

    /**
     * Inflates the list from Database entries
     * Images are stored in files
     * Delete entries by long press on list
     *
     */
   public void inflateList(){
       //Get List View and set adapter
       ListAdapter myListAdapter;
       myListAdapter = new ListAdapter(this);
       ListView myList = findViewById(R.id.ListView);
       myList.setAdapter(myListAdapter);

       ImageDAO imageDAO = new ImageDAO(this);
       imageDAO.selectAll(myListAdapter);

       //Long Press to delete
       myList.setOnItemLongClickListener((list, view, position, id) -> {
           AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
           alertDialogBuilder.setTitle(getResources().getString(R.string.delete))
                   //Set Prompt Message
                   .setMessage(getResources().getString(R.string.confirm))
                   //Set Yes Button
                   .setPositiveButton(getResources().getString(R.string.yes), (click, arg) -> {

                       //Delete from DB
                       imageDAO.deleteImage(myListAdapter, position);

                   })
                   //Set Empty No button
                   .setNegativeButton(getResources().getString(R.string.no), (click, arg) -> {
                   });
           alertDialogBuilder.create().show();
           return true;
       });

   }

    /**
     * Reinflates list after activity is resumed
     */
   public void onResume() {
       super.onResume();
       //Wipes List and repopulates based on new data
       inflateList();
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
                .setMessage(R.string.saved_help)
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
        } else if (itemId == R.id.about) {
            Intent about = new Intent(this, About.class);
            startActivity(about);
        }
        return false;
    }

}