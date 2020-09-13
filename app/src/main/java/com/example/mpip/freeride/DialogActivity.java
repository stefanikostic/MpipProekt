package com.example.mpip.freeride;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DialogActivity extends AppCompatActivity {
    Button bmx, beachCruiser, commutingBike, roadBike, comfortBike, mountainBike;
    String id1="Sbi8xvMOTB";
    String id2="OmHHww0vDq";
    String id3="kyimtWwlTq";
    String id4="SupXrlr0Mv";
    String id5="aoKHGh6oF5";
    String id6="PuoiQal9ed";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity);
        bmx=(Button)findViewById(R.id.button);
        beachCruiser=(Button)findViewById(R.id.button3);
        commutingBike=(Button)findViewById(R.id.button4);
        roadBike=(Button)findViewById(R.id.button5);
        comfortBike=(Button)findViewById(R.id.button6);
        mountainBike=(Button)findViewById(R.id.button7);
    }
}
