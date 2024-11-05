package com.example.project;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.widget.RemoteViews;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;

public class SOSWidgetProvider extends AppWidgetProvider {

    private FusedLocationProviderClient fusedLocationClient;
    private SessionManager sessionManager;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, SOSWidgetProvider.class);
            intent.setAction("com.example.project.SOS_ACTION");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sos_widget);
            views.setOnClickPendingIntent(R.id.sosButtonWidget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if ("com.example.project.SOS_ACTION".equals(intent.getAction())) {
            sessionManager = new SessionManager(context);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();
                        String locationLink = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                        String sosMessage = sessionManager.getSOSMessage();

                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        ArrayList<Contact> contacts = sessionManager.getContacts();
                        if (!contacts.isEmpty()) {
                            sendSmsToContacts(context, sosMessage + " " + locationLink, contacts);
                            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            if (vibrator != null && vibrator.hasVibrator()) {
                                vibrator.vibrate(2000); // Vibrate for 2000 milliseconds (2 seconds)
                            }
                        }
                    }
                }
            });
        }
    }

    private void sendSmsToContacts(Context context, String message, ArrayList<Contact> contacts) {
        SmsManager smsManager = SmsManager.getDefault();
        for (Contact contact : contacts) {
            smsManager.sendTextMessage(contact.getNumber(), null, message, null, null);
        }
    }
}
