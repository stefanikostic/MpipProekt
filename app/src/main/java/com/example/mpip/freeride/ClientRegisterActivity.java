package com.example.mpip.freeride;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.*;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ClientRegisterActivity extends AppCompatActivity {
    EditText et1, et2, et3;
    FloatingActionButton fab;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_register);
        db = new Database(this);

        et1 = (EditText) findViewById(R.id.reg_ime);
        et2 = (EditText) findViewById(R.id.reg_surname);
        et3 = (EditText) findViewById(R.id.reg_tel);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et1.getText().toString();
                String surname = et2.getText().toString();
                String tel = et3.getText().toString();

                if(name.equals("") || surname.equals("") || tel.equals(""))
                {
                    Toast.makeText(v.getContext(), "Please fill out all the fields.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent fromIntent = getIntent();
                    String email = fromIntent.getStringExtra("email");
                    String pass = fromIntent.getStringExtra("pass");
                    Boolean insert = db.insertUser(pass, email, name, surname, tel);
                    if(insert)
                    {
                        Toast.makeText(getApplicationContext(), "Registration as client is successful!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                    }
                }
            }
        });
    }
}
