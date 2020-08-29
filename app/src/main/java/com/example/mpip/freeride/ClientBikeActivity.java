package com.example.mpip.freeride;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import com.example.mpip.freeride.domain.Bike;
import com.example.mpip.freeride.domain.Location;
import com.example.mpip.freeride.fragments.TimePickerFragment;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Time;
import java.util.Calendar;

public class ClientBikeActivity extends AppCompatActivity {

    private ImageView imageViewBike;
    private static final int START_TIME_PICKER_ID = 1;
    private static final int END_TIME_PICKER_ID = 2;
    Button button;
    Context mContext = this;
    TimePicker timePicker;
    TextView showTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_bike);
        button = (Button) findViewById(R.id.button);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        showTime = (TextView) findViewById(R.id.time);
        Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        showTime.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, android.text.format.DateFormat.is24HourFormat(mContext));
                timePickerDialog.show();
            }
        });
    }


}