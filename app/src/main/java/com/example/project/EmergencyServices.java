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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main6), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListView listView = findViewById(R.id.listView);
        List<EmergencyService> services = new ArrayList<>();
        services.add(new EmergencyService("Police", "100"));
        services.add(new EmergencyService("Women Department", "1091"));
        services.add(new EmergencyService("Ambulance", "102"));
        services.add(new EmergencyService("Fire", "101"));

        EmergencyServiceAdapter adapter = new EmergencyServiceAdapter(this, services);
        listView.setAdapter(adapter);
    }
}