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
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;
import java.util.Objects;

public class LoginActivity extends Activity implements View.OnClickListener{

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.constrainLayout || view.getId() == R.id.Logo){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),0);
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

                boolean flag = true;
                s1 = e1.getText().toString();
                s2 = e2.getText().toString();


                    final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Renters");
                    query.whereEqualTo("email",e1.getText().toString());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if(e == null){
                                if(objects.size()>0){
                                 for(ParseObject object : objects){
                                     if(object.getString("password").matches(e2.getText().toString())){
                                         goToNextActivity(2);
                                         Toast.makeText(getApplicationContext(),"SUCCESS", Toast.LENGTH_SHORT).show();

                                     }else {
                                         Toast.makeText(getApplicationContext(),"Incorrect password or username", Toast.LENGTH_SHORT).show();

                                     }

                                 }

                                }else {
                                    final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Users");
                                    query.whereEqualTo("email", e1.getText().toString());
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            if(e == null){
                                                if(objects.size() > 0 ){
                                                    for(ParseObject object : objects){
                                                        if(object.getString("password").matches(e2.getText().toString())){
                                                            goToNextActivity(1);
                                                            Toast.makeText(getApplicationContext(),"SUCCESS", Toast.LENGTH_SHORT).show();

                                                        }else {
                                                            Toast.makeText(getApplicationContext(),"Incorrect password or username", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                }
                                                else {
                                                    Toast.makeText(getApplicationContext(),"Incorrect password or username", Toast.LENGTH_SHORT).show();

                                                }
                                            }else {
                                                Toast.makeText(getApplicationContext(),"Incorrect password or username", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }else {




                            }
                        }
                    });

                }

        });

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    private void tryInRenters() {

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Renters");
        query.whereEqualTo("email",e1.getText().toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects.size()>0){
                        for(ParseObject object : objects){
                            if(object.getString("password").matches(e2.getText().toString())){
                                goToNextActivity(2);
                                Toast.makeText(getApplicationContext(),"SUCCESS", Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(getApplicationContext(),"Incorrect password or username", Toast.LENGTH_SHORT).show();

                            }

                        }

                    }else {
                        e.printStackTrace();
                    }
                }else {
                    e.printStackTrace();
                }
            }
        });
    }


    public void goToNextActivity(int c) {
        Intent i = new Intent(LoginActivity.this, ClientMainActivity.class);
         if(c==2){
             i = new Intent(LoginActivity.this, RenterMainActivity.class);
         }
        //Toast.makeText(getApplicationContext(), "Successful Login", Toast.LENGTH_SHORT).show();
        Bundle extras = new Bundle();
        extras.putString("email", s1);
        extras.putString("password", s2);
        i.putExtras(extras);
        startActivity(i);
    }


}
