package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class instructions extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.instructions);

        Button home = findViewById(R.id.buttonHome);
        Button menu = findViewById(R.id.buttonMenu);
        Button profile = findViewById(R.id.buttonProfile);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(instructions.this, home.class);
            startActivity(intent);
        });

        menu.setOnClickListener(v -> {
            Intent intent = new Intent(instructions.this, menu.class);
            startActivity(intent);
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(instructions.this, Profile.class);
            startActivity(intent);
        });


    }
}
