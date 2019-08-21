package com.example.mpip.freeride;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity
{
    Button register;
    Button sign;
    Button test;

    EditText e1, e2;

    Database db;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = (Button) findViewById(R.id.login_reg);
        sign = (Button) findViewById(R.id.login_sign);
        test = (Button) findViewById(R.id.btn_test);

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

                String s1 = "";
                s1 = e1.getText().toString();
                String s2 = "";
                s2 = e2.getText().toString();

                boolean check = db.checkLogin(s1, s2);

                if(check)
                {
                    //go to rent activity
                    Toast.makeText(getApplicationContext(), "Successful Login", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getApplicationContext(), RentActivity.class);
                    i.putExtra("email", s1);
                    i.putExtra("password", s2);
                    startActivity(i);
                }
                else
                {
                    //delete fields and show toast
                    Toast.makeText(getApplicationContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
                    e1.setText("");
                    e2.setText("");
                }
            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), testActivity.class);
                startActivity(i);
            }
        });

    }

}
