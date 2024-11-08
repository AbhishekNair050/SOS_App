package com.example.project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {

    private static double LAT1; // Center latitude
    private static double LON1; // Center longitude
    private static double RADIUS; // Radius in meters
    private static final String PHONE_NUMBER = "9137027042"; // SMS recipient

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Handler handler;
    private Runnable smsRunnable;
    private boolean isOutsideGeofence = false;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        handler = new Handler();
        startForegroundService();
        startLocationUpdates();
    }

    @SuppressLint("ForegroundServiceType")
    private void startForegroundService() {
        String channelId = "LocationServiceChannel";
        NotificationChannel channel = new NotificationChannel(channelId, "Location Service", NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) manager.createNotificationChannel(channel);

        Intent notificationIntent = new Intent(this, home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new Notification.Builder(this, channelId)
                .setContentTitle("Location Monitoring")
                .setContentText("Monitoring location in background")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)  // Check every 5 seconds
                .setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        Geofence selectedGeofence = sessionManager.getSelectedGeofence();

        if (selectedGeofence != null) {
            LAT1 = selectedGeofence.getLatitude();
            LON1 = selectedGeofence.getLongitude();
            RADIUS = selectedGeofence.getRadius();
        } else {
            Log.e("LocationService", "No selected geofence found.");
            return;
        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    double currentLat = location.getLatitude();
                    double currentLon = location.getLongitude();
                    double distance = calculateDistance(LAT1, LON1, currentLat, currentLon);

                    if (distance > RADIUS) {
                        if (!isOutsideGeofence) {
                            isOutsideGeofence = true;
                            startSmsTask();
                        }
                    } else {
                        if (isOutsideGeofence) {
                            isOutsideGeofence = false;
                            stopSmsTask();
                        }
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // Convert to meters
    }

    private void sendSms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.i("LocationService", "SMS sent successfully.");
        } catch (Exception e) {
            Log.e("LocationService", "SMS failed to send.", e);
        }
    }

    private void startSmsTask() {
        smsRunnable = new Runnable() {
            @Override
            public void run() {
                sendSms(PHONE_NUMBER, "Alert: Moved out of geofence.");
                handler.postDelayed(this, 30000); // Schedule next SMS in 30 seconds
            }
        };
        handler.post(smsRunnable); // Start the task immediately
    }

    private void stopSmsTask() {
        if (smsRunnable != null) {
            handler.removeCallbacks(smsRunnable);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("UPDATE_GEOFENCE".equals(action)) {
                LAT1 = intent.getDoubleExtra("latitude", LAT1);
                LON1 = intent.getDoubleExtra("longitude", LON1);
                RADIUS = intent.getIntExtra("radius", (int) RADIUS);
                restartLocationUpdates(); // Update location settings
                checkInitialLocation(); // Check if the current location is outside the new geofence
                return START_STICKY;
            }
        }

        // Handle the initial call if no action is specified
        initializeGeofenceFromPreferences();
        return START_STICKY;
    }

    private void initializeGeofenceFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("GeofencePrefs", Context.MODE_PRIVATE);
        LAT1 = sharedPreferences.getFloat("latitude", 0);
        LON1 = sharedPreferences.getFloat("longitude", 0);
        RADIUS = sharedPreferences.getInt("radius", 0);

        if (LAT1 == 0 && LON1 == 0 && RADIUS == 0) {
            Log.e("LocationService", "No geofence details found.");
            stopSelf();
        } else {
            restartLocationUpdates();
            checkInitialLocation(); // Check the initial location status
        }
    }

    private void restartLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        startLocationUpdates();
    }

    private boolean isGeofenceListEmpty() {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        ArrayList<Geofence> geofences = sessionManager.getGeofences();
        return geofences == null || geofences.isEmpty();
    }

    private void checkInitialLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double currentLat = location.getLatitude();
                    double currentLon = location.getLongitude();
                    double distance = calculateDistance(LAT1, LON1, currentLat, currentLon);

                    if (distance > RADIUS) {
                        isOutsideGeofence = true;
                        startSmsTask();
                    } else {
                        isOutsideGeofence = false;
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        stopSmsTask();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}