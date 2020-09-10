package com.example.mpip.freeride;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MenuItem;
import android.widget.GridView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.mpip.freeride.domain.Bike;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.*;

import java.util.ArrayList;
import java.util.List;

public class RentedBikesActivity extends AppCompatActivity {
    private ArrayList<Bike> onlyBikes = new ArrayList<>();
    private GridView gridView;
    private String clientId;
    private ArrayList<String> rents_ids = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_rented_bikes);
        gridView = (GridView) findViewById(R.id.gridview_bikes2);
        Intent i = getIntent();
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
        RentedBikeAdapter bikeAdapter = new RentedBikeAdapter(RentedBikesActivity.this, arr, rents_ids.toArray(new String[0]), clientId);
        gridView.setAdapter(bikeAdapter);
    }
}
