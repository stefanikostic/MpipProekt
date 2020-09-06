package com.example.mpip.freeride;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import com.example.mpip.freeride.domain.Location;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AddBicycleActivity extends AppCompatActivity {

    public static final int GET_FROM_GALLERY = 3;
    private ImageView image;
    private ImageButton imageButton;
    private AppCompatButton addBike;
    private AutoCompleteTextView actv;
    private Button changePic;
    private EditText et;
    private EditText et2;
    Bitmap bitmap = null;
    Uri uri = null;
    Database db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bicycle);
        Toolbar toolbar = findViewById(R.id.toolbar);
        db = new Database(this);
        image = (ImageView) findViewById(R.id.imageView);
        changePic = (Button) findViewById(R.id.changePic);
        addBike = (AppCompatButton) findViewById(R.id.btn_add_bike);
        actv = (AutoCompleteTextView) findViewById(R.id.category);
        et = (EditText) findViewById(R.id.price);
        et2 = (EditText) findViewById(R.id.model_name);
        setSupportActionBar(toolbar);
        String[] categories = (String[]) db.getCategories().toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, R.layout.autocomplete_textview, categories);
        actv.setThreshold(1);
        actv.setAdapter(adapter);
        actv.setTextColor(Color.parseColor("#000000"));
        imageButton = findViewById(R.id.imageButton);
        Intent intent=getIntent();
        int id=intent.getIntExtra("id", 0);
        int catID=intent.getIntExtra("categoryId", 0);
        String name=intent.getStringExtra("name");
        String img=intent.getStringExtra("image");
        float price=intent.getFloatExtra("price", 0);
        if(id==0 || catID==0 || name.equals("") || img.equals("") || price==0){
            imageButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if(ActivityCompat.checkSelfPermission(AddBicycleActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(AddBicycleActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

                        return;
                    }
                    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    i.setType("image/*");
                    startActivityForResult(i, 1);
                }
            });
        }
        else{
            actv.setText(db.getCategoryByID(catID));
            et2.setText(name);
            Uri uri=Uri.parse(img);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            image.setImageBitmap(bitmap);
            et.setText(String.valueOf(price));
            imageButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if(ActivityCompat.checkSelfPermission(AddBicycleActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(AddBicycleActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

                        return;
                    }
                    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    i.setType("image/*");
                    startActivityForResult(i, 1);
                }
            });
        }
        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    /*            if(ActivityCompat.checkSelfPermission(AddBicycleActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(AddBicycleActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

                    return;
                }
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                i.setType("image/*");
                startActivityForResult(i, 2);*/
            }
        });
        addBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int price = Integer.parseInt(et.getText().toString());
                Intent i = getIntent();
                String email = i.getStringExtra("email");
                int renter_id = db.getRenterId(email);
                Location location = db.getRenterLocation(email);
                double lat = location.getLatitude();
                double longi = location.getLongitude();
                String cat = actv.getText().toString();
                int category_id = db.getCategoryID(cat);
                String modelName = et2.getText().toString();
                Boolean insert = db.insertBike(0, price, modelName, renter_id, category_id, lat, longi, uri.toString());
                if(insert) {
                    Toast.makeText(view.getContext(), "You added the bike successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(AddBicycleActivity.this, RenterMainActivity.class);
                    intent1.putExtra("email", email);
                    startActivity(intent1);
                }
            }

        });

    }
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 1, outputStream);
        return outputStream.toByteArray();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if(resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            bitmap = null;
            try {

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                image.setImageBitmap(bitmap);
                image.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.INVISIBLE);
                changePic.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
