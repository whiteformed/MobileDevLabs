package com.example.mobiledevlabs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ActivityLogin extends AppCompatActivity {
    TextView warningText;
    EditText inpLog;
    EditText inpPass;
    Button confirmButton;
    Context context = this;
    String country = "";
    double latitude = 0;
    double longitude = 0;

    private String getLocation(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 10);
            if (addresses.size() > 0) {
                for (Address adr : addresses) {
                    if (adr.getCountryName() != null && adr.getCountryName().length() > 0) {
                        country = adr.getCountryName();

                        break;

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return country;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        } else {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            assert locationManager != null;
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            assert location != null;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            country = getLocation(latitude, longitude);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        warningText = findViewById(R.id.warningText);
        inpLog = findViewById(R.id.inpLogin);
        inpPass = findViewById(R.id.inpPassword);
        confirmButton = findViewById(R.id.confirmButton);

        warningText.setVisibility(View.INVISIBLE);

        SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(this);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUser()) {
                    String errorMsg = "Something went wrong!";
                    warningText.setVisibility(View.VISIBLE);
                    warningText.setText(errorMsg);

                    return;
                }

                warningText.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(context, ActivityMain.class);
                intent.putExtra("login", inpLog.getText().toString());
                if (latitude != 0 && longitude != 0) {
                    intent.putExtra("country", country);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                }
                startActivity(intent);
            }
        });
    }

    private boolean validateUser() {
        String log = inpLog.getText().toString();
        String pw = inpPass.getText().toString();

        SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(this);
        if (!sqlDatabaseHelper.hasUser(new User("1", "1")))
            sqlDatabaseHelper.insertUser(new User("1", "1"));

        return (sqlDatabaseHelper.hasUser(new User(log, pw)));
    }
}
