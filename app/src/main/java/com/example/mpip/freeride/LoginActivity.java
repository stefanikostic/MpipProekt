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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
/*
        String[] permissionArrays = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_DOCUMENTS, Manifest.permission.MEDIA_CONTENT_CONTROL};

        if(checkForPermission(this, Manifest.permission.MANAGE_DOCUMENTS)
        && checkForPermission(this, Manifest.permission.MEDIA_CONTENT_CONTROL)) {
            //Permission granted here
            //TODO
        } else {
            requestPermissions(new String[]{Manifest.permission.MANAGE_DOCUMENTS, Manifest.permission.MEDIA_CONTENT_CONTROL}, 1);
        }*/

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
    }

   /* private static boolean checkForPermission(final Context context, String permission) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        //If permission is granted then it returns 0 as result
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted here
                    //TODO

                }
                break;
        }
    }*/

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
