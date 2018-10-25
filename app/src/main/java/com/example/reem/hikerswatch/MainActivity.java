package com.example.reem.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // check for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //permission not granted  // ask for it
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            //permission granted  // get location upgrades
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            //get the last known location
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            //if the last known location is not null then call the fn "updateLocation
            if (lastKnownLocation != null){
                updateLocationInfo(lastKnownLocation);
            }


        }
    }

    // whenever the user says YES or NO to a request for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }

    public void startListening(){
        // check for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //permission granted  // get location updares
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }

    public void updateLocationInfo(Location location){
        // get access to all txt views
        TextView latTextView = findViewById(R.id.latTextView);
        TextView longTextView = findViewById(R.id.longTextView);
        TextView altTextView = findViewById(R.id.altTextView);
        TextView accuracyTextView = findViewById(R.id.accuracyTextView);
        TextView addressTextView = findViewById(R.id.addressTextView);

        //update txtviews with current location info
        latTextView.setText("Lattitude: " + Double.toString(location.getLatitude()));
        longTextView.setText("Longitude: " + Double.toString(location.getLatitude()));
        altTextView.setText("Altitude: " + Double.toString(location.getAltitude()));
        accuracyTextView.setText("Accuracy: " + Double.toString(location.getAccuracy()));

        //Reverse geocoding to get the address
        String address = "Couldn't find the address!!";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            if (listAddresses != null && listAddresses.size()>0){
                address = "Address:\n";

                //get street address
                if (listAddresses.get(0).getThoroughfare() != null){
                    address += listAddresses.get(0).getThoroughfare() + "\n";
                }
                //get the city
                if (listAddresses.get(0).getLocality() != null){
                    address += listAddresses.get(0).getLocality() + ", ";
                }
                //get country
                if (listAddresses.get(0).getCountryName() != null){
                    address += listAddresses.get(0).getCountryName() + "\n";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //update the addressTextView
        addressTextView.setText(address);

    }

}
