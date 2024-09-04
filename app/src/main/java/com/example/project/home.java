// app/src/main/java/com/example/project/home.java
package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class home extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.homepage);

        sessionManager = new SessionManager(this);

        String email = getIntent().getStringExtra("email");
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String userName = dbHelper.getUserNameByEmail(email);

        TextView usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText("Welcome " + userName);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void menu(View view) {
        Intent intent = new Intent(this, menu.class);
        startActivity(intent);
    }

    public void logout(View view) {
        sessionManager.logoutUser();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}