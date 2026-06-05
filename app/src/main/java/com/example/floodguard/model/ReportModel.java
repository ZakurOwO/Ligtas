package com.example.floodguard.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

@IgnoreExtraProperties
public class ReportModel {
    private String id;
    private String userId; // maps to submittedBy
    private String reporterName; // maps to submitterName
    private String contactNumber;
    private String location;
    private String barangay;
    private String description;
    private String imageUrl; // maps to photoUrl
    private String status;
    private double gpsLat;
    private double gpsLng;
    private String severity;
    
    @ServerTimestamp
    private Timestamp createdAt; // maps to submittedAt
    @ServerTimestamp
    private Timestamp updatedAt;

    public ReportModel() {}

    // Compatibility Getters/Setters for existing Android Code
    public String getReportId() { return id; }
    public void setReportId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setSubmittedBy(String userId) { this.userId = userId; }
    public String getSubmittedBy() { return userId; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    public void setSubmitterName(String name) { this.reporterName = name; }
    public String getSubmitterName() { return reporterName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getBarangay() { return barangay; }
    public void setBarangay(String barangay) { this.barangay = barangay; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPhotoUrl(String photoUrl) { this.imageUrl = photoUrl; }
    public String getPhotoUrl() { return imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getGpsLat() { return gpsLat; }
    public void setGpsLat(double gpsLat) { this.gpsLat = gpsLat; }

    public double getGpsLng() { return gpsLng; }
    public void setGpsLng(double gpsLng) { this.gpsLng = gpsLng; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setSubmittedAt(Timestamp submittedAt) { this.createdAt = submittedAt; }
    public Timestamp getSubmittedAt() { return createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
