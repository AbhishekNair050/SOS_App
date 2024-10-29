package com.example.project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private SeekBar radiusSeekBar;
    private LatLng currentLatLng;
    private Button setRadiusButton;
    private EditText geofenceNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geofencing);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        radiusSeekBar = findViewById(R.id.seekBar);
        setRadiusButton = findViewById(R.id.radius);
        geofenceNameEditText = findViewById(R.id.geofenceName);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (currentLatLng != null) {
                    drawCircle(currentLatLng, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        setRadiusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLatLng != null) {
                    String geofenceName = geofenceNameEditText.getText().toString();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("geofenceName", geofenceName);
                    resultIntent.putExtra("latitude", currentLatLng.latitude);
                    resultIntent.putExtra("longitude", currentLatLng.longitude);
                    resultIntent.putExtra("radius", radiusSeekBar.getProgress());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        drawCircle(currentLatLng, radiusSeekBar.getProgress());
                    }
                });
    }

    private void drawCircle(LatLng latLng, int radius) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
        mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeWidth(0f)
                .fillColor(0x550000FF));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    public boolean isWithinGeofence(LatLng currentLocation, LatLng geofenceCenter, int radius) {
        float[] distance = new float[1];
        Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,
                geofenceCenter.latitude, geofenceCenter.longitude, distance);
        return distance[0] <= radius;
    }
}