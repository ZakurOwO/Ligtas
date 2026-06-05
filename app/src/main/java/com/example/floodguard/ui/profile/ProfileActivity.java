package com.example.floodguard.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.floodguard.R;
import com.example.floodguard.model.UserModel;
import com.example.floodguard.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvBarangay, tvRole, tvStatus;
    private Button btnSignOut, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        tvBarangay = findViewById(R.id.tv_profile_barangay);
        tvRole = findViewById(R.id.tv_profile_role);
        tvStatus = findViewById(R.id.tv_profile_status);
        btnSignOut = findViewById(R.id.btn_sign_out);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        progressBar = findViewById(R.id.progress_bar);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        btnSignOut.setOnClickListener(v -> signOut());
        btnResetPassword.setOnClickListener(v -> sendPasswordReset());

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            openLogin();
            return;
        }

        loadProfile(currentUser);
    }

    private void loadProfile(FirebaseUser firebaseUser) {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("users").document(firebaseUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);

                    UserModel user = documentSnapshot.toObject(UserModel.class);
                    if (user == null) {
                        tvName.setText(firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "LigTAS User");
                        tvEmail.setText(firebaseUser.getEmail());
                        tvBarangay.setText("Barangay not set");
                        tvRole.setText("Resident");
                        tvStatus.setText("Active");
                        return;
                    }

                    tvName.setText(nonEmpty(user.getFullName(), "LigTAS User"));
                    tvEmail.setText(nonEmpty(user.getEmail(), firebaseUser.getEmail()));
                    tvBarangay.setText(nonEmpty(user.getBarangay(), "Barangay not set"));
                    tvRole.setText(formatRole(user.getRole()));
                    tvStatus.setText(user.isActive() ? "Active" : "Inactive");
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Could not load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendPasswordReset() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "No email is attached to this account.", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.sendPasswordResetEmail(user.getEmail())
                .addOnSuccessListener(unused -> Toast.makeText(this, "Password reset email sent.", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Reset failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void signOut() {
        btnSignOut.setEnabled(false);
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            auth.signOut();
            openLogin();
        });
    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String nonEmpty(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private String formatRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return "Resident";
        }
        if ("mdrrmo_officer".equals(role)) {
            return "MDRRMO Officer";
        }
        return role.substring(0, 1).toUpperCase() + role.substring(1).replace("_", " ");
    }
}
