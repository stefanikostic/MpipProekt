package com.example.mpip.freeride;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.domain.Location;
import com.example.mpip.freeride.fragments.TimePickerFragment;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Time;
import java.util.Calendar;
import java.util.Locale;

import static java.util.Calendar.MONTH;

public class ClientBikeActivity extends AppCompatActivity {

    private ImageView imageViewBike;
    private static final int START_TIME_PICKER_ID = 1;
    private static final int END_TIME_PICKER_ID = 2;
    private TextView pickTimeFrom, pickTimeTo, pickDate, pickDateTo;
    Button rentHourly, rentDaily;
    private Context mContext = this;
    ConstraintLayout cl1, chooseDateTime;
    TimePicker timePicker;
    private TextView showTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_bike);
        pickTimeFrom = (TextView) findViewById(R.id.pickTimeFrom);
        pickTimeTo = (TextView) findViewById(R.id.pickTimeTo);
        pickDate = (TextView) findViewById(R.id.pickDate);
        showTime = (TextView) findViewById(R.id.time);
        rentHourly = (Button) findViewById(R.id.rentHourly);
        rentDaily = (Button) findViewById(R.id.rentDaily);
        cl1 = (ConstraintLayout) findViewById(R.id.constraint1);
        chooseDateTime = (ConstraintLayout) findViewById(R.id.chooseDateTime);
        pickDateTo = (TextView) findViewById(R.id.pickDateTo);
        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        final int mMonth = calendar.get(MONTH);
        final int mYear = calendar.get(Calendar.YEAR);
        imageViewBike = (ImageView) findViewById(R.id.imageViewBike);
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
        rentHourly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cl1.setVisibility(View.INVISIBLE);
                chooseDateTime.setVisibility(View.VISIBLE);
            }
        });
        rentDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cl1.setVisibility(View.INVISIBLE);
                pickTimeFrom.setVisibility(View.INVISIBLE);
                pickTimeTo.setVisibility(View.INVISIBLE);
                pickDate.setVisibility(View.VISIBLE);
                pickDateTo.setVisibility(View.VISIBLE);
                chooseDateTime.setVisibility(View.VISIBLE);
            }
        });

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(MONTH, month);
                        String monthName = calendar.getDisplayName(MONTH, Calendar.LONG, Locale.US);
                        pickDate.setText(dayOfMonth + "." + monthName);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        pickDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(MONTH, month);
                        String monthName = calendar.getDisplayName(MONTH, Calendar.LONG, Locale.US);
                        pickDateTo.setText(dayOfMonth + "." + monthName);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        pickTimeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        pickTimeFrom.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, android.text.format.DateFormat.is24HourFormat(mContext));
                timePickerDialog.show();
            }
        });
        pickTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        pickTimeTo.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, android.text.format.DateFormat.is24HourFormat(mContext));
                timePickerDialog.show();
            }
        });
    }


}