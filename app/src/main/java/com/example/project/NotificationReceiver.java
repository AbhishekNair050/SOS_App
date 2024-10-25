// app/src/main/java/com/example/project/NotificationReceiver.java
package com.example.project;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("medicineName");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "medicineChannel")
                .setSmallIcon(R.drawable.ic_medicine)
                .setContentTitle("Medicine Reminder")
                .setContentText("Time to take your medicine: " + medicineName)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}