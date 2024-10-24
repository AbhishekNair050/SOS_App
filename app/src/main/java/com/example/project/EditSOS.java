package com.example.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditSOS extends AppCompatActivity {
    private SessionManager sessionManager;
    private EditText editTextSOSMessage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.editsos);

        sessionManager = new SessionManager(this);
        editTextSOSMessage = findViewById(R.id.editTextSOSMessage);
        Button buttonSaveSOSMessage = findViewById(R.id.buttonSaveSOSMessage);

        Button home = findViewById(R.id.buttonHome);
        Button menu = findViewById(R.id.buttonMenu);
        Button profile = findViewById(R.id.buttonProfile);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(EditSOS.this, home.class);
            startActivity(intent);
        });

        menu.setOnClickListener(v -> {
            Intent intent = new Intent(EditSOS.this, menu.class);
            startActivity(intent);
        });

        // Load saved SOS message
        String savedMessage = sessionManager.getSOSMessage();
        editTextSOSMessage.setText(savedMessage);

        buttonSaveSOSMessage.setOnClickListener(v -> {
            String sosMessage = editTextSOSMessage.getText().toString();
            sessionManager.saveSOSMessage(sosMessage);
            Toast.makeText(EditSOS.this, "SOS message saved", Toast.LENGTH_SHORT).show();
        });

    }
}