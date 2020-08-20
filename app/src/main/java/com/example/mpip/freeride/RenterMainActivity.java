package com.example.mpip.freeride;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.domain.Location;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RenterMainActivity extends AppCompatActivity {

    Database db;
    ArrayList<Bike> bikes;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new Database(this);
        try {
            bikes = readFileFromSQLite();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridView gridView = (GridView) findViewById(R.id.gridview_bikes);
        Bike [] arr = bikes.toArray(new Bike[0]);
        BikeAdapter bikeAdapter = new BikeAdapter(getApplicationContext(), bikes.toArray(new Bike[0]));
        gridView.setAdapter(bikeAdapter);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RenterMainActivity.this, AddBicycleActivity.class);
                Intent intent = getIntent();
                String email = intent.getStringExtra("email");
                i.putExtra("email", email);
                startActivity(i);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<Bike> readFileFromSQLite() throws IOException {

        ArrayList<Bike> bicycles = new ArrayList<Bike>();
        final int takeFlags = getIntent().getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        Cursor cursor = db.getAllBikes();
        System.out.println(cursor.getCount());
        if(cursor.getCount()!=0){
            while(cursor.moveToNext())
            {
                int id = cursor.getColumnIndex("id");
                String name = cursor.getString(cursor.getColumnIndex("model_name"));
                int price = cursor.getInt(cursor.getColumnIndex("Price"));
                int rented = cursor.getInt(cursor.getColumnIndex("Rented"));
                int category_id = cursor.getInt(cursor.getColumnIndex("category_id"));
                double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex("image_url")));
                /*InputStream is = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);*/
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                int renter_id = cursor.getInt(cursor.getColumnIndex("renter_id"));
                Location location = new Location(latitude, longitude);
                Bike bike = new Bike(id, name, price, bitmap, rented, location, renter_id, category_id);
                bicycles.add(bike);
            }

            cursor.close();
        }

        return bicycles;
    }

}
