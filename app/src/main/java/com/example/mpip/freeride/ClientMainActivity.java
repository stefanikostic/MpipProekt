package com.example.mpip.freeride;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.domain.Location;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ClientMainActivity extends AppCompatActivity {

    Database db;
    ArrayList<Bike> bikes = new ArrayList<Bike>();
    Bitmap bitmap = null;
    Uri uri = null;
    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    Cursor cursorImages;
    GridView gridView;
    FloatingActionButton fab;
    ArrayList<String> bikes1 = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        gridView=(GridView) findViewById(R.id.gridview_bikes1);
        fab = findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        db = new Database(this);
        Cursor cursor = db.getAllAvailableBikes();
        int counter = cursor.getCount();
        int [] ids = new int[counter];
        String [] names = new String[counter];
        int [] prices = new int[counter];
        int [] category_ids = new int[counter];
        double [] latitudes = new double[counter];
        double [] longitudes = new double[counter];
        String [] images = new String [counter];
        for(int i = 0; i < cursor.getCount(); i++) {
            if (cursor != null) {
                while(cursor.moveToNext()) {
                        ids[i] = cursor.getInt(cursor.getColumnIndex("id"));
                        names[i] = cursor.getString(cursor.getColumnIndex("model_name"));
                        prices[i] = cursor.getInt(cursor.getColumnIndex("Price"));
                        category_ids[i] = cursor.getInt(cursor.getColumnIndex("category_id"));
                        latitudes[i] = cursor.getDouble(cursor.getColumnIndex("latitude"));
                        longitudes[i] = cursor.getDouble(cursor.getColumnIndex("longitude"));
                        images[i] = cursor.getString(cursor.getColumnIndex("image_url"));
                        bikes1.add(images[i]);
                        int rented = cursor.getInt(cursor.getColumnIndex("Rented"));
                        int renter_id = cursor.getInt(cursor.getColumnIndex("renter_id"));
                        Location location = new Location(latitudes[i], longitudes[i]);
                        bikes.add(new Bike(ids[i], names[i], prices[i], images[i], rented, location, renter_id, category_ids[i]));
                    }
            }
        }
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                try {
                    handdlee();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                // User refused to grant permission.
            }
        }
    }

    public void handdlee() throws FileNotFoundException {
        String[] niza = (String[]) bikes1.toArray(new String[0]);
        for (int i = 0; i < niza.length; i++) {
            Uri imageUri = Uri.parse(niza[i]);
            InputStream is = getContentResolver().openInputStream(imageUri);

            Bitmap bitmap = BitmapFactory.decodeStream(is);

            bitmaps.add(bitmap);
        }
        Bike [] arr = bikes.toArray(new Bike[0]);
        BikeAdapter bikeAdapter = new BikeAdapter(getApplicationContext(), bikes.toArray(new Bike[0]), (Bitmap[]) bitmaps.toArray(new Bitmap[0]));
        gridView.setAdapter(bikeAdapter);

    }

   /* @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<Bike> readFileFromSQLite(Cursor cursor) throws IOException {
        ArrayList<Bike> bicycles = new ArrayList<Bike>();

        for(int i = 0; i < cursor.getCount(); i++)
        {
            if(cursor != null)
            {
                if(cursor.moveToFirst())
                {
                    while(!cursor.isAfterLast())
                    {
                        ids[i] =
                    }
                int id = cursor.getColumnIndex("id");
                String name = cursor.getString(cursor.getColumnIndex("model_name"));
                int price = cursor.getInt(cursor.getColumnIndex("Price"));
                int rented = cursor.getInt(cursor.getColumnIndex("Rented"));
                int category_id = cursor.getInt(cursor.getColumnIndex("category_id"));
                double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex("image_url")));
                Bitmap bitmap = null;
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                i.setType("image/*");
                startActivityForResult(i, 2);
                int renter_id = cursor.getInt(cursor.getColumnIndex("renter_id"));
                Location location = new Location(latitude, longitude);
                Bike bike = new Bike(id, name, price, bitmap, rented, location, renter_id, category_id);
                bicycles.add(bike);
            }
            cursor.close();
        }
        return bicycles;
    }*/

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if(resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }*/
}
