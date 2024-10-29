package com.example.project;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class LocationService extends Service {

    private FusedLocationProviderClient fusedLocationClient;
    private LatLng geofenceCenter;
    private int radius;
    private SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sessionManager = new SessionManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        geofenceCenter = new LatLng(intent.getDoubleExtra("latitude", 0),
                intent.getDoubleExtra("longitude", 0));
        radius = intent.getIntExtra("radius", 0);

        startLocationUpdates();
        return START_STICKY;
    }
    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private boolean isWithinGeofence(LatLng currentLocation, LatLng geofenceCenter, int radius) {
        float[] distance = new float[1];
        Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,
                geofenceCenter.latitude, geofenceCenter.longitude, distance);
        return distance[0] <= radius;
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (!isWithinGeofence(currentLatLng, geofenceCenter, radius)) {
                    ArrayList<Contact> contacts = sessionManager.getContacts();
                    sendSmsToContacts("Outside the geofence", contacts);
                }
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
    private void sendSmsToContacts(String message, ArrayList<Contact> contacts) {
        SmsManager smsManager = SmsManager.getDefault();
        for (Contact contact : contacts) {
            smsManager.sendTextMessage(contact.getNumber(), null, message, null, null);
        }
        Toast.makeText(this, "SOS message sent via SMS", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}