package com.oneapplab.module1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ConfirmationActivity extends AppCompatActivity implements View.OnClickListener {
    private Button accept;
    private Button decline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        accept = (Button) findViewById(R.id.acceptBtn);
        decline = (Button) findViewById(R.id.declineBtn);


        accept.setOnClickListener(this);
        decline.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if (view == accept) {
            storeLatLng();
            Toast.makeText(this, "You have accepted the order.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);

            startActivityForResult(intent, 1);
        } else if (view == decline) {

            Toast.makeText(this, "You have declined the order.", Toast.LENGTH_LONG).show();


        }


    }

    public void storeLatLng() {
        SharedPrefManager2.getInstance(getApplicationContext()).saveLatLng(56.04f,45.09f);

    }
}
