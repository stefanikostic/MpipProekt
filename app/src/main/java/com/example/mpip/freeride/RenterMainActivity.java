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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.GridView;
import android.widget.Toast;
import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.domain.Location;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RenterMainActivity extends AppCompatActivity {

    Database db;
    ArrayList<Bike> bikes = new ArrayList<Bike>();
    GridView gridView;
    FloatingActionButton fab;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        gridView = (GridView) findViewById(R.id.gridview_renter);
        fab = findViewById(R.id.fab1);
        setSupportActionBar(toolbar);
        db = new Database(this);
        final Bitmap bitmap;
        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Renters");
        query.whereEqualTo("email", getIntent().getStringExtra("email"));
        query.findInBackground(new FindCallback<ParseObject>() {
                                   @Override
                                   public void done(List<ParseObject> objects, ParseException e) {
                                       if (e == null) {
                                           final String renter_id = objects.get(0).getObjectId();
                                           final ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Bike");
                                           query1.whereEqualTo("renter_id", renter_id);
                                           query1.findInBackground(new FindCallback<ParseObject>() {
                                               @Override
                                               public void done(List<ParseObject> objects, ParseException e) {
                                                   if (e == null) {
                                                       if (objects.size() > 0) {
                                                           for (ParseObject o : objects) {
                                                               final String id = o.getObjectId();
                                                               final String name = o.getString("name");
                                                               final int price = o.getInt("price");
                                                               final String category_id = o.getString("category_id");
                                                               double latitude = o.getDouble("latitude");
                                                               final boolean rented = o.getBoolean("rented");
                                                               double longitude = o.getDouble("longitude");
                                                               final Location location = new Location(latitude, longitude);
                                                               ParseFile img = (ParseFile) o.get("image");
                                                               Bitmap bmp = null;
                                                               if (img != null) {
                                                                   try {
                                                                       bmp = BitmapFactory.decodeStream(img.getDataStream());
                                                                   } catch (ParseException ex) {
                                                                       ex.printStackTrace();
                                                                   }
                                                                   Bike bike = new Bike(id, name, price, bmp, rented, location, renter_id, category_id);
                                                                   bikes.add(bike);

                                                           }}
                                                           handdlee();
                                                       }
                                                   } else {
                                                       e.printStackTrace();
                                                   }
                                               }
                                           });
                                       }
                                   }
                               });

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

/*    @Override
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
    }*/

    public void handdlee() {
        BikeAdapter bikeAdapter = new BikeAdapter(RenterMainActivity.this, bikes.toArray(new Bike[0]));
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
