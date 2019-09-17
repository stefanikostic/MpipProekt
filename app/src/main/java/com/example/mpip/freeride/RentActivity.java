package com.example.mpip.freeride;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RentActivity extends Activity
{
    Button btn1;

    ListView listView;

    Database db;
    Cursor cursor, cursorDates, cursorImages;

    TextView available, price, category, location;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);

        btn1 = findViewById(R.id.rent_offer);
        listView = findViewById(R.id.rent_listView);
        available = findViewById(R.id.listing_available);
        price = findViewById(R.id.listing_price);
        category = findViewById(R.id.listing_category);
        location = findViewById(R.id.listing_location);

        db = new Database(this);
        cursor = db.getAllOffers();
        cursorDates = db.getDates();
        cursorImages = db.getImages();

        extras = getIntent().getExtras();

        int brOglasi = cursor.getCount();

        int prices[] = new int[brOglasi];
        String longitude[] = new String[brOglasi];
        String latitude[] = new String[brOglasi];
        String address[] = new String[brOglasi];
        int idCategory[] = new int[brOglasi];
        String datesFrom[] = new String[brOglasi];
        String datesTo[] = new String[brOglasi];
        int idOglas[] = new int[brOglasi];
        String [] images = new String[cursorImages.getCount()];

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), OfferActivity.class);
                i.putExtras(extras);
                startActivity(i);
            }
        });


        for(int i = 0; i < cursor.getCount(); i++)
        {
            if(cursor != null)
            {
                if(cursor.moveToFirst())
                {
                    while(!cursor.isAfterLast())
                    {
                        latitude[i] = cursor.getString(cursor.getColumnIndex("Langitude"));
                        longitude[i] = cursor.getString(cursor.getColumnIndex("Longitude"));
                        prices[i] = cursor.getInt(cursor.getColumnIndex("Price"));
                        idCategory[i] = cursor.getInt(cursor.getColumnIndex("idK"));
                        idOglas[i] = cursor.getInt(cursor.getColumnIndex("idO"));
                    }
                }
            }
        }

        for(int i = 0; i < cursorDates.getCount(); i++)
        {
            if(cursorDates != null)
            {
                if(cursorDates.moveToFirst())
                {
                    while(!cursorDates.isAfterLast())
                    {
                        datesFrom[i] = cursorDates.getString(cursorDates.getColumnIndex("DateFrom"));
                        datesTo[i] = cursorDates.getString(cursorDates.getColumnIndex("DateTo"));
                    }
                }
            }
        }

        for(int i = 0; i<cursorImages.getCount(); i++)
        {
            if(cursorImages != null)
            {
                if(cursorImages.moveToFirst())
                {
                    while(!cursorImages.isAfterLast())
                    {
                        images[i] = cursorImages.getString(cursorImages.getColumnIndex("PathToImage"));
                    }
                }
            }
        }

        ArrayList bitmaps = new ArrayList<Bitmap>();


        for(int i = 0; i < images.length; i++)
        {
            Uri imageUri = Uri.parse(images[i]);

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

        for(int i = 0; i<latitude.length; i++)
        {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try
            {
                addresses = geocoder.getFromLocation(Double.parseDouble(latitude[i]), Double.parseDouble(longitude[i]), 1);
                address[i] = addresses.get(i).getAddressLine(0);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        String categories[] = new String[3];

        for(int i = 0; i < 3; i++)
        {
            ArrayList<String> list = db.getCategories();
            categories[i] = list.get(i);
        }

        //myAdapter adapter = new myAdapter(this, prices, address, datesFrom, datesTo, categories, bitmaps);

    }


    class myAdapter extends ArrayAdapter<String>
    {
        Context context;
        int prices[];
        String address[];
        String datesFrom[];
        String datesTo[];
        String category[];
        Bitmap [] bitmaps;
        myAdapter(Context c, int prices[], String address[], String datesFrom[], String datesTo[], String categories[], ArrayList images)
        {
            super(c, R.layout.listing, R.id.listing_available, address);
            this.context = c;
            this.address = address;
            this.datesFrom = datesFrom;
            this.datesTo = datesTo;
            this.category = categories;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View listing = layoutInflater.inflate(R.layout.listing, parent, false);
            ImageView images = listing.findViewById(R.id.listing_image);
            TextView available = listing.findViewById(R.id.listing_available);
            TextView price = listing.findViewById(R.id.listing_price);
            TextView categori = listing.findViewById(R.id.listing_category);
            TextView location = listing.findViewById(R.id.listing_location);

            images.setImageBitmap(bitmaps[position]);
            available.setText(address[position]);
            price.setText(prices[position]);
            categori.setText(category[position]);
            location.setText(address[position]);

            return listing;

        }
    }


}