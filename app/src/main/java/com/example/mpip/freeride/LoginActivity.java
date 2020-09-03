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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class LoginActivity extends Activity implements View.OnClickListener{

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.constrainLayout || view.getId() == R.id.Logo){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }

    }

    private static final int REQUEST_CODE = 101 ;
    Button register;
    Button sign;


    EditText e1, e2;
    ConstraintLayout constraintLayout;
    ImageView Logo;

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

        constraintLayout = (ConstraintLayout) findViewById(R.id.constrainLayout);
        Logo = (ImageView) findViewById(R.id.Logo);

        constraintLayout.setOnClickListener(this);
        Logo.setOnClickListener(this);


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
