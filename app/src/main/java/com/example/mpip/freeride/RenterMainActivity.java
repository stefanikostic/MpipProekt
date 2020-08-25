package com.example.mpip.freeride;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;
import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.domain.Location;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RenterMainActivity extends AppCompatActivity {

    Database db;
    ArrayList<Bike> bikes = new ArrayList<Bike>();
    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    public static final int GALLERY_INTENT_CALLED = 1;
    public static final int GALLERY_KITKAT_INTENT_CALLED = 2;
    ArrayList<String> bikes1 = new ArrayList<String>();
    Uri uri;
    Bitmap bitmap = null;
    GridView gridView;
    FloatingActionButton fab;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        gridView=(GridView) findViewById(R.id.gridview_bikes);
        fab = findViewById(R.id.fab1);
        setSupportActionBar(toolbar);
        db = new Database(this);
        Cursor cursor = db.getAllAvailableBikes();
        int counter = cursor.getCount();
        int[] ids = new int[counter];
        String[] names = new String[counter];
        int[] prices = new int[counter];
        int[] category_ids = new int[counter];
        double[] latitudes = new double[counter];
        double[] longitudes = new double[counter];
        String[] images = new String[counter];
        for (int i = 0; i < cursor.getCount(); i++) {
            if (cursor != null) {
                    while(cursor.moveToNext()) {
                        ids[i] = cursor.getInt(cursor.getColumnIndex("id"));
                        names[i] = cursor.getString(cursor.getColumnIndex("model_name"));
                        prices[i] = cursor.getInt(cursor.getColumnIndex("Price"));
                        category_ids[i] = cursor.getInt(cursor.getColumnIndex("category_id"));
                        latitudes[i] = cursor.getDouble(cursor.getColumnIndex("latitude"));
                        longitudes[i] = cursor.getDouble(cursor.getColumnIndex("longitude"));
                        images[i] = cursor.getString(cursor.getColumnIndex("image_url"));
                        int rented = cursor.getInt(cursor.getColumnIndex("Rented"));
                        int renter_id = cursor.getInt(cursor.getColumnIndex("renter_id"));
                        Location location = new Location(latitudes[i], longitudes[i]);
                        images[i] = cursor.getString(cursor.getColumnIndex("image_url"));
                        bikes1.add(images[i]);
                        Bike bike = new Bike(ids[i], names[i], prices[i], images[i], rented, location, renter_id, category_ids[i]);
                        bikes.add(bike);
                    }
            }
        }

           requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);

                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                    // app-defined int constant that should be quite unique



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
                uri = Uri.parse(cursor.getString(cursor.getColumnIndex("image_url")));
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                           1);
                } else {
                    InputStream is = getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(is);
                }
                int renter_id = cursor.getInt(cursor.getColumnIndex("renter_id"));
                Location location = new Location(latitude, longitude);
                Bike bike = new Bike(id, name, price, bitmap, rented, location, renter_id, category_id);
                bicycles.add(bike);
            }

            cursor.close();
        }

        return bicycles;
    }*/

    public void readFile(Uri uri, Bitmap bitmap) throws FileNotFoundException {
        InputStream is = getContentResolver().openInputStream(uri);
        bitmap = BitmapFactory.decodeStream(is);
    }
//
//
//    public void getPhoto(Uri uri){
//        if (Build.VERSION.SDK_INT <19){
//            Intent intent = new Intent();
//            intent.setType("*/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(intent, GALLERY_INTENT_CALLED);
//        } else {
//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("*/*");
//            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
//        }
//    }


/*    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    readFile(uri, bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                // Permission Denied
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/
}
