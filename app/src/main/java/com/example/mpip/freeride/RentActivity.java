package com.example.mpip.freeride;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class RentActivity extends Activity
{
    Button btn1;

    ListView listView;

    Database db;
    Cursor cursor;

    int prices[];
    String longitude[];
    String latitude[];
    int idCategoty[];
    String dates[];

    TextView available, price, category, location;

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
                        idCategoty[i] = cursor.getInt(cursor.getColumnIndex("idK"));
                    }
                }
            }
        }


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), OfferActivity.class);
                Bundle extras = getIntent().getExtras();
                i.putExtras(extras);
                startActivity(i);
            }
        });



    }
    

}