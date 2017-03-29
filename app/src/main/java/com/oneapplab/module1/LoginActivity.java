package com.oneapplab.module1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class LoginActivity extends AppCompatActivity  {

    private RadioGroup businessType;
    String customerType;
   private  Button accepBtn;
    TrackGPS gps;
    Double latitude;
    Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accepBtn= (Button) findViewById(R.id.accepBtn);


        businessType = (RadioGroup) findViewById(R.id.businessGroup);


        accepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLatLng();
                goToMain();
            }
        });

    }


    public void goToMain(){

        int selectedId = businessType.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        customerType = (String) radioButton.getText();

        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("cust",customerType);
        intent.putExtra("lats",34.89);
        intent.putExtra("lngs",23.90);
        startActivity(intent);
    }
    public void showLatLng(){

                // create class object
                gps = new TrackGPS(LoginActivity.this);


                // check if GPS enabled
                if(gps.canGetLocation()){

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();





                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }

    }





