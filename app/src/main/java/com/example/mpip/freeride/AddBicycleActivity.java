package com.example.mpip.freeride;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.*;
import com.example.mpip.freeride.domain.Location;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    
    ArrayAdapter<String> adapter;
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
        final ArrayList<String> list_categories =new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.autocomplete_textview, list_categories);


        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Categories");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size() > 0){
                        for(ParseObject object : objects){
                            list_categories.add(object.getString("name"));
                        }
                        actv.setAdapter(adapter);
                        actv.setThreshold(1);
                        actv.setTextColor(Color.parseColor("#000000"));
                    }else {
                        e.printStackTrace();
                    }
                }else {
                    e.printStackTrace();
                }
            }
        });

        imageButton = findViewById(R.id.imageButton);
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
