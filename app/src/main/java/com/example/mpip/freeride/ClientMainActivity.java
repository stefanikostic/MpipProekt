package com.example.mpip.freeride;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.*;

import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.domain.BikeDistance;
import com.example.mpip.freeride.service.Common;
import com.example.mpip.freeride.service.LocationService;
import com.example.mpip.freeride.service.SendLocationToActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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




        gridView=(GridView) findViewById(R.id.gridview_client);
        fab = findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Bike");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject o : objects) {
                            String id = o.getObjectId();
                            String name = o.getString("name");
                            int price = o.getInt("price");
                            String category_id = o.getString("category_id");
                            double latitude = o.getDouble("latitude");
                            boolean rented = o.getBoolean("rented");
                            double longitude = o.getDouble("longitude");
                            String renter_id = o.getString("renter_id");
                            com.example.mpip.freeride.domain.Location location = new com.example.mpip.freeride.domain.Location(latitude, longitude);
                            ParseFile img = (ParseFile) o.get("image");
                            try {
                                assert img != null;
                                Bitmap bitmap = BitmapFactory.decodeByteArray(img.getData(), 0, img.getData().length);
                                Bike bike = new Bike(id, name, price, bitmap, rented, location, renter_id, category_id);
                                onlyBikes.add(bike);
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });



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
        Bike [] arr = new Bike[bikes.size()];
        int i = 0;
        for(BikeDistance bd : bikes){
            arr[i] = bd.getBike();
            i++;
        }
        BikeAdapter bikeAdapter = new BikeAdapter(ClientMainActivity.this, arr);
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