package com.example.nasadailyimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;

import java.io.File;

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

   public void inflateList(){
       //Get List View and set adapter
       ListAdapter myListAdapter;
       myListAdapter = new ListAdapter(this);
       ListView myList = findViewById(R.id.ListView);
       myList.setAdapter(myListAdapter);

       //Get DB connection
       ImageDB dbHelper = new ImageDB(this, ImageDB.dbName, null, ImageDB.dbVersion);
       SQLiteDatabase db = dbHelper.getWritableDatabase();

       //Populate ListAdapter from DB
       //Get column names
       String[] columns = {"_id", ImageDB.colDate, ImageDB.colUrl, ImageDB.colUrlHD, ImageDB.colExplain};

       //Query into cursor
       Cursor result = db.query(false, ImageDB.tableName, columns, null, null, null, null, null, null);

       result.moveToFirst();
       result.moveToPrevious();
       //iterate results
       while (result.moveToNext()) {
           int colIDIndex = result.getColumnIndex("_id");
           int colDateIndex = result.getColumnIndex(ImageDB.colDate);
           int colUrlIndex = result.getColumnIndex(ImageDB.colUrl);
           int colUrlHDIndex = result.getColumnIndex(ImageDB.colUrlHD);
           int colExplainIndex = result.getColumnIndex(ImageDB.colExplain);

           //Save results
           long id = result.getLong(colIDIndex);
           String date = result.getString(colDateIndex);
           String url = result.getString(colUrlIndex);
           String urlHD = result.getString(colUrlHDIndex);
           String explain = result.getString(colExplainIndex);

           //Load saved image from file
           String path = date + ".png";
           Bitmap image = BitmapFactory.decodeFile(getFilesDir() + "/" + path);

           //create object and save to list
           SavedImage save = new SavedImage(id, date, image, url, urlHD, explain);
           myListAdapter.addList(save);

       }
       //Close Cursor
       result.close();

       //Update List
       myListAdapter.notifyDataSetChanged();

       //Long Press to delete
       myList.setOnItemLongClickListener((list, view, position, id) -> {
           AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
           alertDialogBuilder.setTitle(getResources().getString(R.string.delete))
                   //Set Prompt Message
                   .setMessage(getResources().getString(R.string.confirm))
                   //Set Yes Button
                   .setPositiveButton(getResources().getString(R.string.yes), (click, arg) -> {
                       //Delete from DB
                       SavedImage obj = myListAdapter.getItem(position);
                       db.execSQL("DELETE FROM " + ImageDB.tableName + " WHERE _id=" + obj.getID());

                       //Delete Image from file
                       File imageFile = new File(getFilesDir() + "/" + obj.getDate() + ".png");
                       imageFile.delete();

                       //Delete from ListAdapter
                       myListAdapter.deleteFromList(position);
                       myListAdapter.notifyDataSetChanged();
                   })
                   //Set Empty No button
                   .setNegativeButton(getResources().getString(R.string.no), (click, arg) -> {
                   });
           alertDialogBuilder.create().show();
           return true;
       });

   }

   public void onResume() {
       super.onResume();
       //Wipes List and repopulates based on new data
       inflateList();
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
                .setMessage(R.string.saved_help)
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
                Intent search = new Intent(this, SearchImage.class);
                startActivity(search);
                break;
            case R.id.saved:

                break;
            case R.id.about:
                Intent about = new Intent(this, About.class);
                startActivity(about);
                break;
        }
        return false;
    }

}