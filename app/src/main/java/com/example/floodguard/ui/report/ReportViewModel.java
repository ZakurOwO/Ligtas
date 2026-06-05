package com.example.floodguard.ui.report;

import android.app.Application;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.floodguard.model.ReportModel;
import com.example.floodguard.repository.ReportRepository;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

public class ReportViewModel extends AndroidViewModel {
    private final ReportRepository repository;
    private final MutableLiveData<Uri> selectedPhotoUri = new MutableLiveData<>();
    private Uri cameraImageUri; // Temporary storage for camera URI to survive recreation
    private double lat = 14.5995, lng = 120.9842; // Default to Manila

    public ReportViewModel(@NonNull Application application) {
        super(application);
        repository = ReportRepository.getInstance();
    }

    public void setSelectedPhotoUri(Uri uri) { selectedPhotoUri.setValue(uri); }
    public LiveData<Uri> getSelectedPhotoUri() { return selectedPhotoUri; }

    public void setCameraImageUri(Uri uri) { this.cameraImageUri = uri; }
    public Uri getCameraImageUri() { return cameraImageUri; }

    public void setLocation(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public LiveData<String> getStatusMsg() { return repository.getStatusMsg(); }
    public LiveData<Boolean> getIsSubmitting() { return repository.getIsSubmitting(); }

    public void submitReport(String barangay, String desc, String severity, String userName) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        ReportModel report = new ReportModel();
        report.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        report.setReporterName(userName);
        report.setBarangay(barangay);
        report.setDescription(desc);
        report.setGpsLat(lat);
        report.setGpsLng(lng);
        report.setSeverity(severity);
        report.setStatus("Pending");
        report.setCreatedAt(Timestamp.now());

        repository.submitReport(report, selectedPhotoUri.getValue());
    }
}
