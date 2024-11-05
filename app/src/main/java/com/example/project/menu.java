package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
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
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable blinkRunnable;
    private AudioManager audioManager;
    private int originalVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        try {
            cameraID = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startBlinking() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.siren);
            mediaPlayer.setLooping(true);
        }

        // Save the current volume and set to maximum
        originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

        mediaPlayer.start();

        blinkRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (isTorchOn) {
                        cameraManager.setTorchMode(cameraID, true);
                        handler.postDelayed(() -> {
                            try {
                                cameraManager.setTorchMode(cameraID, false);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }, 150);
                        handler.postDelayed(this, 300);
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        };
        handler.post(blinkRunnable);
    }

    private void stopBlinking() {
        handler.removeCallbacks(blinkRunnable);
        try {
            cameraManager.setTorchMode(cameraID, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Restore the original volume
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
    }

    public void toggleTorch(View view) {
        Button button = (Button) view;
        TextView textView = findViewById(R.id.torch);
        if (isTorchOn) {
            stopBlinking();
            textView.setText("");
        } else {
            startBlinking();
            textView.setText("Flashlight On");
        }
        isTorchOn = !isTorchOn;
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
    public void Medication(View view) {
        Intent intent = new Intent(this, Medication.class);
        startActivity(intent);
    }
    public void EmergencyLocation(View view) {
        Intent intent = new Intent(this, EmergencyLocation.class);
        startActivity(intent);
    }
    public void Geofencing(View view) {
        Intent intent = new Intent(this, geofence_list.class);
        startActivity(intent);
    }
}