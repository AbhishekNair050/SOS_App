package com.example.project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.telephony.SmsManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;

public class SOSService extends Service {

    private FusedLocationProviderClient fusedLocationClient;
    private SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sessionManager = new SessionManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLocationAndSendSms();
        return START_NOT_STICKY;
    }

    @SuppressLint("MissingPermission")
    private void getLocationAndSendSms() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    String locationLink = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                    String sosMessage = sessionManager.getSOSMessage();
                    if (ActivityCompat.checkSelfPermission(SOSService.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    } else {
                        ArrayList<Contact> contacts = sessionManager.getContacts();
                        if (!contacts.isEmpty()) {
                            sendSmsToContacts(sosMessage + " " + locationLink, contacts);
                        }
                    }
                }
            }
        });
    }

    private void sendSmsToContacts(String message, ArrayList<Contact> contacts) {
        SmsManager smsManager = SmsManager.getDefault();
        for (Contact contact : contacts) {
            smsManager.sendTextMessage(contact.getNumber(), null, message, null, null);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
