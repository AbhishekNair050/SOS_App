package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class geofence_list extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_GEOFENCE = 1;
    private List<String> geofences = new ArrayList<>();
    private FenceAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.geofence_list);

        sessionManager = new SessionManager(this);
        geofences = sessionManager.getGeofences();
        if (geofences == null) {
            geofences = new ArrayList<>();
        }

        ListView listView = findViewById(R.id.listView);
        adapter = new FenceAdapter(this, geofences);
        listView.setAdapter(adapter);

        int selectedPosition = sessionManager.getSelectedGeofence();
        if (selectedPosition != -1) {
            adapter.setSelectedPosition(selectedPosition);
        }

        Button addButton = findViewById(R.id.buttonAdd);
        Button deleteButton = findViewById(R.id.buttonDelete);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(geofence_list.this, geofencing.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_GEOFENCE);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedPosition = adapter.getSelectedPosition();
                if (selectedPosition != -1 && selectedPosition < geofences.size()) {
                    geofences.remove(selectedPosition);
                    adapter.notifyDataSetChanged();
                    saveGeofences();
                }
            }
        });
    }

    // geofence_list.java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_GEOFENCE && resultCode == RESULT_OK) {
            String geofenceName = data.getStringExtra("geofenceName");
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            int radius = data.getIntExtra("radius", 0);

            if (geofenceName != null) {
                String geofenceDetails = geofenceName + " (" + latitude + ", " + longitude + ") - " + radius + "m";
                geofences.add(geofenceDetails);
                adapter.notifyDataSetChanged();
                saveGeofences();

                Intent serviceIntent = new Intent(this, LocationService.class);
                serviceIntent.putExtra("latitude", latitude);
                serviceIntent.putExtra("longitude", longitude);
                serviceIntent.putExtra("radius", radius);
                startService(serviceIntent);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sessionManager.saveSelectedGeofence(adapter.getSelectedPosition());
    }

    private void saveGeofences() {
        sessionManager.saveGeofences(new ArrayList<>(geofences));
        sessionManager.saveDataToFirebase(sessionManager.getUserEmail());
    }
}