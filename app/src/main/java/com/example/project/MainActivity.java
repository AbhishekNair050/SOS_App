// app/src/main/java/com/example/project/MainActivity.java
package com.example.project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
        );
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);
        createNotificationChannel();

        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, home.class);
            intent.putExtra("email", sessionManager.getUserEmail());
            startActivity(intent);
            finish();
        }

        TextView tv = findViewById(R.id.tv4);
        TextView ForgotPassword = findViewById(R.id.forgotPassword);
        ForgotPassword.setOnClickListener(v -> {
            ForgotPasswordDialogFragment dialog = new ForgotPasswordDialogFragment();
            dialog.show(getSupportFragmentManager(), "ForgotPasswordDialogFragment");
        });
        tv.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, register.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        });

        ImageView Google = findViewById(R.id.imageViewGoogle);
        ImageView Facebook = findViewById(R.id.imageViewFacebook);
        ImageView Twitter = findViewById(R.id.imageViewTwitter);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void login(View view) {
        EditText et1 = findViewById(R.id.emailInput);
        EditText et2 = findViewById(R.id.PasswordInput);
        String email = et1.getText().toString();
        String password = et2.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        sessionManager.createLoginSession(email);
                        Intent intent = new Intent(this, home.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "medicineChannel",
                    "Medicine Reminder",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for medicine reminders");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}