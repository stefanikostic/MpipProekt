package com.example.mpip.freeride;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ClientRegisterFragment extends Fragment {

    EditText et1, et2, et3, et4, et5, et6, et7;
    Button register;

    Database db;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_client_register, container, false);
        db = new Database(this.getActivity());

        et1 = (EditText) view.findViewById(R.id.reg_email);
        et2 = (EditText) view.findViewById(R.id.reg_pass);
        et3 = (EditText) view.findViewById(R.id.reg_confirm);
        et4 = (EditText) view.findViewById(R.id.reg_ime);
        et5 = (EditText) view.findViewById(R.id.reg_surname);
        et6 = (EditText) view.findViewById(R.id.reg_tel);
        register = (Button) view.findViewById(R.id.reg_register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = et1.getText().toString();
                String s2 = et2.getText().toString();
                String s3 = et3.getText().toString();
                String s4 = et4.getText().toString();
                String s5 = et5.getText().toString();
                String s6 = et6.getText().toString();

                if(s1.equals("") || s2.equals("") || s3.equals("") || s4.equals("") || s5.equals("") || s6.equals("") || s7.equals(""))
                {
                    Toast.makeText(v.getContext(), "Please fill out all the fields.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(s2.equals(s3))
                    {
                        boolean checkm = db.checkMail(s1);
                        if(!checkm)
                        {
                            Boolean insert = db.insertUser(s2, s1, s4, s5, s6, s7);
                            if(insert)
                            {
                                Toast.makeText(v.getContext(), "Register Successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getActivity(), LoginActivity.class);
                                startActivity(i);
                            }
                        }
                        else
                            Toast.makeText(v.getContext(), "E-mail alredy exists", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(v.getContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                        et2.setText("");
                        et3.setText("");
                    }
                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

}
