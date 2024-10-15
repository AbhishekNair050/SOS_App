package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class menu extends AppCompatActivity {
    private CameraManager cameraManager;
    private String cameraID;
    private boolean isTorchOn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraID = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main3), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void toggleTorch(View view) {
        Button button = (Button) view;
        try {
            if (isTorchOn) {
                cameraManager.setTorchMode(cameraID, false); // Turn off
                button.setText("Flashlight");
            } else {
                cameraManager.setTorchMode(cameraID, true); // Turn on
                button.setText("Flashlight On");
            }
            isTorchOn = !isTorchOn; // Flip the state
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    public void instructions(View view) {
        Intent intent = new Intent(this, instructions.class);
        startActivity(intent);
    }
    public void Editsos(View view) {
        Intent intent = new Intent(this, EditSOS.class);
        startActivity(intent);
    }
    public void EmergencyServices(View view) {
        Intent intent = new Intent(this, EmergencyServices.class);
        startActivity(intent);
    }
    public void EmergencyContacts(View view) {
        Intent intent = new Intent(this, EmergenceyContact.class);
        startActivity(intent);
    }
}
