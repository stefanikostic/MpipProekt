package com.example.mpip.freeride;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity
{

    EditText e2, e3, e4, e5, e6;
    Button register;

    Database db;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new Database(this);

        e2 = (EditText) findViewById(R.id.reg_pass);
        e3 = (EditText) findViewById(R.id.reg_confirm);
        e4 = (EditText) findViewById(R.id.reg_email);
        e5 = (EditText) findViewById(R.id.reg_ime);
        e6 = (EditText) findViewById(R.id.reg_surname);

        register = (Button) findViewById(R.id.reg_register);

      /*  register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s2 = e2.getText().toString();
                String s3 = e3.getText().toString();
                String s4 = e4.getText().toString();
                String s5 = e5.getText().toString();
                String s6 = e6.getText().toString();

                if(s2.equals("") || s3.equals("") || s4.equals("") || s5.equals("") || s6.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please fill out all the fields.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(s2.equals(s3))
                    {
                        boolean checkm = db.checkMail(s4);
                        if(!checkm)
                        {
                            Boolean insert = db.insertUser(s2, s4, s5, s6);
                            if(insert)
                            {
                                Toast.makeText(getApplicationContext(), "Register Successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                            }
                        }
                        else
                            Toast.makeText(getApplicationContext(), "E-mail alredy exists", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                        e2.setText("");
                        e3.setText("");
                    }
                }
            }
        });*/
    }
}
