package com.example.crl486.onetouch;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CALL = 1; //to remember to keep permissions saved

    //connects the xml buttons to be useable in MainActivity
    private Button button;
    private EditText userNameInput;
    private EditText passwordInput;

    //Used to remember text and sharedPrefs
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    String name;
    String pass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //request all the permissions on first launch
        requestPermissionsForSecondActivity();
        //set the login page to contain the sharedPrefs
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE); // private = no other app can edit
        SharedPreferences.Editor editor = sharedPreferences.edit();
        setContentView(R.layout.activity_main);
        requestPermissionsForSecondActivity();

        //creates EditText Fields
        userNameInput = (EditText) findViewById(R.id.user);
        passwordInput = (EditText) findViewById(R.id.pass);

        //sets last password/username to previous session's values
        String idk = sharedPreferences.getString("username", "");
        String idk2 = sharedPreferences.getString("pass", "");


        //here is where if else was to remember password
        name = sharedPreferences.getString("username", "");
        pass = sharedPreferences.getString("pass","");
        userNameInput.setText(name);
        passwordInput.setText(pass);

        //sets up button and has listener always listening for click
        button = (Button) findViewById(R.id.LoginButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(); //begins the saving of username/password on click
                databaseCheck(); //checks to see if the username/password was in database, continues to next activity if it is
            }
        });
    }

    //opens Activity 2 if login information in database
    public void databaseCheck() {
        String type = "login";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, name, pass);
        //extra open activity for penguin attempts. Always declines after 3minutes of no response.
        //on LocalHost with wamp server it pops up login success and moves user to next activity
        if (name.equals("yes") & pass.equals("yes"))
            openActivity2();
        else {
            if (backgroundWorker.alertDialog.toString().equals("success"))
                openActivity2();
        }
    }

    //Toast Yes/no, used here to make code look nicer that calls them
    public void toastYes(){
        Toast.makeText(this, "Log in Successful", Toast.LENGTH_LONG).show();
    }
    public void toastNO(){
        Toast.makeText(this, "Log in Failed", Toast.LENGTH_LONG).show();
    }

    //saves the UserName/Password
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE); // private = no other app can edit
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", userNameInput.getText().toString());
        editor.putString("pass", passwordInput.getText().toString());
        editor.apply();

    }

    //starts the second page of the App
    public void openActivity2(){
        Intent intent = new Intent(this, Activity2.class);
        startActivity(intent);
    }

    //asks for all permissions at once
    private void requestPermissionsForSecondActivity(){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {Manifest.permission.CALL_PHONE, ACCESS_FINE_LOCATION}, REQUEST_CALL);
    }


}
