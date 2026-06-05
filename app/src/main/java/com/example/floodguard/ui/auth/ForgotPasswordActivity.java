package com.example.floodguard.ui.auth;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.floodguard.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button sendResetButton;
    private TextView messageTextView;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.et_email);
        sendResetButton = findViewById(R.id.btn_reset_password);
        messageTextView = findViewById(R.id.tv_reset_message);
        progressBar = findViewById(R.id.progress_bar);

        sendResetButton.setOnClickListener(v -> sendPasswordResetLink());
    }

    private void sendPasswordResetLink() {
        String email = emailEditText.getText().toString().trim();
        messageTextView.setText("");
        emailEditText.setError(null);

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            showError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address");
            showError("Enter a valid email address");
            return;
        }

        setLoading(true);
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    setLoading(false);

                    if (task.isSuccessful()) {
                        messageTextView.setTextColor(getColor(R.color.icon_green));
                        messageTextView.setText("Password reset email sent. Check your inbox or spam folder.");
                    } else {
                        showError(getFirebaseErrorMessage(task.getException(), "Failed to send password reset email."));
                    }
                });
    }

    private String getFirebaseErrorMessage(Exception exception, String fallback) {
        return exception != null && exception.getMessage() != null ? exception.getMessage() : fallback;
    }

    private void showError(String message) {
        messageTextView.setTextColor(getColor(R.color.icon_red));
        messageTextView.setText(message);
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        sendResetButton.setEnabled(!isLoading);
        emailEditText.setEnabled(!isLoading);
    }
}
