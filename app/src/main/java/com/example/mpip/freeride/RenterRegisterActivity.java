package com.example.mpip.freeride;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RenterRegisterActivity extends AppCompatActivity {
    EditText et1, et2, et3, et4;
    FloatingActionButton fab;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_register);
        db = new Database(this);

        et1 = (EditText) findViewById(R.id.reg_ime);
        et2 = (EditText) findViewById(R.id.reg_surname);
        et3 = (EditText) findViewById(R.id.reg_tel);
        et4 = (EditText) findViewById(R.id.store_name);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et1.getText().toString();
                String surname = et2.getText().toString();
                String tel = et3.getText().toString();
                String storeName = et4.getText().toString();

                if(name.equals("") || surname.equals("") || tel.equals(""))
                {
                    Toast.makeText(v.getContext(), "Please fill out all the fields.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                        Intent fromIntent = getIntent();
                        String email = fromIntent.getStringExtra("email");
                        String pass = fromIntent.getStringExtra("pass");
                        Toast.makeText(getApplicationContext(), "\"Let's pick your shop address...", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), RenterMapsActivity.class);
                        i.putExtra("email", email);
                        i.putExtra("pass", pass);
                        i.putExtra("name", name);
                        i.putExtra("surn", surname);
                        i.putExtra("tel", tel);
                        i.putExtra("storeName", storeName);
                        startActivity(i);
                }
            }
        });
    }
}
