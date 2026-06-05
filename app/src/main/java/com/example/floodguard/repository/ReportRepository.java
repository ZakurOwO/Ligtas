package com.example.floodguard.repository;

import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.example.floodguard.model.ReportModel;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

public class ReportRepository {
    private static final String TAG = "ReportRepository";
    private static ReportRepository instance;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final MutableLiveData<String> statusMsg = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSubmitting = new MutableLiveData<>();

    private ReportRepository() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance("gs://floodguard-ca1e4.firebasestorage.app");
    }

    public static synchronized ReportRepository getInstance() {
        if (instance == null) {
            instance = new ReportRepository();
        }
        return instance;
    }

    public MutableLiveData<String> getStatusMsg() { return statusMsg; }
    public MutableLiveData<Boolean> getIsSubmitting() { return isSubmitting; }

    public void submitReport(ReportModel report, Uri photoUri) {
        isSubmitting.postValue(true);
        statusMsg.postValue(null);
        
        if (photoUri != null) {
            Log.d(TAG, "Starting photo upload: " + photoUri);
            String fileName = "reports/" + report.getUserId() + "/" + System.currentTimeMillis() + ".jpg";
            StorageReference photoRef = storage.getReference().child(fileName);

            photoRef.putFile(photoUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        if (task.getException() != null) throw task.getException();
                    }
                    return photoRef.getDownloadUrl();
                })
                .addOnSuccessListener(uri -> {
                    Log.d(TAG, "Photo uploaded. URL: " + uri);
                    report.setPhotoUrl(uri.toString());
                    saveReportToFirestore(report);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Upload failed: " + e.getMessage());
                    saveReportToFirestore(report, null, e.getMessage());
                });
        } else {
            saveReportToFirestore(report, null, null);
        }
    }

    private void saveReportToFirestore(ReportModel report, String photoUrl, String uploadError) {
        Map<String, Object> payload = buildReportPayload(report, photoUrl, uploadError);
        db.collection("reports").add(payload)
                .addOnSuccessListener(documentReference -> {
                    isSubmitting.postValue(false);
                    AuditLogRepository.getInstance().log("Resident Reports", "Submitted report " + documentReference.getId(), "Success");
                    if (uploadError == null) {
                        statusMsg.postValue("Report submitted successfully!");
                    } else {
                        statusMsg.postValue("Report submitted. Photo will need review.");
                    }
                })
                .addOnFailureListener(e -> {
                    isSubmitting.postValue(false);
                    AuditLogRepository.getInstance().log("Resident Reports", "Failed to submit report", "Failed");
                    statusMsg.postValue("Database error: " + e.getMessage());
                });
    }

    private void saveReportToFirestore(ReportModel report) {
        saveReportToFirestore(report, report.getPhotoUrl(), null);
    }

    private Map<String, Object> buildReportPayload(ReportModel report, String photoUrl, String uploadError) {
        Timestamp now = Timestamp.now();
        Map<String, Object> payload = new HashMap<>();
        payload.put("submittedBy", report.getUserId());
        payload.put("userId", report.getUserId());
        payload.put("reporterName", safe(report.getReporterName(), "Resident"));
        payload.put("resident", safe(report.getReporterName(), "Resident"));
        payload.put("residentName", safe(report.getReporterName(), "Resident"));
        payload.put("barangay", safe(report.getBarangay(), "Unknown location"));
        payload.put("location", safe(report.getBarangay(), "Unknown location"));
        payload.put("description", safe(report.getDescription(), "No description provided."));
        payload.put("severity", safe(report.getSeverity(), "Moderate"));
        payload.put("status", "Pending");
        payload.put("gpsLat", report.getGpsLat());
        payload.put("gpsLng", report.getGpsLng());
        payload.put("dateSubmitted", now);
        payload.put("createdAt", now);
        payload.put("updatedAt", now);

        if (photoUrl != null && !photoUrl.trim().isEmpty()) {
            payload.put("imageUrl", photoUrl);
            payload.put("photoUrl", photoUrl);
            payload.put("image", photoUrl);
            payload.put("photoUploadStatus", "uploaded");
        } else if (uploadError != null) {
            payload.put("photoUploadStatus", "failed");
            payload.put("photoUploadError", uploadError);
        } else {
            payload.put("photoUploadStatus", "none");
        }
        return payload;
    }

    private String safe(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }
}
