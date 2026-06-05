package com.example.floodguard.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.floodguard.MainActivity;
import com.example.floodguard.R;
import com.example.floodguard.utils.ValidationUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        progressBar = findViewById(R.id.progress_bar);

        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

        if (mAuth.getCurrentUser() != null) {
            openMainActivity();
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Invalid email");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            prepareUserProfile(user);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void prepareUserProfile(FirebaseUser firebaseUser) {
        db.collection("users").document(firebaseUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        updateFcmTokenAndOpen(firebaseUser.getUid());
                    } else {
                        createMissingUserProfile(firebaseUser);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Profile error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createMissingUserProfile(FirebaseUser firebaseUser) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("uid", firebaseUser.getUid());
        profile.put("fullName", firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "LigTAS User");
        profile.put("email", firebaseUser.getEmail());
        profile.put("barangay", "");
        profile.put("role", "resident");
        profile.put("active", true);
        profile.put("createdAt", Timestamp.now());
        profile.put("updatedAt", Timestamp.now());

        db.collection("users").document(firebaseUser.getUid()).set(profile)
                .addOnSuccessListener(unused -> updateFcmTokenAndOpen(firebaseUser.getUid()))
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Could not create profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateFcmTokenAndOpen(String uid) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("fcmToken", task.getResult());
                updates.put("updatedAt", Timestamp.now());
                db.collection("users").document(uid).update(updates)
                        .addOnCompleteListener(updateTask -> openMainActivity());
            } else {
                openMainActivity();
            }
        });
    }

    private void openMainActivity() {
        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
