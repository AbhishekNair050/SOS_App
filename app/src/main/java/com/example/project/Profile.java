package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    private EditText emailEditText, usernameEditText;
    private TextView tvSOS, tvContacts;
    private Button buttonSave;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        EdgeToEdge.enable(this);

        sessionManager = new SessionManager(this);

        usernameEditText = findViewById(R.id.editTextname);
        emailEditText = findViewById(R.id.editTextemail);
        tvSOS = findViewById(R.id.tvSOS);
        tvContacts = findViewById(R.id.tvContacts);
        buttonSave = findViewById(R.id.buttonSave);

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

        // Load data from SessionManager
        usernameEditText.setText(sessionManager.getUsername());
        emailEditText.setText(sessionManager.getUserEmail());
        tvSOS.setText(sessionManager.getSOSMessage());
        // Assuming tvContacts is a TextView to display contacts

        ArrayList<Contact> contacts = sessionManager.getContacts();
        if (contacts != null) {
            StringBuilder contactStringBuilder = new StringBuilder();
            for (Contact contact : contacts) {
                contactStringBuilder.append(contact.getName()).append(" - ").append(contact.getNumber()).append("\n");
            }
            tvContacts.setText(contactStringBuilder.toString());
        }
        

        tvSOS.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, EditSOS.class);
            startActivity(intent);
        });

        tvContacts.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, EmergenceyContact.class);
            startActivity(intent);
        });

        buttonSave.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            sessionManager.saveUsername(username);
            sessionManager.saveDataToFirebase(sessionManager.getUserEmail());
        });
    }
}
