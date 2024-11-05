// app/src/main/java/com/example/project/register.java
package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class register extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_account);

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("en");
        dbRef = FirebaseDatabase.getInstance().getReference("users");
        sessionManager = new SessionManager(this);
        mCallbackManager = CallbackManager.Factory.create();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ImageView CreateAccGoogle = findViewById(R.id.imageViewGoogle);
        ImageView CreateAccFacebook = findViewById(R.id.imageViewFacebook);
        ImageView CreateAccTwitter = findViewById(R.id.imageViewTwitter);

        CreateAccTwitter.setOnClickListener(v -> signInWithTwitter());
        CreateAccGoogle.setOnClickListener(v -> signIn());
        CreateAccFacebook.setOnClickListener(v -> signInWithFacebook());

        TextView tv = findViewById(R.id.tvp1);
        tv.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        });

    }

    private void signInWithTwitter() {
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");

        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            pendingResultTask
                    .addOnSuccessListener(authResult -> handleSignInResult(authResult))
                    .addOnFailureListener(e -> {
                        Toast.makeText(register.this, "Twitter login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            mAuth.startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener(authResult -> handleSignInResult(authResult))
                    .addOnFailureListener(e -> {
                        Toast.makeText(register.this, "Twitter login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void handleSignInResult(AuthResult authResult) {
        FirebaseUser user = authResult.getUser();
        if (user != null) {
            User newUser = new User(user.getDisplayName(), user.getEmail());
            dbRef.child(user.getUid()).setValue(newUser)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(register.this, "Twitter login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(register.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(register.this, "Facebook login canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(register.this, "Facebook login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            User newUser = new User(user.getDisplayName(), user.getEmail());
                            dbRef.child(user.getUid()).setValue(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(register.this, "Facebook login successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(this, home.class);
                                        sessionManager.createLoginSession(user.getEmail());
                                        sessionManager.saveUsername(user.getDisplayName());
                                        sessionManager.loadDataFromFirebase(user.getUid());
                                        intent.putExtra("email", user.getEmail());
                                        intent.putExtra("username", user.getDisplayName());
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(register.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(register.this, "Facebook login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

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
                                            dbRef.child(user.getUid()).setValue(newUser)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(register.this, "Account created successfully. Please log in.", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(this, MainActivity.class);
                                                        startActivity(intent);
                                                        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                                                        finish();
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
            Log.d("RegisterActivity", "Passwords do not match");
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty() || password.isEmpty() || cfmPassword.isEmpty() || name.isEmpty()) {
            Log.d("RegisterActivity", "Fields are empty");
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            User newUser = new User(name, email);
                            dbRef.child(user.getUid()).setValue(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        sessionManager.createLoginSession(email);
                                        sessionManager.saveUsername(name);
                                        Log.d("RegisterActivity", "Account created successfully");
                                        Toast.makeText(register.this, "Account created successfully. Welcome.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(this, MainActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.d("RegisterActivity", "Error saving user data: " + e.getMessage());
                                        Toast.makeText(register.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.d("RegisterActivity", "Error: User is null");
                            Toast.makeText(register.this, "Error: User is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("RegisterActivity", "Error creating account: " + task.getException().getMessage());
                        Toast.makeText(register.this, "Error creating account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}