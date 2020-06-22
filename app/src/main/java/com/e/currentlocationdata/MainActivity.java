package com.e.currentlocationdata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import static java.lang.String.format;

public class MainActivity extends AppCompatActivity {

    private TextView latTextView, lonTextView, tvAddress, tvAccuracy, tvAltitude;
    LocationManager locationManager;
    LocationListener locationListener;
    private ProgressBar indeterminateBar;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        indeterminateBar = findViewById(R.id.indeterminateBar);

        // get location
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
                tvAddress = findViewById(R.id.tvAddress);
                tvAccuracy = findViewById(R.id.tvAccuracy);
                tvAltitude = findViewById(R.id.tvAltitude);
                latTextView = findViewById(R.id.latTextView);
                lonTextView = findViewById(R.id.lonTextView);
                DecimalFormat df = new DecimalFormat("###.##");

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                    String address = "Address Not Found";
                    if(listAddress != null && listAddress.size() > 0 ){
                        if(listAddress.get(0).getAddressLine(0) != null){
                            tvAddress.setText(format("Address: \n%s ", listAddress.get(0).getAddressLine(0)));
                            indeterminateBar.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        tvAddress.setText(address);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                latTextView.setText(format("Latitude: %s", df.format(location.getLatitude())));
                lonTextView.setText(format("Longitude: %s", df.format(location.getLongitude())));
                tvAccuracy.setText(format("Accuracy: %s", df.format(location.getAccuracy())));
                tvAltitude.setText(format("Altitude: %s", df.format(location.getAltitude())));
            }// end onLocationChanged

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) { }
        };// end locationListener

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,10, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if( lastKnownLocation != null) {
                updateLocationInfo(lastKnownLocation);
            }
        }
    }// end onCreate

    @Override
    protected void onStart() {
        super.onStart();
        indeterminateBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    } // end Permission Check


    public void updateLocationInfo(Location location){
    // takes in location
    }

    public void openMaps(View view) {
        Intent mapIntent = new Intent(getApplicationContext(), MapsActivity.class);
        mapIntent.putExtra("latitude", latitude );
        mapIntent.putExtra("longitude", longitude);
        startActivity(mapIntent);
    }
}