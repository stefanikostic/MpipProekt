package com.example.mpip.freeride;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.domain.BikeDistance;
import com.example.mpip.freeride.service.Common;
import com.example.mpip.freeride.service.LocationService;
import com.example.mpip.freeride.service.SendLocationToActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ClientMainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private LocationService locationService;
    private boolean mBound = false;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder)iBinder;
            locationService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationService = null;
            mBound = false;
        }
    };
    private double myLat;
    private double myLong;


    private Database db;
    private ArrayList<BikeDistance> bikes = new ArrayList<BikeDistance>();
    private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private ArrayList<Bike> onlyBikes = new ArrayList<>();
    private GridView gridView;
    private FloatingActionButton fab;
    private ArrayList<String> bikes1 = new ArrayList<String>();
    public void updateLongLat(double lat, double lng) throws FileNotFoundException {
        myLat = lat;
        myLong = lng;
        convertBikes();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);
        Toolbar toolbar = findViewById(R.id.toolbar);


        Dexter.withActivity(this)
                .withPermissions(Arrays.asList(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION))
                                .withListener(new MultiplePermissionsListener() {
                                    @Override
                                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                locationService.requestLocationUpdates();
                                            }
                                        }, 2000);
                                        bindService(new Intent(ClientMainActivity.this, LocationService.class),
                                                mServiceConnection,
                                                Context.BIND_AUTO_CREATE);
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                    }
                                }).check();




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
                        com.example.mpip.freeride.domain.Location location = new com.example.mpip.freeride.domain.Location(latitudes[i], longitudes[i]);
                        Bike b=new Bike(ids[i], names[i], prices[i], images[i], rented, location, renter_id, category_ids[i]);
                        onlyBikes.add(b);
                    }
            }
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_DOCUMENTS},
                    1);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if(mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                try {
                    convertBikes();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                // User refused to grant permission.
            }
        }
    }

    private void convertBikes() throws FileNotFoundException {
        bikes = new ArrayList<>();
        for(Bike b : onlyBikes){
            com.example.mpip.freeride.domain.Location l = b.getLocation();
            float distance = distance(myLat, myLong, l.getLatitude(), l.getLongitude());
            BikeDistance bd = new BikeDistance(b, distance);
            bikes.add(bd);
        }
        Collections.sort(bikes);
        this.handdlee();
    }

    public void handdlee() throws FileNotFoundException {
        bitmaps = new ArrayList<Bitmap>();
        String[] niza = (String[]) bikes1.toArray(new String[0]);
        Bike [] arr = new Bike[bikes.size()];
        int i = 0;
        for(BikeDistance bd : bikes){
            arr[i] = bd.getBike();
            i++;
        }
        for (i = 0; i < niza.length; i++) {
            System.out.println(arr[i].getId());
            int a = arr[i].getId()-1;
            Uri imageUri = Uri.parse(niza[a]);

           InputStream is = getBaseContext().getContentResolver().openInputStream(imageUri);

           Bitmap bitmap = BitmapFactory.decodeStream(is);

           bitmaps.add(bitmap);
        }
        BikeAdapter bikeAdapter = new BikeAdapter(getApplicationContext(), arr, (Bitmap[]) bitmaps.toArray(new Bitmap[0]));
        gridView.setAdapter(bikeAdapter);
//        bikeAdapter.notifyDataSetChanged();

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


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if(key.equals(Common.KEY_REQUESTING_LOCATION_UPDATES))
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListenLocation(SendLocationToActivity event) throws FileNotFoundException {
        if(event != null) {
           myLat = event.getLocation().getLatitude();
           myLong = event.getLocation().getLongitude();
           convertBikes();
        }
    }
}
