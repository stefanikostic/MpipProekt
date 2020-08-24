package com.example.mpip.freeride;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.domain.Location;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ClientBikeActivity extends AppCompatActivity {

    private ImageView imageViewBike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_bike);
        imageViewBike = findViewById(R.id.imageViewBike);
        Database db = new Database(this);
        Intent i = getIntent();
        int id = i.getIntExtra("bikeId", 0);
        Cursor cursor = db.getBike(id);
        for(int j = 0; j < cursor.getCount(); j++) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex("model_name"));
                    int price = cursor.getInt(cursor.getColumnIndex("Price"));
                    int category_id = cursor.getInt(cursor.getColumnIndex("category_id"));
                    double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                    double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                    String image = cursor.getString(cursor.getColumnIndex("image_url"));
                    int rented = cursor.getInt(cursor.getColumnIndex("Rented"));
                    int renter_id = cursor.getInt(cursor.getColumnIndex("renter_id"));
                    Location location = new Location(latitude, longitude);
                    Bike bike = new Bike(id, name, price, image, rented, location, renter_id, category_id);
                    Uri imageUri = Uri.parse(image);
                    InputStream is = null;
                    try {
                        is = getContentResolver().openInputStream(imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    imageViewBike.setImageBitmap(bitmap);
                }
            }
        }
    }
}
