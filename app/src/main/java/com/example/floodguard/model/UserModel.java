package com.example.floodguard.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String uid;
    private String fullName;
    private String email;
    private String barangay;
    private String role; // "resident" | "mdrrmo_officer" | "admin"
    private String fcmToken;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isActive;

    public UserModel() {}

    public UserModel(String uid, String fullName, String email, String barangay, String role) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.barangay = barangay;
        this.role = role;
        this.isActive = true;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBarangay() { return barangay; }
    public void setBarangay(String barangay) { this.barangay = barangay; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
