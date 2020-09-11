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

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.domain.Location;
import com.parse.*;


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
    private TextView perHour;
    private Button changePic;
    private Context mContext;
    private EditText et;
    private EditText et2;
    Bitmap bitmap = null;
    Uri uri = null;
    String bikeId;

    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bicycle);
        image = (ImageView) findViewById(R.id.imageView);
        changePic = (Button) findViewById(R.id.changePic);
        addBike = (AppCompatButton) findViewById(R.id.btn_add_bike);
        actv = (AutoCompleteTextView) findViewById(R.id.category);
        perHour = (TextView) findViewById(R.id.perHour);
        perHour.setVisibility(View.INVISIBLE);
        et = (EditText) findViewById(R.id.price);
        et2 = (EditText) findViewById(R.id.model_name);
        final ArrayList<String> list_categories = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.autocomplete_textview, list_categories);
        Intent intent = getIntent();
        bikeId = intent.getStringExtra("id");
        final ParseQuery<ParseObject> queryCat = new ParseQuery<ParseObject>("Categories");
        queryCat.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject object : objects) {
                            list_categories.add(object.getString("name"));
                        }
                        actv.setAdapter(adapter);
                        actv.setThreshold(1);
                        actv.setTextColor(Color.parseColor("#000000"));
                    } else {
                        e.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }
            }

        });

        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Bike");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (final ParseObject object : objects) {
                            if (object.getObjectId().equals(bikeId)) {
                                queryCat.whereEqualTo("objectId", object.getString("category_id"));
                                queryCat.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> list, ParseException e) {
                                        if (e == null) {
                                            ParseObject category = list.get(0);
                                            String nameCat = category.getString("name");
                                            actv.setText(nameCat);
                                            et2.setText(object.getString("name"));
                                            int br = object.getInt("price");
                                            et.setText(String.valueOf(br));
                                            ParseFile imageFile = (ParseFile) object.get("image");
                                            try {
                                                assert imageFile != null;
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageFile.getData(), 0, imageFile.getData().length);
                                                image.setImageBitmap(bitmap);
                                                image.setVisibility(View.VISIBLE);
//                                                imageButton.setVisibility(View.INVISIBLE);
                                            } catch (ParseException ex) {
                                                ex.printStackTrace();
                                            }
                                        }
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
                if (ActivityCompat.checkSelfPermission(AddBicycleActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

            }
        });
        addBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final int price = Integer.parseInt(et.getText().toString());
                final String modelName = et2.getText().toString();
                Intent i = getIntent();
                final String email = i.getStringExtra("email");
                final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Renters");
                query.whereEqualTo("email", getIntent().getStringExtra("email"));
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
                                        final String category_id = objects.get(0).getObjectId();
                                        if (getIntent().getStringExtra("bikeExists") != null) {
                                            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Bike");
                                            query2.whereEqualTo("objectId", bikeId);
                                            query2.getFirstInBackground(new GetCallback<ParseObject>() {
                                                @Override
                                                public void done(ParseObject parseObject, ParseException e) {
                                                    parseObject.put("price", price);
                                                    parseObject.put("name", modelName);
                                                    parseObject.put("category_id", category_id);
                                                    parseObject.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if (e == null) {
                                                                Toast.makeText(getApplicationContext(), "You edited this bike successfully!", Toast.LENGTH_SHORT).show();
                                                                Intent intent1 = new Intent(AddBicycleActivity.this, RenterMainActivity.class);
                                                                intent1.putExtra("email", email);
                                                                startActivity(intent1);
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            final ParseObject object = new ParseObject("Bike");
                                            object.put("price", price);
                                            object.put("name", modelName);
                                            object.put("renter_id", id);
                                            object.put("category_id", category_id);
                                            object.put("rented", false);
                                            object.put("latitude", lat);
                                            object.put("longitude", longi);
                                            ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lat, longi);
                                            object.put("location", parseGeoPoint);
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
                                    } else{
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                perHour.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

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
