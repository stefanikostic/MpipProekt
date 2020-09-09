package com.example.mpip.freeride;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.domain.Location;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.MONTH;

public class ClientBikeActivity extends AppCompatActivity {

    private ImageView imageViewBike;
    FloatingActionButton next, viewMap;
    private TextView pickTimeFrom, pickTimeTo, pickDate, pickDateTo, bikeName, totalPrice, billingInfo;
    Button rentHourly, rentDaily;
    private Context mContext = this;
    private int startMonth, endMonth, startDay, endDay, startHour, startMinute, endHour, endMinute;
    private double bLatitude;
    private double bLongitude;
    private int bPrice;
    ConstraintLayout cl1, chooseDateTime, constraint3;
    TimePicker timePicker;
    private TextView showTime;
    private String id, clientId;
    private Calendar date1, date2;
    int total;
    int hours;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_client_bike);
        pickTimeFrom = (TextView) findViewById(R.id.pickTimeFrom);
        pickTimeTo = (TextView) findViewById(R.id.pickTimeTo);
        pickDate = (TextView) findViewById(R.id.pickDate);
        showTime = (TextView) findViewById(R.id.time);
        rentHourly = (Button) findViewById(R.id.rentHourly);
        rentDaily = (Button) findViewById(R.id.rentDaily);
        clientId = getIntent().getStringExtra("client_id");
        cl1 = (ConstraintLayout) findViewById(R.id.constraint1);
        chooseDateTime = (ConstraintLayout) findViewById(R.id.chooseDateTime);
        pickDateTo = (TextView) findViewById(R.id.pickDateTo);
        bikeName = (TextView) findViewById(R.id.bikeName);
        totalPrice = (TextView) findViewById(R.id.totalPrice);
        constraint3 = (ConstraintLayout) findViewById(R.id.constraint3);
        next = (FloatingActionButton) findViewById(R.id.next);
        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        final int mMonth = calendar.get(MONTH);
        final int mYear = calendar.get(Calendar.YEAR);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation_view3);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_home:
                        Intent intent1 = new Intent(ClientBikeActivity.this, ClientMainActivity.class);
                        intent1.putExtra("id", clientId);
                        startActivity(intent1);
                        break;
                   case R.id.ic_bikes:
                       Intent intent2 = new Intent(ClientBikeActivity.this, RentedBikesActivity.class);
                       intent2.putExtra("client_id", clientId);
                       startActivity(intent2);
                       break;
                    case R.id.ic_exit:
                        Intent intent3 = new Intent(ClientBikeActivity.this, LoginActivity.class);
                        startActivity(intent3);
                        break;
                }


                return false;
            }
        });
        imageViewBike = (ImageView) findViewById(R.id.imageViewBike);
        totalPrice = (TextView) findViewById(R.id.totalPrice);
        billingInfo = (TextView) findViewById(R.id.billingInfo);
        viewMap = (FloatingActionButton) findViewById(R.id.view_map);
        id = getIntent().getStringExtra("bikeId");
        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Bike");
        query.whereEqualTo("objectId", id);
        query.findInBackground(new FindCallback<ParseObject>() {
                                   @Override
                                   public void done(List<ParseObject> objects, ParseException e) {
                                       ParseObject o = objects.get(0);
                                       final String name = o.getString("name");
                                       bPrice = o.getInt("price");
                                       final String category_id = o.getString("category_id");
                                       bLatitude = o.getDouble("latitude");
                                       bLongitude = o.getDouble("longitude");
                                       final boolean rented = o.getBoolean("category_id");
                                       final Location location = new Location(bLatitude, bLatitude);
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
                                           Bike bike = new Bike(id, name, bPrice, bmp, rented, location, renter_id, category_id);
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
                final DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(MONTH, month);
                        startMonth = month + 1;
                        startDay = dayOfMonth;
                        String monthName = calendar.getDisplayName(MONTH, Calendar.LONG, Locale.US);
                        int dayB  = dayOfMonth;
                        int monthB = month;
                        int yearB = year;
                        date1 = Calendar.getInstance();
                        date1.set(Calendar.YEAR, yearB);
                        date1.set(MONTH, monthB);
                        date1.set(Calendar.DAY_OF_MONTH, dayB);
                        date1.set(Calendar.HOUR_OF_DAY,2);
                        date1.set(Calendar.MINUTE,00);
                        date1.set(Calendar.SECOND,0);
                        date1.set(Calendar.MILLISECOND,0);
                        pickDate.setText(dayOfMonth + ". " + monthName);
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
                        endMonth = month + 1;
                        endDay = dayOfMonth;
                        String monthName = calendar.getDisplayName(MONTH, Calendar.LONG, Locale.US);
                        if(!pickDate.getText().toString().equals("Pick start date")) {
                            if(startDay > endDay || startMonth > endMonth) {
                                Toast.makeText(getApplicationContext(), "Invalid values of start date and end date!", Toast.LENGTH_SHORT).show();
                            } else {
                                pickDateTo.setText(dayOfMonth + ". " + monthName);
                                int total = estimatePriceDaily(startDay, startMonth, endDay, endMonth);
                                billingInfo.setText("Billing info ");
                                totalPrice.setText("Total price: " + total + " denars");
                                constraint3.setVisibility(View.VISIBLE);
                                int dayB  = view.getDayOfMonth();
                                int monthB= view.getMonth();
                                int yearB = view.getYear();
                                date2 = Calendar.getInstance();
                                date2.set(Calendar.YEAR, yearB);
                                date2.set(MONTH, monthB);
                                date2.set(Calendar.DAY_OF_MONTH, dayB);
                                date2.set(Calendar.HOUR_OF_DAY,2);
                                date2.set(Calendar.MINUTE,00);
                                date2.set(Calendar.SECOND,0);
                                date2.set(Calendar.MILLISECOND,0);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "You must enter start date!", Toast.LENGTH_SHORT).show();
                        }
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
                        startHour = hourOfDay;
                        startMinute = minute;
                        pickTimeFrom.setText(String.format("%02d", hourOfDay)+ ":" + String.format("%02d", minute));
                        Toast.makeText(getApplicationContext(), "Let's add end time!", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                funkcija(hour, minute);
                            }
                        }, 350);
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

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startHour < endHour){
                    goToNextActivity();
                } else if(startHour == endHour){
                    if(startMinute < endMinute) {
                        goToNextActivity();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid values of start time and end time!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid values of start time and end time!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                double lat = i.getDoubleExtra("latitude", 0);
                double longi = i.getDoubleExtra("longitude", 0);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+lat+","+longi+"&daddr="+bLatitude+","+bLongitude));
                startActivity(intent);
            }
        });
    }

    private void goToNextActivity() {
        Intent i = new Intent(ClientBikeActivity.this, RentedBikesActivity.class);
        ParseObject object = new ParseObject("Rents");
        object.put("client_id", clientId);
        object.put("bike_id", id);
        object.put("price", total);
        object.put("date_from", date1.getTime());
        if(pickDateTo.getVisibility()!=View.INVISIBLE)
            object.put("date_to", date2.getTime());
        object.put("hours", hours);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    ParseQuery<ParseObject> bikeObj = new ParseQuery<ParseObject>("Bike");
                    bikeObj.whereEqualTo("objectId", id);
                    bikeObj.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if(e == null) {
                                parseObject.put("rented", true);
                                parseObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(getApplicationContext(), "You rented this bike successfully!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), RentedBikesActivity.class);
                                            intent.putExtra("client_id", getIntent().getStringExtra("client_id"));
                                            startActivity(intent);
                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });

                }else {
                    Toast.makeText(getApplicationContext(), "Rentering failed! Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void funkcija(int hour, int minute) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                if(!pickTimeFrom.getText().toString().equals("Pick start date")) {
                    endHour = hourOfDay;
                    endMinute = minute;
                    if(startHour >= endHour) {
                        Toast.makeText(getApplicationContext(), "Invalid values of start time and end time!", Toast.LENGTH_SHORT).show();
                    } else {
                        pickTimeTo.setText(String.format("%02d", hourOfDay)+ ":" + String.format("%02d", minute));
                        total = estimatePriceHourly();
                        billingInfo.setText("Billing info ");
                        totalPrice.setText("Total price: " + total + " denars");
                        constraint3.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, hour, minute, android.text.format.DateFormat.is24HourFormat(mContext));
        timePickerDialog.show();
    }

    private int estimatePriceHourly() {
        int startHour = Integer.parseInt(pickTimeFrom.getText().toString().substring(0, 2));
        int endHour = Integer.parseInt(pickTimeTo.getText().toString().substring(0, 2));
        hours = endHour - startHour;
        int price = (endHour - startHour) * bPrice;
        return price;
    }

    private int estimatePriceDaily(int startDay, int startMonth, int endDay, int endMonth) {
        int monthDiff = endMonth - startMonth;
        if(monthDiff == 0){
            int dayDiff = endDay - startDay;
            hours = dayDiff * 16;
            return (dayDiff * 16) * bPrice;
        }
        return 0;
    }
}

