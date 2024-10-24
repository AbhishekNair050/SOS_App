package com.example.project;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class EmergencyServices extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.emergencyservices);

        ListView listView = findViewById(R.id.listView);
        List<EmergencyService> services = new ArrayList<>();
        services.add(new EmergencyService("Police", "100"));
        services.add(new EmergencyService("Women Department", "1091"));
        services.add(new EmergencyService("Ambulance", "102"));
        services.add(new EmergencyService("Fire", "101"));
        services.add(new EmergencyService("Child Helpline", "1098"));
        services.add(new EmergencyService("Senior Citizen Helpline", "1090"));
        services.add(new EmergencyService("Disaster Management", "108"));
        services.add(new EmergencyService("Railway Enquiry", "139"));
        services.add(new EmergencyService("Road Accident Emergency Service", "1073"));

        EmergencyServiceAdapter adapter = new EmergencyServiceAdapter(this, services);
        listView.setAdapter(adapter);
    }
}