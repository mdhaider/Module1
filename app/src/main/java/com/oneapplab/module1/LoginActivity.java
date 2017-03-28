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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accepBtn= (Button) findViewById(R.id.accepBtn);


        businessType = (RadioGroup) findViewById(R.id.businessGroup);


        accepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        startActivity(intent);
    }


}
