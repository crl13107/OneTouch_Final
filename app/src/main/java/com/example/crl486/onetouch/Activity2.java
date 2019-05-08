package com.example.crl486.onetouch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.widget.Toast.LENGTH_LONG;


public class Activity2 extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {

    //FusedLocation to create client and access built in methods
    FusedLocationProviderClient client;
    TextView textView;
    SettingsClient clientTwo;

    FusedLocationProviderClient mfusedlocationclient;
    private LocationRequest locationRequest;

    //connects XML textViews
    private TextView latitude;
    private TextView longitude;
    //Permissions to ask again is user originally said no
    private static final int RequestPermisionCode = 1;
    private static final int REQUEST_CALL = 1; //used to identify permission request.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //When view is first created, uses activity 2 XML
        //ping and phone call start calls on click
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        pingStart();
        phoneCallStart();
    }

    //creates the location request and sets intervals in between how often it refreshes
    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(50);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //starts when SoS picture is clicked
    private void pingStart() {
        createLocationRequest(); //sends request for listener to get location
        //asks for permissions if not previously given
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
        client = LocationServices.getFusedLocationProviderClient(this);
        //Starts listener to recieve location on Click
        ImageView ping = findViewById(R.id.image_Ping);
        ping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(Activity2.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                //Assigns Location to text views and displays for user, won't be showed in actual industry use
                client.getLastLocation().addOnSuccessListener(Activity2.this, new OnSuccessListener<Location>() {
                    @Override

                    public void onSuccess(Location location) {
                        if (location != null) {
                            textView = findViewById(R.id.latitude_text);
                            Double b = location.getLatitude();
                            textView.setText("" + b);
                            textView = findViewById(R.id.longitude_text);
                            Double a = location.getLongitude();
                            textView.setText("" + a);
                            if ((b>41.1 && b<41.25) && (a>-77.5 && a<-77.4)) { //**Checks to make sure the are on campus, or toasts them instead of sending ping
                                createReport(a.toString(), b.toString());
                                Toast toast = Toast.makeText(getApplicationContext(), "Police are now on the way", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            } else {
                                Toast.makeText(Activity2.this, "You aren't in lock haven!", LENGTH_LONG).show();
                            }
                        } else {
                            //location service must be turned on for ~5s, or will produce an error with null location
                            Toast.makeText(Activity2.this, "Null location, please turn on location", LENGTH_LONG).show();

                        }
                    }
                });
            }
        });

    }

    //create report in database when it is called
    private void createReport(String a,String b){
        String type = "register";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type,"joe",a,b);
    }

    //create phone call when picture clicked
    private void phoneCallStart() {
        ImageView imageCall = findViewById(R.id.image_Call); //makes the image create phone call
        imageCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
    }
    //request phone call permission if originally declined
    private void requestPhoneCallPermissions() {
        ActivityCompat.requestPermissions(Activity2.this,
                new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
    }

    //Make phone call
    private void makePhoneCall() {
        if (ContextCompat.checkSelfPermission(Activity2.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Activity2.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            //tell:phonenumber required for  phoneCall to go through. Uses personal phone number
            //since you don't want to be calling public safety everytime you call it
            String phoneNumber = "tel:18148762523";
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber)));
        }

    }

    //Remember if user gives permissions to make phone calls
    //Toast denies if user has said no on app start and on use
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ;
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //request Location and phone call permission toegther
    private void requestPermission() {
        ActivityCompat.requestPermissions(Activity2.this,
                new String[]{ACCESS_FINE_LOCATION}, REQUEST_CALL);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
    }


}