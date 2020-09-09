package com.example.mpip.freeride;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.mpip.freeride.domain.Location;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;


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
    private Context mContext;
    private EditText et;
    private EditText et2;
    Bitmap bitmap = null;
    Uri uri = null;
    Database db;
    Intent intent=getIntent();
    String id=intent.getStringExtra("id");

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
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
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

        query=new ParseQuery<ParseObject>("Bike");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size()>0){
                        for(ParseObject object : objects){
                            if(object.getObjectId().equals(id)){
                                actv.setText(object.getObjectId());
                                et2.setText(object.getString("name"));
                                int br= (int) object.getNumber("price");
                                et.setText(br);
                                ParseFile imageFile = (ParseFile) object.get("image");
                                imageFile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        image.setImageBitmap(bitmap);
                                    }
                                });
                            }
                        }
                    }
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
            public void onClick(final View view) {
                final int price = Integer.parseInt(et.getText().toString());
                Intent i = getIntent();
                final String email = i.getStringExtra("email");
                final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Renters");
                query.whereEqualTo("email",getIntent().getStringExtra("email"));
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, com.parse.ParseException e) {
                        if (e == null) {
                            final String id = objects.get(0).getObjectId();
                            final double lat = objects.get(0).getDouble("latitude");
                            final double longi = objects.get(0).getDouble("longitude");
                            final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Categories");
                            query.whereEqualTo("name", actv.getText().toString());
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, com.parse.ParseException e) {
                                    if (e == null) {
                                        String category_id = objects.get(0).getObjectId();
                                        String modelName = et2.getText().toString();
                                        ParseObject object = new ParseObject("Bike");
                                        object.put("price", price);
                                        object.put("name", modelName);
                                        object.put("renter_id", id);
                                        object.put("category_id", category_id);
                                        object.put("rented", false);
                                        object.put("latitude", lat);
                                        object.put("longitude", longi);
                                        try {
                                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            final byte[] byteArray = stream.toByteArray();
                                            final ParseFile file = new ParseFile("image", byteArray);
                                            object.put("image", file);
                                            object.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(com.parse.ParseException e) {
                                                    if (e == null) {
                                                        Toast.makeText(getApplicationContext(), "You added the bike successfully!", Toast.LENGTH_SHORT).show();
                                                        Intent intent1 = new Intent(AddBicycleActivity.this, RenterMainActivity.class);
                                                        intent1.putExtra("email", email);
                                                        startActivity(intent1);
                                                    }
                                                }

                                            });

                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }

                            });

                        } else {
                            e.printStackTrace();
                        }
                    }

                });



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
