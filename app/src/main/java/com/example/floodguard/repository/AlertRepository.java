package com.example.floodguard.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.floodguard.data.DefaultMobileData;
import com.example.floodguard.model.Alert;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.Collections;
import java.util.List;

public class AlertRepository {
    private static AlertRepository instance;
    private final FirebaseFirestore db;
    private final MutableLiveData<ApiResult<List<Alert>>> activeAlertsData = new MutableLiveData<>();
    private ListenerRegistration activeAlertsListener;

    private AlertRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized AlertRepository getInstance() {
        if (instance == null) {
            instance = new AlertRepository();
        }
        return instance;
    }

    /**
     * Returns a single LiveData instance for active alerts.
     * It starts the Firestore listener only once.
     */
    public LiveData<ApiResult<List<Alert>>> getActiveAlerts() {
        if (activeAlertsListener == null) {
            startListeningForAlerts();
        }
        return activeAlertsData;
    }

    private void startListeningForAlerts() {
        activeAlertsData.setValue(ApiResult.loading());
        activeAlertsListener = db.collection("alerts")
                .whereEqualTo("status", "Active")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        activeAlertsData.setValue(ApiResult.success(DefaultMobileData.alerts()));
                        return;
                    }
                    if (value != null) {
                        List<Alert> alerts = value.toObjects(Alert.class);
                        if (alerts.isEmpty()) {
                            activeAlertsData.setValue(ApiResult.success(DefaultMobileData.alerts()));
                            return;
                        }
                        Collections.sort(alerts, (first, second) -> {
                            if (first.getCreatedAt() == null && second.getCreatedAt() == null) {
                                return 0;
                            }
                            if (first.getCreatedAt() == null) {
                                return 1;
                            }
                            if (second.getCreatedAt() == null) {
                                return -1;
                            }
                            return second.getCreatedAt().compareTo(first.getCreatedAt());
                        });
                        activeAlertsData.setValue(ApiResult.success(alerts));
                    }
                });
    }

    public void cleanup() {
        if (activeAlertsListener != null) {
            activeAlertsListener.remove();
            activeAlertsListener = null;
        }
    }
}
