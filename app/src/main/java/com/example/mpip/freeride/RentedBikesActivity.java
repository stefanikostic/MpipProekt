package com.example.mpip.freeride;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.service.LocationService;
import com.example.mpip.freeride.service.SendLocationToActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RentedBikesActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ArrayList<Bike> onlyBikes = new ArrayList<>();
    private GridView gridView;
    private String clientId;
    private double myLat;
    int count = 0;
    Timer timer;
    private double myLong;
    private ProgressBar progressBar;
    private ConstraintLayout constraintLayout;
    private RelativeLayout relativeLayout;
    private ArrayList<String> rents_ids = new ArrayList<>();
    private LocationService locationService;
    private boolean mBound = false;
    RentedBikeAdapter bikeAdapter;

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

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        progressBar.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);
        constraintLayout.setVisibility(View.INVISIBLE);
        count = 0;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                count++;
                progressBar.setProgress(count);
                if(count == 15){
                    timer.cancel();
                    if(bikeAdapter != null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handle();
                            }
                        });
                    } else {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something here
                                constraintLayout.setVisibility(View.VISIBLE);
                                //if(onlyBikes.size()==0)
                                //  available.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                relativeLayout.setVisibility(View.INVISIBLE);
                            }
                        }, 2000);
                    }
                }
            }
        };
        timer.schedule(timerTask, 0, 100);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_rented_bikes);
        gridView = (GridView) findViewById(R.id.gridview_bikes2);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl1);
        progressBar = (ProgressBar) findViewById(R.id.progressbar1);
        Intent i = getIntent();
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintRented);
        clientId = i.getStringExtra("client_id");
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation_view2);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_home:
                        Intent intent1 = new Intent(RentedBikesActivity.this, ClientMainActivity.class);
                        intent1.putExtra("id", clientId);
                        startActivity(intent1);
                        break;
//                    case R.id.ic_bikes:
                    case R.id.ic_exit:
                        Intent intent3 = new Intent(RentedBikesActivity.this, LoginActivity.class);
                        startActivity(intent3);
                        break;
                }


                return false;
            }
        });
        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Rents");
        query.whereEqualTo("client_id", clientId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject o : objects) {
                            rents_ids.add(o.getObjectId());
                            final ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Bike");
                            query1.whereEqualTo("objectId", o.getString("bike_id"));
                            query1.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null){
                                        if(list.size() > 0){
                                            ParseObject o = list.get(0);
                                            String bikeId = o.getObjectId();
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
                                                Bike bike = new Bike(bikeId, name, price, bitmap, rented, location, renter_id, category_id);
                                                onlyBikes.add(bike);
                                                handle();
                                            } catch (ParseException ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    void handle(){
        Bike [] arr = new Bike[onlyBikes.size()];
        int j = 0;
        for(Bike bd : onlyBikes){
            arr[j] = bd;
            j++;
        }
        bikeAdapter = new RentedBikeAdapter(RentedBikesActivity.this, arr, rents_ids.toArray(new String[0]), clientId, myLat, myLong);
        gridView.setAdapter(bikeAdapter);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something here
                constraintLayout.setVisibility(View.VISIBLE);
                //if(onlyBikes.size()==0)
                //  available.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.INVISIBLE);
            }
        }, 2000);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if(key.equals(Common.KEY_REQUESTING_LOCATION_UPDATES))
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListenLocation(SendLocationToActivity event) throws FileNotFoundException {
        if(event != null) {
            myLat = event.getLocation().getLatitude();
            myLong = event.getLocation().getLongitude();
        }
    }
}
