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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.oneapplab.module1.R.id.et1;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private Button btnSubmit;
    private EditText itemName;
    private RadioGroup businessType;
    FirebaseDatabase database;
    DatabaseReference myRef, shopRef;
    private static final String TAG = "myfirebsae";
    private TextView tv;
    String token;
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    ;
    String customerType, cust;
    String buyItem;
    String userId;
    MyFirebaseMessagingService myService;
    String item;
    private GoogleMap map;
    double latitude;

    double longitude;
    double latitudes, longitudes;
    float lat, lng;
    String tokens;
    MyFirebaseMessagingService service;
    ArrayList<String> phoneNumbers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        latitudes = extras.getDouble("lats");
        longitudes = extras.getDouble("lngs");
        cust = extras.getString("cust");

        latitude = extras.getDouble("lat");
        longitude = extras.getDouble("lng");

        itemName = (EditText) findViewById(et1);
        btnSubmit = (Button) findViewById(R.id.button);
        businessType = (RadioGroup) findViewById(R.id.businessGroup);
        tv = (TextView) findViewById(R.id.tv);
        btnSubmit.setOnClickListener(this);
        database = FirebaseDatabase.getInstance();
        lat = SharedPrefManager2.getInstance(this).getLat();
        lng = SharedPrefManager2.getInstance(this).getLng();

//        database.setPersistenceEnabled(true);
        id(this);


        myRef = database.getReference(cust);

        //map frgament initialization
        /*try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will
                //execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
*/


    }


    public void submitRequest() {

        //get token
        token = SharedPrefManager.getInstance(this).getDeviceToken();

        //get shopName
        buyItem = itemName.getText().toString();

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

        shopRef = database.getReference().child("Shop");

        shopRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectPhoneNumbers((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });


        //Getting database value
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                item = user.buyItem;
                tokens = user.token;
               /* String cust=user.uniqueID;
                String toke=user.token;*/
                // tv.setText(key+""+" "+tokens);

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(lat, lng);
        map.addMarker(new MarkerOptions().position(sydney).title("Shop Store"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    }

    private void collectPhoneNumbers(Map<String, Object> token) {

        phoneNumbers = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : token.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            phoneNumbers.add((String) singleUser.get("token"));
        }

        tv.setText(phoneNumbers.toString());
        sendPost();
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    Call post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "key=AAAAOXj52qY:APA91bEVrdrskWzd9tqgeTvWkS7-_YkEPpHEVoIusAlVsjiKpPTRPedf2HWeM27KwhFIBIJRR-zoAzfPvjgjuLkJlyxC9olU2r8DrKAPNFnEcGTr__cNJn05t472apySZLekP2E2kcOK")
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;

    }

    public void sendPost() {

        for (String name : phoneNumbers) {
            /*if(phoneNumbers.equals(token)){
                return;
            }*/
            try {
                JSONObject jsonObject = new JSONObject();
                JSONObject param = new JSONObject();
                jsonObject.put("to", name);
                param.put("body", buyItem);
                jsonObject.put("notification", param);
                post("https://fcm.googleapis.com/fcm/send", jsonObject.toString(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("Error", e.toString());
                                //Something went wrong
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    String responseStr = response.body().string();
                                    Log.d("Response", responseStr);
                                    // Do what you want to do with the response.
                                } else {
                                    // Request not successful
                                }
                            }
                        }
                );
            } catch (JSONException ex) {
                Log.d("Exception", "JSON exception", ex);
            }
        }
    }
}