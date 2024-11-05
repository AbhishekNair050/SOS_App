// app/src/main/java/com/example/project/Medication.java
package com.example.project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Medication extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private SessionManager sessionManager;
    private ArrayList<Medicine> medicines;
    private MedicineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.medication);

        Button home = findViewById(R.id.buttonHome);
        Button menu = findViewById(R.id.buttonMenu);
        Button profile = findViewById(R.id.buttonProfile);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, home.class);
            startActivity(intent);
        });

        menu.setOnClickListener(v -> {
            Intent intent = new Intent(this, menu.class);
            startActivity(intent);
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
        });

        sessionManager = new SessionManager(this);
        medicines = sessionManager.getMedicines();
        if (medicines == null) {
            medicines = new ArrayList<>();
        }

        ListView medicineListView = findViewById(R.id.medicineListView);
        adapter = new MedicineAdapter(this, medicines);
        medicineListView.setAdapter(adapter);

        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.SCHEDULE_EXACT_ALARM
            }, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Medication", "Permissions granted");
            } else {
                Log.d("Medication", "Permissions denied");
            }
        }
    }

    public void showAddMedicineDialog(View view) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_medicine);

        EditText medicineName = dialog.findViewById(R.id.medicineName);
        EditText dosage = dialog.findViewById(R.id.dosage);
        EditText timesPerDay = dialog.findViewById(R.id.timesPerDay);
        EditText timings = dialog.findViewById(R.id.timings);
        Button addMedicineButton = dialog.findViewById(R.id.addMedicineButton);

        addMedicineButton.setOnClickListener(v -> {
            String name = medicineName.getText().toString();
            String dose = dosage.getText().toString();
            int times = Integer.parseInt(timesPerDay.getText().toString());
            ArrayList<String> timingList = new ArrayList<>(Arrays.asList(timings.getText().toString().split(",")));

            Medicine medicine = new Medicine();
            medicine.setName(name);
            medicine.setDosage(dose);
            medicine.setTimesPerDay(times);
            medicine.setTimings(timingList);

            medicines.add(medicine);
            sessionManager.saveMedicines(medicines);

            scheduleMedicineNotifications(this, medicine);
            adapter.notifyDataSetChanged();

            dialog.dismiss();
        });

        dialog.show();
    }

    @SuppressLint("ScheduleExactAlarm")
    public void scheduleMedicineNotifications(@NonNull Context context, @NonNull Medicine medicine) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("medicineName", medicine.getName());

        for (String time : medicine.getTimings()) {
            String[] timeParts = time.split(":");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            Log.d("Medication", "Scheduled notification for " + medicine.getName() + " at " + calendar.getTime());
        }
    }
}