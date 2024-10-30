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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class geofencing extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SeekBar radiusSeekBar;
    private LatLng currentLatLng;
    private Button setRadiusButton;
    private EditText geofenceNameEditText;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.geofencing);

        radiusSeekBar = findViewById(R.id.seekBar);
        setRadiusButton = findViewById(R.id.radius);
        geofenceNameEditText = findViewById(R.id.geofenceName);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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


                    // Start LocationService
                    Intent serviceIntent = new Intent(geofencing.this, LocationService.class);
                    serviceIntent.putExtra("latitude", currentLatLng.latitude);
                    serviceIntent.putExtra("longitude", currentLatLng.longitude);
                    serviceIntent.putExtra("radius", radiusSeekBar.getProgress());
                    startService(serviceIntent);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    drawCircle(currentLatLng, radiusSeekBar.getProgress());
                }
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
}