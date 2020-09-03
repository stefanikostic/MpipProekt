package com.example.mpip.freeride;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class LoginActivity extends Activity
{
    private static final int REQUEST_CODE = 101 ;
    Button register;
    Button sign;


    EditText e1, e2;

    Database db;

    Handler handler = new Handler();
    String s1 = "";
    String s2 = "";
    int check;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Activity acc = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register = (Button) findViewById(R.id.login_reg);
        sign = (Button) findViewById(R.id.login_sign);


        e1 = (EditText) findViewById(R.id.login_email);
        e2 = (EditText) findViewById(R.id.login_pass);

        db = new Database(this);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                s1 = e1.getText().toString();
                s2 = e2.getText().toString();

                int check = db.checkLogin(s1, s2);

                if(check==0)
                {
                    //delete fields and show toast
                    Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
                    e1.setText("");
                    e2.setText("");
                }
                else
                {
                    goToNextActivity(check);
                }
            }
        });
        ParseObject object = new ParseObject("Stefani");
        object.put("proba","andreja");
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.i("Save", "Yes");
                }else {
                    Log.i("Save","No");
                }
            }
        });

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }


    public void goToNextActivity(int c) {
        if (c == 1) {
            Intent i = new Intent(LoginActivity.this, ClientMainActivity.class);
            //go to rent activity
            //Toast.makeText(getApplicationContext(), "Successful Login", Toast.LENGTH_SHORT).show();
            Bundle extras = new Bundle();
            extras.putString("email", s1);
            extras.putString("password", s2);
            i.putExtras(extras);
            startActivity(i);
        } else if(c==2){
                Intent i = new Intent(LoginActivity.this, RenterMainActivity.class);
                //go to rent activity
                //Toast.makeText(getApplicationContext(), "Successful Login", Toast.LENGTH_SHORT).show();
                Bundle extras = new Bundle();
                extras.putString("email", s1);
                extras.putString("password", s2);
                i.putExtras(extras);
                startActivity(i);
            }
    }
}
