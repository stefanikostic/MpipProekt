package com.example.mpip.freeride;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

public class OfferActivity extends Activity
{

    public static final int IMAGE_PICK_CODE = 1000;
    public static final int PERMISSION_CODE = 1001;
    EditText e1, e2, e3;
    Number n1;

    RadioButton r1, r2;
    Button btn;

    CalendarView cv;

    Button select = (Button) findViewById(R.id.offer_images);
    ImageView imageView;

    Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        cv = (CalendarView) findViewById(R.id.offer_calendar);

        e1 = (EditText) findViewById(R.id.offer_lokacija);
        e2 = (EditText) findViewById(R.id.offer_model);
        e3 = (EditText) findViewById(R.id.offer_year);

        r1 = (RadioButton) findViewById(R.id.offer_start);
        r2 = (RadioButton) findViewById(R.id.offer_end);

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            String start, end;
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day)
            {
                if(r1.isSelected())
                {
                    start = day + ":" + month + ":" + year;
                }
                else if(r2.isSelected())
                {
                    end = day + ":" + month + ":" + year;
                }
                e3.setText(start + "-" + end);
            }
        });





        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED)
                    {
                        //permission not granted, need to request it
                        String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //shows popup for runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else
                    {
                        //permission already granted
                        pickImage();
                    }
                }
                else
                {
                    //system os is less than marshmallow
                    pickImage();
                }
            }
        });

        done = (Button) findViewById(R.id.offer_done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void pickImage()
    {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE)
        {
            //set image to image view
            imageView = (ImageView) findViewById(R.id.offer_iv);
            imageView.setImageURI(data.getData());
        }
    }

    @Override
    //handle result of runtime permission
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //permission was granted
                    pickImage();
                }
                else
                {
                    //permission was denied
                    Toast.makeText(getApplicationContext(), "Permission denied",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
