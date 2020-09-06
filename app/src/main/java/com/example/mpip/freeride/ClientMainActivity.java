package com.example.mpip.freeride;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
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
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_CODE = 101;

    android.location.Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;

    LocationManager locationManager;

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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }

        gridView=(GridView) findViewById(R.id.gridview_client);
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
                        com.example.mpip.freeride.domain.Location location = new com.example.mpip.freeride.domain.Location(latitudes[i], longitudes[i]);
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
    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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
        BikeAdapter bikeAdapter = new BikeAdapter(ClientMainActivity.this, arr, (Bitmap[]) bitmaps.toArray(new Bitmap[0]));
        gridView.setAdapter(bikeAdapter);

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(ClientMainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (ClientMainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ClientMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                myLat = location.getLatitude();
                myLong = location.getLongitude();
            } else if (location1 != null) {
                myLat = location1.getLatitude();
                myLong = location1.getLongitude();
            } else if (location2 != null) {
                myLat = location2.getLatitude();
                myLong = location2.getLongitude();

            } else {
                Toast.makeText(this, "Unble to Trace your location", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public float distance(double myLat, double myLong, double latBike, double longBike) {
        Location locationA = new Location("point A");

        locationA.setLatitude(myLat);
        locationA.setLongitude(myLong);

        Location locationB = new Location("point B");

        locationB.setLatitude(latBike);
        locationB.setLongitude(longBike);

        float distance = locationA.distanceTo(locationB);

       return distance;
    }

}
