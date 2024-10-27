// app/src/main/java/com/example/project/register.java
package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class register extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_account);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ImageView CreateAccGoogle = findViewById(R.id.imageViewGoogle);
        CreateAccGoogle.setOnClickListener(v -> signIn());

        TextView tv = findViewById(R.id.tvp1);
        tv.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // app/src/main/java/com/example/project/register.java
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.fetchSignInMethodsForEmail(acct.getEmail())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        if (isNewUser) {
                            mAuth.signInWithCredential(credential)
                                    .addOnCompleteListener(this, signInTask -> {
                                        if (signInTask.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            User newUser = new User(user.getDisplayName(), user.getEmail());
                                            db.collection("users").document(user.getUid()).set(newUser)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(register.this, "Account created successfully. Please log in.", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(this, MainActivity.class);
                                                        startActivity(intent);
                                                        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                                                        finish(); // Close the register activity
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(register.this, "Error creating account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            Toast.makeText(register.this, "Error creating account: " + signInTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(register.this, "Email already exists. Please log in.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(register.this, "Error checking email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void create_acc(View view) {
        EditText ed1 = findViewById(R.id.emailInput);
        EditText ed2 = findViewById(R.id.PasswordInput);
        EditText ed3 = findViewById(R.id.cfmPasswordInput);
        EditText ed4 = findViewById(R.id.namelInput);

        String email = ed1.getText().toString();
        String password = ed2.getText().toString();
        String cfmPassword = ed3.getText().toString();
        String name = ed4.getText().toString();

        if (!password.equals(cfmPassword)) {
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty() || password.isEmpty() || cfmPassword.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        User newUser = new User(name, email);
                        db.collection("users").document(user.getUid()).set(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(register.this, "Account created successfully. Please log in.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, MainActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                                    finish(); // Close the register activity
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(register.this, "Error creating account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(register.this, "Error creating account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}