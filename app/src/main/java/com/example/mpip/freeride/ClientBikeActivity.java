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
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.domain.Location;
import com.example.mpip.freeride.fragments.TimePickerFragment;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.MONTH;

public class ClientBikeActivity extends AppCompatActivity {

    private ImageView imageViewBike;
    private static final int START_TIME_PICKER_ID = 1;
    private static final int END_TIME_PICKER_ID = 2;
    private TextView pickTimeFrom, pickTimeTo, pickDate, pickDateTo, bikeName;
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
        bikeName = (TextView) findViewById(R.id.bikeName);
        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        final int mMonth = calendar.get(MONTH);
        final int mYear = calendar.get(Calendar.YEAR);
        imageViewBike = (ImageView) findViewById(R.id.imageViewBike);
        Database db = new Database(this);
        Intent i = getIntent();
        final String id = i.getStringExtra("bikeId");
        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Bike");
        query.whereEqualTo("objectId", id);
        query.findInBackground(new FindCallback<ParseObject>() {
                                   @Override
                                   public void done(List<ParseObject> objects, ParseException e) {
                                       ParseObject o = objects.get(0);
                                       final String name = o.getString("name");
                                       final int price = o.getInt("price");
                                       final String category_id = o.getString("category_id");
                                       Double latitude = o.getDouble("latitude");
                                       Double longitude = o.getDouble("longitude");
                                       final boolean rented = o.getBoolean("category_id");
                                       final Location location = new Location(latitude, longitude);
                                       final String renter_id = o.getString("renter_id");
                                       ParseFile image = (ParseFile) o
                                               .get("image");
                                       Bitmap bmp = null;
                                       if (image != null) {
                                           try {
                                               bmp = BitmapFactory.decodeStream(image.getDataStream());
                                           } catch (ParseException ex) {
                                               ex.printStackTrace();
                                           }
                                           Bike bike = new Bike(id, name, price, bmp, rented, location, renter_id, category_id);
                                           imageViewBike.setImageBitmap(bmp);
                                           bikeName.setText(name);
                                       }

                                   }
            });
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

                    @SuppressLint("ShowToast")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, final int minute) {
                        pickTimeFrom.setText(hourOfDay + ":" + minute);
                        Toast.makeText(getApplicationContext(), "Let's add end time!", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                funkcija(hour, minute);

                            }
                        }, 1000);


                    }
                }, hour, minute, android.text.format.DateFormat.is24HourFormat(mContext));
                timePickerDialog.show();
            }
        });
        pickTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        pickTimeTo.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, android.text.format.DateFormat.is24HourFormat(mContext));
                timePickerDialog.show();*/
             funkcija(hour, minute);
            }
        });

    }

    private void funkcija(int hour, int minute) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                pickTimeTo.setText(hourOfDay + ":" + minute);
            }
        }, hour, minute, android.text.format.DateFormat.is24HourFormat(mContext));
        timePickerDialog.show();
    }


}