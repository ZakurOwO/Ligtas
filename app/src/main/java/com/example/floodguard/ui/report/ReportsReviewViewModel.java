package com.example.floodguard.ui.report;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.floodguard.model.ReportModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class ReportsReviewViewModel extends AndroidViewModel {
    private final FirebaseFirestore db;
    private final MutableLiveData<List<ReportModel>> reports = new MutableLiveData<>();
    private final MutableLiveData<String> statusMsg = new MutableLiveData<>();

    public ReportsReviewViewModel(@NonNull Application application) {
        super(application);
        db = FirebaseFirestore.getInstance();
        fetchPendingReports();
    }

    public LiveData<List<ReportModel>> getReports() { return reports; }
    public LiveData<String> getStatusMsg() { return statusMsg; }

    public void fetchPendingReports() {
        db.collection("reports")
                .whereEqualTo("status", "pending")
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        statusMsg.setValue("Error fetching reports: " + error.getMessage());
                        return;
                    }
                    if (value != null) {
                        List<ReportModel> list = new ArrayList<>();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                            ReportModel report = doc.toObject(ReportModel.class);
                            report.setReportId(doc.getId());
                            list.add(report);
                        }
                        reports.setValue(list);
                    }
                });
    }

    public void updateReportStatus(String reportId, String newStatus) {
        String officerUid = FirebaseAuth.getInstance().getUid();
        db.collection("reports").document(reportId)
                .update("status", newStatus,
                        "reviewedBy", officerUid,
                        "reviewedAt", Timestamp.now())
                .addOnSuccessListener(aVoid -> statusMsg.setValue("Report " + newStatus))
                .addOnFailureListener(e -> statusMsg.setValue("Update failed: " + e.getMessage()));
    }
}
