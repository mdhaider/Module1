package com.oneapplab.module1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnSubmit;
    private EditText itemName;
    private RadioGroup businessType;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private static final String TAG = "myfirebsae";
    private TextView tv;
    String token;
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";;
    String customerType,cust;
    String userId;
    MyFirebaseMessagingService myService;
    String item;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras =getIntent().getExtras();
        cust= extras.getString("cust");

        itemName = (EditText) findViewById(R.id.et1);
        btnSubmit = (Button) findViewById(R.id.button);
        businessType = (RadioGroup) findViewById(R.id.businessGroup);
        tv = (TextView) findViewById(R.id.tv);
        btnSubmit.setOnClickListener(this);
        database = FirebaseDatabase.getInstance();

//        database.setPersistenceEnabled(true);
        id(this);



        myRef = database.getReference(cust);


    }

    public void submitRequest() {

         //get token
        token = SharedPrefManager.getInstance(this).getDeviceToken();

        //get shopName
        String buyItem = itemName.getText().toString();

        //get business type selected
       /* int selectedId = businessType.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        customerType = (String) radioButton.getText();*/

        //check token for null values
        if (token == null) {
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        //Set values to Database
        User user = new User(buyItem, token);

        userId = myRef.push().getKey();
        myRef.child(uniqueID).setValue(user);



        //Getting database value
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                item= user.buyItem;
               /* String cust=user.uniqueID;
                String toke=user.token;
              tv.setText(cust+""+" "+toke);*/

                itemName.setText("");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });

    }


    @Override
    public void onClick(View view) {
        if (view == btnSubmit) {
            submitRequest();
        }

    }
    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);

            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }

        return uniqueID;
    }

}

