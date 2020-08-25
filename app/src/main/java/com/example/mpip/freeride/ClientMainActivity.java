package com.example.mpip.freeride;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.domain.BikeDistance;
import com.example.mpip.freeride.domain.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ClientMainActivity extends AppCompatActivity {

    double dis;
    double myLat;
    double myLong;
    private static final int REQUEST_CODE = 101;

    android.location.Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;

    Database db;
    ArrayList<BikeDistance> bikes = new ArrayList<BikeDistance>();
    Bitmap bitmap = null;
    Uri uri = null;
    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    Cursor cursorImages;
    GridView gridView;
    FloatingActionButton fab;
    ArrayList<String> bikes1 = new ArrayList<String>();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        geocoder = new Geocoder(this, Locale.getDefault());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

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
                        dis=distance(myLat, myLong, latitudes[i], longitudes[i]);
                        Bike b=new Bike(ids[i], names[i], prices[i], images[i], rented, location, renter_id, category_ids[i]);
                        bikes.add(new BikeDistance(b, dis));

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
        Collections.sort(bikes);
        Bike [] arr=new Bike[bikes.size()];
        int i=0;
        for(BikeDistance bd:bikes){
            arr[i]=bd.getBike();
            i++;
        }
        for (i = 0; i < niza.length; i++) {
            int a=arr[i].getId()-1;
            Uri imageUri = Uri.parse(niza[a]);
            InputStream is = getContentResolver().openInputStream(imageUri);

            Bitmap bitmap = BitmapFactory.decodeStream(is);

            bitmaps.add(bitmap);
        }
        BikeAdapter bikeAdapter = new BikeAdapter(getApplicationContext(), arr, (Bitmap[]) bitmaps.toArray(new Bitmap[0]));
        gridView.setAdapter(bikeAdapter);

    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            return;
        }
        Task<android.location.Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(android.location.Location location) {

                if (location != null) {
                    currentLocation = location;
                    myLat = location.getLatitude();
                    myLong = location.getLongitude();
                    currentLocation.setLatitude(myLat);
                    currentLocation.setLongitude(myLong);
                }
            }
        });
    }

    public double distance(double myLat, double myLong, double latBike, double longBike) {
        double radius = 6378137;
        double deltaLat = latBike - myLat;
        double deltaLon = longBike - myLong;
        double angle = 2 * Math.asin( Math.sqrt(
                Math.pow(Math.sin(deltaLat/2), 2) +
                        Math.cos(myLat) * Math.cos(latBike) *
                                Math.pow(Math.sin(deltaLon/2), 2) ) );
        return radius * angle;
    }

}
