package com.example.mpip.freeride;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.View;
import android.widget.GridView;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.domain.BikeDistance;
import com.example.mpip.freeride.service.LocationService;
import com.example.mpip.freeride.service.SendLocationToActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.parse.*;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.*;

public class ClientMainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private double myLat;
    private double myLong;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;


    private ArrayList<BikeDistance> bikes = new ArrayList<BikeDistance>();
    private ArrayList<Bike> onlyBikes = new ArrayList<>();
    private GridView gridView;
    private FloatingActionButton fab;


    @TargetApi(29)
    @RequiresApi(api = 29)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    ClientMainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );
        } else {
            startLocationService();
        }


        gridView=(GridView) findViewById(R.id.gridview_bikes1);
        fab = findViewById(R.id.fab);
        setSupportActionBar(toolbar);

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
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                startLocationService();

            }else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
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
        BikeAdapter bikeAdapter = new BikeAdapter(getApplicationContext(), arr);
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

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null){
            for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
                return false;
            }
            return false;
    }

    private void startLocationService() {
        if(!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
        if(isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private static class Task extends AsyncTask<Void, Void, List<Object>> {
        private WeakReference<Context> mContextRef;
        public Task(Context context) {
            mContextRef = new WeakReference<>(context);
            //later when you need your context just use the 'get()' method. like : mContextRef.get() (this will return a Context Object.
        }

        @Override
        protected void onPreExecute() {
            // show progress Dialog
            //this method is processed in the MainThread, though it can prepare data from the background thread.
        }

        @Override
        protected List<Object> doInBackground(Void ... params) {
            List<Object> mList = new ArrayList<>();
            //Call your content provider here and gather the cursor and process your data..
            //return the list of object your want to show on the MainThread.
            return mList;
        }

        @Override
        protected void onPostExecute(List<Object> list) {
            if(list.size() > 0) {
                //do your stuff : i.e populate a listView
            }
        }
    }
}
