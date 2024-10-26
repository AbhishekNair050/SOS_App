package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordDialogFragment extends DialogFragment {

    private FirebaseAuth mAuth;
    private EditText emailInput;
    private Button btnResetPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forgot_password_dialog, container, false);

        mAuth = FirebaseAuth.getInstance();
        emailInput = view.findViewById(R.id.emailInput);
        btnResetPassword = view.findViewById(R.id.btnResetPassword);

        btnResetPassword.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Password reset email sent", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Error sending password reset email", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        return view;
    }
}