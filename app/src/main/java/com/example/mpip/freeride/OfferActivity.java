package com.example.mpip.freeride;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OfferActivity extends Activity
{

    public static final int IMAGE_PICK_CODE = 1000;
    public static final int PERMISSION_CODE = 1001;
    EditText e1, e2, e3;
    Number n1;

    RadioButton r1, r2;
    RadioGroup group;
    Button btn;

    ArrayList<Bitmap> bitmaps;

    CalendarView cv;

    Button select;
    ImageView imageView;

    Button done, showMap;

    Database db;
    Geocoder geocoder;

    String email, pass;
    String start = "", end = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        db = new Database(this);;

        Intent i = getIntent();

        email = i.getStringExtra("email");
        pass = i.getStringExtra("password");

        Toast.makeText(getApplicationContext(), email + " " + pass, Toast.LENGTH_SHORT).show();

        boolean create = db.createEmptyOffer();
        if(create)
        {
            //Toast.makeText(getApplicationContext(), "Successful making of empty offer", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //Toast.makeText(getApplicationContext(), "Error while making of empty offer", Toast.LENGTH_SHORT).show();
        }

        db.addCategories();

        select = findViewById(R.id.offer_images);

        cv = (CalendarView) findViewById(R.id.offer_calendar);

        e1 = (EditText) findViewById(R.id.offer_lokacija);
        e2 = (EditText) findViewById(R.id.offer_price);
        e3 = (EditText) findViewById(R.id.offer_year);

        r1 = (RadioButton) findViewById(R.id.offer_start);
        r2 = (RadioButton) findViewById(R.id.offer_end);
        group = (RadioGroup) findViewById(R.id.radioGroup);

        //get the spinner from the xml.
        final Spinner dropdown = findViewById(R.id.offer_spinner);
        //create a list of items for the spinner.

        ArrayList<String> items = db.getCategories();

       //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day)
            {
                int startDay = 0, startMonth = 0, startYear = 0;
                int endDay = 0, endMonth = 0, endYear = 0;

                int selected = group.getCheckedRadioButtonId();

                if(selected == r1.getId())
                {
                    start = day + "." + month + "." + year;
                    startDay = day;
                    startMonth = month;
                    startYear = year;
                }
                else if(selected == r2.getId())
                {
                    end = day + "." + month + "." + year;
                    endDay = day;
                    endMonth = month;
                    endYear = year;
                }
                else
                    Toast.makeText(getApplicationContext(), "Please select a start or end date first", Toast.LENGTH_SHORT).show();

                if(!start.equals("") && !end.equals(""))
                {
                    if(startYear <= endYear)
                    {
                        if(startMonth <= endMonth)
                        {
                            if(startDay < endDay)
                                e3.setText(start + " - " + end);
                            else
                                Toast.makeText(getApplicationContext(), "Please select an appropriate date", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Please select an appropriate date", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Please select an appropriate date", Toast.LENGTH_SHORT).show();

                }
            }
        });


        done = (Button) findViewById(R.id.offer_done);
        geocoder = new Geocoder(this, Locale.getDefault());
        showMap = findViewById(R.id.offer_showMap);

        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                String location = e1.getText().toString();
                double latitude=0;
                double longitude=0;

                try
                {
                    List<Address> addresses = geocoder.getFromLocationName(location, 1);
                    Address address = addresses.get(0);
                    if(addresses.size() > 0)
                    {
                        latitude = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                Bundle b = new Bundle();
                b.putDouble("longitude", longitude);
                b.putDouble("latitude", latitude);
                i.putExtras(b);
                startActivity(i);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), email + " " + pass, Toast.LENGTH_SHORT).show();

                int id_l = db.getLoginID(email, pass);

                String item = dropdown.getSelectedItem().toString();
                int id_k = db.getCategoryID(item);

                int cena = Integer.parseInt(e2.getText().toString());

                String location = e1.getText().toString();
                double latitude=0;
                double longitude=0;

                try
                {
                    List<Address> addresses = geocoder.getFromLocationName(location, 1);
                    Address address = addresses.get(0);
                    if(addresses.size() > 0)
                    {
                        latitude = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


                int id = db.getEmptyOfferID();

                boolean update = db.updateOffer(id, id_l, latitude, longitude, cena, id_k);
                boolean dates = db.createDates(id, start, end);

                if(update && dates)
                    Toast.makeText(getApplicationContext(), "Offer updated successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Offer NOT updated successfully", Toast.LENGTH_SHORT).show();
                
                Intent i = new Intent(getApplicationContext(), RentActivity.class);
                startActivity(i);
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(ActivityCompat.checkSelfPermission(OfferActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(OfferActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

                    return;
                }

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                i.setType("image/*");
                startActivityForResult(i, 1);

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        db = new Database(this);

        int id = db.getEmptyOfferID();

        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            bitmaps = new ArrayList<>();

            ClipData clipData = data.getClipData();

            if(clipData != null)
            {
                for(int i = 0; i < clipData.getItemCount(); i++)
                {
                    Uri imageUri = clipData.getItemAt(i).getUri();

                    boolean insert = db.insertImage(id, imageUri.toString());

                    if(insert)
                        Toast.makeText(getApplicationContext(), "Image(s) inserted successfully", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Image(s) NOT inserted successfully", Toast.LENGTH_SHORT).show();

                    try
                    {
                        InputStream is = getContentResolver().openInputStream(imageUri);

                        Bitmap bitmap = BitmapFactory.decodeStream(is);

                        bitmaps.add(bitmap);
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }


                }
            }
            else
            {
                Uri imageUri = data.getData();

                boolean insert = db.insertImage(id, imageUri.toString());

                if(insert)
                    Toast.makeText(getApplicationContext(), "Image(s) inserted successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Image(s) NOT inserted successfully", Toast.LENGTH_SHORT).show();

                try
                {
                    InputStream is = getContentResolver().openInputStream(imageUri);

                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    bitmaps.add(bitmap);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }

            }

        }

        ViewPager vp = findViewById(R.id.vp);
        AdapterPagerImageSlider imageAdapter = new AdapterPagerImageSlider(OfferActivity.this, bitmaps);
        vp.setAdapter(imageAdapter);

    }

}

class AdapterPagerImageSlider extends PagerAdapter {

    private ArrayList<Bitmap> bitmaps;
    private Context context;
    private LayoutInflater layoutInflater;

    public AdapterPagerImageSlider(Context context, ArrayList<Bitmap> bitmaps) {
        this.context = context;
        this.bitmaps= bitmaps;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.inflater_imageslider, container, false);

        ImageView imageView = view.findViewById(R.id.image);
        imageView.setImageBitmap(bitmaps.get(position)); //this set image from bitmap

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == o);
    }
}

