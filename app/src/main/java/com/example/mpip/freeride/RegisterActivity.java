package com.example.mpip.freeride;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class RegisterActivity extends Activity implements View.OnClickListener{
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.constrainLayout){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }

    }

    EditText e2, e3, e4;
    ConstraintLayout constraintLayout;
    Button signup, signup2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        e2 = (EditText) findViewById(R.id.reg_pass);
        e3 = (EditText) findViewById(R.id.reg_confirm);
        e4 = (EditText) findViewById(R.id.reg_email);
        signup = (Button) findViewById(R.id.signup);
        signup2 = (Button) findViewById(R.id.signup2);
        constraintLayout = (ConstraintLayout) findViewById(R.id.constrainLayout);
        constraintLayout.setOnClickListener(this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields("Renter");
            }
        });
        signup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields("Client");
            }
        });
    }
    public void checkFields(String act) {
        String s2 = e2.getText().toString();
        String s3 = e3.getText().toString();
        String s4 = e4.getText().toString();
        if(s2.equals("") || s3.equals("") || s4.equals(""))
        {
            Toast.makeText(getApplicationContext(), "Please fill out all the fields.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(s2.equals(s3))
            {
                boolean checkm = checkMail(s4)[0];
                if(checkm)
                {
                    Intent i = new Intent(getApplicationContext(), RenterRegisterActivity.class);
                    if(act.equals("Client"))
                        i = new Intent(getApplicationContext(), ClientRegisterActivity.class);
                    i.putExtra("email", s4);
                    i.putExtra("pass", s2);
                    startActivity(i);
                }
                else
                    Toast.makeText(getApplicationContext(), "E-mail already exists", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                e2.setText("");
                e3.setText("");
            }
        }
    }

    private boolean[] checkMail(String s4) {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Users");
        query.whereEqualTo("email", s4);
        final boolean[] flag = {true};
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null){
                    if (list.size()>0) {
                        flag[0] = false;
                    }
                }
            }
        });
        query = new ParseQuery<ParseObject>("Renters");
        query.whereEqualTo("email", s4);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null){
                    if(list.size() > 0){
                        flag[0] = false;
                    }
                }
            }
        });
        return flag;
    }
}
