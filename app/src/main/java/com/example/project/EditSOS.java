package com.example.project;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.editsos);

        sessionManager = new SessionManager(this);
        editTextSOSMessage = findViewById(R.id.editTextSOSMessage);
        Button buttonSaveSOSMessage = findViewById(R.id.buttonSaveSOSMessage);

        // Load saved SOS message
        String savedMessage = sessionManager.getSOSMessage();
        editTextSOSMessage.setText(savedMessage);

        buttonSaveSOSMessage.setOnClickListener(v -> {
            String sosMessage = editTextSOSMessage.getText().toString();
            sessionManager.saveSOSMessage(sosMessage);
            Toast.makeText(EditSOS.this, "SOS message saved", Toast.LENGTH_SHORT).show();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main5), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}